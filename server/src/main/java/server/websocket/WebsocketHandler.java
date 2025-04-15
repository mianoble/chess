package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.w3c.dom.CDATASection;
import service.UserService;
import websocket.commands.ConnectCommand;
import websocket.commands.LeaveCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.ResignCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

@WebSocket
public class WebsocketHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        System.out.println("Received WebSocket message: " + message);
        try {
            if (message.contains("\"commandType\":\"CONNECT\"")) {
                ConnectCommand com = new Gson().fromJson(message, ConnectCommand.class);

                var authDAO = new MySQLAuthDAO();
                AuthData auth = authDAO.getAuth(com.getAuthToken());
                if (auth == null) {
                    ErrorMessage error = new ErrorMessage("Invalid auth token.");
                    session.getRemote().sendString(new Gson().toJson(error));
                    return; // Don't continue
                }
                String user = auth.username();

                // connections.add(user, session, com.getGameID()); // add new session for this new player
                connectPlayer(session, com);
            } else if (message.contains("\"commandType\":\"MAKE_MOVE\"")) {
                MakeMoveCommand com = new Gson().fromJson(message, MakeMoveCommand.class);

                var authDAO = new MySQLAuthDAO();
                AuthData auth = authDAO.getAuth(com.getAuthToken());

//                if (auth == null) return;
                if (auth == null) {
                    System.out.println("Invalid auth token. Sending error.");
                    ErrorMessage error = new ErrorMessage("Unauthorized move attempt — invalid auth token.");
                    try {
                        session.getRemote().sendString(new Gson().toJson(error));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                }

                String user = auth.username();


                makeMove(session, com, user);
            } else if (message.contains("\"commandType\":\"RESIGN\"")) {
                ResignCommand com = new Gson().fromJson(message, ResignCommand.class);
                playerResign(session, com);
            } else if (message.contains("\"commandType\":\"LEAVE\"")) {
                LeaveCommand com = new Gson().fromJson(message, LeaveCommand.class);

                //todo double check this
                var authDAO = new MySQLAuthDAO();
                AuthData auth = authDAO.getAuth(com.getAuthToken());
                if (auth == null) { return; }

                String user = auth.username();
                playerLeave(session, com, user);
                Thread.sleep(50);
                connections.remove(user);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        // Thread.sleep(50);
        connections.remove(session);
    }

    private void connectPlayer(Session session, ConnectCommand com) throws IOException {
        try {
            var authDAO = new MySQLAuthDAO();
            AuthData auth = authDAO.getAuth(com.getAuthToken());
            if (auth == null) {
                var error = new ErrorMessage("Invalid auth token.");
                session.getRemote().sendString(new Gson().toJson(error));
                return;
            }
            String user = auth.username();

            var gameDAO = new dataaccess.MySQLGameDAO();
            var game = gameDAO.getGame(com.getGameID());
            ChessGame.TeamColor color = null;

            if (user.equals(game.whiteUsername())) {
                color = ChessGame.TeamColor.WHITE;
            } else if (user.equals(game.blackUsername())) {
                color = ChessGame.TeamColor.BLACK;
            }

            connections.add(user, session, com.getGameID());

            NotificationMessage notif;
            if (color == null) {
                notif = new NotificationMessage(user + " has joined the game as a " + "spectator.");
            }
            else {
            // send message to everyone in game that player joined
                notif = new NotificationMessage(user + " has joined the game on the " + color + " side.");
            }
            connections.broadcast(user, com.getGameID(), notif);

            // send board to the current user
            LoadGameMessage gameMessage = new LoadGameMessage(game.game());
            session.getRemote().sendString(new Gson().toJson(gameMessage));
        } catch (Exception e) {
            e.printStackTrace();
            try {
                var error = new websocket.messages.ErrorMessage("Failed to connect to game: " + e.getMessage());
                session.getRemote().sendString(new Gson().toJson(error));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    private void makeMove(Session session, MakeMoveCommand com, String user) {
        try {
            var authDAO = new MySQLAuthDAO();
            AuthData auth = authDAO.getAuth(com.getAuthToken());
            if (auth == null) {
                var error = new ErrorMessage("Unauthorized move attempt — invalid auth token.");
                session.getRemote().sendString(new Gson().toJson(error));
                return;
            }
            GameDAO gameDAO = new dataaccess.MySQLGameDAO();
            GameData gameData = gameDAO.getGame(com.getGameID());
            if (gameData == null) {
                var error = new ErrorMessage("Game not found.");
                session.getRemote().sendString(new Gson().toJson(error));
                return;
            }
            ChessGame game = gameData.game();
            if (game.isGameOver()) {
                var error = new ErrorMessage("The game is over.");
                session.getRemote().sendString(new Gson().toJson(error));
                return;
            }
            ChessGame.TeamColor playerColor;
            if (auth.username().equals(gameData.whiteUsername())) {
                playerColor = ChessGame.TeamColor.WHITE;
            } else if (auth.username().equals(gameData.blackUsername())) {
                playerColor = ChessGame.TeamColor.BLACK;
            } else {
                var error = new ErrorMessage("You are not a player in this game.");
                session.getRemote().sendString(new Gson().toJson(error));
                return;
            }
            if (game.getTeamTurn() != playerColor) {
                var error = new ErrorMessage("It's not your turn.");
                session.getRemote().sendString(new Gson().toJson(error));
                return;
            }

            ChessMove move = com.getMove(); // get the move from the command

            var piece = game.getBoard().getPiece(move.getStartPosition());
            if (piece == null || piece.getTeamColor() != playerColor) {
                var error = new ErrorMessage("You can only move your own pieces.");
                session.getRemote().sendString(new Gson().toJson(error));
                return;
            }
            game.makeMove(move);
            gameDAO.updateGame(new GameData(com.getGameID(), gameData.whiteUsername(), gameData.blackUsername(),
                    gameData.gameName(), game));
            LoadGameMessage updateMessage = new LoadGameMessage(game);
            for (var con : connections.connections.keySet()) {
                if (connections.connections.get(con).equals(com.getGameID())) {
                    con.send(new Gson().toJson(updateMessage));
                }
            }



            ChessGame.TeamColor turn = game.getTeamTurn();
            if (turn == ChessGame.TeamColor.WHITE) {
                ChessGame.TeamColor opponent = ChessGame.TeamColor.WHITE;
                connections.broadcast(user, com.getGameID(), new NotificationMessage(gameData.blackUsername() +
                        " made this move: " + move.toString()));
                if (game.isInCheckmate(opponent)) {
                    connections.broadcast(null, com.getGameID(), new NotificationMessage("Checkmate! " +
                            gameData.blackUsername() + " lost the game."));
                } else if (game.isInStalemate(opponent)) {
                    connections.broadcast(null, com.getGameID(), new NotificationMessage("Stalemate! It's a tie."));
                } else if (game.isInCheck(opponent)) {
                    connections.broadcast(null, com.getGameID(), new NotificationMessage("Look out! " +
                            gameData.blackUsername() + " is in check."));
                }
            } else {
                ChessGame.TeamColor opponent = ChessGame.TeamColor.BLACK;
                connections.broadcast(user, com.getGameID(), new NotificationMessage(gameData.whiteUsername() +
                        " made this move: " + move.toString()));
                if (game.isInCheckmate(opponent)) {
                    connections.broadcast(null, com.getGameID(), new NotificationMessage("Checkmate! " +
                            gameData.whiteUsername() + " lost the game."));
                } else if (game.isInStalemate(opponent)) {
                    connections.broadcast(null, com.getGameID(), new NotificationMessage("Stalemate! It's a tie."));
                } else if (game.isInCheck(opponent)) {
                    connections.broadcast(null, com.getGameID(), new NotificationMessage("Look out! " +
                            gameData.whiteUsername() + " is in check."));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                var error = new websocket.messages.ErrorMessage("Failed to make move: " + e.getMessage());
                session.getRemote().sendString(new Gson().toJson(error));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

//    private void makeMove(Session session, MakeMoveCommand com, String user) {
//        try {
//            var authDAO = new MySQLAuthDAO();
//            var gameDAO = new MySQLGameDAO();
//            AuthData auth = authDAO.getAuth(com.getAuthToken());
//            if (auth == null) {
//                sendError(session, "Unauthorized move attempt — invalid auth token.");
//                return;
//            }
//            GameData gameData = gameDAO.getGame(com.getGameID());
//            if (gameData == null) {
//                sendError(session, "Game not found.");
//                return;
//            }
//            ChessGame game = gameData.game();
//            if (game.isGameOver()) {
//                sendError(session, "The game is over.");
//                return;
//            }
//            ChessGame.TeamColor playerColor = getPlayerColor(auth.username(), gameData);
//            if (playerColor == null) {
//                sendError(session, "You are not a player in this game.");
//                return;
//            }
//            if (game.getTeamTurn() != playerColor) {
//                sendError(session, "It's not your turn.");
//                return;
//            }
//            ChessMove move = com.getMove();
//            var piece = game.getBoard().getPiece(move.getStartPosition());
//            if (piece == null || piece.getTeamColor() != playerColor) {
//                sendError(session, "You can only move your own pieces.");
//                return;
//            }
//            ChessGame.TeamColor opponent = (playerColor == ChessGame.TeamColor.WHITE)
//                    ? ChessGame.TeamColor.BLACK
//                    : ChessGame.TeamColor.WHITE;
//            String opponentUsername = (opponent == ChessGame.TeamColor.WHITE)
//                    ? gameData.whiteUsername()
//                    : gameData.blackUsername();
//            game.makeMove(move);
//            gameDAO.updateGame(new GameData(com.getGameID(), gameData.whiteUsername(), gameData.blackUsername(),
//                    gameData.gameName(), game));
//            LoadGameMessage updateMessage = new LoadGameMessage(game);
//            for (var con : connections.connections.keySet()) {
//                if (connections.connections.get(con).equals(com.getGameID())) {
//                    con.send(new Gson().toJson(updateMessage));
//                }
//            }
//
//            if (game.isInCheckmate(opponent)) {
//                connections.broadcast(null, com.getGameID(),
//                        new NotificationMessage("Checkmate! " + opponentUsername + " lost the game."));
//                game.setGameOver(true);
//                gameDAO.updateGame(gameData);
//            } else if (game.isInStalemate(opponent)) {
//                connections.broadcast(null, com.getGameID(),
//                        new NotificationMessage("Stalemate! It's a tie."));
//                game.setGameOver(true);
//                gameDAO.updateGame(gameData);
//            } else if (game.isInCheck(opponent)) {
//                connections.broadcast(null, com.getGameID(), new NotificationMessage("Look out! " +
//                        opponentUsername + " is in check."));
//            } else {
//                connections.broadcast(user, com.getGameID(), new NotificationMessage(user + " made a move."));
//            }
//        } catch(Exception e) {
//            e.printStackTrace();
//            try {
//                sendError(session, "Failed to make move: " + e.getMessage());
//            } catch (IOException ioException) {
//                ioException.printStackTrace();
//            }
//        }
//    }

    private void sendError(Session session, String message) throws IOException {
        ErrorMessage error = new ErrorMessage(message);
        session.getRemote().sendString(new Gson().toJson(error));
    }

    private ChessGame.TeamColor getPlayerColor(String username, GameData gameData) {
        if (username.equals(gameData.whiteUsername())) {
            return ChessGame.TeamColor.WHITE;
        } else if (username.equals(gameData.blackUsername())) {
            return ChessGame.TeamColor.BLACK;
        } else {
            return null;
        }
    }

    private void playerResign(Session session, ResignCommand com) throws ResponseException {
        //todo
        try {
            AuthTokenDAO authDAO = new dataaccess.MySQLAuthDAO();
            GameDAO gameDAO = new MySQLGameDAO();
            var auth = authDAO.getAuth(com.getAuthToken());
            var game = gameDAO.getGame(com.getGameID());

            if (!auth.username().equals(game.whiteUsername()) && !auth.username().equals(game.blackUsername())) {
                var error = new ErrorMessage("You can't resign as an observer.");
                session.getRemote().sendString(new Gson().toJson(error));
                return;
            }

            if (game.game().isGameOver()) {
                var error = new ErrorMessage("You can't resign. The game is over.");
                session.getRemote().sendString(new Gson().toJson(error));
                return;
            }

            game.game().setGameOver(true);
            gameDAO.updateGame(game);

            connections.broadcast(null, com.getGameID(), new NotificationMessage(auth.username() +
                    " has resigned. The game is over."));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    private void playerLeave(Session session, LeaveCommand com, String user) {
        try {
            GameDAO gameDAO = new dataaccess.MySQLGameDAO();
            GameData gameData = gameDAO.getGame(com.getGameID());

            GameData updatedGameData = null;
            boolean changed = false;

            if (user.equals(gameData.whiteUsername())) {
                updatedGameData = new GameData(gameData.gameID(), null, gameData.blackUsername(), gameData.gameName(), gameData.game());
                changed = true;
            } else if (user.equals(gameData.blackUsername())) {
                updatedGameData = new GameData(gameData.gameID(), gameData.whiteUsername(), null, gameData.gameName(), gameData.game());
                changed = true;
            } else {
                NotificationMessage notif = new NotificationMessage(user + " has left the game as a spectator.");
                connections.broadcast(user, com.getGameID(), notif);
                return;
            }

            if (changed) {
                gameDAO.updateGame(updatedGameData);
            }

            // send message to everyone in game that player left
            NotificationMessage notif = new NotificationMessage(user + " has left the game.");
            connections.broadcast(user, com.getGameID(), notif);


        } catch (IOException | ResponseException e) {
            throw new RuntimeException(e);
        }
    }
}

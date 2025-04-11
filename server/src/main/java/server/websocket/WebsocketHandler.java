package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.GameDAO;
import dataaccess.MySQLAuthDAO;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.UserService;
import websocket.commands.ConnectCommand;
import websocket.commands.LeaveCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.ResignCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;

@WebSocket
public class WebsocketHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
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

                connections.add(user, session, com.getGameID()); // add new session for this new player
                connectPlayer(session, com);
            } else if (message.contains("\"commandType\":\"MAKE_MOVE\"")) {
                MakeMoveCommand com = new Gson().fromJson(message, MakeMoveCommand.class);
                makeMove(session, com);
            } else if (message.contains("\"commandType\":\"RESIGN\"")) {
                ResignCommand com = new Gson().fromJson(message, ResignCommand.class);
                playerResign(session, com);
            } else if (message.contains("\"commandType\":\"LEAVE\"")) {
                LeaveCommand com = new Gson().fromJson(message, LeaveCommand.class);

                //todo double check this
                var authDAO = new MySQLAuthDAO();
                AuthData auth = authDAO.getAuth(com.getAuthToken());
                String user = auth.username();

                connections.remove(user);
                playerLeave(session, com);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void connectPlayer(Session session, ConnectCommand com) throws IOException {
        // check if connect is valid


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

            if (color == null) {
                NotificationMessage notif = new NotificationMessage(user + " has joined the game as a " +
                        "spectator.");
                connections.broadcast(user, notif);
            }
            else {
            // send message to everyone in game that player joined
            NotificationMessage notif = new NotificationMessage(user + " has joined the game on the " +
                    color + " side.");
                connections.broadcast(user, notif);
            }

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

    private void makeMove(Session session, MakeMoveCommand com) {
        try {
            GameDAO gameDAO = new dataaccess.MySQLGameDAO();
            GameData gameData = gameDAO.getGame(com.getGameID());
            ChessGame game = gameData.game();

            ChessMove move = com.getMove(); // get the move from the command
            game.makeMove(move);

            // update the game in the DAO
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
                ChessGame.TeamColor opponent = ChessGame.TeamColor.BLACK;
                if (game.isInCheckmate(opponent)) {
                    connections.broadcast(null, new NotificationMessage("Checkmate! " +
                            gameData.blackUsername() + " lost the game."));
                } else if (game.isInStalemate(opponent)) {
                    connections.broadcast(null, new NotificationMessage("Stalemate! It's a tie."));
                } else if (game.isInCheck(opponent)) {
                    connections.broadcast(null, new NotificationMessage("Look out! " +
                            gameData.blackUsername() + " is in check."));
                } else {
                    connections.broadcast(null, new NotificationMessage(gameData.blackUsername() +
                            " made a move."));
                }
            } else {
                ChessGame.TeamColor opponent = ChessGame.TeamColor.WHITE;
                if (game.isInCheckmate(opponent)) {
                    connections.broadcast(null, new NotificationMessage("Checkmate! " +
                            gameData.whiteUsername() + " lost the game."));
                } else if (game.isInStalemate(opponent)) {
                    connections.broadcast(null, new NotificationMessage("Stalemate! It's a tie."));
                } else if (game.isInCheck(opponent)) {
                    connections.broadcast(null, new NotificationMessage("Look out! " +
                            gameData.whiteUsername() + " is in check."));
                } else {
                    connections.broadcast(null, new NotificationMessage(gameData.whiteUsername() +
                            " made a move."));
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

    private void playerResign(Session session, ResignCommand com) {
        //todo
    }

    private void playerLeave(Session session, LeaveCommand com) {
        try {
            // send message to everyone in game that player left
            NotificationMessage notif = new NotificationMessage(com.getUsername() + " has left the game.");
            connections.broadcast(com.getUsername(), notif);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

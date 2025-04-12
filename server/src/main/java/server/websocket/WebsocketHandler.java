package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
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
                if (auth == null) return;

                String user = auth.username();
                playerLeave(session, com, user);
                Thread.sleep(50);
                connections.remove(user);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

            System.out.println("Attempting move with token: " + com.getAuthToken());

            AuthData auth = authDAO.getAuth(com.getAuthToken());
            if (auth == null) {
                System.out.println("Auth is null. Sending error message to client.");
                var error = new ErrorMessage("Unauthorized move attempt — invalid auth token.");
                System.out.println("Sending error: " + new Gson().toJson(error));
                session.getRemote().sendString(new Gson().toJson(error));
                return;
            }

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
                    connections.broadcast(null, com.getGameID(), new NotificationMessage("Checkmate! " +
                            gameData.blackUsername() + " lost the game."));
                } else if (game.isInStalemate(opponent)) {
                    connections.broadcast(null, com.getGameID(), new NotificationMessage("Stalemate! It's a tie."));
                } else if (game.isInCheck(opponent)) {
                    connections.broadcast(null, com.getGameID(), new NotificationMessage("Look out! " +
                            gameData.blackUsername() + " is in check."));
                } else {
                    connections.broadcast(user, com.getGameID(), new NotificationMessage(gameData.blackUsername() +
                            " made a move."));
                }
            } else {
                ChessGame.TeamColor opponent = ChessGame.TeamColor.WHITE;
                if (game.isInCheckmate(opponent)) {
                    connections.broadcast(null, com.getGameID(), new NotificationMessage("Checkmate! " +
                            gameData.whiteUsername() + " lost the game."));
                } else if (game.isInStalemate(opponent)) {
                    connections.broadcast(null, com.getGameID(), new NotificationMessage("Stalemate! It's a tie."));
                } else if (game.isInCheck(opponent)) {
                    connections.broadcast(null, com.getGameID(), new NotificationMessage("Look out! " +
                            gameData.whiteUsername() + " is in check."));
                } else {
                    connections.broadcast(user, com.getGameID(), new NotificationMessage(gameData.whiteUsername() +
                            " made a move."));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                var error = new websocket.messages.ErrorMessage("Failed to make move: " + e.getMessage());
                // connections.broadcast(null, com.getGameID(), error);
                session.getRemote().sendString(new Gson().toJson(error));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    private void playerResign(Session session, ResignCommand com) throws ResponseException {
        //todo
        try {
            AuthTokenDAO authDAO = new dataaccess.MySQLAuthDAO();
            GameDAO gameDAO = new MySQLGameDAO();
            var auth = authDAO.getAuth(com.getAuthToken());
            var game = gameDAO.getGame(com.getGameID());

            game.game().setGameOver(true);
            gameDAO.updateGame(game);

            connections.broadcast(null, com.getGameID(), new NotificationMessage(auth.username() +
                    " has resigned. The game is over."));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        /*
        try {



        // 3. Have resigning player leave
        playerLeave(session, new LeaveCommand(com.getAuthString(), com.getGameID()));

        // 4. Kick observers (could filter based on users not being white/black)
        List<String> removeList = new ArrayList<>();
        for (var entry : connections.connections.entrySet()) {
            Connection conn = entry.getKey();
            int id = entry.getValue();

            if (id == com.getGameID() && !conn.getUsername().equals(game.whiteUsername()) && !conn.getUsername().equals(game.blackUsername())) {
                // Notify remaining players
                var kickNotif = new NotificationMessage(conn.getUsername() + " has been removed (game is over).");
                connections.broadcast(conn.getUsername(), kickNotif);
                removeList.add(conn.getUsername());
            }
        }

        for (String username : removeList) {
            connections.remove(username);
        }

    } catch (Exception e) {
        e.printStackTrace(); // Or send error back to client
    }
         */
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

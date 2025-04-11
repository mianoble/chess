package client;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import model.ResponseException;
import org.eclipse.jetty.io.EndPoint;
import ui.BoardPrintUpdater;
import ui.GameplayClient;
import websocket.commands.ConnectCommand;
import websocket.commands.LeaveCommand;
import websocket.commands.MakeMoveCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import javax.websocket.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static ui.EscapeSequences.ERASE_LINE;
import static websocket.commands.ConnectCommand.Role.PLAYER;
//import javax.websocket.Endpoint;

public class WebsocketCommunicator extends Endpoint {
    Session session;
    NotificationHandler notificationHandler;
    GameplayClient gameplayClient;
    private ChessGame.TeamColor teamColor;

    public WebsocketCommunicator(String url, NotificationHandler notificationHandler) throws Exception {
        try {
            url = url.replace("http", "ws");
            URI uri = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, uri);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    messageHandler(message);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    // handling messages that come in FROM the server
    private void messageHandler(String message) {
        if (message.contains("\"serverMessageType\":\"LOAD_GAME\"")) {
            LoadGameMessage loadGame = new Gson().fromJson(message, LoadGameMessage.class);
            printGame(loadGame.getGame());

        }
        else if (message.contains("\"serverMessageType\":\"ERROR\"")) {
            ErrorMessage error = new Gson().fromJson(message, ErrorMessage.class);
            printNotif(error.getErrorMessage());
        }
        else if (message.contains("\"serverMessageType\":\"NOTIFICATION\"")) {
            NotificationMessage notif = new Gson().fromJson(message, NotificationMessage.class);
            printNotif(notif.getMessage());
        }
    }

    // todo
    private void printNotif(String message) {
        // System.out.print(ERASE_LINE + '\r');
        System.out.print("\n" + message + "\n >>> ");
    }

    private void printGame(ChessGame game) {
        // System.out.print(ERASE_LINE + '\r');
        // todo: finish this, change to gameplayclient add methods or whateverrrr
        gameplayClient.getBoardPrintUpdater().boardUpdate(game);
        gameplayClient.getBoardPrintUpdater().boardPrint(teamColor, null);

        System.out.print(" >>> ");
    }

    // todo: implement methods for sending messages TO the server
    //  (ex: when someone joins a game, we send that message to the server, then the server will sent out
    //  notif to everyone else connected)

    // sendMessage class (called every time necessary -
    //  join game, spectate game, make move, resign, leave game, in check?, etc.)
    public void userJoinedAGame(String auth, String username, int gameID, ConnectCommand.Role role) throws IOException {
        ConnectCommand con;
        con = new ConnectCommand(auth, gameID, username, role);
        String json = new Gson().toJson(con);
        session.getBasicRemote().sendText(json);
    }

    public void playerMadeMove(String auth, int gameID, ChessMove chessMove) {
        MakeMoveCommand move;
        move = new MakeMoveCommand(auth, gameID, chessMove);
        String json = new Gson().toJson(move);
        session.getAsyncRemote().sendText(json);
    }


    public void userLeftAGame(String auth, int id, String user) throws IOException {
        LeaveCommand leave;
        leave = new LeaveCommand(auth, id, user);
        String json = new Gson().toJson(leave);
        session.getBasicRemote().sendText(json);
    }

}

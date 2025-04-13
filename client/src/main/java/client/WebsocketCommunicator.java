package client;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import model.ResponseException;
import ui.GameplayClient;
import websocket.commands.ConnectCommand;
import websocket.commands.LeaveCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.ResignCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static ui.EscapeSequences.*;
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
            notificationHandler.notify(loadGame);
        }
        else if (message.contains("\"serverMessageType\":\"ERROR\"")) {
            ErrorMessage error = new Gson().fromJson(message, ErrorMessage.class);
            notificationHandler.notify(error);
        }
        else if (message.contains("\"serverMessageType\":\"NOTIFICATION\"")) {
            NotificationMessage notif = new Gson().fromJson(message, NotificationMessage.class);
            notificationHandler.notify(notif);
        }
    }

    public void setGameplayClient(GameplayClient client) {
        this.gameplayClient = client;
    }

    public void userJoinedAGame(String auth, String username, int gameID, ConnectCommand.Role role) throws IOException {
        ConnectCommand con;
        con = new ConnectCommand(auth, gameID, username, role);
        String json = new Gson().toJson(con);
        session.getBasicRemote().sendText(json);
    }

    public void playerMadeMove(String auth, int gameID, ChessMove chessMove, String currUser) throws IOException {
        MakeMoveCommand move;
        move = new MakeMoveCommand(auth, gameID, chessMove, currUser);
        String json = new Gson().toJson(move);
        session.getBasicRemote().sendText(json);
    }


    public void userLeftAGame(String auth, int id, String user) throws IOException {
        LeaveCommand leave;
        leave = new LeaveCommand(auth, id, user);
        String json = new Gson().toJson(leave);
        session.getBasicRemote().sendText(json);
    }

    public void sendResign(String authToken, int gameID) {
        ResignCommand resign;
        resign = new ResignCommand(authToken, gameID);
        String msg = new Gson().toJson(resign);
        session.getAsyncRemote().sendText(msg);
    }

    public void setTeamColor(ChessGame.TeamColor color) {
        this.teamColor = color;
    }

}

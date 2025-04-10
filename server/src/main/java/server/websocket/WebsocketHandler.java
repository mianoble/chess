package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.ConnectCommand;

@WebSocket
public class WebsocketHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        if (message.contains("\"commandType\":\"CONNECT\"")) {
            ConnectCommand con = new Gson().fromJson(message, ConnectCommand.class);
            connections.add(con.getUsername(), session, con.getGameID());


        }
    }
}

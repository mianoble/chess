package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Connection, Integer> connections = new ConcurrentHashMap<>();

    public void add(String username, Session session, Integer gameID) {
        var connection = new Connection(username, session);
        System.out.println("Adding connection for " + username + " on game " + gameID);
        connections.put(connection, gameID);
    }

    public void remove(Session session) {
        connections.keySet().removeIf(conn -> conn.getSession().equals(session));
    }

    public void remove(String user) {
        connections.keySet().removeIf(conn -> conn.getUsername().equals(user));
    }

    public void broadcast(String excludeUser, int gameID, ServerMessage notif) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var entry : connections.entrySet()) {
            Connection c = entry.getKey();
            int id = entry.getValue();

            String test = ("Attempting to send to " + c.getUsername() + " (open? " + c.session.isOpen() + ")");
            System.out.println(test);

            // check if the game id is the right one. if not, skip to next loop iteration
            if (id != gameID) { continue; }

            // if session is open, broadcast to all but current user
            if (c.session.isOpen()) {
                if (!c.getUsername().equals(excludeUser)) {
                    System.out.println("Sending to " + c.getUsername() + ": " + new Gson().toJson(notif));

                    c.send(new Gson().toJson(notif));
                }
            } else {
                removeList.add(c);
            }
        }

        // remove the lost connections
        for (var c : removeList) {
            connections.remove(c);
        }
    }
}

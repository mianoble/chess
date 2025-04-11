package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.NotificationMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Connection, Integer> connections = new ConcurrentHashMap<>();

    public void add(String username, Session session, Integer gameID) {
        var connection = new Connection(username, session);
        connections.put(connection, gameID);
    }

    public void remove(String username) {
        connections.keySet().removeIf(conn -> conn.getUsername().equals(username));
    }

    public void broadcast(String excludeUser, NotificationMessage notif) throws IOException {
        // find what game they are in and get the gameID
        Integer userGameID = null;
        for (var entry : connections.entrySet()) {
            if (entry.getKey().getUsername().equals(excludeUser)) {
                userGameID = entry.getValue();
                break;
            }
        }
        if (userGameID == null) return;

        var removeList = new ArrayList<Connection>();
        for (var entry : connections.entrySet()) {
            Connection c = entry.getKey();
            int id = entry.getValue();

            // check if the game id is the right one. if not, skip to next loop iteration
            if (!Objects.equals(id, userGameID)) continue;

            // if session is open, broadcast to all but current user
            if (c.session.isOpen()) {
                if (!c.getUsername().equals(excludeUser)) {
                    c.send(notif.toString());
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

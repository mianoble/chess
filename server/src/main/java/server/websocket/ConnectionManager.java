package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

import javax.management.Notification;
import java.io.IOException;
import java.util.ArrayList;
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

    public void broadcast(String excludeUser, Notification notif) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.keySet()) {
            if (c.session.isOpen()) {
                if (!c.getUsername().equals(excludeUser)) {
                    c.send(notif.toString());
                }
            } else {
                removeList.add(c);
            }
        }

        for (var c : removeList) {
            connections.remove(c);
        }
    }
}

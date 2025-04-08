package client;

import model.ResponseException;
import org.eclipse.jetty.io.EndPoint;

import javax.websocket.*;
//import javax.websocket.Endpoint;

public class WebsocketCommunicator extends Endpoint {
    Session session;
    NotificationHandler notificationHandler;

    public WebsocketCommunicator(String url, NotificationHandler notificationHandler) throws ResponseException {

    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }
}

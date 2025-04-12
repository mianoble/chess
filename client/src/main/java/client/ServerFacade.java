package client;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import model.*;
import model.ResponseException;
import websocket.commands.ConnectCommand;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Map;

public class ServerFacade {

    private final String serverURL;
    private static String authToken;
    private static String username;
    WebsocketCommunicator ws;
    HttpCommunicator http;
    NotificationHandler notificationHandler;

    public ServerFacade(String url, NotificationHandler nh) {
        serverURL = url;
        authToken = "";
        username = "";
        this.notificationHandler = nh;
    }

    public String getAuthID() {
        return authToken;
    }

    public String getServerURL() {
        return serverURL;
    }

    public String getUsername() {
        return username;
    }

    public void register(RegisterReq req) throws ResponseException {
        var path = "/user";
        RegisterRes result = this.makeRequest("POST", path, req, RegisterRes.class);
        authToken = result.authToken();
        username = result.username();;
    }

    public void login(LoginReq req) throws ResponseException {
        var path = "/session";
        LoginRes result = this.makeRequest("POST", path, req, LoginRes.class);
        authToken = result.authToken();
        username = result.username();
    }

    public void logout() throws ResponseException {
        var path = "/session";
        this.makeRequest("DELETE", path, authToken, LogoutRes.class);
    }

    public void create(String gameName) throws ResponseException {
        var path = "/game";
        this.makeCreateRequest("POST", path, gameName);
    }

    private void makeCreateRequest(String method, String path, String gameName)
            throws ResponseException {
        try {
            URL url = (new URI(serverURL + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(!method.equals("GET"));

            writeHeader(http);
            if (!method.equals("GET")) { // Only write body for non-GET requests
                writeCreateBody(gameName, http);
            }

            throwIfNotSuccessful(http);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ResponseException(500, e.getMessage());
        }
    }

    private static void writeCreateBody(String obj, HttpURLConnection http) throws IOException {
        if (obj != null) {
            http.setRequestProperty("Content-Type", "application/json");
            String jsonBody = new Gson().toJson(Map.of("gameName", obj));
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(jsonBody.getBytes());
            }
        }
    }

    public void join(JoinReq req) throws ResponseException {
        var path = "/game";
        JoinReq realReq = new JoinReq(authToken, req.playerColor(), req.gameID());
        this.makeRequest("PUT", path, realReq, LogoutRes.class);
    }

    public void update(GameData game) throws ResponseException {
        var path = "/game/update";
        this.makeRequest("PUT", path, game, LoginRes.class);
    }

    public ListRes list() throws ResponseException {
        var path = "/game";
        ListRes res = this.makeRequest("GET", path, authToken, ListRes.class);
        return res;
    }

    public LogoutRes clear() throws ResponseException {
        var path = "/db";
        return this.makeRequest("DELETE", path, null, LogoutRes.class);
    }

    private <T> T makeRequest(String method, String path, Object obj, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverURL + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(!method.equals("GET"));

            writeHeader(http);
            if (!method.equals("GET")) { // Only write body for non-GET requests
                writeBody(obj, http);
            }

            throwIfNotSuccessful(http);

            return readBody(http, responseClass);
        } catch (Exception e) {
             throw new ResponseException(500, e.getMessage());
        }
    }

    private static void writeHeader(HttpURLConnection http) throws IOException {
        http.addRequestProperty("Content-Type", "application/json");
        http.addRequestProperty("authorization", authToken);
    }

    private static void writeBody(Object obj, HttpURLConnection http) throws IOException {
        if (obj != null) {
            http.setRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(obj);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new ResponseException(status, "failure: " + status);
        }
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    public void playerConnect(String authToken, int gameID) throws Exception {
        if (ws == null) {
//            ws = new WebsocketCommunicator(serverURL, notificationHandler);
            throw new IllegalStateException("WebSocket not initialized. You must set it from GameplayClient.");
        }
        ws.userJoinedAGame(authToken, username, gameID, ConnectCommand.Role.PLAYER);
    }

    public void playerLeave(String auth, int gameID) throws IOException {
        //ws = new WebsocketCommunicator(serverURL, notificationHandler);
//        ws.userLeftAGame(auth, gameID, username);
        if (ws != null) {
            ws.userLeftAGame(auth, gameID, username);
        } else {
            throw new IOException("WebSocket not initialized");
        }
    }

    public void makeMove(String auth, int gameID, ChessMove move) throws Exception {
//        ws = new WebsocketCommunicator(serverURL, notificationHandler);
//        ws.playerMadeMove(auth, gameID, move);
        if (ws != null) {
            ws.playerMadeMove(auth, gameID, move);
        } else {
            throw new IllegalStateException("WebSocket not initialized");
        }
    }

    public void setWebSocket(WebsocketCommunicator ws) {
        this.ws = ws;
    }

    public WebsocketCommunicator getWebSocket() {
        return this.ws;
    }
}

package facade;

import com.google.gson.Gson;
import dataaccess.ResponseException;
import model.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.List;

public class ServerFacade {

    private final String serverURL;
    private static String authToken;

    public ServerFacade(String url) {
        serverURL = url;
        authToken = "";
    }

    public RegisterResult register(RegisterRequest req) throws ResponseException {
        var path = "/user";
        RegisterResult result = this.makeRequest("POST", path, req, RegisterResult.class);
        authToken = result.authToken();
        return result;
    }

    public LoginResult login(LoginRequest req) throws ResponseException {
        var path = "/session";
        LoginResult result = this.makeRequest("POST", path, req, LoginResult.class);
        authToken = result.authToken();
        return result;
    }

    public LogoutResult logout() throws ResponseException {
        var path = "/session";
        return this.makeRequest("DELETE", path, authToken, LogoutResult.class);
    }

    public CreateResult create(CreateRequest req) throws ResponseException {
        var path = "/game";
        CreateRequest realReq = new CreateRequest(authToken, req.gameName());
        return this.makeRequest("POST", path, realReq, CreateResult.class);
    }

    public EmptyResult join(JoinRequest req) throws ResponseException {
        var path = "/game";
        JoinRequest realReq = new JoinRequest(authToken, req.playerColor(), req.gameID());
        return this.makeRequest("PUT", path, realReq, EmptyResult.class);
    }

    public ListResult list() throws ResponseException {
        var path = "/game";
        ListRequest realReq = new ListRequest(authToken);
        return this.makeRequest("GET", path, realReq, ListResult.class);
    }

    public EmptyResult clear() throws ResponseException {
        var path = "/db";
        return this.makeRequest("DELETE", path, null, EmptyResult.class);
    }

    private <T> T makeRequest(String method, String path, Object obj, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverURL + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeHeader(obj, http);
            writeBody(obj, http);

            //http.connect();
            throwIfNotSuccessful(http);

            return readBody(http, responseClass);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ResponseException(500, e.getMessage());
        }
    }

    private static void writeHeader(Object obj, HttpURLConnection http) throws IOException {
        if (obj != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(obj);
            http.addRequestProperty("authorization", authToken);
        }
    }

    private static void writeBody(Object obj, HttpURLConnection http) throws IOException {
        if (obj != null) {
//            http.addRequestProperty("Content-Type", "application/json");
            http.setRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(obj);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
//                if (reqData.startsWith("\"") && reqData.endsWith("\"")) {
//                    reqData = reqData.substring(1, reqData.length() - 1);
//                }
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

}

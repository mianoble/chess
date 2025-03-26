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

public class ServerFacade {

    private final String serverURL;

    public ServerFacade(String url) {
        serverURL = url;
    }

    public RegisterResult register(RegisterRequest req) throws ResponseException {
        var path = "/user";
        return this.makeRequest("POST", path, req, RegisterResult.class);
    }

    public LoginResult login(LoginRequest req) throws ResponseException {
        var path = "/session";
        return this.makeRequest("POST", path, req, LoginResult.class);
    }

    public LogoutResult logout(String id) throws ResponseException {
        var path = "/session";
        return this.makeRequest("DELETE", path, id, LogoutResult.class);

    }

    public CreateResult create(CreateRequest req) throws ResponseException {
        var path = "/game";
        return this.makeRequest("POST", path, req, CreateResult.class);
    }

    public EmptyResult join(JoinRequest req) throws ResponseException {
        var path = "/game";
        return this.makeRequest("PUT", path, req, EmptyResult.class);
    }

    public ListResult list(ListRequest req) throws ResponseException {
        var path = "/game";
        return this.makeRequest("GET", path, req, ListResult.class);
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

            writeBody(obj, http);
            http.connect();
            throwIfNotSuccessful(http);

            return readBody(http, responseClass);
        } catch (Exception e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    private static void writeBody(Object obj, HttpURLConnection http) throws IOException {
        if (obj != null) {
            http.addRequestProperty("Content-Type", "application/json");
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

}

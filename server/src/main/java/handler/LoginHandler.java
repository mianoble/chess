package handler;

import com.google.gson.Gson;
import dataaccess.AuthTokenDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.LoginRequest;
import model.LoginResult;
import org.eclipse.jetty.http.HttpParser;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class LoginHandler implements Route {
    private final Gson gson;
    private final UserService userService;

    public LoginHandler(UserDAO userDAO, AuthTokenDAO authTokenDAO) {
        this.gson = new Gson();
        this.userService = new UserService(userDAO, authTokenDAO);
    }


    @Override
    public Object handle(Request req, Response res) throws Exception {
        try {
            LoginRequest loginRequest = gson.fromJson(req.body(), LoginRequest.class);

            LoginResult loginResult = userService.login(loginRequest);

            res.status(200);
            return gson.toJson(loginResult);
        } catch (Exception e) {
            res.status(500);
            return gson.toJson(new DataAccessException("Failed to login // still testing"));
        }
    }
}

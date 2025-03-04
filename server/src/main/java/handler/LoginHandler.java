package handler;

import com.google.gson.Gson;
import dataaccess.AuthTokenDAO;
import dataaccess.DataAccessException;
import dataaccess.ResponseException;
import dataaccess.UserDAO;
import model.LoginRequest;
import model.LoginResult;
import org.eclipse.jetty.http.HttpParser;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;

public class LoginHandler extends MasterHandler {
    private final Gson gson;
    private final UserService userService;

    public LoginHandler(UserDAO userDAO, AuthTokenDAO authTokenDAO) {
        this.gson = new Gson();
        this.userService = new UserService(userDAO, authTokenDAO);
    }


    @Override
    public Object specificHandler(Request req, Response res) throws Exception {
        LoginRequest loginRequest = gson.fromJson(req.body(), LoginRequest.class);
        LoginResult loginResult = userService.login(loginRequest);
        res.status(200);
        return gson.toJson(loginResult);
    }
}


//public class LoginHandler implements Route {
//    private final Gson gson;
//    private final UserService userService;
//
//    public LoginHandler(UserDAO userDAO, AuthTokenDAO authTokenDAO) {
//        this.gson = new Gson();
//        this.userService = new UserService(userDAO, authTokenDAO);
//    }
//
//
//    @Override
//    public Object handle(Request req, Response res) throws Exception {
//        try {
//            LoginRequest loginRequest = gson.fromJson(req.body(), LoginRequest.class);
//            LoginResult loginResult = userService.login(loginRequest);
//            res.status(200);
//            return gson.toJson(loginResult);
//        } catch (ResponseException re) {
//            res.status(re.status());
//            String json = gson.toJson(Map.of("message", re.getMessage()));
//            res.body(json);
//            return json;
//        }
//        catch (Exception e) {
//            res.status(500);
//            String json = gson.toJson(Map.of("message", e.getMessage()));
//            res.body(json);
//            return json;
//        }
//    }
//}

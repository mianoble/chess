package handler;

import com.google.gson.Gson;
import dataaccess.AuthTokenDAO;
import dataaccess.ResponseException;
import dataaccess.UserDAO;
import model.LogoutResult;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;

public class LogoutHandler extends MasterHandler{
    private final Gson gson;
    private final UserService userService;

    public LogoutHandler(UserDAO userDAO, AuthTokenDAO authTokenDAO) {
        gson = new Gson();
        userService = new UserService(userDAO, authTokenDAO);
    }

    @Override
    public Object specificHandler(Request req, Response res) throws Exception {
            String authToken = req.headers("authorization");
            LogoutResult logoutRes = userService.logout(authToken);
            res.status(200);
            return gson.toJson(logoutRes);
    }
}

//public class LogoutHandler implements Route {
//    private final Gson gson;
//    private final UserService userService;
//
//    public LogoutHandler(UserDAO userDAO, AuthTokenDAO authTokenDAO) {
//        gson = new Gson();
//        userService = new UserService(userDAO, authTokenDAO);
//    }
//
//
//    @Override
//    public Object handle(Request req, Response res) throws Exception {
//        try {
//            String authToken = req.headers("authorization");
//            LogoutResult logoutRes = userService.logout(authToken);
//            res.status(200);
//            return gson.toJson(logoutRes);
//        } catch (ResponseException re) {
//            res.status(re.status());
//            String json = gson.toJson(Map.of("message", re.getMessage()));
//            res.body(json);
//            return json;
//        } catch (Exception e) {
//            res.status(500);
//            String json = gson.toJson(Map.of("message", e.getMessage()));
//            res.body(json);
//            return json;
//        }
//    }
//}

package handler;

import com.google.gson.Gson;
import dataaccess.AuthTokenDAO;
import dataaccess.UserDAO;
import model.LogoutResult;
import service.UserService;
import spark.Request;
import spark.Response;

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
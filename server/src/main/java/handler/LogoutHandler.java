package handler;

import com.google.gson.Gson;
import dataaccess.AuthTokenDAO;
import dataaccess.DataAccessException;
import dataaccess.ResponseException;
import dataaccess.UserDAO;
import model.LogoutRequest;
import model.LogoutResult;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class LogoutHandler implements Route {
    private final Gson gson;
    private final UserService userService;

    public LogoutHandler(UserDAO userDAO, AuthTokenDAO authTokenDAO) {
        gson = new Gson();
        userService = new UserService(userDAO, authTokenDAO);
    }


    @Override
    public Object handle(Request req, Response res) throws Exception {
        try {
            LogoutRequest logoutReq = gson.fromJson(req.body(), LogoutRequest.class);

            LogoutResult logoutRes = userService.logout(logoutReq);
            res.status(200);
            return gson.toJson(logoutRes);
        } catch (Exception e) {
            res.status(500);
            return gson.toJson(new DataAccessException("Some error in logoutHandler"));
        }
    }
}

package handler;

import com.google.gson.Gson;
import dataaccess.AuthTokenDAO;
import dataaccess.GameDAO;
import dataaccess.ResponseException;
import dataaccess.UserDAO;
import model.EmptyResult;
import service.ClearService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;

public class ClearHandler implements Route {
    private final Gson gson;
    private final ClearService clearService;

    public ClearHandler(UserDAO userDAO, AuthTokenDAO authTokenDAO, GameDAO gameDAO) {
        gson = new Gson();
        clearService = new ClearService(userDAO, authTokenDAO, gameDAO);
    }

    @Override
    public Object handle(Request req, Response res) throws ResponseException {
        try {
            EmptyResult result = clearService.clear();

            res.status(200);
            return gson.toJson(result);
        } catch (ResponseException re) {
            res.status(401);
            String json = gson.toJson(Map.of("message", re.getMessage()));
            res.body(json);
            return json;
        }
        catch (Exception e) {
            res.status(401);
            String json = gson.toJson(Map.of("message", e.getMessage()));
            res.body(json);
            return json;
        }
    }
}

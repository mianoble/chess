package handler;

import com.google.gson.Gson;
import dataaccess.AuthTokenDAO;
import dataaccess.GameDAO;
import dataaccess.ResponseException;
import model.ListRequest;
import model.ListResult;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;

public class ListHandler implements Route {
    private final Gson gson;
    private final GameService gameService;

    public ListHandler(GameDAO gameDAO, AuthTokenDAO authTokenDAO) {
        this.gson = new Gson();
        this.gameService = new GameService(gameDAO, authTokenDAO);
    }


    @Override
    public Object handle(Request req, Response res) throws Exception {
        try {
            String authToken = req.headers("authorization");

            //idk if i need this
            if (authToken == null || authToken.isEmpty()) { //make sure authToken is not null
                res.status(401);
                String json = gson.toJson(Map.of("message", "Error: authToken cannot be null"));
                res.body(json);
                return json;
            }

            ListRequest listRequest = new ListRequest(authToken);

            ListResult listResult = gameService.list(listRequest);

            res.status(200);
            return gson.toJson(listResult);
        } catch (ResponseException re) {
            res.status(re.status());
            String json = gson.toJson(Map.of("message", re.getMessage()));
            res.body(json);
            return json;
        } catch (Exception e) {
            res.status(500);
            String json = gson.toJson(Map.of("message", e.getMessage()));
            res.body(json);
            return json;
        }

    }
}

package handler;

import com.google.gson.Gson;
import dataaccess.AuthTokenDAO;
import dataaccess.GameDAO;
import dataaccess.ResponseException;
import model.CreateRequest;
import model.JoinRequest;
import model.JoinResult;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;

public class JoinHandler implements Route {
    private final Gson gson;
    private final GameService gameService;

    public JoinHandler (GameDAO gameDAO, AuthTokenDAO authTokenDAO) {
        this.gson = new Gson();
        this.gameService = new GameService(gameDAO, authTokenDAO);
    }

    @Override
    public Object handle(Request req, Response res) throws Exception {
        try {
            String authToken = req.headers("authorization");
            JoinRequest tempReq = gson.fromJson(req.body(), JoinRequest.class);

            JoinRequest joinRequest = new JoinRequest(authToken, tempReq.playerColor(), tempReq.gameID());

            JoinResult joinResult = gameService.join(joinRequest);

            res.status(200);
            return gson.toJson(joinResult);
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

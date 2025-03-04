package handler;

import com.google.gson.Gson;
import dataaccess.AuthTokenDAO;
import dataaccess.GameDAO;
import dataaccess.ResponseException;
import model.EmptyResult;
import model.JoinRequest;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;

public class JoinHandler extends MasterHandler {
    private final Gson gson;
    private final GameService gameService;

    public JoinHandler (GameDAO gameDAO, AuthTokenDAO authTokenDAO) {
        this.gson = new Gson();
        this.gameService = new GameService(gameDAO, authTokenDAO);
    }


    @Override
    public Object specificHandler(Request req, Response res) throws Exception {
        String authToken = req.headers("authorization");
        JoinRequest tempReq = gson.fromJson(req.body(), JoinRequest.class);
        JoinRequest joinRequest = new JoinRequest(authToken, tempReq.playerColor(), tempReq.gameID());
        EmptyResult joinResult = gameService.join(joinRequest);
        res.status(200);
        return gson.toJson(joinResult);
    }
}
package handler;

import com.google.gson.Gson;
import dataaccess.AuthTokenDAO;
import dataaccess.GameDAO;
import model.EmptyResult;
import model.UpdateRequest;
import service.GameService;
import spark.Request;
import spark.Response;

public class UpdateHandler extends MasterHandler{
    private final Gson gson;
    private final GameService gameService;

    public UpdateHandler(GameDAO gameDAO, AuthTokenDAO authTokenDAO) {
        this.gson = new Gson();
        this.gameService = new GameService(gameDAO, authTokenDAO);
    }

    @Override
    public Object specificHandler(Request req, Response res) throws Exception {
        String authToken = req.headers("authorization");

        UpdateRequest updateReq = gson.fromJson(req.body(), UpdateRequest.class);
        updateReq = new UpdateRequest(authToken, updateReq.gameData());

        EmptyResult result = gameService.update(updateReq);
        res.status(200);
        return gson.toJson(result);
    }

}

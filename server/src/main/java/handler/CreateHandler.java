package handler;

import com.google.gson.Gson;
import dataaccess.AuthTokenDAO;
import dataaccess.GameDAO;
import model.CreateRequest;
import model.CreateResult;
import service.GameService;
import spark.Request;
import spark.Response;

public class CreateHandler extends MasterHandler {
    private final Gson gson;
    private final GameService gameService;

    public CreateHandler(GameDAO gameDAO, AuthTokenDAO authTokenDAO) {
        this.gson = new Gson();
        this.gameService = new GameService(gameDAO, authTokenDAO);
    }

    @Override
    public Object specificHandler(Request req, Response res) throws Exception {
        String authToken = req.headers("authorization");
        CreateRequest tempReq = gson.fromJson(req.body(), CreateRequest.class);
        CreateRequest createRequest = new CreateRequest(authToken, tempReq.gameName());
        CreateResult createResult = gameService.create(createRequest);
        res.status(200);
        return gson.toJson(createResult);
    }
}
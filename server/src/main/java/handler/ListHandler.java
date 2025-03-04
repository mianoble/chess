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


public class ListHandler extends MasterHandler {
    private final Gson gson;
    private final GameService gameService;

    public ListHandler(GameDAO gameDAO, AuthTokenDAO authTokenDAO) {
        this.gson = new Gson();
        this.gameService = new GameService(gameDAO, authTokenDAO);
    }

    @Override
    public Object specificHandler(Request req, Response res) throws Exception {
        String authToken = req.headers("authorization");
        ListRequest listRequest = new ListRequest(authToken);
        ListResult listResult = gameService.list(listRequest);
        res.status(200);
        return gson.toJson(listResult);
    }
}
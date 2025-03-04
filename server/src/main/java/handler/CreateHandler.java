package handler;

import com.google.gson.Gson;
import dataaccess.AuthTokenDAO;
import dataaccess.GameDAO;
import dataaccess.ResponseException;
import dataaccess.UserDAO;
import model.CreateRequest;
import model.CreateResult;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;

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


//public class CreateHandler implements Route {
//    private final Gson gson;
//    private final GameService gameService;
//
//    public CreateHandler(GameDAO gameDAO, AuthTokenDAO authTokenDAO) {
//        this.gson = new Gson();
//        this.gameService = new GameService(gameDAO, authTokenDAO);
//    }
//
//    @Override
//    public Object handle(Request req, Response res) throws Exception {
//        try {
//            String authToken = req.headers("authorization");
//            CreateRequest tempReq = gson.fromJson(req.body(), CreateRequest.class);
//            CreateRequest createRequest = new CreateRequest(authToken, tempReq.gameName());
//            CreateResult createResult = gameService.create(createRequest);
//            res.status(200);
//            return gson.toJson(createResult);
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

package handler;

import com.google.gson.Gson;
import dataaccess.ResponseException;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;

public abstract class MasterHandler implements Route {
    protected Gson gson = new Gson();

    @Override
    public Object handle(Request req, Response res) throws Exception {
        try {
            return specificHandler(req, res);
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

    public abstract Object specificHandler(Request req, Response res) throws Exception;
}
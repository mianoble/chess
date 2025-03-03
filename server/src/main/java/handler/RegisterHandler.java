package handler;

import dataaccess.AuthTokenDAO;
import dataaccess.DataAccessException;
import dataaccess.ResponseException;
import dataaccess.UserDAO;
import model.RegisterRequest;
import model.RegisterResult;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;
import com.google.gson.Gson;

import java.util.Map;

public class RegisterHandler implements Route {
    private final Gson gson;
    private final UserService userService;

    public RegisterHandler(UserDAO userDAO, AuthTokenDAO authTokenDAO) {
        gson = new Gson();
        userService = new UserService(userDAO, authTokenDAO);
    }

    /*
    The server handler classes serve as a translator between HTTP and Java.
    Your handlers will convert an HTTP request into Java usable objects & data.
    The handler then calls the appropriate service.
    When the service responds, the handler converts the response object back to JSON and sends the HTTP response.
    This could include converting thrown exception types into the appropriate HTTP status codes if necessary.
     */

    @Override
    public Object handle(Request req, Response res) throws Exception {
        try {
            RegisterRequest requestToRegister = gson.fromJson(req.body(), RegisterRequest.class);

            RegisterResult result = userService.register(requestToRegister);

            res.status(200);
            return gson.toJson(result);

        } catch (ResponseException re) {
            res.status(re.status());
            String json = gson.toJson(Map.of("message", re.getMessage()));
            res.body(json);
            return json;
        }
        catch (Exception e) {
            res.status(500);
            String json = gson.toJson(Map.of("message", e.getMessage()));
            res.body(json);
            return json;
        }

    }
}

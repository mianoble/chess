package server;

import dataaccess.*;
import handler.*;
import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        localUserDAO userDAO = new localUserDAO();
        localAuthDAO authDAO = new localAuthDAO();
        localGameDAO gameDAO = new localGameDAO();

        // UserService
        // register
        Spark.post("/user", new RegisterHandler(userDAO, authDAO)); //input userDAO and authTokenDAO somehow
        // log in
        Spark.post("/session", new LoginHandler(userDAO, authDAO));
        // log out
        Spark.delete("/session", new LogoutHandler(userDAO, authDAO));

        // GameService
        // create game
        Spark.post("/game", new CreateHandler(gameDAO, authDAO));
        // join game
//        Spark.put("/game", new JoinHandler(...,...));
        // list games
//        Spark.get("/game", new ListHandler(...,...));

        // ClearService
        Spark.delete("/db", new ClearHandler(userDAO, authDAO, gameDAO));

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}

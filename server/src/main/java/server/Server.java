package server;

import dataaccess.*;
import handler.ClearHandler;
import handler.LoginHandler;
import spark.*;
import handler.RegisterHandler;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        localUserDAO userDAO = new localUserDAO();
        localAuthDAO authDAO = new localAuthDAO();
        localGameDAO gameDAO = new localGameDAO();
        // register
        Spark.post("/user", new RegisterHandler(userDAO, authDAO)); //input userDAO and authTokenDAO somehow
        // log in
        Spark.get("/session", new LoginHandler(userDAO, authDAO));

        // clear
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

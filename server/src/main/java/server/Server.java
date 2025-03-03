package server;

import dataaccess.AuthTokenDAO;
import dataaccess.UserDAO;
import dataaccess.localAuthDAO;
import dataaccess.localUserDAO;
import spark.*;
import handler.RegisterHandler;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        localUserDAO userDAO = new localUserDAO();
        localAuthDAO authDAO = new localAuthDAO();
        Spark.post("/user", new RegisterHandler(userDAO, authDAO)); //input userDAO and authTokenDAO somehow

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

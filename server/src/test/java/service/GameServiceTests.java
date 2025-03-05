package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Response;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTests {
    static GameService gameService = null;
    static UserService userService = null;

    private static UserDAO userDAO;
    private static AuthTokenDAO authTokenDAO;
    private static GameDAO gameDAO;

    @BeforeAll
    static void init() throws ResponseException {
        userDAO = new LocalUserDao();
        authTokenDAO = new LocalAuthDao();
        gameDAO = new LocalGameDao();

        gameService = new GameService(gameDAO, authTokenDAO);
        userService = new UserService(userDAO, authTokenDAO);
    }

    @BeforeEach
    void clear() throws ResponseException {
        userDAO.clear();
        authTokenDAO.clear();
        gameDAO.clear();
    }

    @Test
    void createPass() throws ResponseException {
        String user = "frogluvr20";
        String password = "secretPassword";
        String email = "myemail@gmail.com";

        RegisterRequest req = new RegisterRequest(user, password, email);
        RegisterResult res = userService.register(req);

        String gameName = "Fun Game!";

        CreateRequest createReq = new CreateRequest(res.authToken(), gameName);
        CreateResult createRes = gameService.create(createReq);

        assertNotNull(createRes.gameID());
    }

    @Test
    void createFail() throws ResponseException {
        String user = "frogluvr20";
        String password = "secretPassword";
        String email = "myemail@gmail.com";

        RegisterRequest req = new RegisterRequest(user, password, email);
        RegisterResult res = userService.register(req);

        String gameName = "Fun Game!";

        CreateRequest createReq = new CreateRequest(res.authToken(), gameName);
        CreateRequest createReq2 = new CreateRequest(res.authToken(), gameName);
        gameService.create(createReq);

        assertThrows(ResponseException.class, () -> gameService.create(createReq2));
    }

//    @Test
//    void joinPass() throws ResponseException {
//        String user = "frogluvr20";
//        String password = "secretPassword";
//        String email = "myemail@gmail.com";
//
//        RegisterRequest req = new RegisterRequest(user, password, email);
//        RegisterResult res = userService.register(req);
//
//        String gameName = "Fun Game!";
//        CreateRequest createReq = new CreateRequest(res.authToken(), gameName);
//        CreateResult createResult = gameService.create(createReq);
//
//        JoinRequest joinReq = new JoinRequest(res.authToken(), "WHITE", createResult.gameID());
//
//    }
}

package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ClearServiceTests {

    static ClearService clearService = null;
    static UserService userService = null;
    static GameService gameService = null;

    private static UserDAO userDAO;
    private static AuthTokenDAO authTokenDAO;
    private static GameDAO gameDAO;

    @BeforeAll
    static void init() throws ResponseException {
        userDAO = new LocalUserDao();
        authTokenDAO = new LocalAuthDao();
        gameDAO = new LocalGameDao();

        clearService = new ClearService(userDAO, authTokenDAO, gameDAO);
        userService = new UserService(userDAO, authTokenDAO);
        gameService = new GameService(gameDAO, authTokenDAO);
    }

    @Test
    void clear() throws ResponseException {
        String user = "frogluvr20";
        String password = "secretPassword";
        String email = "myemail@gmail.com";
        RegisterRequest req = new RegisterRequest(user, password, email);
        RegisterResult res = userService.register(req);
        assertEquals(user, res.username());

        String gameName = "Fun Game!";
        CreateRequest createReq = new CreateRequest(res.authToken(), gameName);
        gameService.create(createReq);

        gameName = "Cool kids server B)";
        createReq = new CreateRequest(res.authToken(), gameName);
        gameService.create(createReq);

        gameName = "Pink pony club";
        createReq = new CreateRequest(res.authToken(), gameName);
        gameService.create(createReq);

        userDAO.clear();
        gameDAO.clear();
        authTokenDAO.clear();
        ListRequest listRequest = new ListRequest(res.authToken());
        assertThrows(ResponseException.class, () -> gameService.list(listRequest));
    }

}

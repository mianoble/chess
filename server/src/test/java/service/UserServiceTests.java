package service;


import dataaccess.*;
import model.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserServiceTests {

    static UserService userService = null;

    private static UserDAO userDAO;
    private static AuthTokenDAO authTokenDAO;
    private static GameDAO gameDAO;

    @BeforeAll
    static void init() throws ResponseException {
        userDAO = new LocalUserDao();
        authTokenDAO = new LocalAuthDao();
        gameDAO = new LocalGameDao();

        userService = new UserService(userDAO, authTokenDAO);
    }

    @BeforeEach
    void clear() throws ResponseException {
        userDAO.clear();
        authTokenDAO.clear();
        gameDAO.clear();
    }

    @Test
    void registerPass() throws ResponseException {
        String user = "frogluvr20";
        String password = "secretPassword";
        String email = "myemail@gmail.com";

        RegisterRequest req = new RegisterRequest(user, password, email);
        RegisterResult res = userService.register(req);
        assertEquals(user, res.username());
    }

    @Test
    void registerFail() throws ResponseException {
        String user1 = "frogluvr20";
        String password1 = "secretPassword";
        String email1 = "myemail@gmail.com";

        String user2 = "frogluvr20";
        String password2 = "secretPassword";
        String email2 = "myemail@gmail.com";

        RegisterRequest req1 = new RegisterRequest(user1, password1, email1);
        RegisterRequest req2 = new RegisterRequest(user2, password2, email2);

        RegisterResult res1 = userService.register(req1);

        assertEquals(user1, res1.username());
        assertThrows(ResponseException.class, () -> userService.register(req2));
    }

    @Test
    void loginPass() throws ResponseException {
        String user = "frogluvr20";
        String password = "secretPassword";
        String email = "myemail@gmail.com";

        RegisterRequest req = new RegisterRequest(user, password, email);
        userService.register(req);

        LoginRequest loginReq = new LoginRequest(user, password);
        LoginResult loginRes = userService.login(loginReq);

        assertEquals(user, loginRes.username());
    }

    @Test
    void loginFail() throws ResponseException {
        String user = "frogluvr20";
        String password = "secretPassword";
        String email = "myemail@gmail.com";

        RegisterRequest req = new RegisterRequest(user, password, email);
        userService.register(req);

        String wrongPassword = "notMyPassword";

        LoginRequest loginReq = new LoginRequest(user, wrongPassword);
        assertThrows(ResponseException.class, () -> userService.login(loginReq));
    }

    @Test
    void logoutPass() throws ResponseException {
        String user = "frogluvr20";
        String password = "secretPassword";
        String email = "myemail@gmail.com";

        RegisterRequest req = new RegisterRequest(user, password, email);
        RegisterResult res = userService.register(req);

        String authID = res.authToken();

        userService.logout(authID);
        assertThrows(ResponseException.class, () -> authTokenDAO.getAuth(authID));
    }

    @Test
    void logoutFail() throws ResponseException {
        String authID = "random auth1234";
        assertThrows(ResponseException.class, () -> userService.logout(authID));
    }
}

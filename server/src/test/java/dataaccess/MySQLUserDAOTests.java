package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MySQLUserDAOTests {
    static MySQLUserDAO mySQLUserDAO = null;

    /*
    private static UserDAO userDAO;
    private static AuthTokenDAO authTokenDAO;
    private static GameDAO gameDAO;
     */

    @BeforeAll
    static void init() throws ResponseException {
        mySQLUserDAO = new MySQLUserDAO();
    }

    @BeforeEach
    void clear() throws ResponseException {
        mySQLUserDAO.clear();
    }

    @Test
    void createUserPass() throws ResponseException {
        UserData myUser = new UserData("mia", "secret", "mia@email.com");
        mySQLUserDAO.createUser(myUser);

        UserData res = mySQLUserDAO.getUser(myUser.username());
        assertEquals(res.username(), myUser.username());
        assertEquals(res.email(), myUser.email());
        assertTrue(mySQLUserDAO.verifyUser(myUser.username(), myUser.password()));
    }

    @Test
    void createUserFail() throws ResponseException {
        UserData myUser = new UserData("mia", "secret", "mia@email.com");
        mySQLUserDAO.createUser(myUser);

        UserData myUser2 = new UserData("mia", "second password", "mia@email.com");
        assertThrows(ResponseException.class, () -> mySQLUserDAO.createUser(myUser2));
    }

    @Test
    void getUserPass() throws ResponseException {
        UserData myUser = new UserData("mia", "secret", "mia@email.com");
        mySQLUserDAO.createUser(myUser);

        UserData res = mySQLUserDAO.getUser(myUser.username());
        assertEquals(res.username(), myUser.username());
        assertEquals(res.email(), myUser.email());
        assertTrue(mySQLUserDAO.verifyUser(myUser.username(), myUser.password()));
    }

    @Test
    void getUserFail() throws ResponseException {
        assertNull(mySQLUserDAO.getUser("thisUserDoesNotExist"));
    }

}

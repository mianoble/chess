package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MySQLUserDAOTests {
    static MySQLUserDAO mySQLUserDAO = null;

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
        UserData myUser = new UserData("me", "secret", "my@email.com");
        mySQLUserDAO.createUser(myUser);

        UserData res = mySQLUserDAO.getUser(myUser.username());
        assertTrue(mySQLUserDAO.verifyUser(myUser.username(), myUser.password()));
        assertEquals(res.email(), myUser.email());
        assertEquals(res.username(), myUser.username());
    }

    @Test
    void getUserFail() throws ResponseException {
        assertNull(mySQLUserDAO.getUser("thisUserDoesNotExist"));
    }

    @Test
    void verifyUserPass() throws ResponseException {
        UserData myUser = new UserData("mia", "secret", "mia@email.com");
        mySQLUserDAO.createUser(myUser);
        assertTrue(mySQLUserDAO.verifyUser(myUser.username(), myUser.password()));
    }

    @Test
    void verifyUserFail() throws ResponseException {
        UserData myUser = new UserData("mia", "secret", "mia@email.com");
        mySQLUserDAO.createUser(myUser);
        assertFalse(mySQLUserDAO.verifyUser(myUser.username(), "wrongPassword"));
    }

    @Test
    void userExistsPass() throws ResponseException {
        UserData myUser = new UserData("mia", "secret", "mia@email.com");
        mySQLUserDAO.createUser(myUser);

        assertTrue(mySQLUserDAO.userExists(myUser.username()));
    }

    @Test
    void userExistsFail() throws ResponseException {
        assertFalse(mySQLUserDAO.userExists("randomUser"));
    }
}

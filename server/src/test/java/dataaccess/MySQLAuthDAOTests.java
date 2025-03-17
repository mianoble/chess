package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MySQLAuthDAOTests {
    static MySQLAuthDAO mySQLAuthDAO = null;

    @BeforeAll
    static void init() throws ResponseException {
        mySQLAuthDAO = new MySQLAuthDAO();
    }

    @BeforeEach
    void clear() throws ResponseException {
        mySQLAuthDAO.clear();
    }

    @Test
    void createAuthPass() throws ResponseException {
        AuthData authData = new AuthData("123", "user");
        mySQLAuthDAO.createAuth(authData);
        AuthData res = mySQLAuthDAO.getAuth("123");
        assertEquals("user", res.username());
    }

    @Test
    void createAuthFail() throws ResponseException {
        AuthData authData = new AuthData("123", "user");
        mySQLAuthDAO.createAuth(authData);
        AuthData authData2 = new AuthData("123", "user");
        assertThrows(ResponseException.class, () -> mySQLAuthDAO.createAuth(authData2));
    }

    @Test
    void getAuthPass() throws ResponseException {
        createAuthPass();
    }

    @Test
    void getAuthFail() throws ResponseException {
        assertNull(mySQLAuthDAO.getAuth("0"));
    }


}

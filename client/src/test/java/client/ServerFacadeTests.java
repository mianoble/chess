package client;

import facade.ServerFacade;
import model.*;
import org.junit.jupiter.api.*;
import server.Server;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    private String authID;


    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        var url = "http://localhost:8080";
        facade = new ServerFacade(url);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    /*
    - logout
    - join
    - list
    - clear
     */

    @Test
    public void registerPass() throws ResponseException {
        String user = "me123";
        String pass = "me123";
        String email = "me123";
        RegisterReq req = new RegisterReq(user,pass,email);
        Assertions.assertDoesNotThrow(() -> facade.register(req));
        authID = facade.getAuthID();
    }

    @Test
    public void registerFail() {
        String user = "";
        String pass = "";
        String email = "me123";
        RegisterReq req = new RegisterReq(user,pass,email);
        Assertions.assertThrows(ResponseException.class, () -> facade.register(req));
    }

    @Test
    public void LoginPass() throws ResponseException {
        String user = "me123";
        String pass = "me123";
        LoginReq req = new LoginReq(user,pass);
        Assertions.assertDoesNotThrow(() -> facade.login(req));
        authID = facade.getAuthID();
    }

    @Test
    public void loginFail() throws ResponseException {
        String user = "me123";
        String pass = "me1234";
        LoginReq req = new LoginReq(user,pass);
        Assertions.assertThrows(ResponseException.class, () -> facade.login(req));
    }

    @Test
    public void logoutPass() throws ResponseException {
        String user = "me123";
        String pass = "me123";
        LoginReq req = new LoginReq(user,pass);
        facade.login(req);
        Assertions.assertDoesNotThrow(() -> facade.logout());
    }

    @Test
    public void logoutFail() throws ResponseException {
        Assertions.assertThrows(ResponseException.class, () -> facade.logout());
    }

    @Test
    public void createPass() throws ResponseException {
        String user = "me123";
        String pass = "me123";
        LoginReq req = new LoginReq(user,pass);
        facade.login(req);
        String gameName = "game123";
        Assertions.assertDoesNotThrow(() -> facade.create(gameName));
    }

    @Test
    public void createFail() throws ResponseException {
        String gameName = "game123";
        Assertions.assertThrows(ResponseException.class, () -> facade.create(gameName));
    }

    @Test
    public void joinPass() throws ResponseException {
        
    }
}

package client;

import model.*;
import org.junit.jupiter.api.*;
import server.Server;
import ui.Repl;

import java.util.UUID;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    private String authID;
    private static NotificationHandler nh;


    @BeforeAll
    public static void init() throws ResponseException {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        var url = "http://localhost:" + port;
        nh = new Repl(url);
        facade = new ServerFacade(url, nh);
        facade.clear();
    }

    @BeforeEach
    void clearStuff() throws ResponseException {
        facade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test
    public void registerPass() throws ResponseException {
        String user = UUID.randomUUID().toString();
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

    private void registerSame() throws ResponseException {
        String user = "me123";
        String pass = "me123";
        String email = "me123";
        RegisterReq req = new RegisterReq(user,pass,email);
        facade.register(req);
    }

    private void loginSame() throws ResponseException {
        String user = "me123";
        String pass = "me123";
        LoginReq req = new LoginReq(user,pass);
        facade.login(req);
    }

    @Test
    public void loginPass() throws ResponseException {
        registerSame();
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
        registerSame();
        loginSame();
        Assertions.assertDoesNotThrow(() -> facade.logout());
    }

    @Test
    public void logoutFail() throws ResponseException {
        registerSame();
        loginSame();
        facade.logout();
        Assertions.assertThrows(ResponseException.class, () -> facade.logout());
    }

    @Test
    public void createPass() throws ResponseException {
        registerSame();
        loginSame();
        String gameName = UUID.randomUUID().toString();
        Assertions.assertDoesNotThrow(() -> facade.create(gameName));
    }

    @Test
    public void createFail() throws ResponseException {
        String gameName = "game123";
        Assertions.assertThrows(ResponseException.class, () -> facade.create(gameName));
    }

    @Test
    public void joinPass() throws ResponseException {
        String gameName = "game123";
        Assertions.assertThrows(ResponseException.class, () -> facade.create(gameName));
    }

    @Test
    public void joinFail() throws ResponseException {
        registerSame();
        loginSame();

        String gameName = UUID.randomUUID().toString();
        facade.create(gameName);
        JoinReq req1 = new JoinReq(authID, "BLACK", 1);
        Assertions.assertThrows(ResponseException.class, () -> facade.join(req1));
    }

    @Test
    public void listPass() throws ResponseException {
        registerSame();
        loginSame();
        Assertions.assertDoesNotThrow(() -> facade.list());
    }

    @Test
    public void listFail() throws ResponseException {
        Assertions.assertThrows(ResponseException.class, () -> facade.list());
    }

    @Test
    public void clearPass() throws ResponseException {
        Assertions.assertDoesNotThrow(() -> facade.clear());
    }

}

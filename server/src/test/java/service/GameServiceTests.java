package service;

import chess.ChessGame;
import dataaccess.*;
import model.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Response;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    @Test
    void joinPass() throws ResponseException {
        String user = "frogluvr20";
        String password = "secretPassword";
        String email = "myemail@gmail.com";
        RegisterRequest req = new RegisterRequest(user, password, email);
        RegisterResult res = userService.register(req);

        String gameName = "Fun Game!";
        CreateRequest createReq = new CreateRequest(res.authToken(), gameName);
        CreateResult createResult = gameService.create(createReq);

        JoinRequest joinReq = new JoinRequest(res.authToken(), "WHITE", createResult.gameID());

        gameService.join(joinReq);
        assertNotNull(gameDAO.getGame(createResult.gameID()));
    }

    @Test
    void joinFail() throws ResponseException {
        String user = "frogluvr20";
        String password = "secretPassword";
        String email = "myemail@gmail.com";
        RegisterRequest req = new RegisterRequest(user, password, email);
        RegisterResult res = userService.register(req);

        int randID = 12345;

        JoinRequest joinReq = new JoinRequest(res.authToken(), "WHITE", 12345);
        assertThrows(ResponseException.class, () -> gameService.join(joinReq));
    }

    @Test
    void listPass() throws ResponseException {
        String user = "frogluvr20";
        String password = "secretPassword";
        String email = "myemail@gmail.com";
        RegisterRequest req = new RegisterRequest(user, password, email);
        RegisterResult res = userService.register(req);

        String gameName = "Fun Game!";
        CreateRequest createReq = new CreateRequest(res.authToken(), gameName);
        CreateResult createRes = gameService.create(createReq);
        GameData game1 = new GameData(createRes.gameID(), null, null, gameName, null);

        gameName = "Cool kids server B)";
        createReq = new CreateRequest(res.authToken(), gameName);
        createRes = gameService.create(createReq);
        GameData game2 = new GameData(createRes.gameID(), null, null, gameName, null);

        gameName = "Pink pony club";
        createReq = new CreateRequest(res.authToken(), gameName);
        createRes = gameService.create(createReq);
        GameData game3 = new GameData(createRes.gameID(), null, null, gameName, null);

        Collection<GameData> expected = new ArrayList<>();
        expected.add(game1);
        expected.add(game2);
        expected.add(game3);
        List<GameData> expectedList = new ArrayList<>(expected);

        ListRequest listRequest = new ListRequest(res.authToken());
        ListResult listResult = gameService.list(listRequest);
        Collection<GameData> actual = listResult.games();
        List<GameData> actualList = new ArrayList<>(actual);

        boolean same = false;
        if (sameLists(expectedList, actualList)) {
            same = true;
        }
        assertTrue(same);
    }

    @Test
    void listFail() throws ResponseException {
        String user = "frogluvr20";
        String password = "secretPassword";
        String email = "myemail@gmail.com";
        RegisterRequest req = new RegisterRequest(user, password, email);
        RegisterResult res = userService.register(req);

        String gameName = "Fun Game!";
        CreateRequest createReq = new CreateRequest(res.authToken(), gameName);
        CreateResult createRes = gameService.create(createReq);
        GameData game1 = new GameData(createRes.gameID(), null, null, "wrongName", null);

        gameName = "Cool kids server B)";
        createReq = new CreateRequest(res.authToken(), gameName);
        createRes = gameService.create(createReq);
        GameData game2 = new GameData(5432, null, null, gameName, null);

        gameName = "Pink pony club";
        createReq = new CreateRequest(res.authToken(), gameName);
        createRes = gameService.create(createReq);
        GameData game3 = new GameData(createRes.gameID(), "frogluvr20", null, gameName, null);

        Collection<GameData> expected = new ArrayList<>();
        expected.add(game1);
        expected.add(game2);
        expected.add(game3);
        List<GameData> expectedList = new ArrayList<>(expected);

        ListRequest listRequest = new ListRequest(res.authToken());
        ListResult listResult = gameService.list(listRequest);
        Collection<GameData> actual = listResult.games();
        List<GameData> actualList = new ArrayList<>(actual);

        boolean same = false;
        if (sameLists(expectedList, actualList)) {
            same = true;
        }
        assertFalse(same);
    }

    boolean sameLists(List<GameData> expect, List<GameData> actual) {
        for (int i = 0; i < expect.size(); i++) {
            if (expect.get(i).gameID() != actual.get(i).gameID()) {
                return false;
            }
            if (expect.get(i).whiteUsername() == null && actual.get(i).whiteUsername() == null ) {
                continue;
            } else if (!expect.get(i).whiteUsername().equals(actual.get(i).whiteUsername())) {
                return false;
            }
            if (expect.get(i).blackUsername() == null && actual.get(i).blackUsername() == null ) {
                continue;
            } else if(!expect.get(i).blackUsername().equals(actual.get(i).blackUsername())) {
                return false;
            }
            if (!expect.get(i).gameName().equals(actual.get(i).gameName())) {
                return false;
            }
            if (!expect.get(i).game().equals(actual.get(i).game())) {
                return false;
            }
        }
        return true;
    }
}

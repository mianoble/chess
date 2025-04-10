package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class MySQLGameDAOTests {
    static MySQLGameDAO mySQLGameDAO = null;

    @BeforeAll
    static void init() throws ResponseException {
        mySQLGameDAO = new MySQLGameDAO();
    }

    @BeforeEach
    void clear() throws ResponseException {
        mySQLGameDAO.clear();
    }

    @Test
    void addGamePass() throws ResponseException {
        ChessGame game = new ChessGame();
        GameData newGame = new GameData(123, "player1", "player2",
                "fun game", game);
        mySQLGameDAO.addGame(newGame);

        GameData res = mySQLGameDAO.getGame(123);
        assertEquals(res.whiteUsername(), newGame.whiteUsername());
        assertEquals(res.blackUsername(), newGame.blackUsername());
        assertEquals(res.gameName(), newGame.gameName());
    }

    @Test
    void addGameFail() throws ResponseException {
        ChessGame game = new ChessGame();
        GameData newGame = new GameData(123, "player1", "player2",
                "fun game", game);
        mySQLGameDAO.addGame(newGame);

        GameData anotherGame = new GameData(123, "player1", "player2",
                "fun game", game);
        assertThrows(ResponseException.class, () -> mySQLGameDAO.addGame(anotherGame));
    }


    @Test
    void getGamePass() throws ResponseException {
        ChessGame game = new ChessGame();
        GameData newGame = new GameData(321, "player a", "player b",
                "fun game", game);
        mySQLGameDAO.addGame(newGame);

        GameData res = mySQLGameDAO.getGame(321);
        assertEquals(res.gameName(), newGame.gameName());
        assertEquals(res.whiteUsername(), newGame.whiteUsername());
        assertEquals(res.blackUsername(), newGame.blackUsername());
    }

    @Test
    void getGameFail() throws ResponseException {
        assertNull(mySQLGameDAO.getGame(345));
    }

    @Test
    void getAllGamesPass() throws ResponseException {
        ChessGame game = new ChessGame();
        GameData newGame = new GameData(123, "player1", "player2",
                "fun game", game);
        mySQLGameDAO.addGame(newGame);

        ChessGame game2 = new ChessGame();
        GameData newGame2 = new GameData(456, "player1", "player2",
                "not fun game", game2);
        mySQLGameDAO.addGame(newGame2);

        assertEquals(2, mySQLGameDAO.getAllGames().size());
    }

    @Test
    void getAllGamesFail() throws ResponseException {
        assertEquals(0, mySQLGameDAO.getAllGames().size());
    }

    @Test
    void getGameIDsPass() throws ResponseException {
        ChessGame game = new ChessGame();
        GameData newGame = new GameData(1, "player1", "player2",
                "fun game", game);
        mySQLGameDAO.addGame(newGame);

        ChessGame game2 = new ChessGame();
        GameData newGame2 = new GameData(2, "player1", "player2",
                "not fun game", game2);
        mySQLGameDAO.addGame(newGame2);

        Set<Integer> gameIDs = mySQLGameDAO.getGameIDs();

        assertEquals(2, gameIDs.size());
    }

    @Test
    void getGameIDsFail() throws ResponseException {
        assertEquals(0, mySQLGameDAO.getGameIDs().size());
    }

    @Test
    void gameExistsPass() throws ResponseException {
        ChessGame game = new ChessGame();
        GameData newGame = new GameData(1, "player1", "player2",
                "fun game", game);
        mySQLGameDAO.addGame(newGame);

        assertTrue(mySQLGameDAO.gameExists("fun game"));
    }

    @Test
    void gameExistsFail() throws ResponseException {
        assertFalse(mySQLGameDAO.gameExists("unknown game"));
    }

    @Test
    void deleteGamePass() throws ResponseException {
        ChessGame game = new ChessGame();
        GameData newGame = new GameData(1, "player1", "player2",
                "fun game", game);
        mySQLGameDAO.addGame(newGame);
        mySQLGameDAO.deleteGame(newGame);
        assertNull(mySQLGameDAO.getGame(1));
    }

    @Test
    void deleteGameFail() throws ResponseException {
        ChessGame game = new ChessGame();
        GameData newGame = new GameData(1, "player1", "player2",
                "fun game", game);
        assertThrows(ResponseException.class, () -> mySQLGameDAO.deleteGame(newGame));

    }
}

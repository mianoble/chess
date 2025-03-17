package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        GameData newGame = new GameData(123, "player1", "player2",
                "fun game", game);
        mySQLGameDAO.addGame(newGame);

        GameData res = mySQLGameDAO.getGame(123);
        assertEquals(res.whiteUsername(), newGame.whiteUsername());
        assertEquals(res.blackUsername(), newGame.blackUsername());
        assertEquals(res.gameName(), newGame.gameName());
    }

    @Test
    void getGameFail() throws ResponseException {
        assertNull(mySQLGameDAO.getGame(345));
    }



}

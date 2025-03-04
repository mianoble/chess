package dataaccess;
import model.GameData;

import java.util.Collection;
import java.util.Set;

public interface GameDAO {

    void addGame(GameData game) throws ResponseException;

    GameData getGame(int id) throws ResponseException;

    Collection<GameData> getAllGames() throws ResponseException;

    Set<Integer> getGameIDs() throws ResponseException;

    void clear() throws ResponseException;

    public boolean gameExists(String gameName) throws ResponseException;

    public void deleteGame(GameData game) throws ResponseException;

}

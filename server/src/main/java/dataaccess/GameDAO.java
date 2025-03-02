package dataaccess;
import Model.GameData;

import java.util.Collection;

public interface GameDAO {

    void addGame(GameData game) throws DataAccessException;

    GameData getGame(int id) throws DataAccessException;

    Collection<GameData> getAllGames() throws DataAccessException;

    void updateGame(GameData newGame) throws DataAccessException;

    void clear() throws DataAccessException;

}

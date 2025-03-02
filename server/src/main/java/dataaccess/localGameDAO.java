package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.HashSet;

public class localGameDAO implements GameDAO {

    private HashSet<GameData> localGameData;

    public localGameDAO() {
        localGameData = new HashSet<>();
    }

    @Override
    public void addGame(GameData game) throws DataAccessException {
        localGameData.add(game);
    }

    @Override
    public GameData getGame(int id) throws DataAccessException {
        for (GameData i : localGameData) {
            if (i.gameID() == id) {
                return i;
            }
        }
        throw new DataAccessException("No game (" + id + ") found");
    }

    @Override
    public Collection<GameData> getAllGames() throws DataAccessException {
        return localGameData;
    }

    @Override
    public void updateGame(GameData newGame) throws DataAccessException {
        if (newGame == null) {
            throw new DataAccessException("New game is null");
        }

        GameData thisGame = null;
        for (GameData i : localGameData) {
            if (i.gameID() == newGame.gameID()) {
                thisGame = i;
                break;
            }
        }

        if (thisGame == null) {
            throw new DataAccessException("No game (ID: " + newGame.gameID() + ") found");
        }
        else {
            localGameData.remove(thisGame);
            localGameData.add(newGame);
        }
    }

    @Override
    public void clear() throws DataAccessException {
        localGameData.clear();
    }
}

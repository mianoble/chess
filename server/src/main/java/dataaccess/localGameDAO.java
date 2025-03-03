package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class localGameDAO implements GameDAO {

    private HashSet<GameData> localGameData;
    Set<Integer> gameIDs;

    public localGameDAO() {
        localGameData = new HashSet<>();
        gameIDs = new HashSet<>();
    }

    @Override
    public void addGame(GameData game) throws ResponseException {
        localGameData.add(game);
    }

    @Override
    public GameData getGame(int id) throws ResponseException {
        for (GameData i : localGameData) {
            if (i.gameID() == id) {
                return i;
            }
        }
        throw new ResponseException(400, "Error: No game (" + id + ") found");
    }

    @Override
    public Collection<GameData> getAllGames() throws ResponseException {
        return localGameData;
    }

    @Override
    public Set<Integer> getGameIDs() throws ResponseException {
        return gameIDs;
    }

    @Override
    public void updateGame(GameData newGame) throws ResponseException {
        if (newGame == null) {
            throw new ResponseException(500, "Error: New game is null");
        }

        GameData thisGame = null;
        for (GameData i : localGameData) {
            if (i.gameID() == newGame.gameID()) {
                thisGame = i;
                break;
            }
        }

        if (thisGame == null) {
            throw new ResponseException(500, "Error: No game (ID: " + newGame.gameID() + ") found");
        }
        else {
            localGameData.remove(thisGame);
            localGameData.add(newGame);
        }
    }

    @Override
    public void clear() throws ResponseException {
        localGameData.clear();
    }

    public boolean gameExists(String gameName) throws ResponseException {
        for (GameData i : localGameData) {
            if (i.gameName().equals(gameName)) {
                return true;
            }
        }
        return false;
    }
}

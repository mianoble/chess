package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class localGameDao implements GameDAO {

    private HashSet<GameData> localGameData;
    Set<Integer> gameIDs;

    public localGameDao() {
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

    public void deleteGame(GameData game) throws ResponseException {
        for (GameData i : localGameData) {
            if (i.equals(game)) {
                localGameData.remove(i);
                return;
            }
        }
        throw new ResponseException(500, "Game " + game.gameID() + " not found");
    }
}

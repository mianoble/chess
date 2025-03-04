package service;

import chess.ChessGame;
import dataaccess.GameDAO;
import dataaccess.ResponseException;
import dataaccess.AuthTokenDAO;
import model.*;

import java.util.Collection;
import java.util.List;
import java.util.Random;

public class GameService {
    private GameDAO gameDAO;
    private AuthTokenDAO authDAO;
    private static final Random random = new Random();

    public GameService (GameDAO gameDAO, AuthTokenDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }
    /*
    public CreateResult create(CreateRequest r) {}
    public JoinResult join(JoinRequest r) {}
    public ListResult list(ListRequest r) {}
     */

    public CreateResult create(CreateRequest r) throws ResponseException {
        if (r.authToken() == null || r.authToken().isEmpty()) {
            throw new ResponseException(500, "Error: authToken cannot be null");
        }

        // verify authtoken is in db
        if (!authDAO.authExists(r.authToken())) {
            throw new ResponseException(401, "Error: authToken does not exist");
        }

        // check if game name is already in db
        if (gameDAO.gameExists(r.gameName())) {
            throw new ResponseException(400, "Error: bad request, game name already taken");
        }

        // make a random gameID & make sure it is not in the set of IDs already
        int randomID = 0;
        do {
            randomID = random.nextInt(1_000_000);
        } while (gameDAO.getGameIDs().contains(randomID));

        gameDAO.getGameIDs().add(randomID);

        // if not yet in db, create it/ add it
        // create new ChessGame first
        ChessGame newGame = new ChessGame();
        GameData newGameData = new GameData(randomID, null, null,
                r.gameName(), newGame); // TODO: how to pull usernames? and from where?

        gameDAO.addGame(newGameData);

        return new CreateResult(randomID);
    }

    public JoinResult join(JoinRequest r) throws ResponseException {
        // check that the game exists with ID
        if (!gameDAO.getGameIDs().contains(r.gameID())) {
            throw new ResponseException(400, "Error: bad request, gameID does not exist");
        }
        // verify authtoken is in db
        if (!authDAO.authExists(r.authID())) {
            throw new ResponseException(401, "Error: authToken does not exist");
        }

        GameData thisGame = gameDAO.getGame(r.gameID());

        // if it exists, add player to the game as requested color (use the username attached to the authtoken)
        AuthData authData = authDAO.getAuth(r.authID());
        String playerName = authData.username();
        if (r.playerColor() == null || r.playerColor().isEmpty()) {
            throw new ResponseException(400, "Error: bad request, color cannot be null");
        } else if (!r.playerColor().equals("WHITE") && !r.playerColor().equals("BLACK")) {
            throw new ResponseException(400, "Error: bad request");
        } else if (r.playerColor().equals("WHITE") &&
                ( thisGame.whiteUsername() == null || thisGame.whiteUsername().isEmpty())) {
            GameData newGame = new GameData(r.gameID(), playerName, thisGame.blackUsername(),
                                thisGame.gameName(), thisGame.game());
            gameDAO.deleteGame(thisGame);
            gameDAO.addGame(newGame);
        } else if (r.playerColor().equals("BLACK") &&
                ( thisGame.blackUsername() == null || thisGame.blackUsername().isEmpty())) {
            GameData newGame = new GameData(r.gameID(), thisGame.whiteUsername(), playerName,
                    thisGame.gameName(), thisGame.game());
            gameDAO.deleteGame(thisGame);
            gameDAO.addGame(newGame);
        } else {
            throw new ResponseException(403, "Error: already taken");
        }

        return new JoinResult();

    }

    public ListResult list(ListRequest r) throws ResponseException{
        // check authtoken first, not null and in db
        if (r.authToken() == null || r.authToken().isEmpty()) {
            throw new ResponseException(500, "Error: authToken cannot be null");
        }

        // verify authtoken is in db
        if (!authDAO.authExists(r.authToken())) {
            throw new ResponseException(401, "Error: unauthorized, authToken does not exist");
        }

        // return a collection / list of all the games and info (gameID, whiteuser, blackuser, and gamename)
        Collection<GameData> games = gameDAO.getAllGames();
        Collection<GameDataForListing> allGames = new java.util.ArrayList<>(List.of());

        for (GameData i: games) {
            GameDataForListing newGameData = new GameDataForListing(i.gameID(), i.whiteUsername(), 
                                                                    i.blackUsername(), i.gameName());
            allGames.add(newGameData);
        }

        return new ListResult(allGames);
    }

}

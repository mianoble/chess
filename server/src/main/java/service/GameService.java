package service;

import chess.ChessGame;
import dataaccess.GameDAO;
import dataaccess.ResponseException;
import dataaccess.AuthTokenDAO;
import model.CreateRequest;
import model.CreateResult;
import model.GameData;
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

    public CreateResult create(CreateRequest r) throws ResponseException{
        if (r.authToken() == null || r.authToken().isEmpty()) {
            throw new ResponseException(500, "Error: authToken cannot be null");
        }

        // verify authtoken is in db
        if (!authDAO.authExists(r.authToken())) {
            throw new ResponseException(500, "Error: authToken does not exist");
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

    public JoinResult join(JoinRequest r) {

    }

}

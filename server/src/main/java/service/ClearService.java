package service;

import dataaccess.AuthTokenDAO;
import dataaccess.GameDAO;
import dataaccess.ResponseException;
import dataaccess.UserDAO;
import model.ClearResult;

public class ClearService {
    private UserDAO userDAO;
    private AuthTokenDAO authTokenDAO;
    private GameDAO gameDAO;

    public ClearService(UserDAO userDAO, AuthTokenDAO authTokenDAO, GameDAO gameDAO) {
        this.userDAO = userDAO;
        this.authTokenDAO = authTokenDAO;
        this.gameDAO = gameDAO;
    }

    public ClearResult clear() throws ResponseException {
            userDAO.clear();
            authTokenDAO.clear();
            gameDAO.clear();

            return new ClearResult ();
    }


}

package service;

import java.util.UUID;

import dataaccess.AuthTokenDAO;
import dataaccess.DataAccessException;
import dataaccess.ResponseException;
import dataaccess.UserDAO;
import model.*;

public class UserService {
    private UserDAO userDAO;
    private AuthTokenDAO authTokenDAO;

    public UserService(UserDAO user, AuthTokenDAO authToken) {
        this.userDAO = user;
        this.authTokenDAO = authToken;
    }


    public RegisterResult register(RegisterRequest registerRequest) throws ResponseException {
        // check if username already taken
        if (userDAO.userExists(registerRequest.username())) {
            throw new ResponseException(403, "Error: username already taken");
        }

        // check that username, password, and email are all filled out
        if (registerRequest.username() == null || registerRequest.username().isEmpty()) {
            throw new ResponseException(400, "Error: Username cannot be null or empty");
        } else if (registerRequest.password() == null || registerRequest.password().isEmpty()) {
            throw new ResponseException(400, "Error: Password cannot be null or empty");
        } else if (registerRequest.email() == null || registerRequest.email().isEmpty()) {
            throw new ResponseException(400, "Error: Email cannot be null or empty");
        }

        UserData newUser = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
        userDAO.createUser(newUser);

        String authID = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authID, registerRequest.username());
        authTokenDAO.createAuth(authData);

        return new RegisterResult(registerRequest.username(), authID);
    }

    public LoginResult login(LoginRequest loginRequest) throws ResponseException{

        if (loginRequest.username() == null || loginRequest.username().isEmpty()) {
            throw new ResponseException(500, "Error: Username cannot be null or empty");
        } else if (loginRequest.password() == null || loginRequest.password().isEmpty()) {
            throw new ResponseException(500, "Error: Password cannot be null or empty");
        }

        UserData thisUser;
        thisUser = userDAO.getUser(loginRequest.username());
        if (thisUser == null) {
            throw new ResponseException(200, "Error: username not found");
        }
        else if (!thisUser.password().equals(loginRequest.password())) {
            throw new ResponseException(401, "Error: unauthorized login, incorrect password");
        }

        String authID = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authID, loginRequest.username());
        authTokenDAO.createAuth(authData);

        return new LoginResult(loginRequest.username(), authID);
    }

    public LogoutResult logout(String authID) throws ResponseException {
        if (authID == null) {
            throw new ResponseException(500, "Error: AuthToken cannot be null or empty");
        }
        if (!authTokenDAO.authExists(authID)) {
            throw new ResponseException(401, "Error: authToken does not exist");
        }

        authTokenDAO.deleteAuth(authID);
        return new LogoutResult();
    }

}

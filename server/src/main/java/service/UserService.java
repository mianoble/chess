package service;

import java.util.UUID;

import dataaccess.AuthTokenDAO;
import dataaccess.DataAccessException;
import dataaccess.ResponseException;
import dataaccess.UserDAO;
import model.AuthData;
import model.RegisterRequest;
import model.RegisterResult;
import model.UserData;

public class UserService {
    private UserDAO userDAO;
    private AuthTokenDAO authTokenDAO;

    public UserService(UserDAO user, AuthTokenDAO authToken) {
        this.userDAO = user;
        this.authTokenDAO = authToken;
    }
    /*
    public RegisterResult register(RegisterRequest registerRequest) {}
    public LoginResult login(LoginRequest loginRequest) {}
    public void logout(LogoutRequest logoutRequest) {}
     */

    public RegisterResult register(RegisterRequest registerRequest) throws ResponseException {
        try {
            // check if username already taken
            if (userDAO.userExists(registerRequest.username())) {
                throw new ResponseException(403, "Error: username already taken");
            }

            // check that username, password, and email are all filled out
            if (registerRequest.username() == null || registerRequest.username().isEmpty()) {
                throw new ResponseException(400, "Username cannot be null or empty"); // TODO: or do i send a failure response?
            } else if (registerRequest.password() == null || registerRequest.password().isEmpty()) {
                throw new ResponseException(400, "Password cannot be null or empty");
            } else if (registerRequest.email() == null || registerRequest.email().isEmpty()) {
                throw new ResponseException(400, "Email cannot be null or empty");
            }

            UserData newUser = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
            userDAO.createUser(newUser);

            String authID = UUID.randomUUID().toString();
            AuthData authData = new AuthData(authID, registerRequest.username());
            authTokenDAO.createAuth(authData);

            return new RegisterResult(registerRequest.username(), authID,
                    "username: " + registerRequest.username() + "authToken:" + authID);

        } catch (DataAccessException e) {
            throw new ResponseException(500, "Error");
        }
    }
}

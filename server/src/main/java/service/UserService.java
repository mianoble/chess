package service;

import java.util.UUID;

import dataaccess.AuthTokenDAO;
import dataaccess.DataAccessException;
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

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        try {
            // check if username already taken
            if (userDAO.userExists(registerRequest.username())) {
                throw new DataAccessException("Error: username already taken");
            }

            // check that username, password, and email are all filled out
            if (registerRequest.username() == null || registerRequest.username().isEmpty()) {
                throw new DataAccessException("Username cannot be null or empty"); // TODO: or do i send a failure response?
            } else if (registerRequest.password() == null || registerRequest.password().isEmpty()) {
                throw new DataAccessException("Password cannot be null or empty");
            } else if (registerRequest.email() == null || registerRequest.email().isEmpty()) {
                throw new DataAccessException("Email cannot be null or empty");
            }

            UserData newUser = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
            userDAO.createUser(newUser);

            String authID = UUID.randomUUID().toString();
            AuthData authData = new AuthData(authID, registerRequest.username());
            authTokenDAO.createAuth(authData);

            return new RegisterResult(registerRequest.username(), authID);

        } catch (DataAccessException e) {
            throw new DataAccessException("Error");
        }
    }
}

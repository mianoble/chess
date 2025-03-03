package dataaccess;

import model.AuthData;

public interface AuthTokenDAO {
    //CRUD
    // Create
    void createAuth(AuthData authData) throws ResponseException;

    // Read
    AuthData getAuth(String authToken) throws ResponseException;

    // Update
    // i think there are no updates for auth tokens?

    // Delete
    void deleteAuth(String authToken) throws ResponseException;

    void clear() throws ResponseException;

    public boolean authExists(String gameName) throws ResponseException;

}

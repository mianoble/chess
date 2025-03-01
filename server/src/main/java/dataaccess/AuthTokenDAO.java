package dataaccess;

import Model.AuthData;

public interface AuthTokenDAO {
    //CRUD
    // Create
    void createAuth(AuthData authData) throws DataAccessException;

    // Read
    AuthData getAuth(String authToken) throws DataAccessException;

    // Update
    // i think there are no updates for auth tokens?

    // Delete
    void deleteAuth(String authToken) throws DataAccessException;

    void clear() throws DataAccessException;

}

package dataaccess;

import Model.AuthData;

import java.util.HashSet;

public class localAuthDAO implements AuthTokenDAO {

    HashSet<AuthData> authDataSet;
    
    @Override
    public void createAuth(AuthData authData) throws DataAccessException {

    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {

    }

    @Override
    public void clear() throws DataAccessException {

    }
}

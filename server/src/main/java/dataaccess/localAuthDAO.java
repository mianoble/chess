package dataaccess;

import Model.AuthData;

import java.util.HashSet;

public class localAuthDAO implements AuthTokenDAO {

    private HashSet<AuthData> localAuthData;

    public localAuthDAO() {
        localAuthData = new HashSet<>();
    }

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {
        localAuthData.add(authData);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        for (AuthData i : localAuthData) {
            if (i.authID().equals(authToken)) {
                return i;
            }
        }
        throw new DataAccessException("No auth token (" + authToken + ") found");
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        for (AuthData i : localAuthData) {
            if (i.authID().equals(authToken)) {
                localAuthData.remove(i);
                break;
            }
        }
    }

    @Override
    public void clear() throws DataAccessException {
        localAuthData.clear();
    }
}

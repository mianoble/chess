package dataaccess;

import model.AuthData;

import java.util.HashSet;

public class LocalAuthDao implements AuthTokenDAO {

    private HashSet<AuthData> localAuthData;

    public LocalAuthDao() {
        localAuthData = new HashSet<>();
    }

    @Override
    public void createAuth(AuthData authData) throws ResponseException {
        localAuthData.add(authData);
    }

    @Override
    public AuthData getAuth(String authToken) throws ResponseException {
        for (AuthData i : localAuthData) {
            if (i.authID().equals(authToken)) {
                return i;
            }
        }
        throw new ResponseException(500, "AuthToken " + authToken + " not found");
    }

    @Override
    public void deleteAuth(String authToken) throws ResponseException {
        for (AuthData i : localAuthData) {
            if (i.authID().equals(authToken)) {
                localAuthData.remove(i);
                return;
            }
        }
        throw new ResponseException(500, "AuthToken " + authToken + " not found");
    }

    @Override
    public void clear() throws ResponseException {
        localAuthData.clear();
    }

    public boolean authExists(String auth) throws ResponseException {
        for (AuthData i : localAuthData) {
            if (i.authID().equals(auth)) {
                return true;
            }
        }
        return false;
    }
}

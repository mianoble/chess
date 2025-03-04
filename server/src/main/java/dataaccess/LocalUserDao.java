package dataaccess;

import model.UserData;

import java.util.HashSet;

public class LocalUserDao implements UserDAO {
    private final HashSet<UserData> localUserData;

    public LocalUserDao() {
        this.localUserData = new HashSet<>();
    }

    @Override
    public void createUser(UserData user) throws ResponseException {
        if (userExists(user.username())) {
            throw new ResponseException(403, "Username (" + user.username() + ") already exists");
        }
        localUserData.add(user);
    }

    @Override
    public UserData getUser(String username) throws ResponseException {

        for (UserData i : localUserData) {
            if (i.username().equals(username)) {
                return i;
            }
        }
        throw new ResponseException(401, "Error: unauthorized");
    }

    public boolean userExists(String username) throws ResponseException {
        for (UserData i : localUserData) {
            if (i.username().equals(username)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void clear() throws ResponseException {
        localUserData.clear();
    }
}

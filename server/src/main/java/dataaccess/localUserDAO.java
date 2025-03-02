package dataaccess;

import Model.UserData;

import java.util.HashSet;

public class localUserDAO implements UserDAO {
    private HashSet<UserData> localUserData;

    public localUserDAO() {
        this.localUserData = new HashSet<>();
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        if (userExists(user.username())) {
            throw new DataAccessException("Username (" + user.username() + ") already exists");
        }
        localUserData.add(user);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {

        for (UserData i : localUserData) {
            if (i.username().equals(username)) {
                return i;
            }
        }
        throw new DataAccessException("User not found");
    }

    public boolean userExists(String username) throws DataAccessException {
        for (UserData i : localUserData) {
            if (i.username().equals(username)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void deleteUser(UserData user) throws DataAccessException {
        localUserData.removeIf(i -> i.equals(user));
    }

    @Override
    public void clear() throws DataAccessException {
        localUserData.clear();
    }


}

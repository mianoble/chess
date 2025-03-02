package dataaccess;
import model.UserData;

public interface UserDAO {
    void createUser(UserData user) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    public boolean userExists(String username) throws ResponseException;

    void deleteUser(UserData user) throws DataAccessException;

    void clear() throws DataAccessException;

}

package dataaccess;
import Model.UserData;

public interface UserDAO {
    void createUser(UserData user) throws DataAccessException;

    void getUser(UserData user) throws DataAccessException;

    void deleteUser(UserData user) throws DataAccessException;

}

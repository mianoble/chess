package dataaccess;
import model.UserData;

public interface UserDAO {
    void createUser(UserData user) throws ResponseException;

    UserData getUser(String username) throws ResponseException;

    public boolean userExists(String username) throws ResponseException;

    void clear() throws ResponseException;

//    boolean userExists(String username);
    boolean verifyUser(String username, String providedClearTextPassword) throws ResponseException;

}

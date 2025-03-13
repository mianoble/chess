package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import javax.xml.crypto.Data;
import java.sql.SQLException;

public class MySQLUserDAO implements UserDAO{

    public MySQLUserDAO() throws ResponseException {
        configureDatabase();
    }

    @Override
    public void createUser(UserData user) throws ResponseException {
        var statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
        var hashedPassword = hashPassword(user.password());
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, user.username());
                ps.setString(2, hashedPassword);
                ps.setString(3, user.email());
                ps.executeUpdate();
                //storeUserPassword(user.username(), user.password());
            }
        }
        catch (SQLException e) {
            throw new ResponseException(200, String.format("unable to update database: %s, %s",
                    statement, e.getMessage()));
        }
    }

    public boolean userExists(String username) {
        var statement = "SELECT * FROM user WHERE username=?";
        try (var conn = DatabaseManager.getConnection()) {
            var ps = conn.prepareStatement(statement);
            ps.setString(1, username);

            var res = ps.executeQuery();
            return res.next();
        } catch (SQLException | ResponseException e) {
            return false;
        }
    }

    @Override
    public UserData getUser(String username) throws ResponseException {
        var statement = "SELECT * FROM user WHERE username=?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var res = ps.executeQuery()) {
                    if (res.next()) {
                        var resPassword = res.getString("password");

                        var resEmail = res.getString("email");
                        return new UserData(username, resPassword, resEmail);
                    }
                }
            }
        } catch (SQLException e) {
            throw new ResponseException(200, String.format("unable to get user: %s, %s",
                statement, e.getMessage()));
        }
        return null;
    }


    @Override
    public void clear() throws ResponseException {
        var statement = "TRUNCATE user";
        try (var conn = DatabaseManager.getConnection()) {
            var ps = conn.prepareStatement(statement);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new ResponseException(500, "unable to clear user");
        }
    }

//    void storeUserPassword(String username, String clearTextPassword) throws ResponseException {
//        String hashedPassword = BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());
//
//        var statement = "INSERT INTO user (password) VALUES (?)";
//        try (var conn = DatabaseManager.getConnection()) {
//            try (var ps = conn.prepareStatement(statement)) {
//                ps.setString(1, hashedPassword);
//                ps.executeUpdate();
//            }
//        }
//        catch (SQLException | ResponseException e) {
//            throw new ResponseException(200, String.format("unable to update hashedpassword: %s, %s",
//                    statement, e.getMessage()));
//        }
//        // write the hashed password in database along with the user's other information
//        // writeHashedPasswordToDatabase(username, hashedPassword);
//
//    }
    public String hashPassword(String clearTextPassword) {
        return BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());
    }
//
//    boolean verifyUser(String hashedPassword, String providedClearTextPassword) {
//        // read the previously hashed password from the database
//        return BCrypt.checkpw(providedClearTextPassword, hashedPassword);
//    }


    @Override
    public boolean verifyUser(String username, String providedClearTextPassword) throws ResponseException{
        // read the previously hashed password from the database
        var hashedPassword = "Error: hashed password not initialized";
        var statement = "SELECT password FROM user WHERE username=?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var res = ps.executeQuery()) {
                    if (res.next()) {
                        hashedPassword = res.getString("password");
                    }
                }
            }
        } catch (SQLException e) {
            throw new ResponseException(200, String.format("unable to get user: %s, %s",
                    statement, e.getMessage()));
        }

        return BCrypt.checkpw(providedClearTextPassword, hashedPassword);
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS user  (
                `username` VARCHAR(256) NOT NULL,
                `password` VARCHAR(256) NOT NULL,
                `email` VARCHAR(256) NOT NULL,
                PRIMARY KEY(username)
            )
            """
            //                `hashedPassword` VARCHAR(256) NOT NULL,
    };

    private void configureDatabase() throws ResponseException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new ResponseException(500, String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}

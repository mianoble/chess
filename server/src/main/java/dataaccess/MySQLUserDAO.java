package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

public class MySQLUserDAO implements UserDAO{

    public MySQLUserDAO() throws ResponseException {
        MySQLUtility.configureDatabase(createStatements);
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
            }
        }
        catch (SQLException e) {
            throw new ResponseException(401, String.format("unable to update database: %s, %s",
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
            throw new ResponseException(401, String.format("unable to get user: %s, %s",
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

    private String hashPassword(String clearTextPassword) {
        String ans = BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());
        return ans;
    }

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
            throw new ResponseException(401, String.format("unable to get user: %s, %s",
                    statement, e.getMessage()));
        }

        boolean ans = BCrypt.checkpw(providedClearTextPassword, hashedPassword);
        return ans;
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
    };
}

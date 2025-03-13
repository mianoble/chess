package dataaccess;

import model.UserData;

import javax.xml.crypto.Data;
import java.sql.SQLException;

public class MySQLUserDAO implements UserDAO{

    public MySQLUserDAO() throws ResponseException {
        configureDatabase();
    }

    @Override
    public void createUser(UserData user) throws ResponseException {
        var statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, user.username());
                ps.setString(2, user.password());
                ps.setString(3, user.email());
                ps.executeUpdate();
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
            throw new ResponseException(500, String.format("unable to get user: %s, %s",
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
            throw new ResponseException(500, String.format("unable to clear user"));
        }
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

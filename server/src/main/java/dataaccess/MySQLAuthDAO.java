package dataaccess;

import model.AuthData;
import model.UserData;

import java.sql.SQLException;

public class MySQLAuthDAO implements AuthTokenDAO{

    public MySQLAuthDAO() throws ResponseException {
        configureDatabase();
    }

    @Override
    public void createAuth(AuthData authData) throws ResponseException {
        var statement = "INSERT INTO auth (authID, username) VALUES (?, ?)";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authData.authID());
                ps.setString(2, authData.username());
                ps.executeUpdate();
            }
        }
        catch (SQLException e) {
            throw new ResponseException(200, String.format("unable to update database: %s, %s",
                    statement, e.getMessage()));
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws ResponseException {
        var statement = "SELECT * FROM auth WHERE authID=?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (var res = ps.executeQuery()) {
                    if (res.next()) {
                        String resAuthID = res.getString("authID");
                        String resUsername = res.getString("username");
                        return new AuthData(resAuthID, resUsername);
                    }
                    else {
                        return null;
                    }
                }
            }
        } catch (SQLException e) {
            throw new ResponseException(500, String.format("unable to get auth token: %s, %s",
                    statement, e.getMessage()));
        }
    }

    @Override
    public void deleteAuth(String authToken) throws ResponseException {
        var statement = "DELETE FROM auth WHERE authID=?";
        try (var conn = DatabaseManager.getConnection()) {
            var ps = conn.prepareStatement(statement);
            ps.setString(1, authToken);

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new ResponseException(500, String.format("unable to delete auth token: %s, %s",
                    statement, e.getMessage()));
        }
    }

    @Override
    public void clear() throws ResponseException {
        var statement = "TRUNCATE auth";
        try (var conn = DatabaseManager.getConnection()) {
            var ps = conn.prepareStatement(statement);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new ResponseException(500, "unable to clear auth");
        }
    }

    @Override
    public boolean authExists(String auth) throws ResponseException {
        var statement = "SELECT * FROM auth WHERE authID=?";
        try (var conn = DatabaseManager.getConnection()) {
            var ps = conn.prepareStatement(statement);
            ps.setString(1, auth);
            var res = ps.executeQuery();
            return res.next();
        } catch (SQLException | ResponseException e) {
            return false;
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS auth  (
                `authID` VARCHAR(256) NOT NULL,
                `username` VARCHAR(256) NOT NULL,
                PRIMARY KEY(authID)
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

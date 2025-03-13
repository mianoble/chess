package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class MySQLGameDAO implements GameDAO{
    public MySQLGameDAO() throws ResponseException {
        configureDatabase();
    }

    @Override
    public void addGame(GameData game) throws ResponseException {
        var statement = "INSERT INTO game (gameID, whiteUsername, blackUsername, gameName, game) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (var conn = DatabaseManager.getConnection()) {
            var ps = conn.prepareStatement(statement);
            var json = new Gson().toJson(game.game());
            ps.setInt(1, game.gameID());
            ps.setString(2, game.whiteUsername());
            ps.setString(3, game.blackUsername());
            ps.setString(4, game.gameName());
            ps.setString(5, json);

            ps.executeUpdate();
        }
        catch (SQLException e) {
            throw new ResponseException(500, String.format("unable to update database: %s, %s",
                    statement, e.getMessage()));
        }
    }

    @Override
    public GameData getGame(int id) throws ResponseException {
        var statement = "SELECT * FROM game WHERE gameID=?";
        try (var conn = DatabaseManager.getConnection()) {
            var ps = conn.prepareStatement(statement);
            ps.setInt(1, id);

            try (var res = ps.executeQuery()) {
                while (res.next()) {
                    int resGameID = res.getInt("gameID");
                    String resWhiteUser = res.getString("whiteUsername");
                    String resBlackUser = res.getString("blackUsername");
                    String resGameName = res.getString("gameName");
                    String resGame = res.getString("game");

                    var gameObj = new Gson().fromJson(resGame, ChessGame.class);
                    return new GameData(resGameID, resWhiteUser, resBlackUser, resGameName, gameObj);
                }
            }
        } catch (SQLException e) {
            throw new ResponseException(500, String.format("unable to get auth token: %s, %s",
                    statement, e.getMessage()));
        }
        return null;
    }

    @Override
    public Collection<GameData> getAllGames() throws ResponseException {
        return List.of();
    }

    @Override
    public Set<Integer> getGameIDs() throws ResponseException {
        return Set.of();
    }

    @Override
    public void clear() throws ResponseException {
        var statement = "TRUNCATE game";
        try (var conn = DatabaseManager.getConnection()) {
            var ps = conn.prepareStatement(statement);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new ResponseException(500, "unable to clear auth");
        }
    }

    @Override
    public boolean gameExists(String gameName) throws ResponseException {
        var statement = "SELECT * FROM game WHERE gameName=?";
        try (var conn = DatabaseManager.getConnection()) {
            var ps = conn.prepareStatement(statement);
            ps.setString(1, gameName);
            var res = ps.executeQuery();
            return res.next();
        } catch (SQLException | ResponseException e) {
            return false;
        }
    }

    @Override
    public void deleteGame(GameData game) throws ResponseException {
        var statement = "DELETE FROM game WHERE gameID=?";
        try (var conn = DatabaseManager.getConnection()) {
            var ps = conn.prepareStatement(statement);
            ps.setInt(1, game.gameID());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new ResponseException(500, String.format("unable to delete auth token: %s, %s",
                    statement, e.getMessage()));
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS game  (
                `gameID` int NOT NULL,
                `whiteUsername` varchar(256) DEFAULT NULL,
                `blackUsername` varchar(256) DEFAULT NULL,
                `gameName` varchar(256),
                `game` TEXT DEFAULT NULL,
                PRIMARY KEY(gameID)
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

package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class MySqlDataAccess implements DataAccess {

    public MySqlDataAccess() throws Exception {
        // create and configure sql database
        configureDatabase();
    }

    //can use an array for table implementation and loop through it for each table
    //just creating and executing the statement
    //private make tables in database
    //table = ""
    //table = "table text"
    //create table statement
    //execute update on statement
    //make gamedata table in database
    //make authdata table in database
    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS users (
            username VARCHAR(255) NOT NULL PRIMARY KEY,
            password VARCHAR(255) NOT NULL,
            email VARCHAR(255) NOT NULL
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS auths (
            username VARCHAR(255) NOT NULL,
            authToken VARCHAR(255) NOT NULL PRIMARY KEY
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS games (
            gameID INT NOT NULL AUTO_INCREMENT,
            whiteUsername VARCHAR(255),
            blackUsername VARCHAR(255),
            gameName VARCHAR(255),
            chessGame TEXT,
            PRIMARY KEY (gameID)
            )
            """
    };

    public void configureDatabase() throws SQLException, DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             var statement = conn.createStatement()) {
            statement.execute("TRUNCATE games");
            statement.execute("TRUNCATE auths");
            statement.execute("TRUNCATE users");
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException("Failed to clear database", e);
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        String statement = "SELECT username, password, email from users WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, username);
                var result = preparedStatement.executeQuery();
                if (result.next()) {
                    var pass = result.getString("password");
                    var email = result.getString("email");
                    return new UserData(username, pass, email);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to get user", e);
        }
        return null;
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        String statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, user.username());
                preparedStatement.setString(2, user.password());
                preparedStatement.setString(3, user.email());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to create user", e);
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        String statement = "SELECT username, authToken from auths WHERE authToken = ?";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, authToken);
                var result = preparedStatement.executeQuery();
                if (result.next()) {
                    var username = result.getString("username");
                    return new AuthData(authToken, username);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to get auth", e);
        }
        return null;
    }

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {
        String statement = "INSERT INTO auths (username, authToken) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, authData.username());
                preparedStatement.setString(2, authData.authToken());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to create auth", e);
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        String statement = "DELETE FROM auths WHERE authToken = ?";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, authToken);
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected == 0) {
                    throw new DataAccessException("No auth found");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to delete auth", e);
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        String statement = "SELECT whiteUsername, blackUsername, gameName, chessGame FROM games WHERE gameID=?";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setInt(1, gameID);
                var result = preparedStatement.executeQuery();
                if (result.next()) {
                    var whiteUsername = result.getString("whiteUsername");
                    var blackUsername = result.getString("blackUsername");
                    var gameName = result.getString("gameName");
                    var chessGame = new Gson().fromJson(result.getString("chessGame"), ChessGame.class);
                    return new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to get game", e);
        }
        return null;
    }

    @Override
    public void createGame(GameData game) throws DataAccessException {
        String chessGameString = new Gson().toJson(game.game());
        String statement = "INSERT INTO games (gameID, whiteUsername, blackUsername, gameName, chessGame) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setInt(1, game.gameID());
                preparedStatement.setString(2, game.whiteUsername());
                preparedStatement.setString(3, game.blackUsername());
                preparedStatement.setString(4, game.gameName());
                preparedStatement.setString(5, chessGameString);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to create game", e);
        }
    }

    @Override
    public GameData updateGame(GameData game) throws DataAccessException {
        String chessGameString = new Gson().toJson(game.game());
        String statement = "UPDATE games SET whiteUsername=?, blackUsername=?, gameName=?, chessGame=? WHERE gameID=?";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, game.whiteUsername());
                preparedStatement.setString(2, game.blackUsername());
                preparedStatement.setString(3, game.gameName());
                preparedStatement.setString(4, chessGameString);
                preparedStatement.setInt(5, game.gameID());
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected == 0) {
                    throw new DataAccessException("No game with gameID: " + game.gameID());
                }
                return game;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update game", e);
        }
    }

    @Override
    public ArrayList<GameData> listGames() {
        return null;
    }
}

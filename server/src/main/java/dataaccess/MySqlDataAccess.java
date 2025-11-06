package dataaccess;

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
            gameID INT NOT NULL,
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
    public void deleteAuth(String authToken) {

    }

    @Override
    public GameData getGame(int gameID) {
        return null;
    }

    @Override
    public void createGame(GameData game) {

    }

    @Override
    public GameData updateGame(GameData game) {
        return null;
    }

    @Override
    public ArrayList<GameData> listGames() {
        return null;
    }
}

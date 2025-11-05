package dataaccess;

import model.*;

import java.util.ArrayList;

public interface DataAccess {
    void clear() throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    void createUser(UserData user) throws DataAccessException;

    AuthData getAuth(String authToken) throws DataAccessException;

    void createAuth(AuthData authData) throws DataAccessException;

    void deleteAuth(String authToken) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    void createGame(GameData game) throws DataAccessException;

    GameData updateGame(GameData game) throws DataAccessException;

    ArrayList<GameData> listGames() throws DataAccessException;
}

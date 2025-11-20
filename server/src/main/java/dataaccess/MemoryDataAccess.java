package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class MemoryDataAccess implements DataAccess {
    private final HashMap<String, UserData> users = new HashMap<>();
    private final HashMap<String, AuthData> validAuths = new HashMap<>();
    private final HashMap<Integer, GameData> games = new HashMap<>();

    //game hashmap and auth hashmap needed for future classes
    @Override
    public void clear() {
        users.clear();
        validAuths.clear();
        games.clear();
    }

    @Override
    public UserData getUser(String username) {
        //throw error if no UserData
        return users.get(username);
    }

    @Override
    public void createUser(UserData user) {
        users.put(user.username(), user);
    }

    @Override
    public AuthData getAuth(String authToken) {
        return validAuths.get(authToken);
    }

    @Override
    public void createAuth(AuthData authData) {
        validAuths.put(authData.authToken(), authData);
    }

    @Override
    public void deleteAuth(String authToken) {
        validAuths.remove(authToken);
    }

    @Override
    public GameData getGame(int gameID) {
        return games.get(gameID);
    }

    @Override
    public int createGame(GameData game) {
        games.put(game.gameID(), game);
        return game.gameID();
    }

    @Override
    public GameData updateGame(GameData game) {
        games.put(game.gameID(), game);
        return game;
    }

    @Override
    public ArrayList<GameData> listGames() {
        return new ArrayList<>(games.values());
    }
}

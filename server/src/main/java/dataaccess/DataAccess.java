package dataaccess;

import model.*;

import java.util.ArrayList;

public interface DataAccess {
    void clear();

    UserData getUser(String username);

    void createUser(UserData user);

    AuthData getAuth(String authToken);

    void createAuth(AuthData authData);

    void deleteAuth(String authToken);

    GameData getGame(int gameID);

    void createGame(GameData game);

    GameData updateGame(GameData game);

    ArrayList<GameData> listGames();
}

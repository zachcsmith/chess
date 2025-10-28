package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;

public class MySqlDataAccess implements DataAccess {

    public MySqlDataAccess() throws DataAccessException {
        // create and configure sql database
    }

    //private make userdata table in database
    //table = ""
    //table = "table text"
    //create table statement
    //execute update on statement
    //private make gamedata table in database
    //private make authdata table in database

    @Override
    public void clear() {
    }

    @Override
    public UserData getUser(String username) {
        return null;
    }

    @Override
    public void createUser(UserData user) {

    }

    @Override
    public AuthData getAuth(String authToken) {
        return null;
    }

    @Override
    public void createAuth(AuthData authData) {

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

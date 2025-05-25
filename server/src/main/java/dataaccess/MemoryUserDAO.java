package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO{

    private final HashMap<String, UserData> userData = new HashMap<>();

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return userData.get(username);
    }

    @Override
    public UserData createuser(UserData user) throws DataAccessException {
        userData.put(user.username(), user);return user;
    }

    @Override
    public void clearUsers() throws DataAccessException {
        userData.clear();
    }
}

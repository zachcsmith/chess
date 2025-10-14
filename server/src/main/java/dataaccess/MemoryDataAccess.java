package dataaccess;

import model.UserData;

import java.util.HashMap;
import java.util.HashSet;

public class MemoryDataAccess implements DataAccess {
    private final HashMap<String, UserData> users = new HashMap<>();

    //game hashmap and auth hashmap needed for future classes
    @Override
    public void clear() {
        users.clear();
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
}

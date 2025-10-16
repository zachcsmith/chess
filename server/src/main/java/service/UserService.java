package service;

import dataaccess.DataAccess;
import model.*;

import java.util.UUID;

public class UserService {
    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    //make registerResult and registerRequest classes
    //I broke this in implementing it
    public AuthData register(UserData user) throws Exception {
        if (dataAccess.getUser(user.username()) != null) {
            throw new Exception("already exits");
        }
        dataAccess.createUser(user);
        var authData = new AuthData(user.username(), generateToken());
        return authData;
    }


    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}

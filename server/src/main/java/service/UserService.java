package service;

import model.*;

import java.util.UUID;

public class UserService {

    //make registerrsult and registerrequest classes
    public AuthData register(UserData user) {
        return new AuthData(generateToken(), user.username());
    }


    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}

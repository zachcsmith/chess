package service;

import dataaccess.DataAccess;
import model.*;
import service.exceptions.AlreadyTakenException;
import service.exceptions.BadRequestException;

import java.util.UUID;

public class UserService {
    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    //make registerResult and registerRequest classes
    //I broke this in implementing it
    public AuthData register(UserData user) throws AlreadyTakenException, BadRequestException {
        if (dataAccess.getUser(user.username()) != null) {
            throw new AlreadyTakenException("Error: already taken");
        }
        if (user.username() == null || user.password() == null || user.email() == null) {
            throw new BadRequestException("Error: bad request");
        }
        dataAccess.createUser(user);
        return new AuthData(generateToken(), user.username());
    }

    public void clear() {
        dataAccess.clear();
    }


    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}

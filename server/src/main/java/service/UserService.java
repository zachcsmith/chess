package service;

import dataaccess.DataAccess;
import handlers.ListGamesResult;
import handlers.LoginRequest;
import handlers.*;
import model.*;
import service.exceptions.AlreadyTakenException;
import service.exceptions.*;

import java.util.Objects;
import java.util.UUID;

public class UserService {
    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    //make registerResult and registerRequest classes
    public AuthData register(UserData user) throws AlreadyTakenException, BadRequestException {
        if (dataAccess.getUser(user.username()) != null) {
            throw new AlreadyTakenException("Error: already taken");
        }
        if (user.username() == null || user.password() == null || user.email() == null) {
            throw new BadRequestException("Error: bad request");
        }
        AuthData auth = new AuthData(generateToken(), user.username());
        dataAccess.createUser(user);
        dataAccess.createAuth(auth);
        return new AuthData(auth.authToken(), user.username());
    }

    public void clear() {
        dataAccess.clear();
    }

    public LoginResult login(LoginRequest request) throws BadRequestException, UnauthorizedException {
        if (request == null || request.username() == null || request.password() == null) {
            throw new BadRequestException("Error: bad request");
        }
        UserData user = dataAccess.getUser(request.username());
        if (user == null || !Objects.equals(user.password(), request.password())) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        String authToken = generateToken();
        dataAccess.createAuth(new AuthData(authToken, user.username()));
        return new LoginResult(user.username(), authToken);
    }

    public void logout(String authToken) throws UnauthorizedException {
        if (authToken == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        AuthData auth = dataAccess.getAuth(authToken);
        if (auth == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        dataAccess.deleteAuth(authToken);
    }


    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}

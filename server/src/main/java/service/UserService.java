package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import handlers.ListGamesResult;
import handlers.LoginRequest;
import handlers.*;
import model.*;
import org.mindrot.jbcrypt.BCrypt;
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
    public AuthData register(UserData user) throws AlreadyTakenException, BadRequestException, DataAccessException {
        if (dataAccess.getUser(user.username()) != null) {
            throw new AlreadyTakenException("Error: username already taken");
        }
        if (user.username() == null || user.password() == null || user.email() == null) {
            throw new BadRequestException("Error: bad request");
        }
        String hashedPass = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        UserData hashedUser = new UserData(user.username(), hashedPass, user.email());

        AuthData auth = new AuthData(generateToken(), user.username());
        dataAccess.createUser(hashedUser);
        dataAccess.createAuth(auth);
        return new AuthData(auth.authToken(), user.username());
    }

    public void clear() throws DataAccessException {
        dataAccess.clear();
    }

    public LoginResult login(LoginRequest request) throws BadRequestException, UnauthorizedException, DataAccessException {
        if (request == null || request.username() == null || request.password() == null) {
            throw new BadRequestException("Error: bad request");
        }
        UserData user = dataAccess.getUser(request.username());
        if (user == null || !BCrypt.checkpw(request.password(), user.password())) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        String authToken = generateToken();
        dataAccess.createAuth(new AuthData(authToken, user.username()));
        return new LoginResult(user.username(), authToken);
    }

    public void logout(String authToken) throws UnauthorizedException, DataAccessException {
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

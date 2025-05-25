package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import handlers.*;
import model.AuthData;
import model.UserData;

public class UserService {
    private final UserDAO userDataAccess;
    private final AuthDAO authDataAccess;

    public UserService(UserDAO userDataAccess, AuthDAO authDataAccess){
        this.authDataAccess = authDataAccess;
        this.userDataAccess = userDataAccess;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException{
        String username = registerRequest.username();
        String password = registerRequest.password();
        String email = registerRequest.email();
        if(username == null || password == null || email == null){
            throw new DataAccessException("Error: bad request");
        }
        if (userDataAccess.getUser(username) != null){
            throw new DataAccessException("Error: already taken");
        }
        userDataAccess.createuser(new UserData(username, password, email));
        String authToken = AuthService.generateAuth();
        AuthData auth = new AuthData(authToken, username);
        authDataAccess.createAuth(auth);

        return new RegisterResult(username, authToken);
    }

    public void clear() throws DataAccessException{
        authDataAccess.clearAuths();
        userDataAccess.clearUsers();
    }
}

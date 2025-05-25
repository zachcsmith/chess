package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO{
    private final HashMap<String, AuthData> authTokens = new HashMap<>();

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return authTokens.get(authToken);
    }

    @Override
    public AuthData createAuth(AuthData auth) throws DataAccessException {
        authTokens.put(auth.authToken(), auth);

        return auth;
    }

    @Override
    public void clearAuths() throws DataAccessException {
        authTokens.clear();
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        authTokens.remove(authToken);
    }
}

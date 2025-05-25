package dataaccess;

import model.UserData;
import org.eclipse.jetty.server.Authentication;

public interface UserDAO {
    UserData getUser(String username) throws DataAccessException;
    UserData createuser(UserData user) throws DataAccessException;

    void clearUsers() throws DataAccessException;
}

package dataaccess;

import model.*;

public interface DataAccess {
    void clear();

    UserData getUser(String username);

    void createUser(UserData user);
}

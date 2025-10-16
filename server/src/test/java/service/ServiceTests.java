package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceTests {

    @Test
    public void clear() throws Exception {
        DataAccess db = new MemoryDataAccess();
        UserService userService = new UserService(db);
        UserData newUser = new UserData("John", "xynd3", "john@email.com");
        AuthData authData = userService.register(newUser);
        assertNotNull(authData.username());
        userService.clear();
        assertNull(db.getUser("John"));
    }


    @Test
    public void RegisterSuccess() throws Exception {
        DataAccess db = new MemoryDataAccess();
        UserService userService = new UserService(db);
        UserData newUser = new UserData("John", "xynd3", "john@email.com");
        AuthData result = userService.register(newUser);
        assertEquals(newUser.username(), result.username());
        assertFalse(result.authToken().isEmpty());
    }

    @Test
    public void RegisterFailDoubleUser() throws Exception {
        DataAccess db = new MemoryDataAccess();
        UserService userService = new UserService(db);
        userService.clear();
        UserData newUser = new UserData("user", null, "john@email.com");
        userService.register(newUser);
        assertThrows(Exception.class, () -> {
            userService.register(newUser);
        });
    }
}


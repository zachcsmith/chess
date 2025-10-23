package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import handlers.LoginRequest;
import handlers.LoginResult;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.exceptions.UnauthorizedException;

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

    @Test
    public void LoginSuccess() throws Exception {
        DataAccess db = new MemoryDataAccess();
        UserService userService = new UserService(db);
        userService.clear();
        UserData newUser = new UserData("user", "pass", "john@email.com");
        userService.register(newUser);
        LoginRequest req = new LoginRequest("user", "pass");
        LoginResult res = userService.login(req);
        assertNotNull(res.authToken());
        assertEquals("user", res.username());
    }

    @Test
    public void LoginFailWrongPassword() throws Exception {
        DataAccess db = new MemoryDataAccess();
        UserService userService = new UserService(db);
        userService.clear();
        UserData newUser = new UserData("user", "pass", "john@email.com");
        userService.register(newUser);
        LoginRequest req = new LoginRequest("user", "password");
        assertThrows(UnauthorizedException.class, () -> {
            userService.login(req);
        });
    }

    @Test
    public void LogoutSuccess() throws Exception {
        DataAccess db = new MemoryDataAccess();
        UserService userService = new UserService(db);
        userService.clear();
        UserData newUser = new UserData("user", "pass", "john@email.com");
        userService.register(newUser);
        LoginRequest req = new LoginRequest("user", "pass");
        LoginResult res = userService.login(req);
        assertNotNull(db.getAuth(res.authToken()));
        userService.logout(res.authToken());
        assertNull(db.getAuth(res.authToken()));
    }

    @Test
    public void LogoutFailNoUser() throws Exception {
        DataAccess db = new MemoryDataAccess();
        UserService userService = new UserService(db);
        userService.clear();
        UserData newUser = new UserData("user", "pass", "john@email.com");
        userService.register(newUser);
        assertThrows(UnauthorizedException.class, () -> {
            userService.logout("Not a Valid Auth Token");
        });
    }
}


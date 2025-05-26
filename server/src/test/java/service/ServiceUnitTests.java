package service;
import dataaccess.*;
import handlers.*;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServiceUnitTests {
    MemoryUserDAO userDataAccess = new MemoryUserDAO();
    MemoryAuthDAO authDataAccess = new MemoryAuthDAO();
    MemoryGameDAO gameDataAccess = new MemoryGameDAO();
    UserService userService = new UserService(userDataAccess,authDataAccess);
    AuthService authService = new AuthService(userDataAccess,authDataAccess);

    @BeforeEach
    void setup()throws DataAccessException{
        userService.clear();
        authService.clear();
    }

    @Test
    void registerTestSuccess() throws DataAccessException {
        RegisterRequest newUser = new RegisterRequest("username", "password", "email@domain");
        RegisterResult result = userService.register(newUser);

        Assertions.assertEquals(newUser.username(), result.username());
        Assertions.assertNotNull(result.authToken());
    }

    @Test
    void registerTestFail() throws DataAccessException{
        RegisterRequest newUser = new RegisterRequest("username", null, "email@domain");
        DataAccessException ex = Assertions.assertThrows(DataAccessException.class, ()-> userService.register(newUser));

        Assertions.assertEquals("Error: bad request", ex.getMessage());
    }

    @Test
    void clearSuccess() throws DataAccessException{
        userService.register(new RegisterRequest("username", "password", "email@domain"));

    }
    @Test
    void logoutTestSuccess() throws DataAccessException{
        RegisterResult result = userService.register(new RegisterRequest("username", "password", "email@domain"));
        String authToken = result.authToken();
        userService.logout(authToken);

        Assertions.assertNotNull(authToken);
        Assertions.assertNull(authDataAccess.getAuth(authToken));
    }

    @Test
    void logoutTestFail() throws DataAccessException{
        DataAccessException ex = Assertions.assertThrows(DataAccessException.class, ()-> userService.logout(null));

        Assertions.assertEquals("Error: unauthorized", ex.getMessage());
    }


    @Test
    void loginTestSuccess() throws DataAccessException{
        userService.register(new RegisterRequest("username", "password", "email@domain"));
        LoginRequest user = new LoginRequest("username", "password");
        LoginResult result = userService.login(user);

        Assertions.assertEquals(user.username(), result.username());
        Assertions.assertNotNull(result.authToken());
    }

    @Test
    void loginTestFail() throws DataAccessException{
        LoginRequest user = new LoginRequest("username", "password");
        DataAccessException ex = Assertions.assertThrows(DataAccessException.class, ()-> userService.login(user));

        Assertions.assertEquals("Error: unauthorized", ex.getMessage());
    }
}

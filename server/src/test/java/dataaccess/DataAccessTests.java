package dataaccess;

import model.*;
import org.junit.jupiter.api.*;
import service.GameService;
import service.UserService;
import service.exceptions.AlreadyTakenException;

import static org.junit.jupiter.api.Assertions.*;

public class DataAccessTests {
    private static MySqlDataAccess sqlDataAccess;
    private static UserService userService;
    private static GameService gameService;
    private static UserData newUser;

    @BeforeAll
    public static void init() throws Exception {
        sqlDataAccess = new MySqlDataAccess();
        userService = new UserService(sqlDataAccess);
        gameService = new GameService(sqlDataAccess);
        newUser = new UserData("John", "pass", "email.com");
    }

    @BeforeEach
    public void reset() throws Exception {
        sqlDataAccess.clear();
    }

    @AfterAll
    public static void empty() throws Exception {
        sqlDataAccess.clear();
    }


    @Test
    public void getUserSuccess() throws Exception {
        sqlDataAccess.createUser(newUser);
        assertNotNull(sqlDataAccess.getUser(newUser.username()));
        UserData res = sqlDataAccess.getUser(newUser.username());
        assertEquals(newUser.username(), res.username());
    }

    @Test
    public void getUserFail() throws Exception {
        assertNull(sqlDataAccess.getUser(newUser.username()));
    }

    @Test
    public void createUserSuccess() throws Exception {
        sqlDataAccess.createUser(newUser);
        UserData res = sqlDataAccess.getUser(newUser.username());
        assertEquals(newUser.username(), res.username());
    }

    @Test
    public void createUserFail() throws Exception {
        sqlDataAccess.createUser(newUser);
        UserData rereg = new UserData(newUser.username(), "yolo", "email");
        assertThrows(DataAccessException.class, () -> {
            sqlDataAccess.createUser(rereg);
        });
    }

    @Test
    public void clearSuccess() throws Exception {
        sqlDataAccess.createUser(newUser);
        sqlDataAccess.clear();
        assertNull(sqlDataAccess.getUser(newUser.username()));
    }

    @Test
    public void createAuthSuccess() throws Exception {
        sqlDataAccess.createAuth(new AuthData("token", newUser.username()));
        assertNotNull(sqlDataAccess.getAuth("token"));
        AuthData res = sqlDataAccess.getAuth("token");
        assertEquals(newUser.username(), res.username());
    }

    @Test
    public void createAuthFail() throws Exception {
        assertThrows(DataAccessException.class, () ->
                sqlDataAccess.createAuth(new AuthData(null, newUser.username())));
    }


}

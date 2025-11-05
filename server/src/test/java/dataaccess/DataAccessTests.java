package dataaccess;

import model.*;
import org.junit.jupiter.api.*;
import service.GameService;
import service.UserService;

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

    }

    @Test
    public void createUserSuccess() throws Exception {
        sqlDataAccess.createUser(newUser);
    }

    @Test
    public void clearSuccess() throws Exception {
        sqlDataAccess.createUser(newUser);
        sqlDataAccess.clear();
        assertNull(sqlDataAccess.getUser(newUser.username()));
    }

}

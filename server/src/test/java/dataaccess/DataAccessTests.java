package dataaccess;

import model.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.GameService;
import service.UserService;

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

    @Test
    public void getUserSuccess() throws Exception {

    }

    @Test
    public void createUserSuccess() throws Exception {
        sqlDataAccess.createUser(newUser);
    }

}

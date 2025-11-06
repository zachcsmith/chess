package dataaccess;

import chess.ChessGame;
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
    private static GameData emptyGame;

    @BeforeAll
    public static void init() throws Exception {
        sqlDataAccess = new MySqlDataAccess();
        userService = new UserService(sqlDataAccess);
        gameService = new GameService(sqlDataAccess);
        newUser = new UserData("John", "pass", "email.com");
        emptyGame = new GameData(1, null, null, "game", new ChessGame());
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

    @Test
    public void getAuthSuccess() throws Exception {
        sqlDataAccess.createAuth(new AuthData("token", newUser.username()));
        AuthData res = sqlDataAccess.getAuth("token");
        assertNotNull(res);
        assertEquals(newUser.username(), res.username());
    }

    @Test
    public void getAuthFail() throws Exception {
        assertNull(sqlDataAccess.getAuth("fake authToken"));
    }

    @Test
    public void deleteAuthSuccess() throws Exception {
        sqlDataAccess.createAuth(new AuthData("token", newUser.username()));
        sqlDataAccess.deleteAuth("token");
        assertNull(sqlDataAccess.getAuth("token"));
    }

    @Test
    public void deleteAuthFail() throws Exception {
        assertThrows(DataAccessException.class, () ->
                sqlDataAccess.deleteAuth("fake token"));
    }

    @Test
    public void createGameSuccess() throws Exception {
        sqlDataAccess.createGame(emptyGame);
        assertNotNull(sqlDataAccess.getGame(emptyGame.gameID()));
    }

    @Test
    public void createGameFail() throws Exception {
        sqlDataAccess.createGame(emptyGame);
        assertThrows(DataAccessException.class, () ->
                sqlDataAccess.createGame(new GameData(1, null, null, "faulty game", new ChessGame())));
    }

    @Test
    public void getGameSuccess() throws Exception {
        sqlDataAccess.createGame(emptyGame);
        GameData res = sqlDataAccess.getGame(emptyGame.gameID());
        assertNotNull(res);
        assertEquals(emptyGame.game(), res.game());
    }

    @Test
    public void getGameFail() throws Exception {
        assertNull(sqlDataAccess.getGame(0));
    }


}

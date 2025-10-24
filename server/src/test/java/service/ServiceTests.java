package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import handlers.*;
import model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.exceptions.AlreadyTakenException;
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
        UserData newUser = new UserData("user", "pass", "john@email.com");
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

    @Test
    public void ListGamesSuccess() throws Exception {
        DataAccess db = new MemoryDataAccess();
        UserService userService = new UserService(db);
        GameService gameService = new GameService(db);
        db.clear();
        UserData newUser = new UserData("user", "pass", "john@email.com");
        userService.register(newUser);
        LoginResult res = userService.login(new LoginRequest("user", "pass"));
        AuthData auth = db.getAuth(res.authToken());
        db.createGame(new GameData(1, null, null, "myGame", new ChessGame()));
        ListGamesResult listRes = gameService.listGames(auth.authToken());
        assertNotNull(listRes);
        assertEquals(1, listRes.games().size());
        assertEquals("myGame", listRes.games().getFirst().gameName());
    }

    @Test
    public void ListGamesFailBadAuth() throws Exception {
        DataAccess db = new MemoryDataAccess();
        UserService userService = new UserService(db);
        GameService gameService = new GameService(db);
        db.clear();
        UserData newUser = new UserData("user", "pass", "john@email.com");
        userService.register(newUser);
        LoginResult res = userService.login(new LoginRequest("user", "pass"));
        AuthData auth = db.getAuth(res.authToken());
        db.createGame(new GameData(1, null, null, "myGame", new ChessGame()));
        assertThrows(UnauthorizedException.class, () -> {
            gameService.listGames("Bad Auth");
        });
    }

    @Test
    public void CreateGameSuccess() throws Exception {
        DataAccess db = new MemoryDataAccess();
        UserService userService = new UserService(db);
        GameService gameService = new GameService(db);
        db.clear();
        UserData newUser = new UserData("user", "pass", "john@email.com");
        userService.register(newUser);
        LoginResult res = userService.login(new LoginRequest("user", "pass"));
        String authToken = res.authToken();
        gameService.createGame(new CreateGameRequest("myGame"), authToken);
        assertEquals(1, gameService.listGames(authToken).games().size());
        assertEquals("myGame", gameService.listGames(authToken).games().getFirst().gameName());
    }

    @Test
    public void CreateGameFail() throws Exception {
        DataAccess db = new MemoryDataAccess();
        UserService userService = new UserService(db);
        GameService gameService = new GameService(db);
        db.clear();
        assertThrows(UnauthorizedException.class, () -> {
            gameService.createGame(new CreateGameRequest("game"), "Not Valid");
        });
    }

    @Test
    public void JoinGameSuccess() throws Exception {
        DataAccess db = new MemoryDataAccess();
        UserService userService = new UserService(db);
        GameService gameService = new GameService(db);
        db.clear();
        UserData newUser = new UserData("user", "pass", "john@email.com");
        userService.register(newUser);
        LoginResult res = userService.login(new LoginRequest("user", "pass"));
        String authToken = res.authToken();
        gameService.createGame(new CreateGameRequest("myGame"), authToken);
        gameService.joinGame(new JoinGameRequest(ChessGame.TeamColor.WHITE, 1), authToken);
        assertEquals("user", gameService.listGames(authToken).games().getFirst().whiteUsername());
    }

    @Test
    public void JoinGameFail() throws Exception {
        DataAccess db = new MemoryDataAccess();
        UserService userService = new UserService(db);
        GameService gameService = new GameService(db);
        db.clear();
        UserData newUser = new UserData("user", "pass", "john@email.com");
        userService.register(newUser);
        AuthData sec_res = userService.register(new UserData("second_user", "pass", "email"));
        LoginResult res = userService.login(new LoginRequest("user", "pass"));
        String authToken = res.authToken();
        gameService.createGame(new CreateGameRequest("myGame"), authToken);
        gameService.joinGame(new JoinGameRequest(ChessGame.TeamColor.WHITE, 1), authToken);
        assertThrows(AlreadyTakenException.class, () -> {
            gameService.joinGame(new JoinGameRequest(ChessGame.TeamColor.WHITE, 1), sec_res.authToken());
        });
    }
}


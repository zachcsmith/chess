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
    GameService gameService = new GameService(userDataAccess, authDataAccess, gameDataAccess);

    @BeforeEach
    void setup()throws DataAccessException{
        userService.clear();
        authService.clear();
        gameService.clear();
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

    @Test
    void createGameTestSuccess() throws DataAccessException{
        CreateGameRequest request = new CreateGameRequest("game");
        CreateGameResult result = gameService.createGame(request);

        Assertions.assertNotNull(gameDataAccess.getGame(result.gameID()));
    }

    @Test
    void createGameTestFail() throws DataAccessException{
        CreateGameRequest request = new CreateGameRequest("game");
        CreateGameResult result = gameService.createGame(request);

        Assertions.assertNotEquals(5, result.gameID());
    }

    @Test
    void joinGameTestSuccess() throws  DataAccessException{
        userService.register(new RegisterRequest("username", "password", "email@domain"));
        CreateGameRequest request = new CreateGameRequest("game");
        CreateGameResult idgrabber = gameService.createGame(request);
        int id = idgrabber.gameID();
        JoinGameRequest joinGameRequest = new JoinGameRequest("WHITE", id);
        gameService.joinGame(joinGameRequest, "username");
        GameData game = gameDataAccess.getGame(id);
        Assertions.assertEquals("username", game.whiteUsername());
    }

    @Test
    void joinGameTestFail() throws  DataAccessException{
        userService.register(new RegisterRequest("username", "password", "email@domain"));
        userService.register(new RegisterRequest("existingUser", "newPassword", "email2@domain"));
        CreateGameRequest request = new CreateGameRequest("game");
        CreateGameResult idgrabber = gameService.createGame(request);
        int id = idgrabber.gameID();
        gameService.joinGame(new JoinGameRequest("WHITE", id), "existingUser");
        JoinGameRequest joinGameRequest = new JoinGameRequest("WHITE", id);
        DataAccessException ex = Assertions.assertThrows(DataAccessException.class, ()-> gameService.joinGame(joinGameRequest, "username"));

        Assertions.assertEquals("Error: already taken", ex.getMessage());
    }

    @Test
    void listGamesTestSuccess() throws DataAccessException{
        RegisterResult res = userService.register(new RegisterRequest("username", "password", "email@domain"));
        String authToken = res.authToken();
        gameService.createGame(new CreateGameRequest("game1"));
        gameService.createGame(new CreateGameRequest("game2"));
        ListGamesResult result = gameService.listGames(authToken);

        Assertions.assertEquals(2, result.games().toArray().length);
    }

    @Test
    void listGamesTestFail() throws DataAccessException{
        RegisterResult res = userService.register(new RegisterRequest("username", "password", "email@domain"));
        String authToken = res.authToken();
        gameService.createGame(new CreateGameRequest("game1"));
        gameService.createGame(new CreateGameRequest("game2"));
        ListGamesResult result = gameService.listGames(authToken);

        Assertions.assertNotEquals(2, result.games().getFirst().gameID());
        Assertions.assertNotEquals(1, result.games().get(1).gameID());
    }
}

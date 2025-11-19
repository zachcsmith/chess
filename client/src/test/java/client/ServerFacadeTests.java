package client;

import handlers.LoginRequest;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import ui.ResponseException;
import ui.ServerFacade;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    private static UserData testUser;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        String url = String.format("http://localhost:%d", port);
        facade = new ServerFacade(url);
        testUser = new UserData("player", "password", "test@email.com");
    }

    @BeforeEach
    public void clearDatabase() {
        facade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void registerSuccess() {
        var authData = facade.register(new UserData("player1", "password", "p1@email.com"));
        assertEquals("player1", authData.username());
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    public void registerFail() {
        var authData = facade.register(new UserData("player1", "password", "p1@email.com"));
        assertEquals("player1", authData.username());
        assertTrue(authData.authToken().length() > 10);
        assertThrows(ResponseException.class, () ->
                facade.register(new UserData("player1", "pass", "email")));
    }

    @Test
    public void logoutSuccess() {
        var authData = facade.register(new UserData("player2", "password", "p1@email.com"));
        facade.logout();
        assertDoesNotThrow(() ->
                facade.register(testUser));
    }

    @Test
    public void logoutFail() {
        assertThrows(ResponseException.class, () ->
                facade.logout());
    }

    @Test
    public void loginSuccess() {
        var authData = facade.register(new UserData("player1", "password", "p1@email.com"));
        facade.logout();
        var result = facade.login(new LoginRequest("player1", "password"));
        assertTrue(result.authToken().length() > 10);
        assertEquals("player1", result.username());
    }

    @Test
    public void loginFail() {
        var authData = facade.register(testUser);
        assertThrows(ResponseException.class, () ->
                facade.login(new LoginRequest("player1", "pass")));
    }

}

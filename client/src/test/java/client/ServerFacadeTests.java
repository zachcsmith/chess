package client;

import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import ui.ServerFacade;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        String url = String.format("http://localhost:%d", port);
        facade = new ServerFacade(url);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void RegisterSuccess() {
        var authData = facade.register(new UserData("player1", "password", "p1@email.com"));
        assertEquals("player1", authData.username());
        assertTrue(authData.authToken().length() > 10);
    }

}

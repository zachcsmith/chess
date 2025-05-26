package server;

import com.google.gson.Gson;
import dataaccess.*;
import model.UserData;
import service.*;
import spark.*;

import java.util.Map;

import static spark.Spark.delete;

public class Server {
    UserService userService;
    UserHandler userHandler;
    GameHandler gameHandler;
    AuthService authService;
    GameService gameService;
    AuthDAO authDAO = new MemoryAuthDAO();
    UserDAO userDAO = new MemoryUserDAO();
    GameDAO gameDAO = new MemoryGameDAO();


    public int run(int desiredPort) {
        userService = new UserService(userDAO, authDAO);
        authService = new AuthService(userDAO, authDAO);
        gameService = new GameService(userDAO, authDAO, gameDAO);

        // Initialize handlers
        userHandler = new UserHandler(userService, authService);
        gameHandler = new GameHandler(gameService);
        Spark.port(desiredPort);

        Spark.staticFiles.location("/web");
        Spark.exception(DataAccessException.class, (exception, req, res) ->{
            res.status(403);
            res.type("application/json");
            res.body(new Gson().toJson(Map.of("error", exception.getMessage())));
        });

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", userHandler::register);
        delete("/db", this::clear);
        Spark.post("/session", userHandler::login);
        Spark.delete("/session", userHandler::logout);
        Spark.post("/game", gameHandler::createGame);

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public Object clear(Request request, Response response){
        response.type("application/json");
        try{
            userService.clear();
            authService.clear();
            gameService.clear();
            response.status(200);
            return "{}";
        } catch (DataAccessException e) {
            response.status(500);
            return new Gson().toJson(Map.of("Failed to clear", "Error"));
        }
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}

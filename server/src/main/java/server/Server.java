package server;

import com.google.gson.Gson;
import dataaccess.*;
import handlers.JoinGameRequest;
import model.AuthData;
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
        Spark.get("/game", gameHandler::listGame);
        Spark.put("/game", this::joinGame);

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public Object clear(Request request, Response response) throws DataAccessException{
        userService.clear();
        gameService.clear();
        return "{}";
    }

    private Object joinGame(Request request, Response res) {
        String authToken = request.headers("authorization");

        if (authToken == null || authToken.isEmpty()) {
            res.status(401);
            return new Gson().toJson(Map.of("message", "Error: unauthorized"));
        }

        try {
            validateToken(authToken);
            String username = getUsername(authToken);
            JoinGameRequest joinGameRequest = new Gson().fromJson(request.body(), JoinGameRequest.class);
            gameService.joinGame(joinGameRequest, username);
            return "{}";
        } catch (DataAccessException e) {
            String msg = e.getMessage();
            switch (msg) {
                case "Error: unauthorized":
                    res.status(401);
                    break;
                case "Error: already taken":
                    res.status(403);
                    break;
                case "Error: bad request":
                    res.status(400);
                    break;
                default:
                    res.status(500);
                    break;
            }
            return new Gson().toJson(Map.of("message", msg));
        }
    }

    private void validateToken(String authToken) throws DataAccessException {
        try {
            if (authToken == null || authToken.isEmpty()) {
                throw new DataAccessException("Error: unauthorized");
            }

            AuthData data = authDAO.getAuth(authToken);
            if (data == null || data.authToken() == null) {
                throw new DataAccessException("Error: unauthorized");
            }

        } catch (Exception e) {
            throw new DataAccessException("Error: unauthorized");
        }
    }

    private String getUsername(String authToken)throws DataAccessException{
        AuthData auth = authDAO.getAuth(authToken);
        return auth.username();
    }




    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}

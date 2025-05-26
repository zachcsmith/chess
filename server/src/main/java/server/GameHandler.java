package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import handlers.CreateGameRequest;
import service.GameService;
import spark.Request;
import spark.Response;

import java.util.Map;

public class GameHandler {
    private final GameService gameService;

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public Object createGame(Request req, Response res) {
        try {
            String authToken = req.headers("authorization");
            if (authToken == null || authToken.isEmpty() || gameService.invalidAuth(authToken)) {
                res.status(401);
                return new Gson().toJson(Map.of("message", "Error: unauthorized"));
            }

            CreateGameRequest createGameRequest = new Gson().fromJson(req.body(), CreateGameRequest.class);
            if (createGameRequest == null || createGameRequest.gameName() == null || createGameRequest.gameName().isEmpty()) {
                res.status(400);
                return new Gson().toJson(Map.of("message", "Error: bad request"));
            }

            var result = gameService.createGame(createGameRequest);
            res.status(200);
            return new Gson().toJson(result);
        } catch (DataAccessException e) {
            String msg = e.getMessage().toLowerCase().contains("unauthorized") ? "401" :
                    e.getMessage().toLowerCase().contains("bad request") ? "400" : "500";
            res.status(Integer.parseInt(msg));
            return new Gson().toJson(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            res.status(500);
            return new Gson().toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    public
}

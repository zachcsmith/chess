package server;

import com.google.gson.Gson;
import dataaccess.MemoryDataAccess;
import handlers.*;
import io.javalin.*;
import io.javalin.http.Context;
import model.*;
import service.GameService;
import service.exceptions.AlreadyTakenException;
import service.UserService;
import service.exceptions.BadRequestException;
import service.exceptions.UnauthorizedException;

public class Server {

    private final Javalin server;
    private final UserService userService;
    private final GameService gameService;

    public Server() {
        server = Javalin.create(config -> config.staticFiles.add("web"));

        var dataAccess = new MemoryDataAccess();
        userService = new UserService(dataAccess);
        gameService = new GameService(dataAccess);
        // Register your endpoints and exception handlers here.
        server.delete("db", this::clear); // call clear method to go run it in the DataAccess layer
        server.post("user", this::register);// can use method reference syntax to directly talk to register()
        server.post("session", this::login);
        server.delete("session", this::logout);
        server.get("game", this::listGames);
        server.post("game", this::createGame);

    }

    // this mirrors a register handler
    private void register(Context ctx) {
        var serializer = new Gson();
        try {
            String reqJson = ctx.body();
            UserData req = serializer.fromJson(reqJson, UserData.class);
            //call to the service and register
            AuthData res = userService.register(req);
            ctx.status(200);
            ctx.result(serializer.toJson(res));
        } catch (AlreadyTakenException e) {
            //need more logic
            ctx.status(403);
            ctx.result(serializer.toJson(new ErrorResponseModel(e.getMessage())));
        } catch (BadRequestException e) {
            ctx.status(400);
            ctx.result(serializer.toJson(new ErrorResponseModel(e.getMessage())));
        } catch (Exception e) {
            ctx.status(500);
            ctx.result(serializer.toJson(new ErrorResponseModel(e.getMessage())));
        }
        //option 1: hard code if/else for errors after sending error msg
        //option 2: expand DataAccessExecption
        //option 3: create new exception classes to sort easier ie: AlreadyTakenException
    }

    private void login(Context ctx) {
        var serializer = new Gson();
        try {
            String reqJson = ctx.body();
            LoginRequest req = serializer.fromJson(reqJson, LoginRequest.class);
            LoginResult res = userService.login(req);
            ctx.status(200);
            ctx.result(serializer.toJson(res));
        } catch (BadRequestException e) {
            ctx.status(400);
            ctx.result(serializer.toJson(new ErrorResponseModel(e.getMessage())));
        } catch (UnauthorizedException e) {
            ctx.status(401);
            ctx.result(serializer.toJson(new ErrorResponseModel(e.getMessage())));
        } catch (Exception e) {
            ctx.status(500);
            ctx.result(serializer.toJson(new ErrorResponseModel(e.getMessage())));
        }
    }

    private void logout(Context ctx) {
        var serializer = new Gson();
        try {
            String req = ctx.header("authorization");
            userService.logout(req);
            ctx.status(200);
        } catch (UnauthorizedException e) {
            ctx.status(401);
            ctx.result(serializer.toJson(new ErrorResponseModel(e.getMessage())));
        } catch (Exception e) {
            ctx.status(500);
            ctx.result(serializer.toJson(new ErrorResponseModel(e.getMessage())));
        }
    }

    private void listGames(Context ctx) {
        var serializer = new Gson();
        try {
            String req = ctx.header("authorization");
            //call to the service and register
            ListGamesResult res = gameService.listGames(req);
            ctx.status(200);
            ctx.result(serializer.toJson(res));
        } catch (UnauthorizedException e) {
            ctx.status(401);
            ctx.result(serializer.toJson(new ErrorResponseModel(e.getMessage())));
        } catch (Exception e) {
            ctx.status(500);
            ctx.result(serializer.toJson(new ErrorResponseModel(e.getMessage())));
        }
    }

    private void createGame(Context ctx) {
        var serializer = new Gson();
        try {
            String authToken = ctx.header("authorization");
            String reqJson = ctx.body();
            CreateGameRequest req = serializer.fromJson(reqJson, CreateGameRequest.class);
            CreateGameResult res = gameService.createGame(req, authToken);
            ctx.status(200);
            ctx.result(serializer.toJson(res));
        } catch (BadRequestException e) {
            ctx.status(400);
            ctx.result(serializer.toJson(new ErrorResponseModel(e.getMessage())));
        } catch (UnauthorizedException e) {
            ctx.status(401);
            ctx.result(serializer.toJson(new ErrorResponseModel(e.getMessage())));
        } catch (Exception e) {
            ctx.status(500);
            ctx.result(serializer.toJson(new ErrorResponseModel(e.getMessage())));
        }
    }

    private void joinGame(Context ctx) {
        var serializer = new Gson();
        try {
            String authToken = ctx.header("authorization");
            String reqJson = ctx.body();
            JoinGameRequest req = serializer.fromJson(reqJson, JoinGameRequest.class);
            JoinGameResult res = gameService.joinGame(req, authToken);
            ctx.status(200);
            ctx.result(serializer.toJson(res));
        } catch (BadRequestException e) {
            ctx.status(400);
            ctx.result(serializer.toJson(new ErrorResponseModel(e.getMessage())));
        } catch (UnauthorizedException e) {
            ctx.status(401);
            ctx.result(serializer.toJson(new ErrorResponseModel(e.getMessage())));
        } catch (AlreadyTakenException e) {
            ctx.status(403);
            ctx.result(serializer.toJson(new ErrorResponseModel(e.getMessage())));
        } catch (Exception e) {
            ctx.status(500);
            ctx.result(serializer.toJson(new ErrorResponseModel(e.getMessage())));
        }
    }

    private void clear(Context ctx) {
        userService.clear();
        gameService.clear();
    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}

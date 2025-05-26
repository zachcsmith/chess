package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import handlers.RegisterRequest;
import handlers.*;
import service.AuthService;
import service.UserService;
import spark.Request;
import spark.Response;

import javax.xml.crypto.Data;
import java.util.Map;

public class UserHandler {
    private final UserService userService;
    private final AuthService authService;
    private final Gson gson = new Gson();

    public UserHandler(UserService userService, AuthService authService){
        this.userService = userService;
        this.authService = authService;
    }

    public Object register(spark.Request req, Response res){
        try{
            RegisterRequest request = gson.fromJson(req.body(), RegisterRequest.class);
            if (request.username() == null || request.password() == null || request.email() == null) {
                res.status(400);
                return gson.toJson(Map.of("message", "Error: bad request"));
            }
            RegisterResult result = userService.register(request);
            res.status(200);
            return gson.toJson(result);
        } catch(DataAccessException ex){
            String msg = ex.getMessage();
            if ("Error: already taken".equals(msg)) {
                res.status(403);
            } else if ("Error: bad request".equals(msg)) {
                res.status(400);
            } else {
                res.status(500);
            }

            return gson.toJson(Map.of("message", msg));
        }
    }

    public Object login(Request req, Response res) throws DataAccessException{
        Gson gson = new Gson();
        try {
            LoginRequest loginRequest = gson.fromJson(req.body(), LoginRequest.class);
            LoginResult result = userService.login(loginRequest);
            res.status(200);
            res.type("application/json");
            return gson.toJson(result);
        }catch(DataAccessException e){
            String msg = e.getMessage();
            if ("Error: bad request".equals(msg)) {
                res.status(400);
            } else if ("Error: unauthorized".equals(msg)) {
                res.status(401);
            } else {
                res.status(500);
            }
            return gson.toJson(Map.of("message", msg));
        }
    }

    public Object logout(Request req, Response res) throws DataAccessException{
        Gson gson = new Gson();
        String authToken = req.headers("authorization");
        try{
            userService.logout(authToken);
            res.status(200);
            return"{}";
        } catch (DataAccessException e) {
            res.status(401);
            return gson.toJson(Map.of("message", e.getMessage()));
        }
    }

}

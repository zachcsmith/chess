package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import handlers.RegisterRequest;
import handlers.*;
import service.AuthService;
import service.UserService;
import spark.Response;

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
}

package server;

import com.google.gson.Gson;
import dataaccess.MemoryDataAccess;
import io.javalin.*;
import io.javalin.http.Context;
import model.*;
import service.UserService;

import java.util.Map;

public class Server {

    private final Javalin server;
    private final UserService userService;

    public Server() {
        var dataAccess = new MemoryDataAccess();
        userService = new UserService(dataAccess);
        server = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        server.delete("db", ctx -> ctx.result("{}"));
        server.post("user", ctx -> register(ctx));// can use method reference syntax to directly talk to register()

    }

    // this mirrors a register handler
    private void register(Context ctx) {
        try {
            var serializer = new Gson();
            String reqJson = ctx.body();
            UserData req = serializer.fromJson(reqJson, UserData.class);
            //call to the service and register
            AuthData res = userService.register(req);

            ctx.result(serializer.toJson(res));
        } catch (Exception e) {
            //need more logic
            ctx.status(403).result();
        }
    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}

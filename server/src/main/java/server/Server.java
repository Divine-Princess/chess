package server;

import com.google.gson.Gson;
import dataaccess.authDAO.AuthDAO;
import dataaccess.authDAO.MemoryAuthDAO;
import dataaccess.userDAO.MemoryUserDAO;
import dataaccess.userDAO.UserDAO;
import io.javalin.*;
import io.javalin.http.Context;
import server.handler.RegisterHandler;
import service.UserService;

import java.util.Map;

public class Server {

    private UserService userService;
    private UserDAO userDAO;
    private AuthDAO authDAO;

    private final Javalin javalin;

    public Server() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        userService = new UserService(userDAO,authDAO);

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                         .post("/user", context -> new RegisterHandler().handle(userService, context))
                         .exception(Exception.class, this::exceptionHandler)
                         .delete("/db", context -> new);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void exceptionHandler(Exception e, Context context) {
        var body = new Gson().toJson(Map.of("message", String.format(e.getMessage())));

        if (e instanceof BadRequestException) {
            context.status(400);
        }
        else if (e instanceof AlreadyTakenException) {
            context.status(403);
        }
        else {
            context.status(500);
        }
        context.json(body);
    }
}

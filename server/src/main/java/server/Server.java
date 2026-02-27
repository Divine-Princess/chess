package server;

import com.google.gson.Gson;
import dataaccess.authDAO.AuthDAO;
import dataaccess.authDAO.MemoryAuthDAO;
import dataaccess.gameDAO.GameDAO;
import dataaccess.gameDAO.MemoryGameDAO;
import dataaccess.userDAO.MemoryUserDAO;
import dataaccess.userDAO.UserDAO;
import io.javalin.*;
import io.javalin.http.Context;
import server.handler.ClearHandler;
import server.handler.RegisterHandler;
import service.GameService;
import service.UserService;
import service.AuthService;

import javax.imageio.spi.RegisterableService;
import java.util.Map;

public class Server {

    private final Javalin javalin;

    public Server() {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        UserService userService = new UserService(userDAO,authDAO);
        GameService gameService = new GameService(gameDAO,authDAO);
        AuthService authService = new AuthService(authDAO);
        RegisterHandler registerHandler = new RegisterHandler();
        ClearHandler clearHandler = new ClearHandler();

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                         .post("/user", context -> registerHandler.handle(userService, context))
                         .exception(Exception.class, this::exceptionHandler)
                         .delete("/db", context
                                 -> clearHandler.handle(context, userService, gameService, authService));
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

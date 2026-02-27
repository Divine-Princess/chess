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
import server.handler.LoginHandler;
import server.handler.RegisterHandler;
import service.GameService;
import service.UserService;
import service.AuthService;

import javax.imageio.spi.RegisterableService;
import java.util.Map;

public class Server {
    UserDAO userDAO;
    AuthDAO authDAO;
    GameDAO gameDAO;
    UserService userService;
    GameService gameService;
    AuthService authService;
    RegisterHandler registerHandler;
    ClearHandler clearHandler;
    LoginHandler loginHandler;

    private final Javalin javalin;

    public Server() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        userService = new UserService(userDAO,authDAO);
        gameService = new GameService(gameDAO,authDAO);
        authService = new AuthService(authDAO);
        registerHandler = new RegisterHandler(userService);
        clearHandler = new ClearHandler();
        loginHandler = new LoginHandler(userService);

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                         .post("/user", context -> registerHandler.handle(context))
                         .post("/session", context -> loginHandler.handle(context))
                         .exception(Exception.class, this::exceptionHandler)
                         .delete("/db", context
                                 -> clearHandler.handle(context, userService, gameService, authService));
                         //.delete("/session", );
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
        else if (e instanceof UnauthorizedException) {
            context.status(401);
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

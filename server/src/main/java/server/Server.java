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
import server.handler.*;
import service.GameService;
import service.UserService;
import service.AuthService;
import java.util.Map;

public class Server {
    UserDAO userDAO;
    AuthDAO authDAO;
    GameDAO gameDAO;
    UserService userService;
    GameService gameService;
    AuthService authService;
    Handler registerHandler;
    Handler clearHandler;
    Handler loginHandler;
    Handler logoutHandler;
    Handler listGamesHandler;
    Handler createGameHandler;

    private final Javalin javalin;

    public Server() {
        initializeServer();

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                         .post("/user", context -> registerHandler.handle(context))
                         .post("/session", context -> loginHandler.handle(context))
                         .post("/game", context -> createGameHandler.handle(context))
                         .get("/game", context -> listGamesHandler.handle(context))
                         .delete("/db", context -> clearHandler.handle(context))
                         .delete("/session", context -> logoutHandler.handle(context))
                         .exception(Exception.class, this::exceptionHandler);
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

        switch (e) {
            case BadRequestException badRequestException -> context.status(400);
            case UnauthorizedException unauthorizedException -> context.status(401);
            case AlreadyTakenException alreadyTakenException -> context.status(403);
            default -> context.status(500);
        }
        context.json(body);
    }

    private void initializeServer() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        userService = new UserService(userDAO,authDAO);
        gameService = new GameService(gameDAO,authDAO);
        authService = new AuthService(authDAO);
        registerHandler = new RegisterHandler(userService);
        clearHandler = new ClearHandler(userService, gameService, authService);
        loginHandler = new LoginHandler(userService);
        logoutHandler = new LogoutHandler(userService);
        listGamesHandler = new ListGamesHandler(gameService);
        createGameHandler = new CreateGameHandler(gameService);
    }
}

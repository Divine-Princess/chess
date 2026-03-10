package server.handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import model.result.ClearResult;
import service.AuthService;
import service.GameService;
import service.UserService;

public class ClearHandler implements Handler {

    private final UserService userService;
    private final GameService gameService;
    private final AuthService authService;

    public ClearHandler(UserService userService, GameService gameService, AuthService authService) {
        this.userService = userService;
        this.gameService = gameService;
        this.authService = authService;
    }

    public void handle(Context context) throws DataAccessException {

        userService.clear();
        gameService.clear();
        ClearResult result = authService.clear();

        context.result(new Gson().toJson(result));
    }
}

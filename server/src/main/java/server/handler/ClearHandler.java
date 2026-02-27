package server.handler;

import com.google.gson.Gson;
import io.javalin.http.Context;
import model.request.ClearRequest;
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

    public void handle(Context context) {

        ClearRequest request = new Gson().fromJson(context.body(), ClearRequest.class);
        ClearResult result = userService.clear(request);

        result = gameService.clear(request);

        result = authService.clear(request);

        context.result(new Gson().toJson(result));
    }
}

package server.handler;

import com.google.gson.Gson;
import io.javalin.http.Context;
import model.request.ListGamesRequest;
import model.request.LogoutRequest;
import model.result.ListGamesResult;
import model.result.LogoutResult;
import service.GameService;

public class ListGamesHandler implements Handler{

    private final GameService gameService;

    public ListGamesHandler(GameService gameService) { this.gameService = gameService; }

    @Override
    public void handle(Context context) {
        String authToken = context.header("Authorization");
        ListGamesRequest request = new ListGamesRequest(authToken);

        ListGamesResult result = gameService.listGames(request);

        context.result(new Gson().toJson(result));

    }
}

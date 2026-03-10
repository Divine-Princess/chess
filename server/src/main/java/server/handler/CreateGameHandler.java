package server.handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import model.request.CreateGameRequest;
import model.result.CreateGameResult;
import service.GameService;
import java.util.Map;


public class CreateGameHandler implements Handler {

    private final GameService gameService;

    public CreateGameHandler(GameService gameService) { this.gameService = gameService; }

    @Override
    public void handle(Context context) throws DataAccessException {

        String authToken = context.header("Authorization");
        var gameNameMap = new Gson().fromJson(context.body(), Map.class);
        CreateGameRequest request;
        if (gameNameMap.get("gameName") == null) {
            request = new CreateGameRequest(authToken, null);
        }
        else {
            String gameName = gameNameMap.get("gameName").toString();
            request = new CreateGameRequest(authToken, gameName);
        }

        CreateGameResult result = gameService.createGame(request);

        context.result(new Gson().toJson(result));

    }
}

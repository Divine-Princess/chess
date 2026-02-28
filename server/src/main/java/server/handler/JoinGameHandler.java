package server.handler;

import com.google.gson.Gson;
import io.javalin.http.Context;
import model.request.JoinGameRequest;
import model.result.JoinGameResult;
import service.GameService;

import java.util.Map;

public class JoinGameHandler implements Handler {

    private final GameService gameService;

    public JoinGameHandler(GameService gameService) { this.gameService = gameService; }

    @Override
    public void handle(Context context) {

        String authToken = context.header("Authorization");
        var gameMap = new Gson().fromJson(context.body(), Map.class);
        System.out.println("GameMap: " + gameMap);
        JoinGameRequest request;
        String playerColor;
        int gameID;

        if (gameMap.get("playerColor") == null) {
            playerColor = null;
        }
        else {
            playerColor = gameMap.get("playerColor").toString();
        }

        if (gameMap.get("gameID") == null) {
            gameID = 0;
        }
        else {
            double gameD = Double.parseDouble(gameMap.get("gameID").toString());
            gameID = (int)gameD;
        }

        System.out.println("PlayerColor: " + playerColor);
        request = new JoinGameRequest(authToken, playerColor, gameID);



        JoinGameResult result = gameService.joinGame(request);

        context.result(new Gson().toJson(result));

    }
}

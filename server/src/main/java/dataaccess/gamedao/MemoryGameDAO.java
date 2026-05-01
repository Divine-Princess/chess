package dataaccess.gamedao;

import model.data.GameData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO{

    private final HashMap<Integer, GameData> game;

    public MemoryGameDAO() {
        this.game = new HashMap<>();
    }

    @Override
    public void createGame(GameData gameData) {
        game.put(gameData.gameID(), gameData);
    }

    @Override
    public GameData getGame(int gameID) {
        return game.get(gameID);
    }

    @Override
    public Collection<GameData> listGames() {
        return game.values();
    }

    @Override
    public void updatePlayers(String playerColor, int gameID, String username) {
        GameData game = getGame(gameID);
        String whiteUsername = game.whiteUsername();
        String blackUsername = game.blackUsername();

        if (playerColor.equals("WHITE")) {
            whiteUsername = username;
        }
        else if (playerColor.equals("BLACK")) {
            blackUsername = username;
        }

        GameData updatedGame = new GameData(game.gameID(), whiteUsername, blackUsername, game.gameName(), game.game());

        this.game.put(gameID, updatedGame);
    }

    @Override
    public void clear() {
        game.clear();
    }
}

package dataaccess.gameDAO;

import model.data.GameData;

import java.util.HashMap;

public class MemoryGameDAO implements GameDAO{

    private final HashMap<String, GameData> game;

    public MemoryGameDAO() {
        this.game = new HashMap<>();
    }

    @Override
    public void createGame(GameData gameData) {
        game.put(gameData.gameName(), gameData);
    }

    @Override
    public GameData getGame(String gameName) {
        return game.get(gameName);
    }

    @Override
    public void deleteGame(String gameName) {
        game.remove(gameName);
    }

    @Override
    public void clear() {
        game.clear();
    }
}

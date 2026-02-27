package dataaccess.gameDAO;

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
    public void deleteGame(int gameID) {
        game.remove(gameID);
    }
    @Override
    public void clear() {
        game.clear();
    }
}

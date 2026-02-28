package dataaccess.gamedao;

import model.data.GameData;

import java.util.Collection;

public interface GameDAO {

    void createGame(GameData gameData);

    GameData getGame(int gameID);

    Collection<GameData> listGames();

    void updateGame(String playerColor, int gameID, String username);

    void clear();
}

package dataaccess.gameDAO;

import model.data.GameData;

import java.util.Collection;

public interface GameDAO {

    public void createGame(GameData gameData);

    public GameData getGame(int gameID);

    public Collection<GameData> listGames();

    public void deleteGame(int gameID);

    public void clear();
}

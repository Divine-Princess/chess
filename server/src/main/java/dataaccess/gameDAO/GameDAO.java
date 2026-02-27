package dataaccess.gameDAO;

import model.data.GameData;

public interface GameDAO {

    public void createGame(GameData gameData);

    public GameData getGame(String gameName);

    public void deleteGame(String username);

    public void clear();
}

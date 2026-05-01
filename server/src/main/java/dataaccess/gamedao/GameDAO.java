package dataaccess.gamedao;

import dataaccess.DataAccessException;
import model.data.GameData;

import java.util.Collection;

public interface GameDAO {

    void createGame(GameData gameData) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    Collection<GameData> listGames() throws DataAccessException;

    void updatePlayers(String playerColor, int gameID, String username) throws DataAccessException;

    void clear() throws DataAccessException;
}

package service;

import dataaccess.authDAO.AuthDAO;
import dataaccess.gameDAO.GameDAO;
import model.data.AuthData;
import model.data.GameData;
import model.request.ClearRequest;
import model.request.ListGamesRequest;
import model.result.ClearResult;
import model.result.ListGamesResult;
import server.UnauthorizedException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class GameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public ListGamesResult listGames(ListGamesRequest listGamesReq) {
        String authToken = listGamesReq.authToken();

        AuthData existingToken = authDAO.getAuth(authToken);

        if (existingToken == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }

        Collection<GameData> gameDataList = gameDAO.listGames();

        return new ListGamesResult(gameDataList);
    }

    public ClearResult clear(ClearRequest clearRequest) {
        gameDAO.clear();
        return new ClearResult();
    }
}

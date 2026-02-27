package service;

import dataaccess.authDAO.AuthDAO;
import dataaccess.gameDAO.GameDAO;
import model.request.ClearRequest;
import model.result.ClearResult;

public class GameService {
    private GameDAO gameDAO;
    private AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public ClearResult clear(ClearRequest clearRequest) {
        gameDAO.clear();
        return new ClearResult();
    }
}

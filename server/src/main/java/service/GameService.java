package service;

import chess.ChessGame;
import dataaccess.authDAO.AuthDAO;
import dataaccess.gameDAO.GameDAO;
import model.data.AuthData;
import model.data.GameData;
import model.request.ClearRequest;
import model.request.CreateGameRequest;
import model.request.ListGamesRequest;
import model.result.ClearResult;
import model.result.CreateGameResult;
import model.result.ListGamesResult;
import server.BadRequestException;
import server.UnauthorizedException;

import java.util.*;

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

    public CreateGameResult createGame(CreateGameRequest createGameReq) throws BadRequestException, UnauthorizedException {
        String authToken = createGameReq.authToken();
        String gameName = createGameReq.gameName();
        System.out.println(authToken);
        System.out.println(gameName);

        if (authToken == null || authToken.isBlank()) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        if (gameName == null) {
            throw new BadRequestException("Error: Game name missing");
        }

        AuthData existingAuth = authDAO.getAuth(authToken);

        if (existingAuth == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }

        int newID = generateID();

        GameData newGame = new GameData(newID, "", "", gameName, new ChessGame());

        return new CreateGameResult(newGame.gameID());
    }

    public ClearResult clear(ClearRequest clearRequest) {
        gameDAO.clear();
        return new ClearResult();
    }

    private int generateID() {
        Random rand = new Random();
        int randID = rand.nextInt(9999) + 1;

        GameData existingGame = gameDAO.getGame(randID);

        if (existingGame != null) {
            generateID();
        }
        return randID;
    }
}

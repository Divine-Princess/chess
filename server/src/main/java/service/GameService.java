package service;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import dataaccess.DataAccessException;
import dataaccess.authdao.AuthDAO;
import dataaccess.gamedao.GameDAO;
import model.data.AuthData;
import model.data.GameData;
import model.request.CreateGameRequest;
import model.request.JoinGameRequest;
import model.request.ListGamesRequest;
import model.result.ClearResult;
import model.result.CreateGameResult;
import model.result.JoinGameResult;
import model.result.ListGamesResult;
import server.AlreadyTakenException;
import server.BadRequestException;
import server.UnauthorizedException;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;

import java.util.*;

public class GameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;
    private int gameID = 1;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public ListGamesResult listGames(ListGamesRequest listGamesReq) throws DataAccessException {
        String authToken = listGamesReq.authToken();

        AuthData existingToken = authDAO.getAuth(authToken);

        if (existingToken == null) {
            throw new UnauthorizedException("unauthorized");
        }

        Collection<GameData> gameDataList = gameDAO.listGames();

        return new ListGamesResult(gameDataList);
    }

    public CreateGameResult createGame(CreateGameRequest createGameReq) throws BadRequestException, UnauthorizedException, DataAccessException {
        String authToken = createGameReq.authToken();
        String gameName = createGameReq.gameName();

        if (authToken == null || authToken.isBlank()) {
            throw new UnauthorizedException("unauthorized");
        }
        if (gameName == null) {
            throw new BadRequestException("Game name missing");
        }

        AuthData existingAuth = authDAO.getAuth(authToken);

        if (existingAuth == null) {
            throw new UnauthorizedException("unauthorized");
        }

        int newID = generateID();

        GameData newGame = new GameData(newID, null, null, gameName, new ChessGame());

        gameDAO.createGame(newGame);

        return new CreateGameResult(newGame.gameID());
    }

    public JoinGameResult joinGame(JoinGameRequest joinGameRequest)
            throws BadRequestException, UnauthorizedException,
            AlreadyTakenException, DataAccessException {

        String authToken = joinGameRequest.authToken();
        String playerColor = joinGameRequest.playerColor();
        int gameID = joinGameRequest.gameID();

        AuthData authData = authDAO.getAuth(authToken);
        GameData gameData = gameDAO.getGame(gameID);

        if (authToken == null || authToken.isBlank() || authData == null) {
            throw new UnauthorizedException("unauthorized");
        }
        else if (playerColor == null || playerColor.isBlank()) {
            throw new BadRequestException("missing player color");
        }
        else if (!playerColor.equals("WHITE") && !playerColor.equals("BLACK")) {
            throw new BadRequestException("Invalid color");
        }
        else if (gameID <= 0 || gameData == null) {
            System.out.println(gameID);
            throw new BadRequestException("invalid gameID");
        }

        String currUsername = authData.username();
        String whiteUsername = gameData.whiteUsername();
        String blackUsername = gameData.blackUsername();

        if ((playerColor.equals("WHITE") && whiteUsername != null && !currUsername.equals(whiteUsername))) {
            throw new AlreadyTakenException("White already taken");
        }
        else if (playerColor.equals("BLACK") && blackUsername != null && !currUsername.equals(blackUsername)) {
            throw new AlreadyTakenException("Black already taken");
        }

        gameDAO.updatePlayers(playerColor, gameID, currUsername);

        return new JoinGameResult();
    }

    public GameData getGame(UserGameCommand command) throws DataAccessException {

        AuthData existingToken = authDAO.getAuth(command.getAuthToken());

        if (existingToken == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }

        GameData gameData = gameDAO.getGame(command.getGameID());
        if (gameData == null) {
            throw new DataAccessException("Error: game does not exist");
        }
        return gameData;
    }

    public ChessGame makeMove(MakeMoveCommand command, String color) throws DataAccessException, InvalidMoveException {

        GameData game = getGame(command);

        ChessGame chessGame = game.game();

        ChessMove move = command.getMove();

        chessGame.makeMove(move);

        gameDAO.updateGame(gameID, chessGame);

    }

    public ClearResult clear() throws DataAccessException {
        gameDAO.clear();
        return new ClearResult();
    }

    private int generateID() {
        return gameID++;
    }
}

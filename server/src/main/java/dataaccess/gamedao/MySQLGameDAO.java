package dataaccess.gamedao;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DatabaseConfigurator;
import model.data.GameData;
import model.data.UserData;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MySQLGameDAO implements GameDAO {

    DatabaseConfigurator db;

    public MySQLGameDAO(DatabaseConfigurator db) {
        this.db = db;
    }

    @Override
    public void createGame(GameData gameData) throws DataAccessException {
        String chessGame = serializeChessGame(gameData.game());
        try (Connection conn = db.setupConnection()) {
            var statement =
                """
                INSERT INTO games (gameID, whiteUsername, blackUsername, gameName, game)
                VALUES (?, ?, ?, ?, ?)
                """;

            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameData.gameID());
                ps.setString(2, gameData.whiteUsername());
                ps.setString(3, gameData.blackUsername());
                ps.setString(4, gameData.gameName());
                ps.setString(5, chessGame);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: Failed to create game");
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (Connection conn = db.setupConnection()) {
            var statement =
             """
             SELECT gameID, whiteUsername, blackUsername, gameName, game
             FROM games 
             where gameID=?
             """;
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int existingGameID = rs.getInt("gameID");
                        String existingWhite = rs.getString("whiteUsername");
                        String existingBlack = rs.getString("blackUsername");
                        String existingName = rs.getString("gameName");
                        String gameJson = rs.getString("game");

                        ChessGame existingGame = deserializeJson(gameJson);
                        return new GameData(existingGameID, existingWhite,
                                existingBlack, existingName, existingGame);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Error: Unable to read game data");
        }
        return null;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        List gameList = new ArrayList();

        try (Connection conn = db.setupConnection()) {
            var statement = "SELECT "

        } catch (Exception e) {
            throw new DataAccessException("Error: Unable to read game data");
        }

        return List.of();
    }

    @Override
    public void updateGame(String playerColor, int gameID, String username) {

    }

    @Override
    public void clear() throws DataAccessException {
        try (Connection conn = db.setupConnection()) {
            var statement = "DELETE FROM games";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: Failed to clear game data");
        }
    }

    private String serializeChessGame(ChessGame game) {
        var serializer = new Gson();

        return serializer.toJson(game);
    }

    private ChessGame deserializeJson(String json) {
        var serializer = new Gson();

        return serializer.fromJson(json, ChessGame.class);
    }
}

package dataaccess.gamedao;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DatabaseConfigurator;
import model.data.GameData;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

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
            throw new DataAccessException("Failed to create game");
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (Connection conn = db.setupConnection()) {
            var statement =
             """
             SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games where gameID=?
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
            throw new DataAccessException("Unable to read game data");
        }
        return null;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        Collection<GameData> gameList = new ArrayList<>();

        try (Connection conn = db.setupConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int existingGameID = rs.getInt("gameID");
                        String existingWhite = rs.getString("whiteUsername");
                        String existingBlack = rs.getString("blackUsername");
                        String existingName = rs.getString("gameName");
                        String gameJson = rs.getString("game");

                        ChessGame existingGame = deserializeJson(gameJson);
                        gameList.add(new GameData(existingGameID, existingWhite,
                                existingBlack, existingName, existingGame));
                    }
                }
            }

        } catch (Exception e) {
            throw new DataAccessException("Unable to read game data");
        }

        return gameList;
    }

    @Override
    public void updatePlayers(String playerColor, int gameID, String username) throws DataAccessException {
        GameData game = getGame(gameID);
        if (game == null) {
            throw new DataAccessException("GameID not found");
        }
        var statement = "";

        if (playerColor.equals("WHITE")) {
            statement =
                    """
                    UPDATE games 
                    SET whiteUsername=?
                    WHERE gameID=?
                    """;

        }
        else if (playerColor.equals("BLACK")) {
            statement =
                    """
                    UPDATE games 
                    SET blackUsername=?
                    WHERE gameID=?
                    """;

        }

        try (Connection conn = db.setupConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(2, gameID);
                if (playerColor.equals("WHITE")) {
                    ps.setString(1, username);
                }
                else if (playerColor.equals("BLACK")) {
                    ps.setString(1, username);
                }
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update game");
        }

    }

    public void updateGame(int gameID, ChessGame chessGame) throws DataAccessException {
        String gameJson = serializeChessGame(chessGame);
        try (Connection conn = db.setupConnection()) {
            var statement =
                    """
                    UPDATE games
                    SET game = ?
                    WHERE gameID = ?
                    """;

            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, gameJson);
                ps.setInt(2, gameID);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update game");
        }

    }


    @Override
    public void clear() throws DataAccessException {
        try (Connection conn = db.setupConnection()) {
            var statement = "DELETE FROM games";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to clear game data");
        }
    }

    private String serializeChessGame(ChessGame game) {
        return new Gson().toJson(game);
    }

    private ChessGame deserializeJson(String json) {
        return new Gson().fromJson(json, ChessGame.class);
    }
}

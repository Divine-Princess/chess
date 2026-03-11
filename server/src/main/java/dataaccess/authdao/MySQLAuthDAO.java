package dataaccess.authdao;

import dataaccess.DataAccessException;
import dataaccess.DatabaseConfigurator;
import model.data.AuthData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLAuthDAO implements AuthDAO {

    DatabaseConfigurator db;

    public MySQLAuthDAO(DatabaseConfigurator db) {
        this.db = db;
    }

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {
        try (Connection conn = db.setupConnection()) {
            var statement = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authData.authToken());
                ps.setString(2, authData.username());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to create authentication");
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        try (Connection conn = db.setupConnection()) {
            var statement = "SELECT authToken, username FROM auth where authToken=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String existingAuthToken = rs.getString("authToken");
                        String existingUsername = rs.getString("username");
                        return new AuthData(existingAuthToken, existingUsername);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Unable to read auth data");
        }
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        try (Connection conn = db.setupConnection()) {
            var statement = "DELETE FROM auth WHERE authToken=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to clear auth data");
        }

    }

    @Override
    public void clear() throws DataAccessException {
        try (Connection conn = db.setupConnection()) {
            var statement = "DELETE FROM auth";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to clear auth data");
        }
    }
}

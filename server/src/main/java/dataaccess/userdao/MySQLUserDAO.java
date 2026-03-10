package dataaccess.userdao;

import com.mysql.cj.x.protobuf.MysqlxPrepare;
import dataaccess.DataAccessException;
import dataaccess.DatabaseConfigurator;
import dataaccess.DatabaseManager;
import model.data.UserData;

import javax.xml.crypto.Data;
import javax.xml.transform.Result;
import java.sql.*;

public class MySQLUserDAO implements UserDAO{

    DatabaseConfigurator db;
    public MySQLUserDAO(DatabaseConfigurator db) {
        this.db = db;
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        try (Connection conn = db.setupConnection()) {
            var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, userData.username());
                ps.setString(2, userData.password());
                ps.setString(3, userData.email());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: Failed to create user");
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (Connection conn = db.setupConnection()) {
            var statement = "SELECT username, password, email FROM users where username=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String existingUsername = rs.getString("username");
                        String existingPassword = rs.getString("password");
                        String existingEmail = rs.getString("email");
                        return new UserData(existingUsername, existingPassword, existingEmail);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Error: Unable to read user data");
        }
        return null;
    }

    @Override
    public void clear() throws DataAccessException {
        try (Connection conn = db.setupConnection()) {
            var statement = "DELETE FROM users";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: Failed to clear data");
        }
    }
}

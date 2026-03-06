package dataaccess.userdao;

import dataaccess.DatabaseManager;
import model.data.UserData;

import java.sql.*;

public class MySQLUserDAO implements UserDAO{

    private final Connection conn;
    public MySQLUserDAO(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void createUser(UserData userData) throws SQLException {
        try (conn) {
            var statement = "";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, userData.username());
                ps.setString(2, userData.password());
                ps.setString(3, userData.email());
            }
        }
    }

    @Override
    public UserData getUser(String username) {
        return null;
    }

    @Override
    public void clear() {

    }
}

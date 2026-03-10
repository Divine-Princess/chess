package dataaccess;

import java.sql.*;

public class DatabaseConfigurator {

    public DatabaseConfigurator() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            String[] createStatements = {
                    """
            CREATE TABLE IF NOT EXISTS  users (
              username VARCHAR(255) PRIMARY KEY NOT NULL,
              password VARCHAR(255) NOT NULL,
              email VARCHAR(255) NOT NULL
            )
            """,
                    """
            CREATE TABLE IF NOT EXISTS  auth (
              authToken VARCHAR(255) NOT NULL,
              username VARCHAR(255) NOT NULL,
              PRIMARY KEY (authToken)
              )
            """,
                    """
            CREATE TABLE IF NOT EXISTS  games (
              gameID int PRIMARY KEY,
              whiteUsername VARCHAR(255),
              blackUsername VARCHAR(255),
              gameName VARCHAR(255) NOT NULL,
              game TEXT DEFAULT NULL
              )
            """
            };
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

    public Connection setupConnection() throws DataAccessException {
        return DatabaseManager.getConnection();
    }

}

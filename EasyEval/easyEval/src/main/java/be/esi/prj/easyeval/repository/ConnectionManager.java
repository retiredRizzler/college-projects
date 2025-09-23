package be.esi.prj.easyeval.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionManager {

    private static Connection connection;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                String dbUrl = "jdbc:sqlite:external-data/easyeval.db";
                connection = DriverManager.getConnection(dbUrl);

                try (Statement stmt = connection.createStatement()) {
                    stmt.execute("PRAGMA foreign_keys = ON;");
                }
            }
            return connection;
        } catch (SQLException e) {
            System.err.println("Failed to get connection: " + e.getMessage()); // for debug
            throw new RepositoryException("Can not make connection", e.getCause());
        }
    }

    public static void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Failed to close connection: " + e.getMessage()); // for debug
            throw new RepositoryException("Can not close connection", e.getCause());
        }
    }
}
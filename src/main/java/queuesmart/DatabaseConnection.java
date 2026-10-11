package queuesmart;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DatabaseConnection {
    private static final Properties PROPERTIES = loadProperties();

    private DatabaseConnection() {}

    private static Properties loadProperties() {
        Properties p = new Properties();
        try (InputStream in = DatabaseConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in == null) throw new IllegalStateException("src/main/resources/db.properties was not found.");
            p.load(in);
            return p;
        } catch (IOException e) {
            throw new IllegalStateException("Could not read db.properties.", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        String url = PROPERTIES.getProperty("db.url");
        String user = PROPERTIES.getProperty("db.user", "root");
        String password = PROPERTIES.getProperty("db.password", " ");

        if (url == null || url.isBlank()) {
            throw new SQLException("db.url is missing.");
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            String msg = e.getMessage() == null ? "" : e.getMessage().toLowerCase();
            if (msg.contains("access denied")) {
                throw new SQLException("MySQL login failed for '" + user + "'. Check the password in db.properties.", e);
            }
            if (msg.contains("unknown database")) {
                throw new SQLException("Database 'queuesmart' does not exist. Run sql/schema.sql in MySQL Workbench.", e);
            }
            if (msg.contains("communications link failure") || msg.contains("connection refused")) {
                throw new SQLException("MySQL is not reachable. Start the MySQL service and try again.", e);
            }
            throw e;
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL Connector/J is missing from the Maven classpath.", e);
        }
    }

    public static boolean testConnection() {
        try (Connection c = getConnection()) {
            return c.isValid(3);
        } catch (SQLException e) {
            return false;
        }
    }
}
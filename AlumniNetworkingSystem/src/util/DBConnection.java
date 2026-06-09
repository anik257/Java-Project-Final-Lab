package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {
    private static String url;
    private static String username;
    private static String password;
    private static String driver;
    private static boolean mockMode = false;
    private static Connection connection = null;

    static {
        Properties props = new Properties();
        InputStream is = null;
        try {
            // First try reading from the src directory directly
            try {
                is = new FileInputStream("src/db.properties");
            } catch (IOException e) {
                // Fallback to resource stream
                is = DBConnection.class.getClassLoader().getResourceAsStream("db.properties");
            }

            if (is != null) {
                props.load(is);
                url = props.getProperty("db.url");
                username = props.getProperty("db.username");
                password = props.getProperty("db.password");
                driver = props.getProperty("db.driver");
            } else {
                System.err.println("[DBConnection] Warning: db.properties file not found. Defaulting to Mock Mode.");
                mockMode = true;
            }
        } catch (IOException e) {
            System.err.println("[DBConnection] Warning: Failed to load db.properties. Defaulting to Mock Mode.");
            mockMode = true;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }

        // Attempt to load driver and test database connection
        if (!mockMode) {
            try {
                Class.forName(driver);
                // Attempt connection with 3-second timeout
                DriverManager.setLoginTimeout(3);
                connection = DriverManager.getConnection(url, username, password);
                System.out.println("[DBConnection] Connected to MySQL database successfully.");
            } catch (ClassNotFoundException e) {
                System.err.println("[DBConnection] MySQL JDBC Driver not found. Defaulting to Mock Mode.");
                mockMode = true;
            } catch (SQLException e) {
                System.out.println("\n======================================================================");
                System.out.println("[DBConnection] WARNING: Unable to connect to MySQL database.");
                System.out.println("  URL: " + url);
                System.out.println("  Error: " + e.getMessage());
                System.out.println("----------------------------------------------------------------------");
                System.out.println(">>> Falling back to In-Memory Fallback Mode (no MySQL server needed) <<<");
                System.out.println("======================================================================\n");
                mockMode = true;
            }
        }
    }

    /**
     * Retrieves the database connection.
     * In mock mode, this will return null.
     */
    public static Connection getConnection() throws SQLException {
        if (mockMode) {
            return null;
        }
        
        // Check if connection is still active; if not, re-establish it
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(url, username, password);
            }
        } catch (SQLException e) {
            // If reconnect fails, switch to mock mode dynamically
            mockMode = true;
            System.out.println("[DBConnection] Database connection lost. Switching to Mock Mode dynamically.");
            return null;
        }
        return connection;
    }

    /**
     * Checks if the database is running in mock mode.
     */
    public static boolean isMockMode() {
        return mockMode;
    }

    /**
     * Manually force mock mode (e.g. for unit testing).
     */
    public static void setMockMode(boolean mode) {
        mockMode = mode;
    }
}

package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

        // Attempt to load driver, create database if needed, and connect
        if (!mockMode) {
            try {
                Class.forName(driver);
                DriverManager.setLoginTimeout(3);

                // --- Step 1: Parse the URL to extract the database name and base server URL ---
                String dbName = extractDatabaseName(url);
                String baseUrl = extractBaseUrl(url);

                if (dbName == null || baseUrl == null) {
                    System.err.println("[DBConnection] Could not parse database name from URL. Defaulting to Mock Mode.");
                    mockMode = true;
                } else {
                    // --- Step 2: Connect to MySQL server (without database) and create DB if needed ---
                    try (Connection serverConn = DriverManager.getConnection(baseUrl, username, password);
                         Statement stmt = serverConn.createStatement()) {
                        stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName);
                        System.out.println("[DBConnection] Database '" + dbName + "' is ready.");
                    }

                    // --- Step 3: Connect to the target database ---
                    connection = DriverManager.getConnection(url, username, password);
                    System.out.println("[DBConnection] Connected to MySQL database successfully.");

                    // --- Step 4: Check if tables exist; if not, run schema.sql and seed.sql ---
                    if (!doesTableExist(connection, dbName, "users")) {
                        System.out.println("[DBConnection] Tables not found. Initializing schema and seed data...");
                        executeSqlFile(connection, "database/schema.sql");
                        executeSqlFile(connection, "database/seed.sql");
                        System.out.println("[DBConnection] Database schema and seed data initialized successfully.");
                    } else {
                        System.out.println("[DBConnection] Database tables already exist. Skipping initialization.");
                    }
                }
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
     * Extracts the database name from a JDBC URL.
     * e.g. jdbc:mysql://localhost:3306/alumni_db?... -> alumni_db
     */
    private static String extractDatabaseName(String jdbcUrl) {
        try {
            // Remove the query string part (everything after '?')
            String urlWithoutParams = jdbcUrl.contains("?") ? jdbcUrl.substring(0, jdbcUrl.indexOf('?')) : jdbcUrl;
            // The database name is the last segment after the last '/'
            int lastSlash = urlWithoutParams.lastIndexOf('/');
            if (lastSlash >= 0 && lastSlash < urlWithoutParams.length() - 1) {
                return urlWithoutParams.substring(lastSlash + 1);
            }
        } catch (Exception e) {
            // Fall through
        }
        return null;
    }

    /**
     * Extracts the base server URL (without database name) from a JDBC URL.
     * e.g. jdbc:mysql://localhost:3306/alumni_db?useSSL=false -> jdbc:mysql://localhost:3306?useSSL=false
     */
    private static String extractBaseUrl(String jdbcUrl) {
        try {
            String params = "";
            String urlWithoutParams = jdbcUrl;
            if (jdbcUrl.contains("?")) {
                int qIndex = jdbcUrl.indexOf('?');
                params = jdbcUrl.substring(qIndex); // includes the '?'
                urlWithoutParams = jdbcUrl.substring(0, qIndex);
            }
            int lastSlash = urlWithoutParams.lastIndexOf('/');
            if (lastSlash >= 0) {
                return urlWithoutParams.substring(0, lastSlash) + params;
            }
        } catch (Exception e) {
            // Fall through
        }
        return null;
    }

    /**
     * Checks if a table exists in the specified database catalog.
     */
    private static boolean doesTableExist(Connection conn, String catalog, String tableName) {
        try {
            DatabaseMetaData meta = conn.getMetaData();
            try (ResultSet rs = meta.getTables(catalog, null, tableName, new String[]{"TABLE"})) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("[DBConnection] Error checking table existence: " + e.getMessage());
        }
        return false;
    }

    /**
     * Reads a SQL file and executes all statements (split by ';').
     * Skips comment lines (starting with '--') and empty lines.
     */
    private static void executeSqlFile(Connection conn, String filePath) {
        // Try multiple paths to locate the SQL file
        File file = new File(filePath);
        if (!file.exists()) {
            // Try relative to src parent
            file = new File("AlumniNetworkingSystem/" + filePath);
        }
        if (!file.exists()) {
            System.err.println("[DBConnection] SQL file not found: " + filePath);
            return;
        }

        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                // Skip comment-only lines
                if (trimmed.startsWith("--") || trimmed.isEmpty()) {
                    continue;
                }
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            System.err.println("[DBConnection] Error reading SQL file '" + filePath + "': " + e.getMessage());
            return;
        }

        // Split by semicolons and execute each statement
        String[] statements = sb.toString().split(";");
        try (Statement stmt = conn.createStatement()) {
            for (String sql : statements) {
                String trimmedSql = sql.trim();
                if (!trimmedSql.isEmpty()) {
                    stmt.execute(trimmedSql);
                }
            }
        } catch (SQLException e) {
            System.err.println("[DBConnection] Error executing SQL from '" + filePath + "': " + e.getMessage());
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

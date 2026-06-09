package dao;

import model.User;
import util.DBConnection;
import util.MockDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public User authenticate(String email, String password) {
        if (DBConnection.isMockMode()) {
            for (User u : MockDatabase.users) {
                if (u.getEmail().equalsIgnoreCase(email) && u.getPassword().equals(password)) {
                    return u;
                }
            }
            return null;
        }

        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] Error during authentication: " + e.getMessage());
        }
        return null;
    }

    public boolean register(User user) {
        if (DBConnection.isMockMode()) {
            if (isEmailExists(user.getEmail())) {
                return false;
            }
            user.setId(MockDatabase.getNextUserId());
            MockDatabase.users.add(user);
            return true;
        }

        String sql = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getRole());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] Error during registration: " + e.getMessage());
        }
        return false;
    }

    public boolean isEmailExists(String email) {
        if (DBConnection.isMockMode()) {
            for (User u : MockDatabase.users) {
                if (u.getEmail().equalsIgnoreCase(email)) {
                    return true;
                }
            }
            return false;
        }

        String sql = "SELECT 1 FROM users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] Error checking email existence: " + e.getMessage());
        }
        return false;
    }

    public User getUserById(int id) {
        if (DBConnection.isMockMode()) {
            for (User u : MockDatabase.users) {
                if (u.getId() == id) {
                    return u;
                }
            }
            return null;
        }

        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] Error retrieving user: " + e.getMessage());
        }
        return null;
    }

    public List<User> getAllUsers() {
        if (DBConnection.isMockMode()) {
            return new ArrayList<>(MockDatabase.users);
        }

        List<User> userList = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                userList.add(new User(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("role")
                ));
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] Error listing users: " + e.getMessage());
        }
        return userList;
    }
}

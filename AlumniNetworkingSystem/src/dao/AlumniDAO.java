package dao;

import model.Alumni;
import model.User;
import util.DBConnection;
import util.MockDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AlumniDAO {
    private UserDAO userDAO = new UserDAO();

    public boolean registerAlumni(Alumni alumni) {
        // Step 1: Register as generic User first
        if (!userDAO.register(alumni)) {
            return false;
        }

        // Step 2: Register specific alumni details
        if (DBConnection.isMockMode()) {
            MockDatabase.alumni.put(alumni.getId(), alumni);
            return true;
        }

        String sql = "INSERT INTO alumni (user_id, job_title, company, industry, years_of_experience, is_willing_to_mentor) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, alumni.getId());
            stmt.setString(2, alumni.getJobTitle());
            stmt.setString(3, alumni.getCompany());
            stmt.setString(4, alumni.getIndustry());
            stmt.setInt(5, alumni.getYearsOfExperience());
            stmt.setBoolean(6, alumni.isWillingToMentor());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[AlumniDAO] Error registering alumni details: " + e.getMessage());
        }
        return false;
    }

    public Alumni getAlumniById(int id) {
        if (DBConnection.isMockMode()) {
            return MockDatabase.alumni.get(id);
        }

        String sql = "SELECT u.id, u.name, u.email, u.password, a.job_title, a.company, a.industry, a.years_of_experience, a.is_willing_to_mentor " +
                     "FROM users u INNER JOIN alumni a ON u.id = a.user_id " +
                     "WHERE u.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Alumni(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("job_title"),
                        rs.getString("company"),
                        rs.getString("industry"),
                        rs.getInt("years_of_experience"),
                        rs.getBoolean("is_willing_to_mentor")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("[AlumniDAO] Error retrieving alumni: " + e.getMessage());
        }
        return null;
    }

    public boolean updateAlumni(Alumni alumni) {
        if (DBConnection.isMockMode()) {
            // Update in users list
            for (User u : MockDatabase.users) {
                if (u.getId() == alumni.getId()) {
                    u.setName(alumni.getName());
                    u.setEmail(alumni.getEmail());
                    u.setPassword(alumni.getPassword());
                    break;
                }
            }
            // Update in alumni map
            MockDatabase.alumni.put(alumni.getId(), alumni);
            return true;
        }

        String updateUserSql = "UPDATE users SET name = ?, email = ?, password = ? WHERE id = ?";
        String updateAlumniSql = "UPDATE alumni SET job_title = ?, company = ?, industry = ?, years_of_experience = ?, is_willing_to_mentor = ? WHERE user_id = ?";
        
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement stmt1 = conn.prepareStatement(updateUserSql);
                 PreparedStatement stmt2 = conn.prepareStatement(updateAlumniSql)) {
                
                // Update User
                stmt1.setString(1, alumni.getName());
                stmt1.setString(2, alumni.getEmail());
                stmt1.setString(3, alumni.getPassword());
                stmt1.setInt(4, alumni.getId());
                stmt1.executeUpdate();

                // Update Alumni
                stmt2.setString(1, alumni.getJobTitle());
                stmt2.setString(2, alumni.getCompany());
                stmt2.setString(3, alumni.getIndustry());
                stmt2.setInt(4, alumni.getYearsOfExperience());
                stmt2.setBoolean(5, alumni.isWillingToMentor());
                stmt2.setInt(6, alumni.getId());
                stmt2.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException e) {
                if (conn != null) {
                    conn.rollback();
                }
                throw e;
            } finally {
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
            }
        } catch (SQLException e) {
            System.err.println("[AlumniDAO] Error updating alumni: " + e.getMessage());
        }
        return false;
    }

    public List<Alumni> getAllMentors() {
        if (DBConnection.isMockMode()) {
            List<Alumni> mentors = new ArrayList<>();
            for (Alumni a : MockDatabase.alumni.values()) {
                if (a.isWillingToMentor()) {
                    mentors.add(a);
                }
            }
            return mentors;
        }

        List<Alumni> mentors = new ArrayList<>();
        String sql = "SELECT u.id, u.name, u.email, u.password, a.job_title, a.company, a.industry, a.years_of_experience, a.is_willing_to_mentor " +
                     "FROM users u INNER JOIN alumni a ON u.id = a.user_id " +
                     "WHERE a.is_willing_to_mentor = TRUE";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                mentors.add(new Alumni(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("job_title"),
                    rs.getString("company"),
                    rs.getString("industry"),
                    rs.getInt("years_of_experience"),
                    rs.getBoolean("is_willing_to_mentor")
                ));
            }
        } catch (SQLException e) {
            System.err.println("[AlumniDAO] Error listing mentors: " + e.getMessage());
        }
        return mentors;
    }

    public List<Alumni> searchAlumni(String query) {
        if (DBConnection.isMockMode()) {
            List<Alumni> matches = new ArrayList<>();
            String lowerQuery = query.toLowerCase();
            for (Alumni a : MockDatabase.alumni.values()) {
                boolean match = a.getName().toLowerCase().contains(lowerQuery) ||
                                (a.getCompany() != null && a.getCompany().toLowerCase().contains(lowerQuery)) ||
                                (a.getIndustry() != null && a.getIndustry().toLowerCase().contains(lowerQuery)) ||
                                (a.getJobTitle() != null && a.getJobTitle().toLowerCase().contains(lowerQuery));
                if (match) {
                    matches.add(a);
                }
            }
            return matches;
        }

        List<Alumni> list = new ArrayList<>();
        String sql = "SELECT u.id, u.name, u.email, u.password, a.job_title, a.company, a.industry, a.years_of_experience, a.is_willing_to_mentor " +
                     "FROM users u INNER JOIN alumni a ON u.id = a.user_id " +
                     "WHERE u.name LIKE ? OR a.company LIKE ? OR a.industry LIKE ? OR a.job_title LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String wildcardQuery = "%" + query + "%";
            stmt.setString(1, wildcardQuery);
            stmt.setString(2, wildcardQuery);
            stmt.setString(3, wildcardQuery);
            stmt.setString(4, wildcardQuery);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Alumni(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("job_title"),
                        rs.getString("company"),
                        rs.getString("industry"),
                        rs.getInt("years_of_experience"),
                        rs.getBoolean("is_willing_to_mentor")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("[AlumniDAO] Error searching alumni: " + e.getMessage());
        }
        return list;
    }
}

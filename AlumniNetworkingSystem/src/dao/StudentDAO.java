package dao;

import model.Student;
import model.User;
import util.DBConnection;
import util.MockDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentDAO {
    private UserDAO userDAO = new UserDAO();

    public boolean registerStudent(Student student) {
        // Step 1: Register as generic User first
        if (!userDAO.register(student)) {
            return false;
        }

        // Step 2: Register specific student details
        if (DBConnection.isMockMode()) {
            MockDatabase.students.put(student.getId(), student);
            return true;
        }

        String sql = "INSERT INTO students (user_id, major, graduation_year, current_status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, student.getId());
            stmt.setString(2, student.getMajor());
            stmt.setInt(3, student.getGraduationYear());
            stmt.setString(4, student.getCurrentStatus());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[StudentDAO] Error registering student details: " + e.getMessage());
        }
        return false;
    }

    public Student getStudentById(int id) {
        if (DBConnection.isMockMode()) {
            return MockDatabase.students.get(id);
        }

        String sql = "SELECT u.id, u.name, u.email, u.password, s.major, s.graduation_year, s.current_status " +
                     "FROM users u INNER JOIN students s ON u.id = s.user_id " +
                     "WHERE u.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Student(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("major"),
                        rs.getInt("graduation_year"),
                        rs.getString("current_status")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("[StudentDAO] Error retrieving student: " + e.getMessage());
        }
        return null;
    }

    public boolean updateStudent(Student student) {
        if (DBConnection.isMockMode()) {
            // Update in users list
            for (User u : MockDatabase.users) {
                if (u.getId() == student.getId()) {
                    u.setName(student.getName());
                    u.setEmail(student.getEmail());
                    u.setPassword(student.getPassword());
                    break;
                }
            }
            // Update in students map
            MockDatabase.students.put(student.getId(), student);
            return true;
        }

        String updateUserSql = "UPDATE users SET name = ?, email = ?, password = ? WHERE id = ?";
        String updateStudentSql = "UPDATE students SET major = ?, graduation_year = ?, current_status = ? WHERE user_id = ?";
        
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Transaction boundaries

            try (PreparedStatement stmt1 = conn.prepareStatement(updateUserSql);
                 PreparedStatement stmt2 = conn.prepareStatement(updateStudentSql)) {
                
                // Update User table
                stmt1.setString(1, student.getName());
                stmt1.setString(2, student.getEmail());
                stmt1.setString(3, student.getPassword());
                stmt1.setInt(4, student.getId());
                stmt1.executeUpdate();

                // Update Student table
                stmt2.setString(1, student.getMajor());
                stmt2.setInt(2, student.getGraduationYear());
                stmt2.setString(3, student.getCurrentStatus());
                stmt2.setInt(4, student.getId());
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
            System.err.println("[StudentDAO] Error updating student: " + e.getMessage());
        }
        return false;
    }
}

package dao;

import model.MentorshipRequest;
import util.DBConnection;
import util.MockDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MentorshipRequestDAO {

    public boolean createRequest(MentorshipRequest request) {
        if (DBConnection.isMockMode()) {
            request.setId(MockDatabase.getNextRequestId());
            request.setRequestDate(new Timestamp(System.currentTimeMillis()));
            // Populate names for easy display
            for (model.User u : MockDatabase.users) {
                if (u.getId() == request.getStudentId()) {
                    request.setStudentName(u.getName());
                }
                if (u.getId() == request.getAlumniId()) {
                    request.setAlumniName(u.getName());
                }
            }
            MockDatabase.mentorshipRequests.add(request);
            return true;
        }

        String sql = "INSERT INTO mentorship_requests (student_id, alumni_id, notes, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, request.getStudentId());
            stmt.setInt(2, request.getAlumniId());
            stmt.setString(3, request.getNotes());
            stmt.setString(4, request.getStatus());
            
            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        request.setId(rs.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[MentorshipRequestDAO] Error creating request: " + e.getMessage());
        }
        return false;
    }

    public boolean updateRequestStatus(int requestId, String status) {
        if (DBConnection.isMockMode()) {
            for (MentorshipRequest r : MockDatabase.mentorshipRequests) {
                if (r.getId() == requestId) {
                    r.setStatus(status);
                    return true;
                }
            }
            return false;
        }

        String sql = "UPDATE mentorship_requests SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, requestId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[MentorshipRequestDAO] Error updating status: " + e.getMessage());
        }
        return false;
    }

    public List<MentorshipRequest> getRequestsByStudent(int studentId) {
        if (DBConnection.isMockMode()) {
            List<MentorshipRequest> list = new ArrayList<>();
            for (MentorshipRequest r : MockDatabase.mentorshipRequests) {
                if (r.getStudentId() == studentId) {
                    list.add(r);
                }
            }
            return list;
        }

        List<MentorshipRequest> list = new ArrayList<>();
        String sql = "SELECT r.*, u_stud.name AS student_name, u_alum.name AS alumni_name " +
                     "FROM mentorship_requests r " +
                     "INNER JOIN users u_stud ON r.student_id = u_stud.id " +
                     "INNER JOIN users u_alum ON r.alumni_id = u_alum.id " +
                     "WHERE r.student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    MentorshipRequest r = new MentorshipRequest(
                        rs.getInt("id"),
                        rs.getInt("student_id"),
                        rs.getInt("alumni_id"),
                        rs.getString("status"),
                        rs.getTimestamp("request_date"),
                        rs.getString("notes")
                    );
                    r.setStudentName(rs.getString("student_name"));
                    r.setAlumniName(rs.getString("alumni_name"));
                    list.add(r);
                }
            }
        } catch (SQLException e) {
            System.err.println("[MentorshipRequestDAO] Error getting student requests: " + e.getMessage());
        }
        return list;
    }

    public List<MentorshipRequest> getRequestsByAlumni(int alumniId) {
        if (DBConnection.isMockMode()) {
            List<MentorshipRequest> list = new ArrayList<>();
            for (MentorshipRequest r : MockDatabase.mentorshipRequests) {
                if (r.getAlumniId() == alumniId) {
                    list.add(r);
                }
            }
            return list;
        }

        List<MentorshipRequest> list = new ArrayList<>();
        String sql = "SELECT r.*, u_stud.name AS student_name, u_alum.name AS alumni_name " +
                     "FROM mentorship_requests r " +
                     "INNER JOIN users u_stud ON r.student_id = u_stud.id " +
                     "INNER JOIN users u_alum ON r.alumni_id = u_alum.id " +
                     "WHERE r.alumni_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, alumniId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    MentorshipRequest r = new MentorshipRequest(
                        rs.getInt("id"),
                        rs.getInt("student_id"),
                        rs.getInt("alumni_id"),
                        rs.getString("status"),
                        rs.getTimestamp("request_date"),
                        rs.getString("notes")
                    );
                    r.setStudentName(rs.getString("student_name"));
                    r.setAlumniName(rs.getString("alumni_name"));
                    list.add(r);
                }
            }
        } catch (SQLException e) {
            System.err.println("[MentorshipRequestDAO] Error getting alumni requests: " + e.getMessage());
        }
        return list;
    }

    public MentorshipRequest getRequestById(int id) {
        if (DBConnection.isMockMode()) {
            for (MentorshipRequest r : MockDatabase.mentorshipRequests) {
                if (r.getId() == id) {
                    return r;
                }
            }
            return null;
        }

        String sql = "SELECT r.*, u_stud.name AS student_name, u_alum.name AS alumni_name " +
                     "FROM mentorship_requests r " +
                     "INNER JOIN users u_stud ON r.student_id = u_stud.id " +
                     "INNER JOIN users u_alum ON r.alumni_id = u_alum.id " +
                     "WHERE r.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    MentorshipRequest r = new MentorshipRequest(
                        rs.getInt("id"),
                        rs.getInt("student_id"),
                        rs.getInt("alumni_id"),
                        rs.getString("status"),
                        rs.getTimestamp("request_date"),
                        rs.getString("notes")
                    );
                    r.setStudentName(rs.getString("student_name"));
                    r.setAlumniName(rs.getString("alumni_name"));
                    return r;
                }
            }
        } catch (SQLException e) {
            System.err.println("[MentorshipRequestDAO] Error getting request by id: " + e.getMessage());
        }
        return null;
    }
}

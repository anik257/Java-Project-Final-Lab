package dao;

import model.Event;
import model.User;
import util.DBConnection;
import util.MockDatabase;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventDAO {

    public boolean createEvent(Event event) {
        if (DBConnection.isMockMode()) {
            event.setId(MockDatabase.getNextEventId());
            // Fetch organizer name
            for (User u : MockDatabase.users) {
                if (u.getId() == event.getOrganizerId()) {
                    event.setOrganizerName(u.getName());
                    break;
                }
            }
            MockDatabase.events.add(event);
            return true;
        }

        String sql = "INSERT INTO events (title, description, event_date, location, organizer_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, event.getTitle());
            stmt.setString(2, event.getDescription());
            stmt.setTimestamp(3, Timestamp.valueOf(event.getDate()));
            stmt.setString(4, event.getLocation());
            stmt.setInt(5, event.getOrganizerId());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        event.setId(rs.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[EventDAO] Error creating event: " + e.getMessage());
        }
        return false;
    }

    public List<Event> getAllEvents() {
        if (DBConnection.isMockMode()) {
            // Make sure organizer names are updated
            for (Event e : MockDatabase.events) {
                for (User u : MockDatabase.users) {
                    if (u.getId() == e.getOrganizerId()) {
                        e.setOrganizerName(u.getName());
                        break;
                    }
                }
            }
            return new ArrayList<>(MockDatabase.events);
        }

        List<Event> list = new ArrayList<>();
        String sql = "SELECT e.*, u.name AS organizer_name " +
                     "FROM events e LEFT JOIN users u ON e.organizer_id = u.id " +
                     "ORDER BY e.event_date ASC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Timestamp ts = rs.getTimestamp("event_date");
                LocalDateTime ldt = ts != null ? ts.toLocalDateTime() : LocalDateTime.now();
                Event event = new Event(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    ldt,
                    rs.getString("location"),
                    rs.getInt("organizer_id")
                );
                event.setOrganizerName(rs.getString("organizer_name"));
                list.add(event);
            }
        } catch (SQLException e) {
            System.err.println("[EventDAO] Error listing events: " + e.getMessage());
        }
        return list;
    }

    public boolean rsvpToEvent(int eventId, int userId) {
        if (DBConnection.isMockMode()) {
            // Check if already RSVP'd
            for (MockDatabase.EventRSVP rsvp : MockDatabase.eventRsvps) {
                if (rsvp.eventId == eventId && rsvp.userId == userId) {
                    return true; // Already RSVP'd is fine
                }
            }
            MockDatabase.eventRsvps.add(new MockDatabase.EventRSVP(eventId, userId));
            return true;
        }

        String sql = "INSERT INTO event_rsvps (event_id, user_id) VALUES (?, ?) " +
                     "ON DUPLICATE KEY UPDATE event_id = event_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[EventDAO] Error RSVPing to event: " + e.getMessage());
        }
        return false;
    }

    public List<User> getAttendees(int eventId) {
        if (DBConnection.isMockMode()) {
            List<User> list = new ArrayList<>();
            for (MockDatabase.EventRSVP rsvp : MockDatabase.eventRsvps) {
                if (rsvp.eventId == eventId) {
                    for (User u : MockDatabase.users) {
                        if (u.getId() == rsvp.userId) {
                            list.add(u);
                        }
                    }
                }
            }
            return list;
        }

        List<User> list = new ArrayList<>();
        String sql = "SELECT u.* FROM users u " +
                     "INNER JOIN event_rsvps r ON u.id = r.user_id " +
                     "WHERE r.event_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("[EventDAO] Error getting attendees: " + e.getMessage());
        }
        return list;
    }

    public List<Event> getEventsByUserRsvp(int userId) {
        if (DBConnection.isMockMode()) {
            List<Event> list = new ArrayList<>();
            for (MockDatabase.EventRSVP rsvp : MockDatabase.eventRsvps) {
                if (rsvp.userId == userId) {
                    for (Event e : MockDatabase.events) {
                        if (e.getId() == rsvp.eventId) {
                            list.add(e);
                        }
                    }
                }
            }
            return list;
        }

        List<Event> list = new ArrayList<>();
        String sql = "SELECT e.*, u.name AS organizer_name " +
                     "FROM events e " +
                     "INNER JOIN event_rsvps r ON e.id = r.event_id " +
                     "LEFT JOIN users u ON e.organizer_id = u.id " +
                     "WHERE r.user_id = ? " +
                     "ORDER BY e.event_date ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Timestamp ts = rs.getTimestamp("event_date");
                    LocalDateTime ldt = ts != null ? ts.toLocalDateTime() : LocalDateTime.now();
                    Event event = new Event(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        ldt,
                        rs.getString("location"),
                        rs.getInt("organizer_id")
                    );
                    event.setOrganizerName(rs.getString("organizer_name"));
                    list.add(event);
                }
            }
        } catch (SQLException e) {
            System.err.println("[EventDAO] Error getting user RSVPs: " + e.getMessage());
        }
        return list;
    }
}

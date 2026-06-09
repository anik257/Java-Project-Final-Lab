package service;

import dao.EventDAO;
import model.Event;
import model.User;

import java.time.LocalDateTime;
import java.util.List;

public class EventService {
    private EventDAO eventDAO = new EventDAO();

    public boolean createEvent(String title, String description, LocalDateTime date, String location, int organizerId) {
        if (title == null || title.trim().isEmpty() || location == null || location.trim().isEmpty() || date == null) {
            System.out.println("Event title, location, and date are required.");
            return false;
        }
        if (date.isBefore(LocalDateTime.now())) {
            System.out.println("Cannot schedule an event in the past.");
            return false;
        }

        Event event = new Event(title, description, date, location, organizerId);
        return eventDAO.createEvent(event);
    }

    public List<Event> listUpcomingEvents() {
        return eventDAO.getAllEvents();
    }

    public boolean registerForEvent(int eventId, int userId) {
        return eventDAO.rsvpToEvent(eventId, userId);
    }

    public List<User> getEventAttendees(int eventId) {
        return eventDAO.getAttendees(eventId);
    }

    public List<Event> getRegisteredEvents(int userId) {
        return eventDAO.getEventsByUserRsvp(userId);
    }
}

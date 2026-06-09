package util;

import model.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MockDatabase {
    public static List<User> users = new ArrayList<>();
    public static Map<Integer, Student> students = new HashMap<>();
    public static Map<Integer, Alumni> alumni = new HashMap<>();
    public static List<MentorshipRequest> mentorshipRequests = new ArrayList<>();
    public static List<Event> events = new ArrayList<>();
    public static List<EventRSVP> eventRsvps = new ArrayList<>();

    private static int nextUserId = 1;
    private static int nextRequestId = 1;
    private static int nextEventId = 1;

    public static class EventRSVP {
        public int eventId;
        public int userId;
        public Timestamp rsvpDate;

        public EventRSVP(int eventId, int userId) {
            this.eventId = eventId;
            this.userId = userId;
            this.rsvpDate = new Timestamp(System.currentTimeMillis());
        }
    }

    static {
        // Seed default Admin (ID 1)
        User admin = new User(nextUserId++, "System Admin", "admin@alumni.com", "admin123", "ADMIN");
        users.add(admin);

        // Seed Alumni 1: Jane Smith (ID 2)
        Alumni jane = new Alumni("Jane Smith", "jane@alumni.com", "jane123", "Senior Software Engineer", "Google", "Tech", 5, true);
        jane.setId(nextUserId++);
        users.add(jane);
        alumni.put(jane.getId(), jane);

        // Seed Alumni 2: John Doe (ID 3)
        Alumni john = new Alumni("John Doe", "john@alumni.com", "john123", "Product Manager", "Meta", "Tech", 8, true);
        john.setId(nextUserId++);
        users.add(john);
        alumni.put(john.getId(), john);

        // Seed Alumni 3: Alice Johnson (ID 4)
        Alumni alice = new Alumni("Alice Johnson", "alice@alumni.com", "alice123", "Data Scientist", "Amazon", "E-Commerce", 3, false);
        alice.setId(nextUserId++);
        users.add(alice);
        alumni.put(alice.getId(), alice);

        // Seed Student 1: Bob Lee (ID 5)
        Student bob = new Student("Bob Lee", "bob@student.com", "bob123", "Computer Science", 2027, "Looking for internships");
        bob.setId(nextUserId++);
        users.add(bob);
        students.put(bob.getId(), bob);

        // Seed Student 2: Charlie Brown (ID 6)
        Student charlie = new Student("Charlie Brown", "charlie@student.com", "charlie123", "Software Engineering", 2026, "Employed");
        charlie.setId(nextUserId++);
        users.add(charlie);
        students.put(charlie.getId(), charlie);

        // Seed Events
        Event event1 = new Event(nextEventId++, "Alumni Tech Panel 2026", "Hear from alumni working at FAANG companies", 
                LocalDateTime.now().plusDays(5), "Main Auditorium & Zoom", admin.getId());
        event1.setOrganizerName(admin.getName());
        events.add(event1);

        Event event2 = new Event(nextEventId++, "Resume Review Workshop", "Get your resume reviewed 1-on-1 by experienced alumni", 
                LocalDateTime.now().plusDays(12), "Career Services Room 302", jane.getId());
        event2.setOrganizerName(jane.getName());
        events.add(event2);

        // Seed Mentorship Request (Charlie requests John)
        MentorshipRequest request1 = new MentorshipRequest(nextRequestId++, charlie.getId(), john.getId(), "PENDING", 
                new Timestamp(System.currentTimeMillis() - 86400000), "Hello John, I would love to learn more about product management at Meta.");
        request1.setStudentName(charlie.getName());
        request1.setAlumniName(john.getName());
        mentorshipRequests.add(request1);

        // Seed RSVPs (Charlie RSVPs to tech panel)
        eventRsvps.add(new EventRSVP(event1.getId(), charlie.getId()));
    }

    public static synchronized int getNextUserId() {
        return nextUserId++;
    }

    public static synchronized int getNextRequestId() {
        return nextRequestId++;
    }

    public static synchronized int getNextEventId() {
        return nextEventId++;
    }
}

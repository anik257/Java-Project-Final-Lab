package test;

import model.*;
import service.*;
import util.DBConnection;

import java.time.LocalDateTime;
import java.util.List;

public class DemoTest {
    private static final UserService userService = new UserService();
    private static final StudentService studentService = new StudentService();
    private static final AlumniService alumniService = new AlumniService();
    private static final RequestService requestService = new RequestService();
    private static final EventService eventService = new EventService();

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("     RUNNING SYSTEM TEST SUITE (DEMOTEST)        ");
        System.out.println("=================================================");
        
        // Ensure mock mode is on for direct self-contained test verification
        // (If MySQL is running and configured, it will verify on MySQL, otherwise on Mock database)
        System.out.println("[Test] Running in " + (DBConnection.isMockMode() ? "Mock In-Memory" : "MySQL JDBC") + " mode.");

        try {
            testRegistrationAndLogin();
            testMentorSearch();
            testMentorshipRequestFlow();
            testEventRSVPFlow();
            
            System.out.println("\n=================================================");
            System.out.println("     ALL TESTS PASSED SUCCESSFULLY! (SUCCESS)    ");
            System.out.println("=================================================");
        } catch (Exception e) {
            System.err.println("\n[Test Failure] A test case failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void testRegistrationAndLogin() {
        System.out.println("\n[Test Case 1] Registration and Authentication");
        
        // 1. Register a student
        String studentEmail = "test_student_" + System.currentTimeMillis() + "@test.com";
        boolean studentReg = studentService.registerStudent("Test Student", studentEmail, "pass123", "Data Science", 2026);
        assert studentReg : "Student registration failed";
        System.out.println("  - Student registered successfully: " + studentEmail);

        // 2. Register an alumni
        String alumniEmail = "test_alumni_" + System.currentTimeMillis() + "@test.com";
        boolean alumniReg = alumniService.registerAlumni("Test Alumni", alumniEmail, "pass123", "Lead Architect", "Netflix", "Tech", 10, true);
        assert alumniReg : "Alumni registration failed";
        System.out.println("  - Alumni registered successfully: " + alumniEmail);

        // 3. Login verify
        User loggedInUser = userService.login(studentEmail, "pass123");
        assert loggedInUser != null : "Student login failed";
        assert loggedInUser.getRole().equals("STUDENT") : "Role mapping mismatch";
        System.out.println("  - Login authenticated successfully for " + loggedInUser.getName());
    }

    private static void testMentorSearch() {
        System.out.println("\n[Test Case 2] Alumni Mentor Searching");

        // Search for Google mentors (mock database contains Google alumni Jane Smith)
        List<Alumni> googleMentors = alumniService.searchMentors("Google");
        assert !googleMentors.isEmpty() : "Could not find Jane Smith at Google";
        System.out.println("  - Successfully searched mentors by Company. Matches: " + googleMentors.size());
        System.out.println("    First match: " + googleMentors.get(0).getName() + " working at " + googleMentors.get(0).getCompany());
        
        // Search by industry
        List<Alumni> techMentors = alumniService.searchMentors("Tech");
        assert !techMentors.isEmpty() : "No mentors in Tech industry found";
        System.out.println("  - Successfully searched mentors by Industry. Matches: " + techMentors.size());
    }

    private static void testMentorshipRequestFlow() {
        System.out.println("\n[Test Case 3] Mentorship Request Lifecycle");

        // Retrieve a student and an alumni
        List<Alumni> mentors = alumniService.getAvailableMentors();
        assert !mentors.isEmpty() : "No available mentors to request";
        Alumni mentor = mentors.get(0);

        // Get a student from mock or user list
        // Let's search mock list
        int studentId = 5; // Bob Lee's ID in Mock database
        if (!DBConnection.isMockMode()) {
            // If in MySQL mode, register a quick student for this test
            String sEmail = "bob_test@test.com";
            if (!userService.isEmailTaken(sEmail)) {
                studentService.registerStudent("Bob Test", sEmail, "pass123", "CS", 2026);
            }
            User u = userService.login(sEmail, "pass123");
            studentId = u.getId();
        }

        // Send request
        boolean requestSent = requestService.sendMentorshipRequest(studentId, mentor.getId(), "Looking for coding advice.");
        assert requestSent : "Failed to send mentorship request";
        System.out.println("  - Mentorship request submitted from Student ID " + studentId + " to Alumni ID " + mentor.getId());

        // Read requests for Alumni
        List<MentorshipRequest> alumRequests = requestService.getAlumniRequests(mentor.getId());
        assert !alumRequests.isEmpty() : "No request found in alumni inbox";
        MentorshipRequest pendingReq = null;
        for (MentorshipRequest r : alumRequests) {
            if (r.getStatus().equals("PENDING") && r.getStudentId() == studentId) {
                pendingReq = r;
                break;
            }
        }
        assert pendingReq != null : "Pending request not found";
        System.out.println("  - Request verified in Alumni inbox: Status = " + pendingReq.getStatus());

        // Respond (Accept)
        boolean accepted = requestService.respondToRequest(pendingReq.getId(), true);
        assert accepted : "Alumni failed to accept request";
        
        // Verify update
        MentorshipRequest updatedReq = new dao.MentorshipRequestDAO().getRequestById(pendingReq.getId());
        assert updatedReq.getStatus().equals("ACCEPTED") : "Request status was not updated to ACCEPTED";
        System.out.println("  - Request successfully ACCEPTED by alumni.");
    }

    private static void testEventRSVPFlow() {
        System.out.println("\n[Test Case 4] Event Planning and RSVP Management");

        // 1. Create a future networking event
        LocalDateTime futureDate = LocalDateTime.now().plusDays(2);
        boolean eventCreated = eventService.createEvent("Tech Mock Interview", "Sharpen your coding skills with mock interviews", futureDate, "Lab Room 102", 1);
        assert eventCreated : "Failed to create event";
        System.out.println("  - Event 'Tech Mock Interview' created successfully.");

        // 2. Fetch events list
        List<Event> allEvents = eventService.listUpcomingEvents();
        assert !allEvents.isEmpty() : "Events list is empty";
        Event lastEvent = allEvents.get(allEvents.size() - 1);
        System.out.println("  - Event listing retrieval verified. Last event: " + lastEvent.getTitle());

        // 3. User RSVPs
        int userId = 1; // Admin user
        boolean rsvpSuccess = eventService.registerForEvent(lastEvent.getId(), userId);
        assert rsvpSuccess : "User failed to RSVP to event";
        System.out.println("  - User ID " + userId + " successfully registered for Event ID " + lastEvent.getId());

        // 4. Verify attendee lists
        List<User> attendees = eventService.getEventAttendees(lastEvent.getId());
        assert !attendees.isEmpty() : "No attendees found after RSVP";
        assert attendees.get(0).getId() == userId : "Attendee ID mismatch";
        System.out.println("  - Attendee list query verified. Attendees count: " + attendees.size());
    }
}

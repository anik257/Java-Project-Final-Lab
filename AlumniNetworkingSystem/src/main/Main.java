package main;

import model.*;
import service.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final UserService userService = new UserService();
    private static final StudentService studentService = new StudentService();
    private static final AlumniService alumniService = new AlumniService();
    private static final RequestService requestService = new RequestService();
    private static final EventService eventService = new EventService();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("      WELCOME TO ALUMNI NETWORKING SYSTEM        ");
        System.out.println("=================================================");
        
        while (true) {
            System.out.println("\n--- MAIN MENU ---");
            System.out.println("1. Login");
            System.out.println("2. Register as Student");
            System.out.println("3. Register as Alumni");
            System.out.println("4. Exit");
            System.out.print("Select an option (1-4): ");
            
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    handleLogin();
                    break;
                case "2":
                    handleStudentRegistration();
                    break;
                case "3":
                    handleAlumniRegistration();
                    break;
                case "4":
                    System.out.println("\nThank you for using Alumni Networking System. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid option. Please enter a number between 1 and 4.");
            }
        }
    }

    private static void handleLogin() {
        System.out.println("\n--- LOGIN ---");
        System.out.print("Enter Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine().trim();

        User user = userService.login(email, password);
        if (user == null) {
            System.out.println("Error: Invalid email or password.");
            return;
        }

        System.out.println("\nLogin successful! Welcome, " + user.getName() + " (" + user.getRole() + ")");

        if (user.getRole().equals("STUDENT")) {
            showStudentMenu(user.getId());
        } else if (user.getRole().equals("ALUMNI")) {
            showAlumniMenu(user.getId());
        } else if (user.getRole().equals("ADMIN")) {
            showAdminMenu(user);
        }
    }

    private static void handleStudentRegistration() {
        System.out.println("\n--- STUDENT REGISTRATION ---");
        System.out.print("Enter Full Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine().trim();
        System.out.print("Enter Major (e.g. Computer Science): ");
        String major = scanner.nextLine().trim();
        
        int gradYear = 0;
        while (true) {
            System.out.print("Enter Graduation Year: ");
            try {
                gradYear = Integer.parseInt(scanner.nextLine().trim());
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid year. Please enter a valid number (e.g. 2026).");
            }
        }

        boolean success = studentService.registerStudent(name, email, password, major, gradYear);
        if (success) {
            System.out.println("Student registration successful! You can now log in.");
        } else {
            System.out.println("Student registration failed. Check inputs or if email is already taken.");
        }
    }

    private static void handleAlumniRegistration() {
        System.out.println("\n--- ALUMNI REGISTRATION ---");
        System.out.print("Enter Full Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine().trim();
        System.out.print("Enter Job Title: ");
        String jobTitle = scanner.nextLine().trim();
        System.out.print("Enter Company: ");
        String company = scanner.nextLine().trim();
        System.out.print("Enter Industry (e.g. Tech, Finance, Health): ");
        String industry = scanner.nextLine().trim();

        int exp = 0;
        while (true) {
            System.out.print("Enter Years of Experience: ");
            try {
                exp = Integer.parseInt(scanner.nextLine().trim());
                break;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }

        System.out.print("Are you willing to mentor students? (y/n): ");
        boolean willing = scanner.nextLine().trim().equalsIgnoreCase("y");

        boolean success = alumniService.registerAlumni(name, email, password, jobTitle, company, industry, exp, willing);
        if (success) {
            System.out.println("Alumni registration successful! You can now log in.");
        } else {
            System.out.println("Alumni registration failed. Check inputs or if email is already taken.");
        }
    }

    // ==========================================
    // STUDENT DASHBOARD
    // ==========================================
    private static void showStudentMenu(int studentId) {
        while (true) {
            System.out.println("\n--- STUDENT DASHBOARD ---");
            System.out.println("1. View/Update My Profile");
            System.out.println("2. View All Available Mentors");
            System.out.println("3. Search Alumni Mentors");
            System.out.println("4. Send Mentorship Request");
            System.out.println("5. View My Sent Requests");
            System.out.println("6. View Upcoming Networking Events");
            System.out.println("7. RSVP to an Event");
            System.out.println("8. View My Registered Events");
            System.out.println("9. Logout");
            System.out.print("Select an option (1-9): ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    handleStudentProfile(studentId);
                    break;
                case "2":
                    listMentors();
                    break;
                case "3":
                    handleMentorSearch();
                    break;
                case "4":
                    handleSendRequest(studentId);
                    break;
                case "5":
                    handleViewStudentRequests(studentId);
                    break;
                case "6":
                    listEvents();
                    break;
                case "7":
                    handleEventRsvp(studentId);
                    break;
                case "8":
                    handleViewStudentRegisteredEvents(studentId);
                    break;
                case "9":
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void handleStudentProfile(int studentId) {
        Student student = studentService.getStudentProfile(studentId);
        if (student == null) {
            System.out.println("Error: Student profile details not found.");
            return;
        }

        System.out.println("\n--- MY PROFILE ---");
        System.out.println("Name: " + student.getName());
        System.out.println("Email: " + student.getEmail());
        System.out.println("Major: " + student.getMajor());
        System.out.println("Graduation Year: " + student.getGraduationYear());
        System.out.println("Current Status: " + student.getCurrentStatus());
        System.out.println("------------------");
        System.out.print("Do you want to update your details? (y/n): ");
        
        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            System.out.print("Enter New Name (leave blank to keep current): ");
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) student.setName(input);

            System.out.print("Enter New Password (leave blank to keep current): ");
            input = scanner.nextLine().trim();
            if (!input.isEmpty()) student.setPassword(input);

            System.out.print("Enter New Major (leave blank to keep current): ");
            input = scanner.nextLine().trim();
            if (!input.isEmpty()) student.setMajor(input);

            System.out.print("Enter Current Status (e.g. Employed, Interning; leave blank to keep): ");
            input = scanner.nextLine().trim();
            if (!input.isEmpty()) student.setCurrentStatus(input);

            if (studentService.updateProfile(student)) {
                System.out.println("Profile updated successfully!");
            } else {
                System.out.println("Profile update failed.");
            }
        }
    }

    private static void listMentors() {
        List<Alumni> mentors = alumniService.getAvailableMentors();
        if (mentors.isEmpty()) {
            System.out.println("No mentors are currently available.");
            return;
        }
        System.out.println("\n--- AVAILABLE ALUMNI MENTORS ---");
        for (Alumni a : mentors) {
            System.out.printf("ID: %d | Name: %s | Role: %s at %s | Industry: %s | Experience: %d yrs\n",
                    a.getId(), a.getName(), a.getJobTitle(), a.getCompany(), a.getIndustry(), a.getYearsOfExperience());
        }
    }

    private static void handleMentorSearch() {
        System.out.print("Enter search keyword (industry, company, job title, or name): ");
        String query = scanner.nextLine().trim();
        List<Alumni> matches = alumniService.searchMentors(query);
        if (matches.isEmpty()) {
            System.out.println("No matching mentors found.");
            return;
        }
        System.out.println("\n--- SEARCH RESULTS ---");
        for (Alumni a : matches) {
            System.out.printf("ID: %d | Name: %s | Role: %s at %s | Industry: %s | Status: %s\n",
                    a.getId(), a.getName(), a.getJobTitle(), a.getCompany(), a.getIndustry(),
                    a.isWillingToMentor() ? "Willing to Mentor" : "Not Mentoring");
        }
    }

    private static void handleSendRequest(int studentId) {
        System.out.print("Enter Alumni ID to request mentorship: ");
        int alumId = 0;
        try {
            alumId = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
            return;
        }

        Alumni mentor = alumniService.getAlumniProfile(alumId);
        if (mentor == null || !mentor.isWillingToMentor()) {
            System.out.println("This alumni is not registered or not currently taking mentorship requests.");
            return;
        }

        System.out.print("Write a brief request message/note: ");
        String notes = scanner.nextLine().trim();

        if (requestService.sendMentorshipRequest(studentId, alumId, notes)) {
            System.out.println("Mentorship request sent successfully!");
        } else {
            System.out.println("Failed to send request.");
        }
    }

    private static void handleViewStudentRequests(int studentId) {
        List<MentorshipRequest> reqs = requestService.getStudentRequests(studentId);
        if (reqs.isEmpty()) {
            System.out.println("You have not sent any mentorship requests.");
            return;
        }
        System.out.println("\n--- MY SENT MENTORSHIP REQUESTS ---");
        for (MentorshipRequest r : reqs) {
            System.out.printf("Req ID: %d | Mentor: %s (ID: %d) | Date: %s | Status: %s\n",
                    r.getId(), r.getAlumniName(), r.getAlumniId(), r.getRequestDate().toString(), r.getStatus());
            System.out.println("  Note: " + r.getNotes());
        }
    }

    private static void listEvents() {
        List<Event> events = eventService.listUpcomingEvents();
        if (events.isEmpty()) {
            System.out.println("No upcoming events are listed.");
            return;
        }
        System.out.println("\n--- UPCOMING NETWORKING EVENTS ---");
        for (Event e : events) {
            System.out.printf("Event ID: %d | Title: %s\n", e.getId(), e.getTitle());
            System.out.println("  Description: " + e.getDescription());
            System.out.println("  Date/Time: " + e.getDate().format(formatter) + " | Location: " + e.getLocation());
            System.out.println("  Organizer: " + (e.getOrganizerName() != null ? e.getOrganizerName() : "Admin"));
            System.out.println("  ----------------------------------------------");
        }
    }

    private static void handleEventRsvp(int userId) {
        System.out.print("Enter Event ID to RSVP: ");
        int eventId = 0;
        try {
            eventId = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid Event ID.");
            return;
        }

        if (eventService.registerForEvent(eventId, userId)) {
            System.out.println("Successfully RSVP'd for the event!");
        } else {
            System.out.println("RSVP failed. Make sure the Event ID is valid.");
        }
    }

    private static void handleViewStudentRegisteredEvents(int studentId) {
        List<Event> events = eventService.getRegisteredEvents(studentId);
        if (events.isEmpty()) {
            System.out.println("You have not RSVP'd for any upcoming events.");
            return;
        }
        System.out.println("\n--- MY REGISTERED EVENTS ---");
        for (Event e : events) {
            System.out.printf("Event ID: %d | %s | Date: %s | Location: %s\n",
                    e.getId(), e.getTitle(), e.getDate().format(formatter), e.getLocation());
        }
    }

    // ==========================================
    // ALUMNI DASHBOARD
    // ==========================================
    private static void showAlumniMenu(int alumniId) {
        while (true) {
            System.out.println("\n--- ALUMNI DASHBOARD ---");
            System.out.println("1. View/Update My Profile");
            System.out.println("2. View Mentorship Requests");
            System.out.println("3. Respond to Mentorship Request");
            System.out.println("4. Create a Networking Event");
            System.out.println("5. View Upcoming Events");
            System.out.println("6. Logout");
            System.out.print("Select an option (1-6): ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    handleAlumniProfile(alumniId);
                    break;
                case "2":
                    handleViewAlumniRequests(alumniId);
                    break;
                case "3":
                    handleRespondToRequest(alumniId);
                    break;
                case "4":
                    handleCreateEvent(alumniId);
                    break;
                case "5":
                    listEvents();
                    break;
                case "6":
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void handleAlumniProfile(int alumniId) {
        Alumni alumni = alumniService.getAlumniProfile(alumniId);
        if (alumni == null) {
            System.out.println("Error: Alumni profile details not found.");
            return;
        }

        System.out.println("\n--- MY PROFILE ---");
        System.out.println("Name: " + alumni.getName());
        System.out.println("Email: " + alumni.getEmail());
        System.out.println("Job Title: " + alumni.getJobTitle());
        System.out.println("Company: " + alumni.getCompany());
        System.out.println("Industry: " + alumni.getIndustry());
        System.out.println("Years of Experience: " + alumni.getYearsOfExperience());
        System.out.println("Willing to Mentor: " + (alumni.isWillingToMentor() ? "Yes" : "No"));
        System.out.println("------------------");
        System.out.print("Do you want to update your details? (y/n): ");

        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            System.out.print("Enter New Name (leave blank to keep current): ");
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) alumni.setName(input);

            System.out.print("Enter New Password (leave blank to keep current): ");
            input = scanner.nextLine().trim();
            if (!input.isEmpty()) alumni.setPassword(input);

            System.out.print("Enter New Job Title (leave blank to keep): ");
            input = scanner.nextLine().trim();
            if (!input.isEmpty()) alumni.setJobTitle(input);

            System.out.print("Enter New Company (leave blank to keep): ");
            input = scanner.nextLine().trim();
            if (!input.isEmpty()) alumni.setCompany(input);

            System.out.print("Enter New Industry (leave blank to keep): ");
            input = scanner.nextLine().trim();
            if (!input.isEmpty()) alumni.setIndustry(input);

            System.out.print("Are you willing to mentor students? (y/n): ");
            alumni.setWillingToMentor(scanner.nextLine().trim().equalsIgnoreCase("y"));

            if (alumniService.updateProfile(alumni)) {
                System.out.println("Profile updated successfully!");
            } else {
                System.out.println("Profile update failed.");
            }
        }
    }

    private static void handleViewAlumniRequests(int alumniId) {
        List<MentorshipRequest> reqs = requestService.getAlumniRequests(alumniId);
        if (reqs.isEmpty()) {
            System.out.println("No mentorship requests received.");
            return;
        }
        System.out.println("\n--- RECEIVED MENTORSHIP REQUESTS ---");
        for (MentorshipRequest r : reqs) {
            System.out.printf("Req ID: %d | Student: %s (ID: %d) | Date: %s | Status: %s\n",
                    r.getId(), r.getStudentName(), r.getStudentId(), r.getRequestDate().toString(), r.getStatus());
            System.out.println("  Note: " + r.getNotes());
        }
    }

    private static void handleRespondToRequest(int alumniId) {
        System.out.print("Enter Request ID to process: ");
        int reqId = 0;
        try {
            reqId = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid Request ID.");
            return;
        }

        System.out.print("Accept this request? (y/n): ");
        boolean accept = scanner.nextLine().trim().equalsIgnoreCase("y");

        if (requestService.respondToRequest(reqId, accept)) {
            System.out.println("Response recorded successfully!");
        } else {
            System.out.println("Failed to process request. Make sure ID is valid and request is PENDING.");
        }
    }

    private static void handleCreateEvent(int organizerId) {
        System.out.println("\n--- CREATE EVENT ---");
        System.out.print("Enter Event Title: ");
        String title = scanner.nextLine().trim();
        System.out.print("Enter Description: ");
        String desc = scanner.nextLine().trim();
        System.out.print("Enter Location: ");
        String loc = scanner.nextLine().trim();

        LocalDateTime date = null;
        while (true) {
            System.out.print("Enter Event Date/Time (yyyy-MM-dd HH:mm): ");
            String dateStr = scanner.nextLine().trim();
            try {
                date = LocalDateTime.parse(dateStr, formatter);
                if (date.isBefore(LocalDateTime.now())) {
                    System.out.println("Event date must be in the future.");
                    continue;
                }
                break;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid format. Please use 'yyyy-MM-dd HH:mm' (e.g. 2026-06-25 14:00).");
            }
        }

        if (eventService.createEvent(title, desc, date, loc, organizerId)) {
            System.out.println("Event created successfully!");
        } else {
            System.out.println("Failed to create event.");
        }
    }

    // ==========================================
    // ADMIN DASHBOARD
    // ==========================================
    private static void showAdminMenu(User admin) {
        while (true) {
            System.out.println("\n--- ADMIN DASHBOARD ---");
            System.out.println("1. View All Registered Users");
            System.out.println("2. Create a Networking Event");
            System.out.println("3. View Upcoming Events");
            System.out.println("4. View Event Attendees");
            System.out.println("5. Logout");
            System.out.print("Select an option (1-5): ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    handleViewAllUsers();
                    break;
                case "2":
                    handleCreateEvent(admin.getId());
                    break;
                case "3":
                    listEvents();
                    break;
                case "4":
                    handleViewEventAttendees();
                    break;
                case "5":
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void handleViewAllUsers() {
        List<User> list = userService.getUserById(0) != null ? null : new dao.UserDAO().getAllUsers();
        if (list == null || list.isEmpty()) {
            System.out.println("No users registered.");
            return;
        }
        System.out.println("\n--- SYSTEM REGISTERED USERS ---");
        for (User u : list) {
            System.out.printf("ID: %d | Name: %s | Email: %s | Role: %s\n",
                    u.getId(), u.getName(), u.getEmail(), u.getRole());
        }
    }

    private static void handleViewEventAttendees() {
        System.out.print("Enter Event ID: ");
        int eventId = 0;
        try {
            eventId = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
            return;
        }

        List<User> attendees = eventService.getEventAttendees(eventId);
        if (attendees.isEmpty()) {
            System.out.println("No attendees found for this event or event does not exist.");
            return;
        }

        System.out.println("\n--- ATTENDEES LIST ---");
        for (User u : attendees) {
            System.out.printf("ID: %d | Name: %s | Email: %s | Role: %s\n",
                    u.getId(), u.getName(), u.getEmail(), u.getRole());
        }
    }
}

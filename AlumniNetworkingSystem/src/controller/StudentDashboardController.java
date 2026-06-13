package controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import model.Alumni;
import model.Event;
import model.MentorshipRequest;
import model.Student;
import model.User;
import service.AlumniService;
import service.EventService;
import service.RequestService;
import service.StudentService;

import java.util.List;

public class StudentDashboardController {

    @FXML
    private Label welcomeLabel;

    // Tabs
    @FXML
    private VBox profileTab;
    @FXML
    private VBox mentorsTab;
    @FXML
    private VBox requestsTab;
    @FXML
    private VBox eventsTab;

    // Profile Fields
    @FXML
    private TextField nameField;
    @FXML
    private TextField majorField;
    @FXML
    private TextField graduationYearField;
    @FXML
    private TextField statusField;
    @FXML
    private TextField passwordField;
    @FXML
    private Label profileStatusLabel;

    // Mentors search and request
    @FXML
    private TextField mentorSearchField;
    @FXML
    private TableView<Alumni> mentorsTable;
    @FXML
    private TableColumn<Alumni, String> mentorNameCol;
    @FXML
    private TableColumn<Alumni, String> mentorJobCol;
    @FXML
    private TableColumn<Alumni, String> mentorCompCol;
    @FXML
    private TableColumn<Alumni, String> mentorIndCol;
    @FXML
    private TableColumn<Alumni, Integer> mentorExpCol;

    @FXML
    private Label selectedMentorLabel;
    @FXML
    private TextArea requestNotesArea;
    @FXML
    private Label requestStatusLabel;
    @FXML
    private Button sendRequestBtn;

    // Requests Table
    @FXML
    private TableView<MentorshipRequest> requestsTable;
    @FXML
    private TableColumn<MentorshipRequest, Integer> reqIdCol;
    @FXML
    private TableColumn<MentorshipRequest, String> reqAlumniCol;
    @FXML
    private TableColumn<MentorshipRequest, String> reqDateCol;
    @FXML
    private TableColumn<MentorshipRequest, String> reqNotesCol;
    @FXML
    private TableColumn<MentorshipRequest, String> reqStatusCol;

    // Events Tables
    @FXML
    private TableView<Event> allEventsTable;
    @FXML
    private TableColumn<Event, String> eventTitleCol;
    @FXML
    private TableColumn<Event, String> eventDateCol;
    @FXML
    private TableColumn<Event, String> eventLocCol;
    @FXML
    private TableColumn<Event, String> eventOrgCol;

    @FXML
    private Label selectedEventLabel;
    @FXML
    private Button rsvpBtn;
    @FXML
    private Label eventRsvpStatusLabel;

    @FXML
    private TableView<Event> myEventsTable;
    @FXML
    private TableColumn<Event, String> myEventTitleCol;
    @FXML
    private TableColumn<Event, String> myEventDateCol;

    private final StudentService studentService = new StudentService();
    private final AlumniService alumniService = new AlumniService();
    private final RequestService requestService = new RequestService();
    private final EventService eventService = new EventService();

    private Student currentStudent;
    private Alumni selectedMentor;
    private Event selectedEvent;

    @FXML
    public void initialize() {
        User user = SceneManager.getCurrentUser();
        if (user != null) {
            welcomeLabel.setText("Welcome, " + user.getName() + "!");
            currentStudent = studentService.getStudentProfile(user.getId());
        }

        // Setup profile fields
        if (currentStudent != null) {
            nameField.setText(currentStudent.getName());
            majorField.setText(currentStudent.getMajor());
            graduationYearField.setText(String.valueOf(currentStudent.getGraduationYear()));
            statusField.setText(currentStudent.getCurrentStatus());
        }

        // Setup mentors table columns
        mentorNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        mentorJobCol.setCellValueFactory(new PropertyValueFactory<>("jobTitle"));
        mentorCompCol.setCellValueFactory(new PropertyValueFactory<>("company"));
        mentorIndCol.setCellValueFactory(new PropertyValueFactory<>("industry"));
        mentorExpCol.setCellValueFactory(new PropertyValueFactory<>("yearsOfExperience"));

        // Setup mentors table listener
        mentorsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedMentor = newVal;
                selectedMentorLabel.setText("Selected: " + newVal.getName());
                sendRequestBtn.setDisable(false);
            } else {
                selectedMentor = null;
                selectedMentorLabel.setText("No mentor selected");
                sendRequestBtn.setDisable(true);
            }
        });

        // Setup requests table columns
        reqIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        reqAlumniCol.setCellValueFactory(new PropertyValueFactory<>("alumniName"));
        reqDateCol.setCellValueFactory(new PropertyValueFactory<>("requestDate"));
        reqNotesCol.setCellValueFactory(new PropertyValueFactory<>("notes"));
        reqStatusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Setup events table columns
        eventTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        eventDateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        eventLocCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        eventOrgCol.setCellValueFactory(new PropertyValueFactory<>("organizerName"));

        myEventTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        myEventDateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        // Setup events selection listener
        allEventsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedEvent = newVal;
                selectedEventLabel.setText("Selected: " + newVal.getTitle());
                rsvpBtn.setDisable(false);
            } else {
                selectedEvent = null;
                selectedEventLabel.setText("Select an event from the list");
                rsvpBtn.setDisable(true);
            }
        });

        // Load data initially
        loadMentors();
        loadRequests();
        loadEvents();
    }

    private void loadMentors() {
        List<Alumni> list = alumniService.getAvailableMentors();
        mentorsTable.setItems(FXCollections.observableArrayList(list));
    }

    private void loadRequests() {
        if (currentStudent != null) {
            List<MentorshipRequest> list = requestService.getStudentRequests(currentStudent.getId());
            requestsTable.setItems(FXCollections.observableArrayList(list));
        }
    }

    private void loadEvents() {
        List<Event> list = eventService.listUpcomingEvents();
        allEventsTable.setItems(FXCollections.observableArrayList(list));

        if (currentStudent != null) {
            List<Event> registered = eventService.getRegisteredEvents(currentStudent.getId());
            myEventsTable.setItems(FXCollections.observableArrayList(registered));
        }
    }

    @FXML
    void showProfileTab(ActionEvent event) {
        setTabVisible(profileTab);
    }

    @FXML
    void showMentorsTab(ActionEvent event) {
        setTabVisible(mentorsTab);
        loadMentors();
        requestStatusLabel.setVisible(false);
    }

    @FXML
    void showRequestsTab(ActionEvent event) {
        setTabVisible(requestsTab);
        loadRequests();
    }

    @FXML
    void showEventsTab(ActionEvent event) {
        setTabVisible(eventsTab);
        loadEvents();
        eventRsvpStatusLabel.setVisible(false);
    }

    private void setTabVisible(VBox selectedTab) {
        profileTab.setVisible(selectedTab == profileTab);
        mentorsTab.setVisible(selectedTab == mentorsTab);
        requestsTab.setVisible(selectedTab == requestsTab);
        eventsTab.setVisible(selectedTab == eventsTab);
    }

    @FXML
    void handleUpdateProfile(ActionEvent event) {
        if (currentStudent == null) return;

        String name = nameField.getText().trim();
        String major = majorField.getText().trim();
        String gradYearStr = graduationYearField.getText().trim();
        String status = statusField.getText().trim();
        String pass = passwordField.getText().trim();

        if (name.isEmpty() || major.isEmpty() || gradYearStr.isEmpty()) {
            showProfileStatus("Name, Major, and Graduation Year are required.", true);
            return;
        }

        int gradYear;
        try {
            gradYear = Integer.parseInt(gradYearStr);
        } catch (NumberFormatException e) {
            showProfileStatus("Graduation year must be a valid number.", true);
            return;
        }

        currentStudent.setName(name);
        currentStudent.setMajor(major);
        currentStudent.setGraduationYear(gradYear);
        currentStudent.setCurrentStatus(status);
        if (!pass.isEmpty()) {
            currentStudent.setPassword(pass);
        }

        if (studentService.updateProfile(currentStudent)) {
            showProfileStatus("Profile updated successfully!", false);
            passwordField.clear();
        } else {
            showProfileStatus("Failed to update profile.", true);
        }
    }

    private void showProfileStatus(String msg, boolean isError) {
        profileStatusLabel.setText(msg);
        if (isError) {
            profileStatusLabel.setStyle("-fx-text-fill: #ef4444;");
        } else {
            profileStatusLabel.setStyle("-fx-text-fill: #10b981;");
        }
        profileStatusLabel.setVisible(true);
    }

    @FXML
    void handleMentorSearch(ActionEvent event) {
        String query = mentorSearchField.getText().trim();
        List<Alumni> list = alumniService.searchMentors(query);
        mentorsTable.setItems(FXCollections.observableArrayList(list));
    }

    @FXML
    void handleSendRequest(ActionEvent event) {
        if (currentStudent == null || selectedMentor == null) return;

        String notes = requestNotesArea.getText().trim();
        if (notes.isEmpty()) {
            showRequestStatus("Please write a short introductory note.", true);
            return;
        }

        if (requestService.sendMentorshipRequest(currentStudent.getId(), selectedMentor.getId(), notes)) {
            showRequestStatus("Mentorship request sent successfully!", false);
            requestNotesArea.clear();
            loadRequests();
        } else {
            showRequestStatus("Failed to send request. You may have already sent one.", true);
        }
    }

    private void showRequestStatus(String msg, boolean isError) {
        requestStatusLabel.setText(msg);
        if (isError) {
            requestStatusLabel.setStyle("-fx-text-fill: #ef4444;");
        } else {
            requestStatusLabel.setStyle("-fx-text-fill: #10b981;");
        }
        requestStatusLabel.setVisible(true);
    }

    @FXML
    void handleRsvp(ActionEvent event) {
        if (currentStudent == null || selectedEvent == null) return;

        if (eventService.registerForEvent(selectedEvent.getId(), currentStudent.getId())) {
            showRsvpStatus("Successfully RSVP'd to event!", false);
            loadEvents();
        } else {
            showRsvpStatus("Failed to RSVP.", true);
        }
    }

    private void showRsvpStatus(String msg, boolean isError) {
        eventRsvpStatusLabel.setText(msg);
        if (isError) {
            eventRsvpStatusLabel.setStyle("-fx-text-fill: #ef4444;");
        } else {
            eventRsvpStatusLabel.setStyle("-fx-text-fill: #10b981;");
        }
        eventRsvpStatusLabel.setVisible(true);
    }

    @FXML
    void handleLogout(ActionEvent event) {
        SceneManager.setCurrentUser(null);
        SceneManager.switchScene("login.fxml", "Sign In - Alumni Link");
    }
}

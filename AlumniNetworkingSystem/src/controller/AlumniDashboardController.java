package controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Alumni;
import model.Event;
import model.MentorshipRequest;
import model.User;
import service.AlumniService;
import service.EventService;
import service.RequestService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class AlumniDashboardController {

    @FXML
    private Label welcomeLabel;

    // Tabs
    @FXML
    private VBox profileTab;
    @FXML
    private VBox requestsTab;
    @FXML
    private VBox eventsTab;

    // Profile Details
    @FXML
    private TextField nameField;
    @FXML
    private TextField jobTitleField;
    @FXML
    private TextField companyField;
    @FXML
    private TextField industryField;
    @FXML
    private TextField experienceField;
    @FXML
    private CheckBox mentorCheckBox;
    @FXML
    private TextField passwordField;
    @FXML
    private Label profileStatusLabel;

    // Requests Table
    @FXML
    private TableView<MentorshipRequest> requestsTable;
    @FXML
    private TableColumn<MentorshipRequest, Integer> reqIdCol;
    @FXML
    private TableColumn<MentorshipRequest, String> reqStudentCol;
    @FXML
    private TableColumn<MentorshipRequest, String> reqDateCol;
    @FXML
    private TableColumn<MentorshipRequest, String> reqNotesCol;
    @FXML
    private TableColumn<MentorshipRequest, Void> reqActionCol;

    // Events Table & Create Form
    @FXML
    private TableView<Event> eventsTable;
    @FXML
    private TableColumn<Event, String> eventTitleCol;
    @FXML
    private TableColumn<Event, String> eventDateCol;
    @FXML
    private TableColumn<Event, String> eventLocCol;
    @FXML
    private TableColumn<Event, String> eventOrgCol;

    @FXML
    private TextField eventTitleField;
    @FXML
    private TextArea eventDescArea;
    @FXML
    private TextField eventDateField;
    @FXML
    private TextField eventLocField;
    @FXML
    private Label eventStatusLabel;

    private final AlumniService alumniService = new AlumniService();
    private final RequestService requestService = new RequestService();
    private final EventService eventService = new EventService();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private Alumni currentAlumni;

    @FXML
    public void initialize() {
        User user = SceneManager.getCurrentUser();
        if (user != null) {
            welcomeLabel.setText("Welcome, " + user.getName() + "!");
            currentAlumni = alumniService.getAlumniProfile(user.getId());
        }

        // Setup profile fields
        if (currentAlumni != null) {
            nameField.setText(currentAlumni.getName());
            jobTitleField.setText(currentAlumni.getJobTitle());
            companyField.setText(currentAlumni.getCompany());
            industryField.setText(currentAlumni.getIndustry());
            experienceField.setText(String.valueOf(currentAlumni.getYearsOfExperience()));
            mentorCheckBox.setSelected(currentAlumni.isWillingToMentor());
        }

        // Setup columns
        reqIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        reqStudentCol.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        reqDateCol.setCellValueFactory(new PropertyValueFactory<>("requestDate"));
        reqNotesCol.setCellValueFactory(new PropertyValueFactory<>("notes"));
        setupActionButtonsColumn();

        eventTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        eventDateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        eventLocCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        eventOrgCol.setCellValueFactory(new PropertyValueFactory<>("organizerName"));

        // Load tabs initial details
        loadRequests();
        loadEvents();
    }

    private void setupActionButtonsColumn() {
        reqActionCol.setCellFactory(param -> new TableCell<>() {
            private final Button acceptBtn = new Button("Accept");
            private final Button rejectBtn = new Button("Reject");
            private final HBox pane = new HBox(5, acceptBtn, rejectBtn);

            {
                acceptBtn.setOnAction(e -> {
                    MentorshipRequest req = getTableView().getItems().get(getIndex());
                    handleRequestResponse(req.getId(), true);
                });
                rejectBtn.setOnAction(e -> {
                    MentorshipRequest req = getTableView().getItems().get(getIndex());
                    handleRequestResponse(req.getId(), false);
                });
                acceptBtn.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 11px;");
                rejectBtn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 11px;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    MentorshipRequest req = getTableView().getItems().get(getIndex());
                    if ("PENDING".equals(req.getStatus())) {
                        setGraphic(pane);
                    } else {
                        Label lbl = new Label(req.getStatus());
                        lbl.setStyle("ACCEPTED".equals(req.getStatus()) ? "-fx-text-fill: #10b981; -fx-font-weight: bold;" : "-fx-text-fill: #ef4444; -fx-font-weight: bold;");
                        setGraphic(lbl);
                    }
                }
            }
        });
    }

    private void handleRequestResponse(int reqId, boolean accept) {
        if (requestService.respondToRequest(reqId, accept)) {
            loadRequests();
        }
    }

    private void loadRequests() {
        if (currentAlumni != null) {
            List<MentorshipRequest> list = requestService.getAlumniRequests(currentAlumni.getId());
            requestsTable.setItems(FXCollections.observableArrayList(list));
        }
    }

    private void loadEvents() {
        List<Event> list = eventService.listUpcomingEvents();
        eventsTable.setItems(FXCollections.observableArrayList(list));
    }

    @FXML
    void showProfileTab(ActionEvent event) {
        setTabVisible(profileTab);
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
        eventStatusLabel.setVisible(false);
    }

    private void setTabVisible(VBox selectedTab) {
        profileTab.setVisible(selectedTab == profileTab);
        requestsTab.setVisible(selectedTab == requestsTab);
        eventsTab.setVisible(selectedTab == eventsTab);
    }

    @FXML
    void handleUpdateProfile(ActionEvent event) {
        if (currentAlumni == null) return;

        String name = nameField.getText().trim();
        String jobTitle = jobTitleField.getText().trim();
        String company = companyField.getText().trim();
        String industry = industryField.getText().trim();
        String experienceStr = experienceField.getText().trim();
        boolean willing = mentorCheckBox.isSelected();
        String pass = passwordField.getText().trim();

        if (name.isEmpty() || jobTitle.isEmpty() || company.isEmpty() || industry.isEmpty() || experienceStr.isEmpty()) {
            showProfileStatus("Name, Job Title, Company, Industry, and Experience are required.", true);
            return;
        }

        int exp;
        try {
            exp = Integer.parseInt(experienceStr);
        } catch (NumberFormatException e) {
            showProfileStatus("Years of experience must be a number.", true);
            return;
        }

        currentAlumni.setName(name);
        currentAlumni.setJobTitle(jobTitle);
        currentAlumni.setCompany(company);
        currentAlumni.setIndustry(industry);
        currentAlumni.setYearsOfExperience(exp);
        currentAlumni.setWillingToMentor(willing);
        if (!pass.isEmpty()) {
            currentAlumni.setPassword(pass);
        }

        if (alumniService.updateProfile(currentAlumni)) {
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
    void handleCreateEvent(ActionEvent event) {
        String title = eventTitleField.getText().trim();
        String desc = eventDescArea.getText().trim();
        String dateStr = eventDateField.getText().trim();
        String loc = eventLocField.getText().trim();

        if (title.isEmpty() || desc.isEmpty() || dateStr.isEmpty() || loc.isEmpty()) {
            showEventStatus("All fields are required.", true);
            return;
        }

        LocalDateTime ldt;
        try {
            ldt = LocalDateTime.parse(dateStr, formatter);
            if (ldt.isBefore(LocalDateTime.now())) {
                showEventStatus("Event date must be in the future.", true);
                return;
            }
        } catch (DateTimeParseException e) {
            showEventStatus("Invalid date format. Use yyyy-MM-dd HH:mm", true);
            return;
        }

        if (eventService.createEvent(title, desc, ldt, loc, currentAlumni.getId())) {
            showEventStatus("Event published successfully!", false);
            eventTitleField.clear();
            eventDescArea.clear();
            eventDateField.clear();
            eventLocField.clear();
            loadEvents();
        } else {
            showEventStatus("Failed to publish event.", true);
        }
    }

    private void showEventStatus(String msg, boolean isError) {
        eventStatusLabel.setText(msg);
        if (isError) {
            eventStatusLabel.setStyle("-fx-text-fill: #ef4444;");
        } else {
            eventStatusLabel.setStyle("-fx-text-fill: #10b981;");
        }
        eventStatusLabel.setVisible(true);
    }

    @FXML
    void handleLogout(ActionEvent event) {
        SceneManager.setCurrentUser(null);
        SceneManager.switchScene("login.fxml", "Sign In - Alumni Link");
    }
}

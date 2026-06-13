package controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import model.Event;
import model.User;
import service.EventService;
import service.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class AdminDashboardController {

    @FXML
    private VBox usersTab;
    @FXML
    private VBox createEventTab;
    @FXML
    private VBox attendeesTab;

    @FXML
    private Button usersBtn;
    @FXML
    private Button createEventBtn;
    @FXML
    private Button attendeesBtn;

    // Users Tab Table
    @FXML
    private TableView<User> usersTable;
    @FXML
    private TableColumn<User, Integer> userIdCol;
    @FXML
    private TableColumn<User, String> userNameCol;
    @FXML
    private TableColumn<User, String> userEmailCol;
    @FXML
    private TableColumn<User, String> userRoleCol;

    // Create Event Form
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

    // Event Attendees
    @FXML
    private ComboBox<Event> eventComboBox;
    @FXML
    private TableView<User> attendeesTable;
    @FXML
    private TableColumn<User, Integer> attendeeIdCol;
    @FXML
    private TableColumn<User, String> attendeeNameCol;
    @FXML
    private TableColumn<User, String> attendeeEmailCol;

    private final UserService userService = new UserService();
    private final EventService eventService = new EventService();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML
    public void initialize() {
        // Bind columns for Users table
        userIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        userNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        userEmailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        userRoleCol.setCellValueFactory(new PropertyValueFactory<>("role"));

        // Bind columns for Attendees table
        attendeeIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        attendeeNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        attendeeEmailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        // Populate users table
        loadUsers();

        // Populate events dropdown
        loadEvents();
    }

    private void loadUsers() {
        List<User> list = new dao.UserDAO().getAllUsers();
        usersTable.setItems(FXCollections.observableArrayList(list));
    }

    private void loadEvents() {
        List<Event> list = eventService.listUpcomingEvents();
        eventComboBox.setItems(FXCollections.observableArrayList(list));
    }

    @FXML
    void showUsersTab(ActionEvent event) {
        setTabVisible(usersTab);
        loadUsers();
    }

    @FXML
    void showCreateEventTab(ActionEvent event) {
        setTabVisible(createEventTab);
        eventStatusLabel.setVisible(false);
    }

    @FXML
    void showAttendeesTab(ActionEvent event) {
        setTabVisible(attendeesTab);
        loadEvents();
        attendeesTable.getItems().clear();
    }

    private void setTabVisible(VBox selectedTab) {
        usersTab.setVisible(selectedTab == usersTab);
        createEventTab.setVisible(selectedTab == createEventTab);
        attendeesTab.setVisible(selectedTab == attendeesTab);
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

        User admin = SceneManager.getCurrentUser();
        int organizerId = admin != null ? admin.getId() : 1;

        if (eventService.createEvent(title, desc, ldt, loc, organizerId)) {
            showEventStatus("Event published successfully!", false);
            eventTitleField.clear();
            eventDescArea.clear();
            eventDateField.clear();
            eventLocField.clear();
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
    void handleEventSelection(ActionEvent event) {
        Event selectedEvent = eventComboBox.getValue();
        if (selectedEvent != null) {
            List<User> list = eventService.getEventAttendees(selectedEvent.getId());
            attendeesTable.setItems(FXCollections.observableArrayList(list));
        }
    }

    @FXML
    void handleLogout(ActionEvent event) {
        SceneManager.setCurrentUser(null);
        SceneManager.switchScene("login.fxml", "Sign In - Alumni Link");
    }
}

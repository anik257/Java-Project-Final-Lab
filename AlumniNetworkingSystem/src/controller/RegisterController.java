package controller;

import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import service.AlumniService;
import service.StudentService;

public class RegisterController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    private VBox studentSection;

    @FXML
    private TextField majorField;

    @FXML
    private TextField gradYearField;

    @FXML
    private VBox alumniSection;

    @FXML
    private TextField jobTitleField;

    @FXML
    private TextField companyField;

    @FXML
    private TextField industryField;

    @FXML
    private TextField experienceField;

    @FXML
    private CheckBox willingToMentorCheckBox;

    @FXML
    private Label errorLabel;

    @FXML
    private Label successLabel;

    @FXML
    private Button registerButton;

    private final StudentService studentService = new StudentService();
    private final AlumniService alumniService = new AlumniService();

    @FXML
    public void initialize() {
        // Populating role options
        roleComboBox.setItems(FXCollections.observableArrayList("Student", "Alumni"));
        
        // Reset message labels
        errorLabel.setVisible(false);
        successLabel.setVisible(false);
    }

    @FXML
    void handleRoleSelectionChange(ActionEvent event) {
        String selectedRole = roleComboBox.getValue();
        if ("Student".equals(selectedRole)) {
            // Show Student section
            studentSection.setVisible(true);
            studentSection.setManaged(true);
            // Hide Alumni section
            alumniSection.setVisible(false);
            alumniSection.setManaged(false);
        } else if ("Alumni".equals(selectedRole)) {
            // Show Alumni section
            alumniSection.setVisible(true);
            alumniSection.setManaged(true);
            // Hide Student section
            studentSection.setVisible(false);
            studentSection.setManaged(false);
        } else {
            // Hide both
            studentSection.setVisible(false);
            studentSection.setManaged(false);
            alumniSection.setVisible(false);
            alumniSection.setManaged(false);
        }
        
        // Clear errors/success labels on role change
        errorLabel.setVisible(false);
        successLabel.setVisible(false);
    }

    @FXML
    void handleRegister(ActionEvent event) {
        // Clear status labels
        errorLabel.setVisible(false);
        successLabel.setVisible(false);

        // Common Fields
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String role = roleComboBox.getValue();

        // Validate Common Fields
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showError("Full Name, Email, and Password are required fields.");
            return;
        }

        if (role == null || role.isEmpty()) {
            showError("Please select a registration role.");
            return;
        }

        boolean success = false;

        if ("Student".equals(role)) {
            String major = majorField.getText().trim();
            String gradYearStr = gradYearField.getText().trim();

            if (major.isEmpty() || gradYearStr.isEmpty()) {
                showError("Major and Graduation Year are required for students.");
                return;
            }

            int gradYear;
            try {
                gradYear = Integer.parseInt(gradYearStr);
            } catch (NumberFormatException e) {
                showError("Graduation Year must be a valid number.");
                return;
            }

            // Register Student using the service
            success = studentService.registerStudent(name, email, password, major, gradYear);

        } else if ("Alumni".equals(role)) {
            String jobTitle = jobTitleField.getText().trim();
            String company = companyField.getText().trim();
            String industry = industryField.getText().trim();
            String experienceStr = experienceField.getText().trim();
            boolean willingToMentor = willingToMentorCheckBox.isSelected();

            if (jobTitle.isEmpty() || company.isEmpty() || industry.isEmpty() || experienceStr.isEmpty()) {
                showError("Job Title, Company, Industry, and Years of Experience are required for alumni.");
                return;
            }

            int experience;
            try {
                experience = Integer.parseInt(experienceStr);
            } catch (NumberFormatException e) {
                showError("Years of Experience must be a valid number.");
                return;
            }

            // Register Alumni using the service
            success = alumniService.registerAlumni(name, email, password, jobTitle, company, industry, experience, willingToMentor);
        }

        if (success) {
            showSuccess("Registration successful! Redirecting to login page...");
            registerButton.setDisable(true);
            
            // Wait 1.5 seconds and redirect back to login scene
            PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
            pause.setOnFinished(e -> SceneManager.switchScene("login.fxml", "Sign In - Alumni Link"));
            pause.play();
        } else {
            showError("Registration failed. The email address might already be registered.");
        }
    }

    @FXML
    void handleBackToLoginRedirect(ActionEvent event) {
        SceneManager.switchScene("login.fxml", "Sign In - Alumni Link");
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        successLabel.setVisible(false);
    }

    private void showSuccess(String message) {
        successLabel.setText(message);
        successLabel.setVisible(true);
        errorLabel.setVisible(false);
    }
}

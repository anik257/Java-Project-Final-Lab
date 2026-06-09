package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.User;
import service.UserService;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private Button loginButton;

    private final UserService userService = new UserService();

    @FXML
    public void initialize() {
        // Clear any previous error and reset visual state
        errorLabel.setVisible(false);
    }

    @FXML
    void handleLogin(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        // 1. Basic validation checks
        if (email.isEmpty() || password.isEmpty()) {
            showError("Email and password fields cannot be empty.");
            return;
        }

        // Disable button to prevent double clicks during processing
        loginButton.setDisable(true);
        errorLabel.setVisible(false);

        // 2. Call backend Service layer for authentication
        User user = userService.login(email, password);

        if (user != null) {
            // 3. Store the logged in user in the global SceneManager context
            SceneManager.setCurrentUser(user);

            // 4. Redirect based on user roles
            String role = user.getRole();
            System.out.println("[LoginController] User logged in. Role: " + role);
            
            switch (role) {
                case "STUDENT":
                    SceneManager.switchScene("student_dashboard.fxml", "Student Portal - " + user.getName());
                    break;
                case "ALUMNI":
                    SceneManager.switchScene("alumni_dashboard.fxml", "Alumni Portal - " + user.getName());
                    break;
                case "ADMIN":
                    SceneManager.switchScene("admin_dashboard.fxml", "Admin Control Console");
                    break;
                default:
                    showError("Unknown user role profile. Access denied.");
                    loginButton.setDisable(false);
            }
        } else {
            showError("Invalid email address or password combination.");
            loginButton.setDisable(false);
        }
    }

    @FXML
    void handleRegisterRedirect(ActionEvent event) {
        // Switches scene to the registration view screen
        System.out.println("[LoginController] Redirecting to registration screen.");
        SceneManager.switchScene("register.fxml", "Create Account - Alumni Link");
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}

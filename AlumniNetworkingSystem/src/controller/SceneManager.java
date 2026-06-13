package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.User;

public class SceneManager {
    private static Stage primaryStage;
    private static User currentUser;

    /**
     * Set the primary stage reference.
     */
    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    /**
     * Retrieve the primary stage.
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Track the currently logged in user session.
     */
    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    /**
     * Get the currently logged in user details.
     */
    public static User getCurrentUser() {
        return currentUser;
    }

    /**
     * Switch the scene of the application window.
     * @param fxmlFile FXML file name located inside the "/view/" resources package.
     * @param title Title of the stage window.
     * @return true if switch succeeded, false otherwise.
     */
    public static boolean switchScene(String fxmlFile, String title) {
        if (primaryStage == null) {
            System.err.println("[SceneManager] Primary stage is not set.");
            return false;
        }

        try {
            // FXML paths are resolved from the class loader resource root, e.g. "/view/login.fxml"
            String viewPath = "/view/" + fxmlFile;
            var resource = SceneManager.class.getResource(viewPath);
            if (resource == null) {
                // Try relative path fallback
                viewPath = "../view/" + fxmlFile;
                resource = SceneManager.class.getResource(viewPath);
            }

            if (resource == null) {
                throw new java.io.FileNotFoundException("Could not find view file: /view/" + fxmlFile);
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            
            // Set the scene
            Scene scene = primaryStage.getScene();
            if (scene == null) {
                scene = new Scene(root);
                primaryStage.setScene(scene);
            } else {
                scene.setRoot(root);
            }

            primaryStage.setTitle(title);
            primaryStage.show();
            System.out.println("[SceneManager] Switched scene to: " + fxmlFile);
            return true;
        } catch (Exception e) {
            System.err.println("[SceneManager] Failed to load FXML: " + fxmlFile);
            e.printStackTrace();
            return false;
        }
    }
}

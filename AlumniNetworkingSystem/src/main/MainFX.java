package main;

import controller.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainFX extends Application {

    @Override
    public void start(Stage primaryStage) {
        // 1. Pass the primary stage to SceneManager for switching scenes
        SceneManager.setPrimaryStage(primaryStage);
        
        // 2. Set min window dimensions to avoid layouts collapsing
        primaryStage.setMinWidth(800.0);
        primaryStage.setMinHeight(600.0);
        
        // 3. Switch screen to the login screen
        SceneManager.switchScene("login.fxml", "Sign In - Alumni Link");
    }

    public static void main(String[] args) {
        // Launches the JavaFX runtime application
        launch(args);
    }
}

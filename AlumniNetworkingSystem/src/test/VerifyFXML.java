package test;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import java.net.URL;

public class VerifyFXML {
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("     RUNNING FXML LOAD VERIFICATION              ");
        System.out.println("=================================================");
        try {
            // Start JavaFX Toolkit
            try {
                Platform.startup(() -> {});
            } catch (IllegalStateException e) {
                // Toolkit already started
            }
            
            // Check login.fxml
            URL loginUrl = VerifyFXML.class.getResource("/view/login.fxml");
            if (loginUrl == null) {
                throw new RuntimeException("Could not find /view/login.fxml");
            }
            new FXMLLoader(loginUrl).load();
            System.out.println("  - login.fxml loaded successfully!");
            
            // Check register.fxml
            URL registerUrl = VerifyFXML.class.getResource("/view/register.fxml");
            if (registerUrl == null) {
                throw new RuntimeException("Could not find /view/register.fxml");
            }
            new FXMLLoader(registerUrl).load();
            System.out.println("  - register.fxml loaded successfully!");

            // Check student_dashboard.fxml
            URL studentDbUrl = VerifyFXML.class.getResource("/view/student_dashboard.fxml");
            if (studentDbUrl == null) {
                throw new RuntimeException("Could not find /view/student_dashboard.fxml");
            }
            new FXMLLoader(studentDbUrl).load();
            System.out.println("  - student_dashboard.fxml loaded successfully!");

            // Check alumni_dashboard.fxml
            URL alumniDbUrl = VerifyFXML.class.getResource("/view/alumni_dashboard.fxml");
            if (alumniDbUrl == null) {
                throw new RuntimeException("Could not find /view/alumni_dashboard.fxml");
            }
            new FXMLLoader(alumniDbUrl).load();
            System.out.println("  - alumni_dashboard.fxml loaded successfully!");

            // Check admin_dashboard.fxml
            URL adminDbUrl = VerifyFXML.class.getResource("/view/admin_dashboard.fxml");
            if (adminDbUrl == null) {
                throw new RuntimeException("Could not find /view/admin_dashboard.fxml");
            }
            new FXMLLoader(adminDbUrl).load();
            System.out.println("  - admin_dashboard.fxml loaded successfully!");
            
            System.out.println("\n=================================================");
            System.out.println("     FXML VERIFICATION PASSED SUCCESSFULLY!       ");
            System.out.println("=================================================");
            System.exit(0);
        } catch (Exception e) {
            System.err.println("\n[Failure] FXML Verification failed!");
            e.printStackTrace();
            System.exit(1);
        }
    }
}

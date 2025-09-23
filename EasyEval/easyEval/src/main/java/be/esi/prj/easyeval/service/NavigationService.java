package be.esi.prj.easyeval.service;

import be.esi.prj.easyeval.fxmlcontroller.SubmissionController;
import be.esi.prj.easyeval.model.Course;
import be.esi.prj.easyeval.model.ExamDocument;
import be.esi.prj.easyeval.fxmlcontroller.CourseManagerController;
import be.esi.prj.easyeval.fxmlcontroller.DocumentManagerController;
import be.esi.prj.easyeval.fxmlcontroller.DocumentCreatorController;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Service class to centralize navigation between different views in the application.
 * This class handles loading FXML files, setting up controllers, and managing stage transitions.
 */
public class NavigationService {

    private static NavigationService instance;
    private Stage primaryStage;

    /**
     * Private constructor for singleton pattern.
     */
    private NavigationService() {
    }

    /**
     * Get the singleton instance of the NavigationService.
     * @return The NavigationService instance
     */
    public static NavigationService getInstance() {
        if (instance == null) {
            instance = new NavigationService();
        }
        return instance;
    }

    /**
     * Set the primary stage for the application.
     * @param stage The primary JavaFX stage
     */
    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    /**
     * Navigate to the Course Manager view.
     */
    public void navigateToCourseManager() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/be/esi/prj/easyeval/fxml/course_manager-view.fxml"));
            Parent root = loader.load();

            CourseManagerController controller = loader.getController();

            setupScene(root, "easyEval - Course Manager");

        } catch (IOException e) {
            showErrorAlert("Navigation Error", "Could not navigate to Course Manager: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Navigate to the Document Manager view for a specific course.
     * @param course The course to display documents for
     */
    public void navigateToDocumentManager(Course course) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/be/esi/prj/easyeval/fxml/document_manager-view.fxml"));
            Parent root = loader.load();

            DocumentManagerController controller = loader.getController();
            controller.initData(course);

            setupScene(root, "easyEval - " + course.getName() + " Exams");

        } catch (IOException e) {
            showErrorAlert("Navigation Error", "Could not navigate to Document Manager: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Navigate to the Document Creator view for editing an exam document.
     * @param examDocument The exam document to edit
     */
    public void navigateToDocumentCreator(ExamDocument examDocument) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/be/esi/prj/easyeval/fxml/document_creator-view.fxml"));
            Parent root = loader.load();

            DocumentCreatorController controller = loader.getController();
            controller.initData(examDocument);

            String title = examDocument.getId() == null ?
                    "easyEval - New Exam" :
                    "easyEval - Edit " + examDocument.getTitle();

            setupScene(root, title);

        } catch (IOException e) {
            showErrorAlert("Navigation Error", "Could not navigate to Document Creator: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Navigate to the Submissions view for a specific exam.
     * @param examDocument The exam document to display submissions for
     */
    public void navigateToSubmissions(ExamDocument examDocument) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/be/esi/prj/easyeval/fxml/submission-view.fxml"));
            Parent root = loader.load();

            SubmissionController controller = loader.getController();
            controller.initData(examDocument);

            String title = "easyEval - " + examDocument.getTitle() + " Submissions";
            setupScene(root, title);

        } catch (IOException e) {
            showErrorAlert("Navigation Error", "Could not navigate to Submissions: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Helper method to set up the scene with the loaded FXML root.
     * @param root The root node of the FXML
     * @param title The title for the stage
     */
    private void setupScene(Parent root, String title) {
        if (primaryStage == null) {
            showErrorAlert("Navigation Error", "Primary stage not set");
            return;
        }

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle(title);

        primaryStage.show();
    }

    /**
     * Shows an error alert with the given title and message.
     */
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
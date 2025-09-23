package be.esi.prj.easyeval.fxmlcontroller;

import be.esi.prj.easyeval.model.Course;
import be.esi.prj.easyeval.service.NavigationService;
import be.esi.prj.easyeval.viewmodel.CourseManagerViewModel;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Controller for the QCM Manager view, which displays and manages courses.
 */
public class CourseManagerController {

    @FXML
    private GridPane coursesGrid;

    @FXML
    private Button addCourseBtn;

    @FXML
    public Button deleteAllCoursesBtn;

    @FXML
    private TextField searchField;

    private CourseManagerViewModel viewModel;
    private NavigationService navigationService;

    /**
     * Initializes the controller.
     * This method is automatically called after the FXML file has been loaded.
     */
    @FXML
    public void initialize() {
        viewModel = new CourseManagerViewModel();
        navigationService = NavigationService.getInstance();

        // Set listener for search bar
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.trim().isEmpty()) {
                viewModel.fetchAllCourses();
            }
        });
        refreshCoursesGrid();

        // Set listener for course list
        viewModel.getCourseList().addListener((ListChangeListener.Change<? extends Course> change) -> {
            Platform.runLater(this::refreshCoursesGrid);
        });
    }

    /**
     * Refreshes the courses grid with current data from the ViewModel.
     */
    private void refreshCoursesGrid() {
        coursesGrid.getChildren().clear();

        int column = 0;
        int row = 0;
        final int ITEMS_PER_ROW = 5;

        for (Course course : viewModel.getCourseList()) {
            VBox courseCard = createCourseCard(course);
            coursesGrid.add(courseCard, column, row);

            column++;
            if (column == ITEMS_PER_ROW) {
                column = 0;
                row++;
            }
        }

        // If no courses, show a message
        if (viewModel.getCourseList().isEmpty()) {
            Label noCoursesLabel = new Label("No courses found. Click 'Add Course' to create one.");
            noCoursesLabel.getStyleClass().add("no-content-message");
            coursesGrid.add(noCoursesLabel, 0, 0, ITEMS_PER_ROW, 1);
        }
    }

    /**
     * Creates a card-like VBox for displaying a course.
     * This method was made with the help of AI (claude.ai)
     */
    private VBox createCourseCard(Course course) {
        VBox card = new VBox(10);
        card.getStyleClass().add("course-card");
        card.setPadding(new Insets(15));
        card.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(8), Insets.EMPTY)));
        card.setBorder(new Border(new BorderStroke(Color.LIGHTGRAY, BorderStrokeStyle.SOLID, new CornerRadii(8), new BorderWidths(1))));

        // Course name
        Label nameLabel = new Label(course.getName());
        nameLabel.getStyleClass().add("card-title");

        // Creation date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Label dateLabel = new Label("Created: " + course.getCreatedAt().format(formatter));
        dateLabel.getStyleClass().add("card-subtitle");

        // Actions row
        HBox actions = new HBox(10);
        actions.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

        Button viewBtn = new Button("Open");
        viewBtn.getStyleClass().add("button-primary");
        viewBtn.setOnAction(e -> handleCourseSelection(course));

        Button deleteBtn = new Button("Delete");
        deleteBtn.getStyleClass().add("button-danger");
        deleteBtn.setOnAction(e -> handleDeleteCourse(course));

        actions.getChildren().addAll(viewBtn, deleteBtn);

        // Add components to card
        card.getChildren().addAll(nameLabel, dateLabel, new Separator(), actions);

        // Make the whole card clickable
        card.setOnMouseClicked(e -> handleCourseSelection(course));

        return card;
    }

    /**
     * Handles the action when the Add Course button is clicked.
     */
    @FXML
    private void handleAddCourse() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add New Course");
        dialog.setHeaderText("Create a new course");
        dialog.setContentText("Course name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            Course newCourse = viewModel.addCourse(name);
            if (newCourse != null) {
                viewModel.setSelectedCourse(newCourse);

                // Optionally navigate to the course's exams directly
                // handleCourseSelection(newCourse);
            }
            else {
                handleAddCourse();
            }
        });
    }

    /**
     * Handles the action when a course is selected.
     */
    private void handleCourseSelection(Course course) {
        viewModel.setSelectedCourse(course);
        navigationService.navigateToDocumentManager(course);
    }

    /**
     * Handles the deletion of a course.
     */
    private void handleDeleteCourse(Course course) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Deletion");
        confirmDialog.setHeaderText("Delete Course");
        confirmDialog.setContentText("Are you sure you want to delete the course '" + course.getName() + "'? This action cannot be undone.");

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean deleted = viewModel.deleteCourse(course.getId());
            if (deleted) {
                // Course was deleted, refresh the grid
                refreshCoursesGrid();
            }
        }
    }

    @FXML
    private void handleDeleteAllCourses() {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Deletion All Courses");
        confirmDialog.setHeaderText("Delete All Courses");
        confirmDialog.setContentText("Are you sure you want to delete all courses? This action cannot be undone.");

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean deleted = viewModel.deleteAllCourses();
            if (deleted) {
                refreshCoursesGrid();
            }
        }
    }

    /**
     * Handles the search action.
     */
    @FXML
    private void handleSearch() {
        String searchText = searchField.getText();
        viewModel.searchCourses(searchText);
    }

    /**
     * Handles clearing the search.
     */
    @FXML
    private void handleClearSearch() {
        searchField.clear();
        viewModel.fetchAllCourses();
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
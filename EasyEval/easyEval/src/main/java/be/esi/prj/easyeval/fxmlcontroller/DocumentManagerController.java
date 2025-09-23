package be.esi.prj.easyeval.fxmlcontroller;

import be.esi.prj.easyeval.model.Course;
import be.esi.prj.easyeval.model.ExamDocument;
import be.esi.prj.easyeval.service.NavigationService;
import be.esi.prj.easyeval.viewmodel.DocumentManagerViewModel;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Controller for the Document Manager view, which displays and manages exam documents for a course.
 */
public class DocumentManagerController {

    @FXML
    private Label courseNameLabel;

    @FXML
    private GridPane examsGrid;

    @FXML
    private Button backButton;

    @FXML
    private Button addExamButton;

    @FXML
    private TextField searchField;

    private DocumentManagerViewModel viewModel;
    private NavigationService navigationService;

    private Course course;

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {
        viewModel = new DocumentManagerViewModel();
        navigationService = NavigationService.getInstance();
    }

    /**
     * Initializes the controller with a course.
     * @param course The course to display exams for
     */
    public void initData(Course course) {
        this.course = course;
        courseNameLabel.setText(course.getName() + " - Exams");

        viewModel.initialize(course);

        // Set listener for the exam list to refresh the grid when it changes
        viewModel.getExamList().addListener((ListChangeListener.Change<? extends ExamDocument> change) -> {
            Platform.runLater(this::refreshExamsGrid);
        });

        refreshExamsGrid();
    }

    /**
     * Refreshes the exams grid with current data from the ViewModel.
     */
    private void refreshExamsGrid() {
        examsGrid.getChildren().clear();

        int column = 0;
        int row = 0;
        final int ITEMS_PER_ROW = 5;

        for (ExamDocument exam : viewModel.getExamList()) {
            VBox examCard = createExamCard(exam);
            examsGrid.add(examCard, column, row);

            column++;
            if (column == ITEMS_PER_ROW) {
                column = 0;
                row++;
            }
        }

        // If no exams, show a message
        if (viewModel.getExamList().isEmpty()) {
            Label noExamsLabel = new Label("No exams found for this course. Click 'Create New Exam' to get started.");
            noExamsLabel.getStyleClass().add("no-content-message");
            examsGrid.add(noExamsLabel, 0, 0, ITEMS_PER_ROW, 1);
        }
    }
    /**
     * Creates a card-like VBox for displaying an exam.
     */
    private VBox createExamCard(ExamDocument exam) {
        VBox card = new VBox(10);
        card.getStyleClass().add("exam-card");
        card.setPadding(new Insets(15));
        card.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(8), Insets.EMPTY)));
        card.setBorder(new Border(new BorderStroke(Color.LIGHTGRAY, BorderStrokeStyle.SOLID, new CornerRadii(8), new BorderWidths(1))));

        // Exam title
        Label titleLabel = new Label(exam.getTitle());
        titleLabel.getStyleClass().add("card-title");

        // Number of questions
        String questionCount = (exam.getQuestions() != null ? exam.getQuestions().size() : 0) + " question(s)";
        Label questionsLabel = new Label(questionCount);
        questionsLabel.getStyleClass().add("card-subtitle");

        // Creation date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Label dateLabel = new Label("Created: " + exam.getCreatedAt().format(formatter));
        dateLabel.getStyleClass().add("card-subtitle");

        // Actions row 1
        HBox actions1 = new HBox(10);
        actions1.setAlignment(javafx.geometry.Pos.CENTER);

        Button editBtn = new Button("Edit");
        editBtn.getStyleClass().add("button-primary");
        editBtn.setOnAction(e -> handleEditExam(exam));

        Button printBtn = new Button("Print");
        printBtn.getStyleClass().add("button-secondary");
        printBtn.setOnAction(e -> handlePrintExam(exam));

        Button deleteBtn = new Button("Delete");
        deleteBtn.getStyleClass().add("button-danger");
        deleteBtn.setOnAction(e -> handleDeleteExam(exam));

        actions1.getChildren().addAll(editBtn, printBtn, deleteBtn);

        // Actions row 2
        HBox actions2 = new HBox(10);
        actions2.setAlignment(javafx.geometry.Pos.CENTER);

        Button printWithAnswersBtn = new Button("Print with Answers");
        printWithAnswersBtn.getStyleClass().add("button-secondary");
        printWithAnswersBtn.setOnAction(e -> handlePrintExamWithAnswers(exam));

        Button submissionsBtn = new Button("Submissions & OCR");
        submissionsBtn.getStyleClass().add("button-primary");
        submissionsBtn.setOnAction(e -> handleOpenSubmissions(exam));

        actions2.getChildren().addAll(printWithAnswersBtn, submissionsBtn);

        // Add components to card
        card.getChildren().addAll(titleLabel, questionsLabel, dateLabel, new Separator(), actions1, actions2);

        // Make the whole card clickable to edit
        card.setOnMouseClicked(e -> handleEditExam(exam));

        return card;
    }

    /**
     * Handles navigating to the Submissions view for an exam.
     */
    private void handleOpenSubmissions(ExamDocument exam) {
        viewModel.setSelectedExam(exam);
        navigationService.navigateToSubmissions(exam);
    }

    /**
     * Handles editing an exam document.
     */
    private void handleEditExam(ExamDocument exam) {
        viewModel.setSelectedExam(exam);
        navigationService.navigateToDocumentCreator(exam);
    }

    /**
     * Handles the action when the Add Exam button is clicked.
     * Method made with the help of AI
     */
    @FXML
    private void handleAddExam() {
        Dialog<ExamDocument> dialog = new Dialog<>();
        dialog.setTitle("Create New Exam");
        dialog.setHeaderText("Enter exam details");

        // Set the button types
        ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        // Create the form grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Create the form fields
        TextField titleField = new TextField();
        titleField.setPromptText("Enter exam title");
        titleField.setPrefWidth(300);

        TextArea instructionsArea = new TextArea();
        instructionsArea.setPromptText("Enter instructions (optional)");
        instructionsArea.setPrefRowCount(3);

        // Add fields to the grid
        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Instructions:"), 0, 1);
        grid.add(instructionsArea, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Enable/Disable create button depending on whether a title was entered
        Button createButton = (Button) dialog.getDialogPane().lookupButton(createButtonType);
        createButton.setDisable(true);

        // Enable button when text is entered
        titleField.textProperty().addListener((observable, oldValue, newValue) -> {
            createButton.setDisable(newValue.trim().isEmpty());
        });

        // Request focus on the title field by default
        Platform.runLater(titleField::requestFocus);

        // Convert the result to an ExamDocument when the create button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                return viewModel.createExam(titleField.getText(), instructionsArea.getText());
            }
            return null;
        });

        Optional<ExamDocument> result = dialog.showAndWait();
        result.ifPresent(exam -> {
            // Navigate to exam editor
            navigationService.navigateToDocumentCreator(exam);
        });
    }

    /**
     * Handles printing an exam without answers.
     */
    private void handlePrintExam(ExamDocument exam) {
        File pdfFile = viewModel.generateExamPDF(exam);
        if (pdfFile != null) {
            openPDF(pdfFile);
        }
    }

    /**
     * Handles printing an exam with answers.
     */
    private void handlePrintExamWithAnswers(ExamDocument exam) {
        File pdfFile = viewModel.generateExamWithAnswersPDF(exam);
        if (pdfFile != null) {
            openPDF(pdfFile);
        }
    }

    /**
     * Opens a PDF file using the system's default PDF viewer.
     */
    private void openPDF(File pdfFile) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(pdfFile);
            } else {
                showInfoAlert("PDF Generated", "PDF has been generated at: " + pdfFile.getAbsolutePath());
            }
        } catch (IOException e) {
            showErrorAlert("Error Opening PDF", "Could not open the generated PDF: " + e.getMessage());
        }
    }

    /**
     * Handles the deletion of an exam.
     */
    private void handleDeleteExam(ExamDocument exam) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Deletion");
        confirmDialog.setHeaderText("Delete Exam");
        confirmDialog.setContentText("Are you sure you want to delete the exam '" + exam.getTitle() + "'? This action cannot be undone.");

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean deleted = viewModel.deleteExam(exam.getId());
            if (deleted) {
                refreshExamsGrid();
            }
        }
    }

    /**
     * Handles searching for exams by title.
     */
    @FXML
    private void handleSearch() {
        // This would typically filter the exams by title
        // For now, just refresh to show all exams
        String searchText = searchField.getText();
        if (searchText.isEmpty()) {
            viewModel.fetchExamsForCourse(course.getId());
        } else {
            // For now, just show all exams
            showInfoAlert("Search", "Would filter exams by: " + searchText);
        }
    }

    /**
     * Handles clearing the search field.
     */
    @FXML
    private void handleClearSearch() {
        searchField.clear();
        viewModel.fetchExamsForCourse(course.getId());
    }

    /**
     * Handles navigating back to the Course Manager view.
     */
    @FXML
    private void handleBack() {
        navigationService.navigateToCourseManager();
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

    /**
     * Shows an info alert with the given title and message.
     */
    private void showInfoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
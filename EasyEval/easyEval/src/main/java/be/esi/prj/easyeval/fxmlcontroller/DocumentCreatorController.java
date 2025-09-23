package be.esi.prj.easyeval.fxmlcontroller;

import be.esi.prj.easyeval.model.ExamDocument;
import be.esi.prj.easyeval.model.Question;
import be.esi.prj.easyeval.repository.CourseRepository;
import be.esi.prj.easyeval.service.NavigationService;
import be.esi.prj.easyeval.viewmodel.DocumentCreatorViewModel;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Optional;

/**
 * Controller for the Document Creator view, which allows creating and editing exam documents.
 */
public class DocumentCreatorController {

    @FXML
    private TextField titleField;

    @FXML
    private TextArea instructionsArea;

    @FXML
    private TextField questionField;

    @FXML
    private TextField answerField;

    @FXML
    private Button addQuestionBtn;

    @FXML
    private Button updateQuestionBtn;

    @FXML
    private Button clearFormBtn;

    @FXML
    private Button saveExamBtn;

    @FXML
    private Label questionCountLabel;

    @FXML
    private TableView<Question> questionsTable;

    @FXML
    private TableColumn<Question, String> questionTextColumn;

    @FXML
    private TableColumn<Question, String> answerColumn;

    @FXML
    private Button editQuestionBtn;

    @FXML
    private Button removeQuestionBtn;

    private DocumentCreatorViewModel viewModel;
    private NavigationService navigationService;

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {
        viewModel = new DocumentCreatorViewModel();
        navigationService = NavigationService.getInstance();

        // Set up table columns
        questionTextColumn.setCellValueFactory(new PropertyValueFactory<>("text"));
        answerColumn.setCellValueFactory(new PropertyValueFactory<>("correctAnswer"));

        // Bind question count label
        questionCountLabel.textProperty().bind(
                Bindings.size(viewModel.getQuestionList()).asString("%d questions"));

        // Bind text fields to ViewModel properties
        titleField.textProperty().bindBidirectional(viewModel.examTitleProperty());
        instructionsArea.textProperty().bindBidirectional(viewModel.examInstructionsProperty());
        questionField.textProperty().bindBidirectional(viewModel.questionTextProperty());
        answerField.textProperty().bindBidirectional(viewModel.correctAnswerProperty());

        // Bind table to ViewModel
        questionsTable.setItems(viewModel.getQuestionList());

        // Bind selection to ViewModel
        questionsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                editQuestionBtn.setDisable(false);
                removeQuestionBtn.setDisable(false);
            } else {
                editQuestionBtn.setDisable(true);
                removeQuestionBtn.setDisable(true);
            }
        });

        // Update button state based on whether we're in edit mode
        SimpleBooleanProperty editMode = new SimpleBooleanProperty(false);
        viewModel.selectedQuestionProperty().addListener((obs, oldVal, newVal) -> {
            editMode.set(newVal != null);
        });

        updateQuestionBtn.disableProperty().bind(editMode.not());
        addQuestionBtn.disableProperty().bind(editMode);

        // Initially disable edit/remove buttons
        editQuestionBtn.setDisable(true);
        removeQuestionBtn.setDisable(true);
    }

    /**
     * Initializes the controller with an exam document.
     * @param examDocument The exam document to edit, or null for a new exam
     */
    public void initData(ExamDocument examDocument) {
        viewModel.initialize(examDocument);
    }

    /**
     * Handles adding a new question.
     */
    @FXML
    private void handleAddQuestion() {
        Question addedQuestion = viewModel.addQuestion();
        if (addedQuestion != null) {
            questionsTable.getSelectionModel().select(addedQuestion);
            questionsTable.scrollTo(addedQuestion);

            questionField.requestFocus();
        }
    }

    /**
     * Handles updating an existing question.
     */
    @FXML
    private void handleUpdateQuestion() {
        Question updatedQuestion = viewModel.updateQuestion();
        if (updatedQuestion != null) {
            // Refresh selection
            questionsTable.getSelectionModel().select(updatedQuestion);
            questionsTable.scrollTo(updatedQuestion);

            questionField.requestFocus();
        }
    }

    /**
     * Handles clearing the question form.
     */
    @FXML
    private void handleClearForm() {
        viewModel.clearQuestionForm();
        questionField.requestFocus();
    }

    /**
     * Handles editing a selected question.
     */
    @FXML
    private void handleEditQuestion() {
        Question selectedQuestion = questionsTable.getSelectionModel().getSelectedItem();
        if (selectedQuestion != null) {
            viewModel.editQuestion(selectedQuestion);
            questionField.requestFocus();
        }
    }

    /**
     * Handles removing a selected question.
     */
    @FXML
    private void handleRemoveQuestion() {
        Question selectedQuestion = questionsTable.getSelectionModel().getSelectedItem();
        if (selectedQuestion != null) {
            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("Confirm Deletion");
            confirmDialog.setHeaderText("Delete Question");
            confirmDialog.setContentText("Are you sure you want to delete this question?");

            Optional<ButtonType> result = confirmDialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                viewModel.removeQuestion(selectedQuestion);
            }
        }
    }

    /**
     * Handles saving the exam document.
     */
    @FXML
    private void handleSaveExam() {
        ExamDocument savedExam = viewModel.saveExam();
        if (savedExam != null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText("Exam Saved");
            alert.setContentText("The exam '" + savedExam.getTitle() + "' has been saved successfully.");
            alert.showAndWait();

            // Navigate back to document manager
            handleBack();
        }
    }

    /**
     * Handles navigating back to the Document Manager view.
     */
    @FXML
    private void handleBack() {
        // Show confirmation if there are unsaved changes
        if (!isSaved()) {
            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("Unsaved Changes");
            confirmDialog.setHeaderText("You have unsaved changes");
            confirmDialog.setContentText("Do you want to save your changes before leaving?");

            ButtonType saveButton = new ButtonType("Save");
            ButtonType dontSaveButton = new ButtonType("Don't Save");
            ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

            confirmDialog.getButtonTypes().setAll(saveButton, dontSaveButton, cancelButton);

            Optional<ButtonType> result = confirmDialog.showAndWait();
            if (result.isPresent()) {
                if (result.get() == saveButton) {
                    ExamDocument savedExam = viewModel.saveExam();
                    if (savedExam == null) {
                        return;
                    }
                } else if (result.get() == cancelButton) {
                    return;
                }
            }
        }

        // Navigate back to the DocumentManager for the course
        if (viewModel.getCurrentExam() != null && viewModel.getCurrentExam().getCourseId() != null) {
            // We need to get the Course object from the courseId
            CourseRepository courseRepository = new CourseRepository();
            try {
                Long courseId = viewModel.getCurrentExam().getCourseId();
                courseRepository.findById(courseId).ifPresent(course ->
                        navigationService.navigateToDocumentManager(course)
                );
            } catch (Exception e) {
                showErrorAlert("Navigation Error", "Could not navigate back: " + e.getMessage());
                navigationService.navigateToCourseManager();
            }
        } else {
            // If we don't have a courseId, just go back to the CourseManager
            navigationService.navigateToCourseManager();
        }
    }

    /**
     * Checks if there are unsaved changes to the exam document.
     * @return true if all changes are saved, false if there are unsaved changes
     */
    private boolean isSaved() {
        ExamDocument currentExam = viewModel.getCurrentExam();

        // If we're creating a new exam and have entered data, it's unsaved
        if (currentExam.getId() == null) {
            boolean hasTitle = !titleField.getText().trim().isEmpty();
            boolean hasInstructions = !instructionsArea.getText().trim().isEmpty();
            boolean hasQuestions = !viewModel.getQuestionList().isEmpty();

            return !(hasTitle || hasInstructions || hasQuestions);
        }

        // For existing exams, check if title or instructions changed
        boolean titleChanged = !currentExam.getTitle().equals(titleField.getText());
        boolean instructionsChanged = !String.valueOf(currentExam.getInstructions()).equals(instructionsArea.getText());

        // Check if questions have been added, removed, or modified, this part was made with the help of AI (claude.ai)
        boolean questionsChanged = false;

        if (currentExam.getQuestions() == null && !viewModel.getQuestionList().isEmpty()) {
            questionsChanged = true;
        } else if (currentExam.getQuestions() != null &&
                currentExam.getQuestions().size() != viewModel.getQuestionList().size()) {
            questionsChanged = true;
        } else if (currentExam.getQuestions() != null) {
            for (int i = 0; i < currentExam.getQuestions().size(); i++) {
                Question origQuestion = currentExam.getQuestions().get(i);
                Question currQuestion = viewModel.getQuestionList().get(i);

                if (!origQuestion.getText().equals(currQuestion.getText()) ||
                        !origQuestion.getCorrectAnswer().equals(currQuestion.getCorrectAnswer())) {
                    questionsChanged = true;
                    break;
                }
            }
        }
        return !(titleChanged || instructionsChanged || questionsChanged);
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
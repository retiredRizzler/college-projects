package be.esi.prj.easyeval.viewmodel;

import be.esi.prj.easyeval.model.ExamDocument;
import be.esi.prj.easyeval.model.Question;
import be.esi.prj.easyeval.repository.RepositoryException;
import be.esi.prj.easyeval.service.ExamDocumentService;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewModel for the Document Creator view which manages the creation and editing of exam documents.
 */
public class DocumentCreatorViewModel {
    private final ExamDocumentService examDocumentService;

    private final ObjectProperty<ExamDocument> currentExam = new SimpleObjectProperty<>();
    private final ObjectProperty<Question> selectedQuestion = new SimpleObjectProperty<>();
    private final StringProperty examTitle = new SimpleStringProperty();
    private final StringProperty examInstructions = new SimpleStringProperty();
    private final ObservableList<Question> questionList = FXCollections.observableArrayList();

    // Question properties
    private final StringProperty questionText = new SimpleStringProperty();
    private final StringProperty correctAnswer = new SimpleStringProperty();

    /**
     * Default constructor.
     */
    public DocumentCreatorViewModel() {
        this.examDocumentService = new ExamDocumentService();
    }

    /**
     * Constructor with service for testing.
     * @param examDocumentService The exam document service to use
     */
    DocumentCreatorViewModel(ExamDocumentService examDocumentService) {
        this.examDocumentService = examDocumentService;
    }

    /**
     * Initializes the ViewModel with an exam document.
     * @param examDocument The exam document to edit or null for a new exam
     */
    public void initialize(ExamDocument examDocument) {
        
        if (examDocument != null) {
            // We're editing an existing exam
            currentExam.set(examDocument);
            examTitle.set(examDocument.getTitle());
            examInstructions.set(examDocument.getInstructions());

            if (examDocument.getQuestions() != null) {
                questionList.setAll(examDocument.getQuestions());
            } else {
                questionList.clear();
            }
        } else {
            // We're creating a new exam
            currentExam.set(new ExamDocument());
            examTitle.set("");
            examInstructions.set("");
            questionList.clear();
        }
        clearQuestionForm();
    }

    /**
     * Adds a new question to the exam document.
     * @return The newly added question, or null if validation failed
     */
    public Question addQuestion() {
        if (questionText.get() == null || questionText.get().trim().isEmpty()) {
            showErrorAlert("Invalid Input", "Question text cannot be empty.");
            return null;
        }

        if (correctAnswer.get() == null || correctAnswer.get().trim().isEmpty()) {
            showErrorAlert("Invalid Input", "Correct answer cannot be empty.");
            return null;
        }

        Question question = new Question(questionText.get(), correctAnswer.get());
        questionList.add(question);
        clearQuestionForm();

        return question;
    }

    /**
     * Updates an existing question.
     * @return The updated question, or null if validation failed
     */
    public Question updateQuestion() {
        Question question = selectedQuestion.get();
        if (question == null) {
            showErrorAlert("Error", "No question selected for update.");
            return null;
        }

        if (questionText.get() == null || questionText.get().trim().isEmpty()) {
            showErrorAlert("Invalid Input", "Question text cannot be empty.");
            return null;
        }

        if (correctAnswer.get() == null || correctAnswer.get().trim().isEmpty()) {
            showErrorAlert("Invalid Input", "Correct answer cannot be empty.");
            return null;
        }

        question.setText(questionText.get());
        question.setCorrectAnswer(correctAnswer.get());

        int index = questionList.indexOf(question);
        if (index >= 0) {
            questionList.set(index, question);
        }

        clearQuestionForm();

        return question;
    }

    /**
     * Removes a question from the exam document.
     * @param question The question to remove
     * @return true if the question was removed, false otherwise
     */
    public boolean removeQuestion(Question question) {
        if (question == null) {
            return false;
        }

        boolean removed = questionList.remove(question);

        // If the currently selected question was removed, clear the form
        if (removed && question.equals(selectedQuestion.get())) {
            clearQuestionForm();
        }

        return removed;
    }

    /**
     * Clears the question form fields.
     */
    public void clearQuestionForm() {
        selectedQuestion.set(null);
        questionText.set("");
        correctAnswer.set("");
    }

    /**
     * Loads a question into the form for editing.
     * @param question The question to edit
     */
    public void editQuestion(Question question) {
        if (question != null) {
            selectedQuestion.set(question);
            questionText.set(question.getText());
            correctAnswer.set(question.getCorrectAnswer());
        }
    }

    /**
     * Saves the exam document with all its questions.
     * @return The saved exam document, or null if an error occurred
     */
    public ExamDocument saveExam() {
        if (examTitle.get() == null || examTitle.get().trim().isEmpty()) {
            showErrorAlert("Invalid Input", "Exam title cannot be empty.");
            return null;
        }

        if (currentExam.get().getCourseId() == null) {
            showErrorAlert("Error", "No course associated with this exam.");
            return null;
        }

        try {
            ExamDocument exam = currentExam.get();
            exam.setTitle(examTitle.get());
            exam.setInstructions(examInstructions.get());

            List<Question> questions = new ArrayList<>(questionList);
            exam.setQuestions(questions);
            ExamDocument savedExam = examDocumentService.saveExamDocument(exam);

            // Update current exam with saved version
            currentExam.set(savedExam);

            return savedExam;
        } catch (RepositoryException e) {
            showErrorAlert("Error saving exam", e.getMessage());
            return null;
        }
    }

    /**
     * Get the exam title property for binding.
     * @return The exam title property
     */
    public StringProperty examTitleProperty() {
        return examTitle;
    }

    /**
     * Get the exam instructions property for binding.
     * @return The exam instructions property
     */
    public StringProperty examInstructionsProperty() {
        return examInstructions;
    }

    /**
     * Get the question text property for binding.
     * @return The question text property
     */
    public StringProperty questionTextProperty() {
        return questionText;
    }

    /**
     * Get the correct answer property for binding.
     * @return The correct answer property
     */
    public StringProperty correctAnswerProperty() {
        return correctAnswer;
    }

    /**
     * Get the current exam property for binding.
     * @return The current exam property
     */
    public ObjectProperty<ExamDocument> currentExamProperty() {
        return currentExam;
    }

    /**
     * Get the current exam.
     * @return The current exam
     */
    public ExamDocument getCurrentExam() {
        return currentExam.get();
    }

    /**
     * Get the selected question property for binding.
     * @return The selected question property
     */
    public ObjectProperty<Question> selectedQuestionProperty() {
        return selectedQuestion;
    }

    /**
     * Get the currently selected question.
     * @return The selected question
     */
    public Question getSelectedQuestion() {
        return selectedQuestion.get();
    }

    /**
     * Get the observable list of questions for binding.
     * @return Observable list of questions
     */
    public ObservableList<Question> getQuestionList() {
        return questionList;
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
package be.esi.prj.easyeval.viewmodel;

import be.esi.prj.easyeval.model.Course;
import be.esi.prj.easyeval.model.ExamDocument;
import be.esi.prj.easyeval.repository.RepositoryException;
import be.esi.prj.easyeval.service.ExamDocumentService;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.io.File;
import java.util.List;

/**
 * ViewModel for handling ExamDocument-related operations and data binding for the Document Manager view.
 */
public class DocumentManagerViewModel {
    private final ExamDocumentService examDocumentService;

    private final ObjectProperty<Course> currentCourse = new SimpleObjectProperty<>();
    private final ObjectProperty<ExamDocument> selectedExam = new SimpleObjectProperty<>();
    private final ObservableList<ExamDocument> examList = FXCollections.observableArrayList();

    /**
     * Default constructor.
     */
    public DocumentManagerViewModel() {
        this.examDocumentService = new ExamDocumentService();
    }

    /**
     * Constructor with service for testing.
     * @param examDocumentService The exam document service to use
     */
    DocumentManagerViewModel(ExamDocumentService examDocumentService) {
        this.examDocumentService = examDocumentService;
    }

    /**
     * Initializes the ViewModel with the current course.
     * @param course The current course
     */
    public void initialize(Course course) {
        currentCourse.set(course);
        fetchExamsForCourse(course.getId());
    }

    /**
     * Fetches all exams for the specified course from the repository and updates the observable list.
     * @param courseId The ID of the course
     */
    public void fetchExamsForCourse(Long courseId) {
        try {
            List<ExamDocument> exams = examDocumentService.findExamDocumentsByCourseId(courseId);
            examList.clear();
            examList.addAll(exams);
        } catch (RepositoryException e) {
            showErrorAlert("Error fetching exams", e.getMessage());
        }
    }

    /**
     * Creates a new exam document.
     * @param title The title of the new exam
     * @param instructions Optional instructions for the exam
     * @return The newly created ExamDocument, or null if an error occurred
     */
    public ExamDocument createExam(String title, String instructions) {
        if (title == null || title.trim().isEmpty()) {
            showErrorAlert("Invalid Input", "Exam title cannot be empty.");
            return null;
        }

        if (currentCourse.get() == null) {
            showErrorAlert("Error", "No course selected.");
            return null;
        }

        try {
            ExamDocument newExam = new ExamDocument();
            newExam.setTitle(title);
            newExam.setInstructions(instructions);
            newExam.setCourseId(currentCourse.get().getId());

            ExamDocument savedExam = examDocumentService.saveExamDocument(newExam);

            // Refresh the exam list
            fetchExamsForCourse(currentCourse.get().getId());

            return savedExam;
        } catch (RepositoryException e) {
            showErrorAlert("Error creating exam", e.getMessage());
            return null;
        }
    }

    /**
     * Deletes an exam by its ID.
     * @param examId The ID of the exam to delete
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteExam(Long examId) {
        try {
            boolean success = examDocumentService.deleteExamDocument(examId);
            if (success) {
                fetchExamsForCourse(currentCourse.get().getId()); // Refresh the list
            }
            return success;
        } catch (RepositoryException e) {
            e.printStackTrace();
            showErrorAlert("Error deleting exam", e.getMessage());
            return false;
        }
    }

    /**
     * Generates a PDF for the specified exam without answers.
     * @param exam The exam to generate a PDF for
     * @return The generated PDF file
     */
    public File generateExamPDF(ExamDocument exam) {
        try {
            return examDocumentService.generateExamDocument(exam);
        } catch (Exception e) {
            showErrorAlert("Error generating PDF", e.getMessage());
            return null;
        }
    }

    /**
     * Generates a PDF for the specified exam with answers.
     * @param exam The exam to generate a PDF for
     * @return The generated PDF file with answers
     */
    public File generateExamWithAnswersPDF(ExamDocument exam) {
        try {
            return examDocumentService.generateExamWithAnswers(exam);
        } catch (Exception e) {
            showErrorAlert("Error generating PDF with answers", e.getMessage());
            return null;
        }
    }

    /**
     * Get the observable list of exams for binding to UI controls.
     * @return Observable list of exams
     */
    public ObservableList<ExamDocument> getExamList() {
        return examList;
    }

    /**
     * Get the current course property for binding.
     * @return The current course property
     */
    public ObjectProperty<Course> currentCourseProperty() {
        return currentCourse;
    }

    /**
     * Get the current course.
     * @return The current course
     */
    public Course getCurrentCourse() {
        return currentCourse.get();
    }

    /**
     * Get the selected exam property for binding.
     * @return The selected exam property
     */
    public ObjectProperty<ExamDocument> selectedExamProperty() {
        return selectedExam;
    }

    /**
     * Get the currently selected exam.
     * @return The selected exam
     */
    public ExamDocument getSelectedExam() {
        return selectedExam.get();
    }

    /**
     * Set the selected exam.
     * @param exam The exam to select
     */
    public void setSelectedExam(ExamDocument exam) {
        selectedExam.set(exam);
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
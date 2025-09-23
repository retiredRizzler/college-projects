package be.esi.prj.easyeval.viewmodel;

import be.esi.prj.easyeval.model.*;
import be.esi.prj.easyeval.repository.*;
import be.esi.prj.easyeval.service.ExamDocumentService;
import be.esi.prj.easyeval.service.TesseractOCRService;
import be.esi.prj.easyeval.utils.OCRProcessor;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ViewModel for handling Submission-related operations and data binding.
 */
public class SubmissionViewModel {
    private final ExamDocumentService examDocumentService;
    private final ExamDocumentRepository examDocumentRepository;
    private final SubmissionRepository submissionRepository;
    private final ScanResultRepository scanResultRepository;
    private final CourseRepository courseRepository;
    private final OCRProcessor ocrProcessor;

    private final ObjectProperty<ExamDocument> selectedExam = new SimpleObjectProperty<>();
    private final ObservableList<ExamDocument> examsList = FXCollections.observableArrayList();
    private final ObservableList<Submission> submissionsList = FXCollections.observableArrayList();
    private final ObservableList<ScanResult> scanResultsList = FXCollections.observableArrayList();
    private final ObservableList<Submission> processedSubmissionsList = FXCollections.observableArrayList();
    private final DoubleProperty progress = new SimpleDoubleProperty(0);

    private final Map<Long, String> submissionFilePaths = new ConcurrentHashMap<>();

    /**
     * Default constructor.
     */
    public SubmissionViewModel() {
        this.examDocumentService = new ExamDocumentService();
        this.examDocumentRepository = new ExamDocumentRepository();
        this.submissionRepository = new SubmissionRepository();
        this.scanResultRepository = new ScanResultRepository();
        this.courseRepository = new CourseRepository();
        this.ocrProcessor = new TesseractOCRService();

        loadExamsForSelection();
    }

    /**
     * Constructor for testing with repositories.
     */
    SubmissionViewModel(ExamDocumentService examDocumentService,
                        ExamDocumentRepository examDocumentRepository,
                        SubmissionRepository submissionRepository,
                        ScanResultRepository scanResultRepository,
                        CourseRepository courseRepository,
                        OCRProcessor ocrProcessor) {
        this.examDocumentService = examDocumentService;
        this.examDocumentRepository = examDocumentRepository;
        this.submissionRepository = submissionRepository;
        this.scanResultRepository = scanResultRepository;
        this.courseRepository = courseRepository;
        this.ocrProcessor = ocrProcessor;
    }

    /**
     * Loads all exams for selection in the UI.
     */
    public void loadExamsForSelection() {
        try {
            List<ExamDocument> exams = examDocumentRepository.findAll();
            examsList.clear();
            examsList.addAll(exams);
        } catch (RepositoryException e) {
            showErrorAlert("Error loading exams", e.getMessage());
        }
    }

    /**
     * Gets a Course object based on courseId.
     *
     * @param courseId The ID of the course
     * @return The Course object, or null if not found
     */
    public Course getCourseForExam(Long courseId) {
        try {
            Optional<Course> courseOpt = courseRepository.findById(courseId);
            return courseOpt.orElse(null);
        } catch (RepositoryException e) {
            showErrorAlert("Error loading course", e.getMessage());
            return null;
        }
    }

    /**
     * Imports scanned files for OCR processing.
     * Clears any existing submissions before importing new ones.
     *
     * @param files            List of scanned files to import
     * @return true if import was successful, false otherwise
     */
    public boolean importScannedFiles(List<File> files) {
        if (selectedExam.get() == null) {
            showErrorAlert("No Exam Selected", "Please select an exam before importing files.");
            return false;
        }

        // Clear existing submissions before importing new ones
        clearAllSubmissions();

        boolean success = true;
        System.out.println("Importing " + files.size());

        for (File file : files) {
            try {
                System.out.println("Importing file: " + file.getAbsolutePath());

                if (!file.exists() || !file.canRead()) {
                    System.err.println("File does not exist or cannot be read: " + file.getAbsolutePath());
                    success = false;
                    continue;
                }

                String studentId = extractStudentId(file);
                if (studentId.isEmpty()) {
                    studentId = "Student_" + System.currentTimeMillis();
                }

                System.out.println("Extracted student ID: " + studentId);

                // Create and save submission
                Submission submission = new Submission();
                submission.setExamId(selectedExam.get().getId());
                submission.setStudentId(studentId);
                submission.setSubmissionDate(LocalDateTime.now());

                // Save submission to database
                submission = submissionRepository.save(submission);
                System.out.println("Saved submission with ID: " + submission.getId());

                // Store file path for this submission
                submissionFilePaths.put(submission.getId(), file.getAbsolutePath());
                System.out.println("Stored file path: " + file.getAbsolutePath());

                // Add to observable list
                submissionsList.add(submission);

            } catch (Exception e) {
                success = false;
                System.err.println("Import Error for file " + file.getName() + ": " + e.getMessage());
                e.printStackTrace();
                showErrorAlert("Import Error", "Failed to import file: " + file.getName() + "\nError: " + e.getMessage());
            }
        }

        return success;
    }

    /**
     * Extracts student ID from a file based on pattern.
     */
    private String extractStudentId(File file) {

            // Just use the file name without extension as student ID
            String fileName = file.getName();
            int dotIndex = fileName.lastIndexOf('.');
            if (dotIndex > 0) {
                return fileName.substring(0, dotIndex);
            }
            return fileName;

    }

    /**
     * Creates a task to process submissions with OCR.
     *
     * @param enhanceImages   Whether to enhance images
     * @param correctRotation Whether to correct rotation
     * @param debug           Whether to enable debug mode
     * @return Task for processing submissions
     */
    public Task<Boolean> processSubmissions(boolean enhanceImages, boolean correctRotation, boolean debug) {
        return new Task<>() {
            @Override
            protected Boolean call() {
                System.out.println("Starting OCR processing with settings:");
                System.out.println("- Image enhancement: " + enhanceImages);
                System.out.println("- Rotation correction: " + correctRotation);
                System.out.println("- Debug mode: " + debug);

                ocrProcessor.setImageEnhancement(enhanceImages);
                ocrProcessor.setRotationCorrection(correctRotation);
                ocrProcessor.setDebugMode(debug);

                int total = submissionsList.size();
                int processed = 0;
                System.out.println("Processing " + total + " submissions");

                for (Submission submission : new ArrayList<>(submissionsList)) {
                    if (isCancelled()) {
                        return false;
                    }

                    try {
                        updateMessage("Processing submission " + (processed + 1) + " of " + total);
                        System.out.println("Processing submission ID: " + submission.getId());

                        String filePath = submissionFilePaths.get(submission.getId());
                        if (filePath == null) {
                            System.err.println("No file path for submission ID " + submission.getId());
                            continue;
                        }

                        File scanFile = new File(filePath);
                        if (!scanFile.exists()) {
                            System.err.println("Scan file does not exist: " + filePath);
                            continue;
                        }

                        System.out.println("File exists: " + scanFile.getAbsolutePath());
                        System.out.println("File size: " + scanFile.length() + " bytes");

                        ExamDocument exam = selectedExam.get();
                        System.out.println("Exam: " + exam.getId() + " - " + exam.getTitle());
                        System.out.println("Questions: " + exam.getQuestions().size());

                        // Log question details
                        for (Question q : exam.getQuestions()) {
                            System.out.println(" - Q" + q.getId() + ": " + q.getText() +
                                    " (Answer: " + q.getCorrectAnswer() + ")");
                        }

                        // Process the exam with OCR
                        Submission processedSubmission = ocrProcessor.processExam(scanFile, exam);
                        System.out.println("OCR processing completed");
                        System.out.println("Extracted student ID: " + processedSubmission.getStudentId());
                        System.out.println("Answers extracted: " + processedSubmission.getStudentAnswers().size());

                        // Update the stored submission with OCR results
                        submission.setStudentId(processedSubmission.getStudentId());
                        for (Long questionId : processedSubmission.getStudentAnswers().keySet()) {
                            String answer = processedSubmission.getStudentAnswers().get(questionId);
                            System.out.println("Setting answer for Q" + questionId + ": " + answer);
                            submission.setAnswer(questionId, answer);
                        }

                        // Save updated submission
                        System.out.println("Saving submission to database");
                        submissionRepository.save(submission);

                        // Calculate score
                        System.out.println("Calculating score");
                        ScanResult scanResult = ocrProcessor.calculateScore(submission, exam);
                        System.out.println("Score: " + scanResult.getScore() + "%");
                        System.out.println("Confidence levels: " + scanResult.getConfidenceLevels().size());

                        // Save scan result
                        System.out.println("Saving scan result to database");
                        scanResult = scanResultRepository.save(scanResult);

                        // Update observable lists on UI thread
                        ScanResult finalScanResult = scanResult;
                        javafx.application.Platform.runLater(() -> {

                            if (!scanResultsList.contains(finalScanResult)) {
                                scanResultsList.add(finalScanResult);
                            }

                            if (!processedSubmissionsList.contains(submission)) {
                                processedSubmissionsList.add(submission);
                            }
                        });

                    } catch (Exception e) {
                        System.err.println("Error processing submission: " + e.getMessage());
                        e.printStackTrace();
                    }

                    processed++;
                    updateProgress(processed, total);
                }

                return true;
            }
        };
    }

    /**
     * Exports results to CSV file.
     *
     * @param directory Directory to save the CSV file
     * @return The generated CSV file
     * @throws IOException If file cannot be written
     */
    public File exportResultsToCSV(File directory) throws IOException {
        if (directory == null || !directory.isDirectory()) {
            throw new IOException("Invalid directory");
        }

        // Create CSV file
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String examName = selectedExam.get().getTitle().replaceAll("[^a-zA-Z0-9]", "_");
        File csvFile = new File(directory, examName + "_results_" + timestamp + ".csv");

        try (FileWriter writer = new FileWriter(csvFile)) {
            writer.write("StudentID,Score (%),ConfidenceLevel (%)");

            for (Question question : selectedExam.get().getQuestions()) {
                writer.write(",Q" + question.getId() + "_Answer,Q" + question.getId() + "_Correct");
            }
            writer.write("\n");

            for (Submission submission : processedSubmissionsList) {
                // Find corresponding scan result
                ScanResult scanResult = null;
                for (ScanResult result : scanResultsList) {
                    if (result.getSubmissionId().equals(submission.getId())) {
                        scanResult = result;
                        break;
                    }
                }

                if (scanResult == null) continue;

                writer.write(submission.getStudentId() + ",");
                writer.write(String.format("%.2f", scanResult.getScore()) + ",");

                double avgConfidence = 0;
                int confidenceCount = 0;
                for (Double confidence : scanResult.getConfidenceLevels().values()) {
                    avgConfidence += confidence;
                    confidenceCount++;
                }
                avgConfidence = confidenceCount > 0 ? avgConfidence / confidenceCount : 0;
                writer.write(String.format("%.2f", avgConfidence));

                // Write answers and correct answers for each question
                for (Question question : selectedExam.get().getQuestions()) {
                    String studentAnswer = submission.getAnswer(question.getId());
                    String correctAnswer = question.getCorrectAnswer();

                    writer.write("," + (studentAnswer != null ? studentAnswer : ""));
                    writer.write("," + correctAnswer);
                }

                writer.write("\n");
            }
        }

        return csvFile;
    }

    /**
     * Generate detailed report for a specific submission.
     *
     * @param submission The submission to report on
     * @return String containing the report
     */
    public String generateDetailedReport(Submission submission) {
        ScanResult scanResult = null;
        for (ScanResult result : scanResultsList) {
            if (result.getSubmissionId().equals(submission.getId())) {
                scanResult = result;
                break;
            }
        }

        if (scanResult == null) {
            return "Aucun résultat de scan trouvé pour cette soumission.";
        }

        String basicReport = ocrProcessor.generateReport(submission, selectedExam.get(), scanResult);

        StringBuilder enhancedReport = new StringBuilder(basicReport);
        enhancedReport.append("\nINFORMATIONS SUPPLÉMENTAIRES\n");
        enhancedReport.append("-----------------------------\n\n");

        enhancedReport.append("Fichier source: ").append(getFileNameForSubmission(submission)).append("\n");

        double avgConfidence = 0;
        int confidenceCount = 0;

        for (Double confidence : scanResult.getConfidenceLevels().values()) {
            avgConfidence += confidence;
            confidenceCount++;
        }

        double finalAvgConfidence = confidenceCount > 0 ? avgConfidence / confidenceCount : 0;
        enhancedReport.append("Confiance moyenne de reconnaissance: ")
                .append(String.format("%.2f%%", finalAvgConfidence * 100)).append("\n");

        return enhancedReport.toString();
    }

    /**
     * Removes a submission from the list and database.
     *
     * @param submission The submission to remove
     * @return true if removal was successful, false otherwise
     */
    public boolean removeSubmission(Submission submission) {
        if (submission == null) {
            return false;
        }

        try {
            // Check if there's a scan result
            Optional<ScanResult> scanResult = scanResultRepository.findBySubmissionId(submission.getId());
            scanResult.ifPresent(result -> {
                scanResultRepository.deleteById(result.getId());
                scanResultsList.removeIf(r -> r.getId().equals(result.getId()));
                processedSubmissionsList.remove(submission);
            });

            // Delete submission
            submissionRepository.deleteById(submission.getId());
            submissionsList.remove(submission);

            // Remove file path tracking
            submissionFilePaths.remove(submission.getId());

            return true;
        } catch (RepositoryException e) {
            showErrorAlert("Remove Error", "Failed to remove submission: " + e.getMessage());
            return false;
        }
    }

    /**
     * Clears all submissions and related data from the database and UI.
     */
    public void clearAllSubmissions() {
        try {
            // Make a copy of the list to avoid ConcurrentModificationException
            List<Submission> submissionsToRemove = new ArrayList<>(submissionsList);

            for (Submission submission : submissionsToRemove) {
                Optional<ScanResult> scanResult = scanResultRepository.findBySubmissionId(submission.getId());
                scanResult.ifPresent(result -> scanResultRepository.deleteById(result.getId()));

                submissionRepository.deleteById(submission.getId());
            }

            submissionsList.clear();
            scanResultsList.clear();
            processedSubmissionsList.clear();
            submissionFilePaths.clear();

        } catch (RepositoryException e) {
            showErrorAlert("Clear Error", "Failed to clear submissions: " + e.getMessage());
        }
    }


    /**
     * Gets the file name for a submission.
     * @param submission The submission
     * @return The file name or path associated with this submission
     */
    public String getFileNameForSubmission(Submission submission) {
        if (submission == null || submission.getId() == null) {
            return "";
        }

        String filePath = submissionFilePaths.get(submission.getId());
        if (filePath == null) {
            return "";
        }

        // Return just the file name, not the full path
        return new File(filePath).getName();
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

        // Load existing submissions for this exam,
        if (exam != null) {
            loadSubmissionsForExam(exam.getId());
        }
    }

    /**
     * Loads existing submissions for an exam.
     * @param examId The ID of the exam
     */
    private void loadSubmissionsForExam(Long examId) {
        try {
            submissionsList.clear();
            scanResultsList.clear();
            processedSubmissionsList.clear();
            submissionFilePaths.clear();

            List<Submission> submissions = submissionRepository.findByExamId(examId);
            submissionsList.addAll(submissions);

            // Load scan results and track processed submissions
            for (Submission submission : submissions) {
                Optional<ScanResult> scanResult = scanResultRepository.findBySubmissionId(submission.getId());
                scanResult.ifPresent(result -> {
                    scanResultsList.add(result);
                    processedSubmissionsList.add(submission);
                });

                // We don't have actual file paths, since they're not stored in the DB
                // In a real application, you'd either store this info or regenerate it
                submissionFilePaths.put(submission.getId(), "Existing submission (no file path)");
            }

        } catch (RepositoryException e) {
            showErrorAlert("Load Error", "Failed to load submissions: " + e.getMessage());
        }
    }

    /**
     * Get the exams list for binding.
     * @return Observable list of exams
     */
    public ObservableList<ExamDocument> getExamsList() {
        return examsList;
    }

    /**
     * Get the submissions list for binding.
     * @return Observable list of submissions
     */
    public ObservableList<Submission> getSubmissionsList() {
        return submissionsList;
    }

    /**
     * Get the scan results list for binding.
     * @return Observable list of scan results
     */
    public ObservableList<ScanResult> getScanResultsList() {
        return scanResultsList;
    }

    /**
     * Get the processed submissions list for binding.
     * @return Observable list of processed submissions
     */
    public ObservableList<Submission> getProcessedSubmissionsList() {
        return processedSubmissionsList;
    }

    /**
     * Get the progress property for binding.
     * @return The progress property
     */
    public DoubleProperty progressProperty() {
        return progress;
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
package be.esi.prj.easyeval.fxmlcontroller;

import be.esi.prj.easyeval.model.ExamDocument;
import be.esi.prj.easyeval.model.Submission;
import be.esi.prj.easyeval.model.ScanResult;
import be.esi.prj.easyeval.service.NavigationService;
import be.esi.prj.easyeval.viewmodel.SubmissionViewModel;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Controller for the Submission view, which allows importing and processing of exam submissions.
 */
public class SubmissionController {

    @FXML
    private Label examTitleLabel;

    @FXML
    private ComboBox<ExamDocument> examSelector;

    @FXML
    private Button importScansButton;

    @FXML
    private Label scanCountLabel;

    @FXML
    private TableView<SubmissionDisplayModel> submissionsTable;

    @FXML
    private TableColumn<SubmissionDisplayModel, String> fileNameColumn;

    @FXML
    private TableColumn<SubmissionDisplayModel, String> studentIdColumn;

    @FXML
    private TableColumn<SubmissionDisplayModel, String> statusColumn;

    @FXML
    private TableColumn<SubmissionDisplayModel, Double> scoreColumn;

    @FXML
    private TableColumn<SubmissionDisplayModel, Double> confidenceColumn;

    @FXML
    private TableColumn<SubmissionDisplayModel, String> dateColumn;

    @FXML
    private Button removeSubmissionButton;

    @FXML
    private Button clearAllButton;

    @FXML
    private ComboBox<String> ocrEngineCombo;

    @FXML
    private CheckBox enhanceImagesCheckbox;

    @FXML
    private CheckBox rotationCorrectionCheckbox;

    @FXML
    private CheckBox debugModeCheckbox;

    @FXML
    private Button processSubmissionsButton;

    @FXML
    private ProgressBar processingProgressBar;

    @FXML
    private Label processingStatusLabel;

    @FXML
    private Button exportResultsButton;

    @FXML
    private Button viewDetailedResultsButton;

    @FXML
    private Button viewAllReportsButton;

    private SubmissionViewModel viewModel;
    private NavigationService navigationService;
    private SimpleBooleanProperty processing = new SimpleBooleanProperty(false);
    private ExamDocument currentExam;
    private Task<Boolean> currentTask;

    /**
     * Display model for submissions table.
     * This nested class helps with displaying submission data in the table.
     */
    public static class SubmissionDisplayModel {
        private String fileName;
        private String studentId;
        private String status;
        private Double score;
        private Double confidence;
        private String date;
        private Submission submission;
        private ScanResult scanResult;

        public SubmissionDisplayModel(String fileName, Submission submission, ScanResult scanResult) {
            this.fileName = fileName;
            this.submission = submission;
            this.scanResult = scanResult;

            this.studentId = submission != null ? submission.getStudentId() : "Unidentified";
            this.status = scanResult != null ? "Processed" : "Pending";
            this.score = scanResult != null ? scanResult.getScore() : null;
            this.confidence = scanResult != null ? calculateAverageConfidence(scanResult) : null;
            this.date = submission != null && submission.getSubmissionDate() != null ?
                    submission.getSubmissionDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "";
        }

        private Double calculateAverageConfidence(ScanResult result) {
            if (result.getConfidenceLevels() == null || result.getConfidenceLevels().isEmpty()) {
                return null;
            }

            Double sum = 0.0;
            for (Double value : result.getConfidenceLevels().values()) {
                sum += value;
            }
            return sum / result.getConfidenceLevels().size();
        }

        // Getters for TableView
        public String getFileName() { return fileName; }
        public String getStudentId() { return studentId; }
        public String getStatus() { return status; }
        public Double getScore() { return score; }
        public Double getConfidence() { return confidence; }
        public String getDate() { return date; }
        public Submission getSubmission() { return submission; }
        public ScanResult getScanResult() { return scanResult; }
    }

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {
        viewModel = new SubmissionViewModel();
        navigationService = NavigationService.getInstance();

        // Configure table columns
        fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        studentIdColumn.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
        confidenceColumn.setCellValueFactory(new PropertyValueFactory<>("confidence"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        // Format score and confidence columns with percentages
        scoreColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.1f%%", item));
                }
            }
        });

        confidenceColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.1f%%", item * 100));
                }
            }
        });

        ocrEngineCombo.setItems(FXCollections.observableArrayList(
                "Tesseract OCR"
        ));
        ocrEngineCombo.getSelectionModel().selectFirst();

        enhanceImagesCheckbox.setSelected(false);
        rotationCorrectionCheckbox.setSelected(true);
        debugModeCheckbox.setSelected(false);

        processSubmissionsButton.disableProperty().bind(
                Bindings.or(
                        Bindings.isEmpty(viewModel.getSubmissionsList()),
                        processing
                )
        );
        processingProgressBar.visibleProperty().bind(processing);

        exportResultsButton.disableProperty().bind(
                Bindings.or(
                        Bindings.isEmpty(viewModel.getProcessedSubmissionsList()),
                        processing
                )
        );

        viewDetailedResultsButton.disableProperty().bind(
                Bindings.or(
                        Bindings.isNull(submissionsTable.getSelectionModel().selectedItemProperty()),
                        Bindings.createBooleanBinding(
                                () -> {
                                    SubmissionDisplayModel selected = submissionsTable.getSelectionModel().getSelectedItem();
                                    return selected == null || selected.getScanResult() == null;
                                },
                                submissionsTable.getSelectionModel().selectedItemProperty()
                        )
                )
        );

        viewAllReportsButton.disableProperty().bind(
                Bindings.isEmpty(viewModel.getProcessedSubmissionsList()));

        removeSubmissionButton.disableProperty().bind(
                Bindings.or(
                        Bindings.isNull(submissionsTable.getSelectionModel().selectedItemProperty()),
                        processing
                )
        );

        clearAllButton.disableProperty().bind(
                Bindings.or(
                        Bindings.isEmpty(viewModel.getSubmissionsList()),
                        processing
                )
        );

        // Bind progress bar to view model progress
        processingProgressBar.progressProperty().bind(viewModel.progressProperty());

        // Setup listeners
        viewModel.getSubmissionsList().addListener((ListChangeListener<Submission>) change -> {
            refreshSubmissionsTable();
            updateScanCountLabel();
        });
        viewModel.getScanResultsList().addListener((ListChangeListener<ScanResult>) change -> {
            refreshSubmissionsTable();
        });

        viewModel.loadExamsForSelection();
        // obliger d'utiliser string convertor pour pouvoir modifier le toString d'un comboBox
        examSelector.setItems(viewModel.getExamsList());
        examSelector.setConverter(new StringConverter<ExamDocument>() {
            @Override
            public String toString(ExamDocument exam) {
                if (exam == null) {
                    return null;
                }
                return exam.getTitle() + " (ID: " + exam.getId() + ")";
            }

            @Override
            public ExamDocument fromString(String string) {
                // nécessaire mais inutile dans notre cas
                return null;
            }
        });
        examSelector.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                currentExam = newValue;
                examTitleLabel.setText(newValue.getTitle() + " - Submissions");
                viewModel.setSelectedExam(newValue);
            }
        });

        if (!viewModel.getExamsList().isEmpty()) {
            examSelector.getSelectionModel().selectFirst();
        }
    }

    /**
     * Updates the scan count label.
     */
    private void updateScanCountLabel() {
        int count = viewModel.getSubmissionsList().size();
        scanCountLabel.setText(count + " scan" + (count != 1 ? "s" : "") + " imported");
    }

    /**
     * Refreshes the submissions table with current data.
     */
    private void refreshSubmissionsTable() {
        List<Submission> submissions = viewModel.getSubmissionsList();
        List<ScanResult> scanResults = viewModel.getScanResultsList();

        submissionsTable.getItems().clear();

        // Add submissions to table
        for (Submission submission : submissions) {
            ScanResult result = null;

            // Find matching scan result if exists
            for (ScanResult scanResult : scanResults) {
                if (scanResult.getSubmissionId().equals(submission.getId())) {
                    result = scanResult;
                    break;
                }
            }

            SubmissionDisplayModel displayModel = new SubmissionDisplayModel(
                    viewModel.getFileNameForSubmission(submission), submission, result);
            submissionsTable.getItems().add(displayModel);
        }
    }

    /**
     * Handle importing scanned files.
     */
    @FXML
    private void handleImportScans() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Scanned Files");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.tiff", "*.tif"),
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(importScansButton.getScene().getWindow());

        if (selectedFiles != null && !selectedFiles.isEmpty()) {

            // Appel à la méthode modifiée, qui efface automatiquement les soumissions existantes
            boolean imported = viewModel.importScannedFiles(selectedFiles);

            if (imported) {
                showInfoAlert("Import Successful", "Successfully imported " +
                        selectedFiles.size() + " file(s).");
            } else {
                showErrorAlert("Import Error", "Failed to import some files. Check log for details.");
            }
        }
    }

    /**
     * Handle removing a selected submission.
     */
    @FXML
    private void handleRemoveSubmission() {
        SubmissionDisplayModel selected = submissionsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("Confirm Removal");
            confirmDialog.setHeaderText("Remove Submission");
            confirmDialog.setContentText("Are you sure you want to remove this submission?");

            Optional<ButtonType> result = confirmDialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                viewModel.removeSubmission(selected.getSubmission());
            }
        }
    }

    /**
     * Handle clearing all submissions.
     */
    @FXML
    private void handleClearAll() {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Clear All");
        confirmDialog.setHeaderText("Clear All Submissions");
        confirmDialog.setContentText("Are you sure you want to remove all submissions?");

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            viewModel.clearAllSubmissions();
        }
    }

    /**
     * Handle processing submissions with OCR.
     */
    @FXML
    private void handleProcessSubmissions() {
        if (viewModel.getSubmissionsList().isEmpty()) {
            showErrorAlert("No Submissions", "There are no submissions to process.");
            return;
        }

        // Get OCR settings
        String ocrEngine = ocrEngineCombo.getValue();
        boolean enhanceImages = enhanceImagesCheckbox.isSelected();
        boolean correctRotation = rotationCorrectionCheckbox.isSelected();
        boolean debugMode = debugModeCheckbox.isSelected();

        System.out.println("Starting OCR processing with settings:");
        System.out.println("- Engine: " + ocrEngine);
        System.out.println("- Enhance images: " + enhanceImages);
        System.out.println("- Correction rotation: " + correctRotation);
        System.out.println("- Debug mode: " + debugMode);

        // Update UI state
        processing.set(true);
        processingStatusLabel.setText("Processing submissions...");

        // Create a task to do the processing
        currentTask = viewModel.processSubmissions(enhanceImages, correctRotation, debugMode);

        // Bind message
        processingStatusLabel.textProperty().bind(currentTask.messageProperty());

        // Handle completion
        currentTask.setOnSucceeded(event -> {
            processing.set(false);
            processingStatusLabel.textProperty().unbind();
            processingStatusLabel.setText("Processing complete!");

            // Refresh the table
            refreshSubmissionsTable();

            showInfoAlert("Processing Complete", "All submissions have been processed successfully.");
        });

        currentTask.setOnFailed(event -> {
            processing.set(false);
            processingStatusLabel.textProperty().unbind();
            processingStatusLabel.setText("Processing failed!");

            Throwable exception = currentTask.getException();
            if (exception != null) {
                exception.printStackTrace();
                showErrorAlert("Processing Error", "Failed to process submissions: " +
                        exception.getMessage());
            } else {
                showErrorAlert("Processing Error", "Failed to process submissions: Unknown error");
            }
        });

        // Start processing
        new Thread(currentTask).start();
    }

    @FXML
    private void handleViewAllReports() {
        if (viewModel.getProcessedSubmissionsList().isEmpty()) {
            showInfoAlert("Aucun rapport", "Aucune soumission n'a été traitée.");
            return;
        }

        // Créer une fenêtre pour afficher tous les rapports
        Stage reportsStage = new Stage();
        reportsStage.setTitle("Tous les rapports");

        // Créer une liste pour sélectionner les soumissions
        ListView<String> submissionsList = new ListView<>();

        // Remplir la liste avec les soumissions traitées
        for (Submission submission : viewModel.getProcessedSubmissionsList()) {
            submissionsList.getItems().add("Matricule: " + submission.getStudentId() +
                    " - Fichier: " + viewModel.getFileNameForSubmission(submission));
        }

        // Créer une zone de texte pour afficher le rapport
        TextArea reportArea = new TextArea();
        reportArea.setEditable(false);
        reportArea.setWrapText(true);

        // Gérer la sélection d'une soumission
        submissionsList.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.intValue() >= 0) {
                Submission selectedSubmission = viewModel.getProcessedSubmissionsList().get(newVal.intValue());
                String report = viewModel.generateDetailedReport(selectedSubmission);
                reportArea.setText(report);
            }
        });

        // Créer des boutons pour exporter le rapport actuel
        Button exportButton = new Button("Exporter ce rapport");
        exportButton.setOnAction(e -> {
            if (submissionsList.getSelectionModel().getSelectedIndex() >= 0) {
                Submission selectedSubmission = viewModel.getProcessedSubmissionsList()
                        .get(submissionsList.getSelectionModel().getSelectedIndex());
                saveReportToFile(selectedSubmission);
            }
        });

        // Disposition de la fenêtre
        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(submissionsList, reportArea);
        splitPane.setDividerPositions(0.3);

        VBox layout = new VBox(10);
        layout.getChildren().addAll(splitPane, exportButton);
        layout.setPadding(new Insets(10));

        Scene scene = new Scene(layout, 800, 600);
        reportsStage.setScene(scene);
        reportsStage.show();
    }

    // Méthode pour sauvegarder un rapport dans un fichier
    private void saveReportToFile(Submission submission) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le rapport");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Fichiers texte", "*.txt"));
        fileChooser.setInitialFileName(submission.getStudentId() + "_rapport.txt");

        File file = fileChooser.showSaveDialog(viewAllReportsButton.getScene().getWindow());
        if (file != null) {
            try {
                String report = viewModel.generateDetailedReport(submission);
                java.nio.file.Files.writeString(file.toPath(), report);
                showInfoAlert("Rapport enregistré", "Rapport enregistré dans " + file.getAbsolutePath());
            } catch (IOException e) {
                showErrorAlert("Erreur d'enregistrement", "Impossible d'enregistrer le rapport: " + e.getMessage());
            }
        }
    }

    /**
     * Handle exporting results to CSV.
     */
    @FXML
    private void handleExportResults() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Export Directory");
        File directory = directoryChooser.showDialog(exportResultsButton.getScene().getWindow());

        if (directory != null) {
            try {
                File csvFile = viewModel.exportResultsToCSV(directory);
                showInfoAlert("Export Successful", "Results exported to " + csvFile.getAbsolutePath());

                // Ask if user wants to open the file
                Alert openFileAlert = new Alert(Alert.AlertType.CONFIRMATION);
                openFileAlert.setTitle("Open File");
                openFileAlert.setHeaderText("Export Complete");
                openFileAlert.setContentText("Do you want to open the exported CSV file?");

                Optional<ButtonType> result = openFileAlert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    try {
                        java.awt.Desktop.getDesktop().open(csvFile);
                    } catch (IOException e) {
                        showErrorAlert("Open File Error", "Could not open the file: " + e.getMessage());
                    }
                }
            } catch (Exception e) {
                showErrorAlert("Export Error", "Failed to export results: " + e.getMessage());
            }
        }
    }

    /**
     * Handle viewing detailed results.
     */
    @FXML
    private void handleViewDetailedResults() {
        SubmissionDisplayModel selected = submissionsTable.getSelectionModel().getSelectedItem();
        if (selected != null && selected.getScanResult() != null) {
            String report = viewModel.generateDetailedReport(selected.getSubmission());

            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Résultats détaillés");
            dialog.setHeaderText("Résultats détaillés pour l'étudiant: " + selected.getStudentId());

            Label matriculeLabel = new Label("Matricule: " + selected.getStudentId());
            matriculeLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

            TextArea textArea = new TextArea(report);
            textArea.setEditable(false);
            textArea.setWrapText(true);
            textArea.setPrefWidth(600);
            textArea.setPrefHeight(400);

            VBox content = new VBox(10);
            content.getChildren().addAll(matriculeLabel, textArea);
            dialog.getDialogPane().setContent(content);

            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

            ButtonType saveButtonType = new ButtonType("Enregistrer le rapport", ButtonBar.ButtonData.LEFT);
            dialog.getDialogPane().getButtonTypes().add(saveButtonType);

            dialog.setResultConverter(buttonType -> {
                if (buttonType == saveButtonType) {
                    saveReportToFile(selected.getSubmission());
                }
                return null;
            });

            dialog.showAndWait();
        } else {
            showErrorAlert("Aucun résultat", "Aucun résultat de scan disponible pour la soumission sélectionnée.");
        }
    }

    /**
     * Handle navigating back to Document Manager.
     */
    @FXML
    private void handleBack() {
        // If processing, show warning
        if (processing.get()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Processing in Progress");
            alert.setHeaderText("OCR Processing is Still Running");
            alert.setContentText("Do you want to cancel processing and go back?");

            ButtonType buttonTypeCancel = new ButtonType("Cancel Processing", ButtonBar.ButtonData.YES);
            ButtonType buttonTypeStay = new ButtonType("Stay Here", ButtonBar.ButtonData.NO);

            alert.getButtonTypes().setAll(buttonTypeCancel, buttonTypeStay);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.orElse(buttonTypeStay) != buttonTypeCancel) {
                return;
            }

            // Cancel the task if running
            if (currentTask != null && currentTask.isRunning()) {
                currentTask.cancel();
            }

            processing.set(false);
        }

        // clear existing submissions when leaving the view
        viewModel.clearAllSubmissions();

        // Navigate back to document manager for the course
        if (currentExam != null && currentExam.getCourseId() != null) {
            navigationService.navigateToDocumentManager(viewModel.getCourseForExam(currentExam.getCourseId()));
        } else {
            navigationService.navigateToCourseManager();
        }
    }

    /**
     * initialize with an exam document.
     * @param examDocument The exam document to work with
     */
    public void initData(ExamDocument examDocument) {
        if (examDocument != null) {
            currentExam = examDocument;
            examTitleLabel.setText(examDocument.getTitle() + " - Submissions");

            // Set the exam in the selector
            for (ExamDocument exam : examSelector.getItems()) {
                if (exam.getId().equals(examDocument.getId())) {
                    examSelector.getSelectionModel().select(exam);
                    break;
                }
            }

            viewModel.setSelectedExam(examDocument);
        }
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
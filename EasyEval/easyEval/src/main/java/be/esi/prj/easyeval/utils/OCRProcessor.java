package be.esi.prj.easyeval.utils;

import be.esi.prj.easyeval.model.ExamDocument;
import be.esi.prj.easyeval.model.ScanResult;
import be.esi.prj.easyeval.model.Submission;

import java.io.File;
import java.io.IOException;

/**
 * Interface for OCR operations.
 */
public interface OCRProcessor {
    /**
     * Initializes the OCR processor.
     */
    void initialize();

    /**
     * Processes an exam submission from a scanned file.
     *
     * @param scanFile File containing the scanned exam
     * @param exam The exam document with questions
     * @return A submission object with recognized answers
     * @throws IOException If file cannot be read
     * @throws Exception If OCR processing fails
     */
    Submission processExam(File scanFile, ExamDocument exam) throws IOException, Exception;

    /**
     * Calculates score for a submission by comparing with correct answers.
     *
     * @param submission The student's submission
     * @param exam The exam document with correct answers
     * @return ScanResult with score and confidence levels
     */
    ScanResult calculateScore(Submission submission, ExamDocument exam);

    /**
     * Generates a report for a submission.
     *
     * @param submission The student's submission
     * @param exam The exam document
     * @param scanResult The scan result with scores
     * @return String containing the formatted report
     */
    String generateReport(Submission submission, ExamDocument exam, ScanResult scanResult);

    /**
     * Sets image enhancement option.
     *
     * @param enabled true to enable enhancement
     */
    void setImageEnhancement(boolean enabled);

    /**
     * Sets rotation correction option.
     *
     * @param enabled true to enable rotation correction
     */
    void setRotationCorrection(boolean enabled);

    /**
     * Sets debug mode for generating diagnostic images.
     *
     * @param enabled true to enable debug mode
     */
    void setDebugMode(boolean enabled);
}
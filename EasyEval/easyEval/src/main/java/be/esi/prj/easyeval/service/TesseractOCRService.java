package be.esi.prj.easyeval.service;

import be.esi.prj.easyeval.model.ExamDocument;
import be.esi.prj.easyeval.model.Question;
import be.esi.prj.easyeval.model.ScanResult;
import be.esi.prj.easyeval.model.Submission;
import be.esi.prj.easyeval.utils.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * OCR service using Tesseract for exam recognition.
 * Implements the OCRProcessor interface.
 * Modified to directly extract all answers in order.
 */
public class TesseractOCRService implements OCRProcessor {
    private final ImageProcessor imageProcessor;
    private final TextExtractor textExtractor;

    private boolean enableDebug = false;
    private boolean enableRotationCorrection = true;
    private boolean enableImageEnhancement = true;

    /**
     * Default constructor.
     */
    public TesseractOCRService() {
        imageProcessor = new ImageProcessor();
        textExtractor = new TextExtractor(imageProcessor);
        initialize();
    }

    @Override
    public void initialize() {
        imageProcessor.cleanDebugFolder();

        imageProcessor.setDebugMode(enableDebug);
        imageProcessor.setRotationCorrection(enableRotationCorrection);
        imageProcessor.setImageEnhancement(enableImageEnhancement);
        textExtractor.setDebugMode(enableDebug);

        imageProcessor.setRedDetectionParameters(0.90f, 0.05f, 0.2f, 0.2f);
    }

    @Override
    public Submission processExam(File scanFile, ExamDocument exam) throws IOException, Exception {
        if (enableDebug) {
            System.out.println("Processing exam: " + exam.getTitle());
            System.out.println("File: " + scanFile.getAbsolutePath());
            System.out.println("Number of questions: " + exam.getQuestions().size());
        }

        BufferedImage originalImage = imageProcessor.loadAndPrepareImage(scanFile);

        ImageZones zones = imageProcessor.divideImageIntoZones(originalImage);

        BufferedImage redTextImage = imageProcessor.extractRedText(originalImage);

        String matricule = textExtractor.extractMatricule(redTextImage, zones.getMatriculeZone());

        if (enableDebug) {
            System.out.println("Extracted student ID: " + matricule);
        }

        Submission submission = new Submission(exam.getId(),
                matricule.isEmpty() ? "UNKNOWN" : matricule);

        List<String> allAnswers = textExtractor.extractAllAnswers(
                redTextImage,
                zones.getQuestionsZone(),
                exam.getQuestions().size());

        for (int i = 0; i < exam.getQuestions().size(); i++) {
            Question question = exam.getQuestions().get(i);
            String answer = "";

            if (i < allAnswers.size()) {
                answer = allAnswers.get(i);
            }

            if (enableDebug) {
                System.out.println("Question " + question.getId() + ": answer = " + answer);
            }

            submission.setAnswer(question.getId(), answer);
        }

        return submission;
    }

    @Override
    public ScanResult calculateScore(Submission submission, ExamDocument exam) {
        if (enableDebug) {
            System.out.println("Calculating score for student: " + submission.getStudentId());
            System.out.println("Number of answers: " + submission.getStudentAnswers().size());
        }

        ScanResult result = new ScanResult(submission.getId(), 0.0);

        int correctAnswers = 0;
        int totalQuestions = exam.getQuestions().size();

        for (Question question : exam.getQuestions()) {
            String studentAnswer = submission.getAnswer(question.getId());
            String correctAnswer = question.getCorrectAnswer();

            if (enableDebug) {
                System.out.println("Question " + question.getId() + ":");
                System.out.println("  - Student answer: " +
                        (studentAnswer != null ? studentAnswer : "not provided"));
                System.out.println("  - Correct answer: " + correctAnswer);
            }

            studentAnswer = textExtractor.cleanAnswer(studentAnswer);
            correctAnswer = textExtractor.cleanAnswer(correctAnswer);

            double confidence = textExtractor.calculateSimilarity(studentAnswer, correctAnswer);
            result.setConfidenceLevel(question.getId(), confidence);

            if (enableDebug) {
                System.out.println("  - Confidence: " + confidence);
                System.out.println("  - Correct: " + (confidence >= 0.8 ? "YES" : "NO"));
            }

            if (confidence >= 0.8) {
                correctAnswers++;
            }
        }

        double score = (double) correctAnswers / totalQuestions * 100;
        result.setScore(score);

        if (enableDebug) {
            System.out.println("Final score: " + score + "% (" +
                    correctAnswers + "/" + totalQuestions + ")");
        }

        return result;
    }

    @Override
    public String generateReport(Submission submission, ExamDocument exam, ScanResult scanResult) {
        StringBuilder report = new StringBuilder();

        report.append("EVALUATION REPORT\n");
        report.append("===================\n\n");

        report.append("Student ID: ").append(submission.getStudentId()).append("\n");
        report.append("Exam: ").append(exam.getTitle()).append("\n");
        report.append("Submission date: ").append(submission.getSubmissionDate()).append("\n");
        report.append("Total score: ").append(String.format("%.2f", scanResult.getScore())).append("%\n\n");

        report.append("ANSWERS DETAILS\n");
        report.append("-------------------\n\n");
        int questionOrder = 0;

        for (Question question : exam.getQuestions()) {
            questionOrder++;
            String studentAnswer = submission.getAnswer(question.getId());
            String correctAnswer = question.getCorrectAnswer();
            Double confidence = scanResult.getConfidenceLevel(question.getId());

            report.append("Question ").append(questionOrder).append(": ")
                    .append(question.getText()).append("\n");
            report.append("Correct answer: ").append(correctAnswer).append("\n");
            report.append("Student answer: ").append(studentAnswer).append("\n");

            if (confidence != null) {
                report.append("Confidence level: ")
                        .append(String.format("%.2f", confidence)).append("\n");

                if (confidence >= 0.8) {
                    report.append("Evaluation: CORRECT\n");
                } else {
                    report.append("Evaluation: INCORRECT\n");
                }
            } else {
                report.append("Evaluation: NOT EVALUATED\n");
            }

            report.append("\n");
        }

        if (enableDebug) {
            try {
                File debugDir = new File("debug-ocr");
                if (!debugDir.exists()) debugDir.mkdirs();

                File reportFile = new File(debugDir, "report.txt");
                try (java.io.FileWriter writer = new java.io.FileWriter(reportFile)) {
                    writer.write(report.toString());
                }

                System.out.println("Report saved in " + reportFile.getAbsolutePath());
            } catch (Exception e) {
                System.err.println("Error saving report: " + e.getMessage());
            }
        }

        return report.toString();
    }

    @Override
    public void setDebugMode(boolean enabled) {
        this.enableDebug = enabled;
        if (imageProcessor != null) imageProcessor.setDebugMode(enabled);
        if (textExtractor != null) textExtractor.setDebugMode(enabled);
    }

    @Override
    public void setRotationCorrection(boolean enabled) {
        this.enableRotationCorrection = enabled;
        if (imageProcessor != null) imageProcessor.setRotationCorrection(enabled);
    }

    @Override
    public void setImageEnhancement(boolean enabled) {
        this.enableImageEnhancement = enabled;
        if (imageProcessor != null) imageProcessor.setImageEnhancement(enabled);
    }

    /**
     * Adjusts red detection parameters.
     * @param hueLow Lower limit for red hue (0.0-1.0)
     * @param hueHigh Upper limit for red hue (0.0-1.0)
     * @param saturation Threshold for saturation (0.0-1.0)
     * @param brightness Threshold for brightness (0.0-1.0)
     */
    public void setRedDetectionParameters(float hueLow, float hueHigh, float saturation, float brightness) {
        if (imageProcessor != null) {
            imageProcessor.setRedDetectionParameters(hueLow, hueHigh, saturation, brightness);
        }
    }

    /**
     * Main method for testing the OCR service.
     */
    public static void main(String[] args) {
        try {
            TesseractOCRService ocrService = new TesseractOCRService();
            ocrService.setDebugMode(true);
            ocrService.setImageEnhancement(true);
            ocrService.setRotationCorrection(true);

            File scanDir = new File("external-data/scans");
            if (!scanDir.exists() || !scanDir.isDirectory()) {
                System.err.println("Scan folder not found: " + scanDir.getAbsolutePath());
                scanDir.mkdirs();
                System.err.println("Folder created. Please place test images in it.");
                return;
            }

            File[] imageFiles = scanDir.listFiles((dir, name) ->
                    name.toLowerCase().endsWith(".jpg") ||
                            name.toLowerCase().endsWith(".jpeg") ||
                            name.toLowerCase().endsWith(".png"));

            if (imageFiles == null || imageFiles.length == 0) {
                System.err.println("No images found in " + scanDir.getAbsolutePath());
                return;
            }

            File scanFile = imageFiles[0];
            System.out.println("Processing image: " + scanFile.getName());

            ExamDocument exam = new ExamDocument("Test Exam", 1L);
            List<Question> questions = new ArrayList<>();

            Question q1 = new Question("What is the capital of France?", "Paris");
            q1.setId(1L);
            Question q2 = new Question("What are the initials of École Supérieur d'informatique?", "ESI");
            q2.setId(2L);
            Question q3 = new Question("What is the most used programming language?", "Python");
            q3.setId(3L);

            questions.add(q1);
            questions.add(q2);
            questions.add(q3);

            exam.setQuestions(questions);

            Submission submission = ocrService.processExam(scanFile, exam);
            System.out.println("\nSubmission created:");
            System.out.println("Student ID: " + submission.getStudentId());

            for (Question q : questions) {
                System.out.println("Answer to Q" + q + ": " + submission.getAnswer(q.getId()));
            }

            ScanResult result = ocrService.calculateScore(submission, exam);
            System.out.println("\nScore: " + result.getScore() + "%");

            String report = ocrService.generateReport(submission, exam, result);
            System.out.println("\nReport:\n" + report);

            System.out.println("Test completed. Check the debug-ocr folder.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
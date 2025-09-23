package be.esi.prj.easyeval.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents the result of scanning and processing a student's exam submission.
 * Contains score and confidence levels for OCR recognition.
 */
public class ScanResult {
    private Long id;
    private Long submissionId;
    private double score;
    private Map<Long, Double> confidenceLevels; // Question ID -> OCR Confidence Level
    private LocalDateTime scanDate;
    
    /**
     * Default constructor.
     */
    public ScanResult() {
        this.confidenceLevels = new HashMap<>();
        this.scanDate = LocalDateTime.now();
    }
    
    /**
     * Constructor with submission ID and score.
     * @param submissionId The ID of the submission
     * @param score The score (percentage or grade)
     */
    public ScanResult(Long submissionId, double score) {
        this();
        this.submissionId = submissionId;
        this.score = score;
    }
    
    /**
     * Full constructor.
     * @param id The scan result ID
     * @param submissionId The ID of the submission
     * @param score The score (percentage or grade)
     * @param scanDate The date and time of the scan
     */
    public ScanResult(Long id, Long submissionId, double score, LocalDateTime scanDate) {
        this();
        this.id = id;
        this.submissionId = submissionId;
        this.score = score;
        this.scanDate = scanDate;
    }
    
    // Getters and setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getSubmissionId() {
        return submissionId;
    }
    
    public void setSubmissionId(Long submissionId) {
        this.submissionId = submissionId;
    }
    
    public double getScore() {
        return score;
    }
    
    public void setScore(double score) {
        this.score = score;
    }
    
    public Map<Long, Double> getConfidenceLevels() {
        return confidenceLevels;
    }
    
    public void setConfidenceLevels(Map<Long, Double> confidenceLevels) {
        this.confidenceLevels = confidenceLevels;
    }
    
    /**
     * Sets the confidence level for a specific question's OCR recognition.
     * @param questionId The ID of the question
     * @param confidenceLevel The confidence level (0.0 to 1.0)
     */
    public void setConfidenceLevel(Long questionId, double confidenceLevel) {
        if (this.confidenceLevels == null) {
            this.confidenceLevels = new HashMap<>();
        }
        this.confidenceLevels.put(questionId, confidenceLevel);
    }
    
    /**
     * Gets the confidence level for a specific question's OCR recognition.
     * @param questionId The ID of the question
     * @return The confidence level (0.0 to 1.0)
     */
    public Double getConfidenceLevel(Long questionId) {
        return this.confidenceLevels.get(questionId);
    }
    
    public LocalDateTime getScanDate() {
        return scanDate;
    }
    
    public void setScanDate(LocalDateTime scanDate) {
        this.scanDate = scanDate;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScanResult that = (ScanResult) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "ScanResult{" +
                "id=" + id +
                ", submissionId=" + submissionId +
                ", score=" + score +
                ", confidenceLevelsCount=" + (confidenceLevels != null ? confidenceLevels.size() : 0) +
                ", scanDate=" + scanDate +
                '}';
    }
}
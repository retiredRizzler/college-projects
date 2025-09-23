package be.esi.prj.easyeval.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a student's exam submission in the easyEval system.
 */
public class Submission {
    private Long id;
    private Long examId;
    private String studentId;
    private Map<Long, String> studentAnswers; // Question ID -> Student Answer
    private LocalDateTime submissionDate;
    
    /**
     * Default constructor.
     */
    public Submission() {
        this.studentAnswers = new HashMap<>();
        this.submissionDate = LocalDateTime.now();
    }
    
    /**
     * Constructor with exam ID and student ID.
     * @param examId The ID of the exam
     * @param studentId The ID/name of the student
     */
    public Submission(Long examId, String studentId) {
        this();
        this.examId = examId;
        this.studentId = studentId;
    }
    
    /**
     * Full constructor.
     * @param id The submission ID
     * @param examId The ID of the exam
     * @param studentId The ID/name of the student
     * @param submissionDate The date and time of submission
     */
    public Submission(Long id, Long examId, String studentId, LocalDateTime submissionDate) {
        this();
        this.id = id;
        this.examId = examId;
        this.studentId = studentId;
        this.submissionDate = submissionDate;
    }
    
    // Getters and setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getExamId() {
        return examId;
    }
    
    public void setExamId(Long examId) {
        this.examId = examId;
    }
    
    public String getStudentId() {
        return studentId;
    }
    
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
    
    public Map<Long, String> getStudentAnswers() {
        return studentAnswers;
    }
    
    public void setStudentAnswers(Map<Long, String> studentAnswers) {
        this.studentAnswers = studentAnswers;
    }
    
    /**
     * Sets a student's answer for a specific question.
     * @param questionId The ID of the question
     * @param answer The student's answer
     */
    public void setAnswer(Long questionId, String answer) {
        if (this.studentAnswers == null) {
            this.studentAnswers = new HashMap<>();
        }
        this.studentAnswers.put(questionId, answer);
    }
    
    /**
     * Gets a student's answer for a specific question.
     * @param questionId The ID of the question
     * @return The student's answer
     */
    public String getAnswer(Long questionId) {
        return this.studentAnswers.get(questionId);
    }
    
    public LocalDateTime getSubmissionDate() {
        return submissionDate;
    }
    
    public void setSubmissionDate(LocalDateTime submissionDate) {
        this.submissionDate = submissionDate;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Submission that = (Submission) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Submission{" +
                "id=" + id +
                ", examId=" + examId +
                ", studentId='" + studentId + '\'' +
                ", answersCount=" + (studentAnswers != null ? studentAnswers.size() : 0) +
                ", submissionDate=" + submissionDate +
                '}';
    }
}
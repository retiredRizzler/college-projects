package be.esi.prj.easyeval.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents an exam document in the easyEval system.
 */
public class ExamDocument {
    private Long id;
    private String title;
    private String instructions;
    private Long courseId;
    private List<Question> questions;
    private LocalDateTime createdAt;
    
    /**
     * Default constructor.
     */
    public ExamDocument() {
        this.questions = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
    }
    
    /**
     * Constructor with title and courseId.
     * @param title The exam title
     * @param courseId The ID of the course this exam belongs to
     */
    public ExamDocument(String title, Long courseId) {
        this();
        this.title = title;
        this.courseId = courseId;
    }
    
    /**
     * Full constructor.
     * @param id The exam ID
     * @param title The exam title
     * @param instructions The exam instructions
     * @param courseId The ID of the course this exam belongs to
     * @param createdAt The creation date and time
     */
    public ExamDocument(Long id, String title, String instructions, Long courseId, LocalDateTime createdAt) {
        this();
        this.id = id;
        this.title = title;
        this.instructions = instructions;
        this.courseId = courseId;
        this.createdAt = createdAt;
    }
    
    // Getters and setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getInstructions() {
        return instructions;
    }
    
    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
    
    public Long getCourseId() {
        return courseId;
    }
    
    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }
    
    public List<Question> getQuestions() {
        return questions;
    }
    
    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
    
    /**
     * Adds a question to this exam document.
     * @param question The question to add
     */
    public void addQuestion(Question question) {
        if (this.questions == null) {
            this.questions = new ArrayList<>();
        }
        this.questions.add(question);
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExamDocument that = (ExamDocument) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "ExamDocument{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", courseId=" + courseId +
                ", questionsCount=" + (questions != null ? questions.size() : 0) +
                ", createdAt=" + createdAt +
                '}';
    }
}
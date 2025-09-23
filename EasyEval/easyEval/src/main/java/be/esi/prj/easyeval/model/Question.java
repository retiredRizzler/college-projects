package be.esi.prj.easyeval.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Classe représentant une question dans le système easyEval.
 */
public class Question {
    private Long id;
    private String text;
    private String correctAnswer;
    private LocalDateTime createdAt;
    
    /**
     * Constructeur par défaut.
     */
    public Question() {
        this.createdAt = LocalDateTime.now();
    }
    
    /**
     * Constructeur avec texte et réponse correcte.
     * @param text Le texte de la question
     * @param correctAnswer La réponse correcte à la question
     */
    public Question(String text, String correctAnswer) {
        this();
        this.text = text;
        this.correctAnswer = correctAnswer;
    }
    
    /**
     * Constructeur complet.
     * @param id L'identifiant de la question
     * @param text Le texte de la question
     * @param correctAnswer La réponse correcte à la question
     * @param createdAt La date de création de la question
     */
    public Question(Long id, String text, String correctAnswer, LocalDateTime createdAt) {
        this.id = id;
        this.text = text;
        this.correctAnswer = correctAnswer;
        this.createdAt = createdAt;
    }
    
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public String getCorrectAnswer() {
        return correctAnswer;
    }
    
    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
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
        Question question = (Question) o;
        return Objects.equals(id, question.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", correctAnswer='" + correctAnswer + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
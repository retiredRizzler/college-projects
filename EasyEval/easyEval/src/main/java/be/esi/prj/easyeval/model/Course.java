package be.esi.prj.easyeval.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Classe représentant une matière.
 */
public class Course {
    private Long id;
    private String name;
    private LocalDateTime createdAt;
    
    /**
     * Constructeur par défaut.
     */
    public Course() {
        this.createdAt = LocalDateTime.now();
    }
    
    /**
     * Constructeur avec nom.
     * @param name Le nom de la matière
     */
    public Course(String name) {
        this();
        this.name = name;
    }
    
    /**
     * Constructeur complet.
     * @param id L'identifiant de la matière
     * @param name Le nom de la matière
     * @param createdAt La date de création de la matière
     */
    public Course(Long id, String name, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
    }
        
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
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
        Course course = (Course) o;
        return Objects.equals(id, course.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}

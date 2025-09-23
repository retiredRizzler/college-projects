package be.esi.prj.easyeval.repository;

import be.esi.prj.easyeval.model.Course;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CourseRepository {
    private final Connection connection;
    private final DateTimeFormatter formatter;

    public CourseRepository() {
        connection = ConnectionManager.getConnection();
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }

    // Constructor with connection for testing
    CourseRepository(Connection connection) {
        this.connection = connection;
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }

    public Optional<Course> findById(long id) {
        String sql = """
                SELECT 
                    * 
                FROM 
                    courses 
                WHERE 
                    id = ?
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("name");
                    String dateTimeText = rs.getString("created_at");
                    LocalDateTime createdAt = LocalDateTime.parse(dateTimeText, formatter);

                    Course course = new Course(id, name, createdAt);
                    return Optional.of(course);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Selection impossible", e);
        }
        return Optional.empty();
    }

    public Optional<Course> findByName(String name) {
        String sql = """
                SELECT 
                    * 
                FROM 
                    courses 
                WHERE 
                    name = ?
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    long id = rs.getLong("id");
                    String dateTimeText = rs.getString("created_at");
                    LocalDateTime createdAt = LocalDateTime.parse(dateTimeText, formatter);

                    Course course = new Course(id, name, createdAt);
                    return Optional.of(course);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Selection impossible", e);
        }
        return Optional.empty();
    }

    public List<Course> findByNameContaining(String substring) {
        List<Course> courses = new ArrayList<>();
        String sql = """
                SELECT 
                    * 
                FROM 
                    courses 
                WHERE 
                    name LIKE ?
                ORDER BY
                    name
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + substring + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    long id = rs.getLong("id");
                    String name = rs.getString("name");
                    String dateTimeText = rs.getString("created_at");
                    LocalDateTime createdAt = LocalDateTime.parse(dateTimeText, formatter);

                    Course course = new Course(id, name, createdAt);
                    courses.add(course);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Selection impossible", e);
        }
        return courses;
    }

    public List<Course> findAll() {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses ORDER BY name";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                long id = rs.getLong("id");
                String name = rs.getString("name");
                String dateTimeText = rs.getString("created_at");
                LocalDateTime createdAt = LocalDateTime.parse(dateTimeText, formatter);

                Course course = new Course(id, name, createdAt);
                courses.add(course);
            }
        } catch (SQLException e) {
            throw new RepositoryException("Selection impossible", e);
        }
        return courses;
    }

    public List<Long> getExamIds(long courseId) {
        List<Long> examIds = new ArrayList<>();
        String sql = """
                SELECT 
                    id 
                FROM 
                    exam_documents 
                WHERE 
                    course_id = ?
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    examIds.add(rs.getLong("id"));
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Selection impossible", e);
        }
        return examIds;
    }

    public boolean existsById(long id) {
        String sql = "SELECT COUNT(*) FROM courses WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RepositoryException("Vérification impossible", e);
        }
    }

    public boolean existsByName(String name) {
        String sql = "SELECT COUNT(*) FROM courses WHERE name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RepositoryException("Vérification impossible", e);
        }
    }

    public long count() {
        String sql = "SELECT COUNT(*) FROM courses";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RepositoryException("Comptage impossible", e);
        }
    }

    private long insert(Course course) {
        String sql = """
                INSERT INTO 
                    courses (name, created_at) 
                VALUES 
                    (?, ?)
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, course.getName());
            String dateTimeText = formatter.format(course.getCreatedAt());
            stmt.setString(2, dateTimeText);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1); // Retourne l'ID généré
                }
            }

        } catch (SQLException e) {
            throw new RepositoryException("Insertion impossible", e);
        }
        return -1;
    }

    private int update(Course course) {
        String sql = """
                UPDATE courses
                SET
                    name = ?, created_at = ?
                WHERE id = ?
                """;
        int updatedRows = 0;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, course.getName());
            String dateTimeText = formatter.format(course.getCreatedAt());
            stmt.setString(2, dateTimeText);
            stmt.setLong(3, course.getId());
            updatedRows = stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Sauvegarde impossible", e);
        }
        return updatedRows; // Retourne le nombre de lignes affectées
    }

    public Course save(Course course) {
        if (course.getId() != null && existsById(course.getId())) {
            update(course);
        } else {
            long newId = insert(course);
            course.setId(newId);
        }
        return course;
    }

    public boolean deleteById(long id) {
        String sql = "DELETE FROM courses WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                new QuestionRepository().deleteOrphanedQuestions(); // same as in ExamDocumentRepository
            }
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RepositoryException("Suppression impossible", e);
        }
    }

    public boolean deleteAllCourses() {
        String sql = "DELETE FROM courses";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RepositoryException("Suppression de tous les cours impossible", e);
        }
    }

    public void close() {
        ConnectionManager.close();
    }
}
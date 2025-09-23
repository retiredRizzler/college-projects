package be.esi.prj.easyeval.repository;

import be.esi.prj.easyeval.model.ExamDocument;
import be.esi.prj.easyeval.model.Question;
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

/**
 * Repository for handling ExamDocument entity persistence operations.
 */
public class ExamDocumentRepository {
    private final Connection connection;
    private final DateTimeFormatter formatter;
    private final QuestionRepository questionRepository;

    /**
     * Default constructor.
     */
    public ExamDocumentRepository() {
        connection = ConnectionManager.getConnection();
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        questionRepository = new QuestionRepository();
    }

    /**
     * Constructor for testing.
     */
    ExamDocumentRepository(Connection connection, QuestionRepository questionRepository) {
        this.connection = connection;
        this.formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.questionRepository = questionRepository;
    }

    /**
     * Finds an exam document by its ID.
     */
    public Optional<ExamDocument> findById(Long id) {
        String sql = """
                SELECT 
                    id, title, instructions, course_id, created_at 
                FROM 
                    exam_documents 
                WHERE 
                    id = ?
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ExamDocument examDocument = new ExamDocument();
                    examDocument.setId(rs.getLong("id"));
                    examDocument.setTitle(rs.getString("title"));
                    examDocument.setInstructions(rs.getString("instructions"));
                    examDocument.setCourseId(rs.getLong("course_id"));
                    examDocument.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

                    // Load questions associated with this exam
                    List<Question> questions = questionRepository.findByExamId(examDocument.getId());
                    examDocument.setQuestions(questions);

                    return Optional.of(examDocument);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Selection impossible", e);
        }
        return Optional.empty();
    }

    /**
     * Retrieves all exam documents.
     */
    public List<ExamDocument> findAll() {
        List<ExamDocument> examDocuments = new ArrayList<>();
        String sql = "SELECT id, title, instructions, course_id, created_at FROM exam_documents ORDER BY created_at DESC";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                ExamDocument examDocument = new ExamDocument();
                examDocument.setId(rs.getLong("id"));
                examDocument.setTitle(rs.getString("title"));
                examDocument.setInstructions(rs.getString("instructions"));
                examDocument.setCourseId(rs.getLong("course_id"));
                examDocument.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                examDocuments.add(examDocument);
            }
        } catch (SQLException e) {
            throw new RepositoryException("Selection impossible", e);
        }
        return examDocuments;
    }

    /**
     * Finds all exam documents for a specific course.
     */
    public List<ExamDocument> findByCourseId(Long courseId) {
        List<ExamDocument> examDocuments = new ArrayList<>();
        String sql = """
                SELECT 
                    id, title, instructions, course_id, created_at 
                FROM 
                    exam_documents 
                WHERE 
                    course_id = ? 
                ORDER BY 
                    created_at DESC
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ExamDocument examDocument = new ExamDocument();
                    examDocument.setId(rs.getLong("id"));
                    examDocument.setTitle(rs.getString("title"));
                    examDocument.setInstructions(rs.getString("instructions"));
                    examDocument.setCourseId(rs.getLong("course_id"));
                    examDocument.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    examDocuments.add(examDocument);

                    // Load questions associated with this exam
                    List<Question> questions = questionRepository.findByExamId(examDocument.getId());
                    examDocument.setQuestions(questions);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Selection impossible", e);
        }
        return examDocuments;
    }

    /**
     * Inserts a new exam document.
     */
    private long insert(ExamDocument examDocument) {
        String sql = """
                INSERT INTO 
                    exam_documents (title, instructions, course_id) 
                VALUES 
                    (?, ?, ?)
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, examDocument.getTitle());
            stmt.setString(2, examDocument.getInstructions());
            stmt.setLong(3, examDocument.getCourseId());
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

    /**
     * Updates an existing exam document.
     */
    private int update(ExamDocument examDocument) {
        String sql = """
                UPDATE 
                    exam_documents 
                SET 
                    title = ?, instructions = ?, course_id = ? 
                WHERE 
                    id = ?
                """;
        int updatedRows = 0;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, examDocument.getTitle());
            stmt.setString(2, examDocument.getInstructions());
            stmt.setLong(3, examDocument.getCourseId());
            stmt.setLong(4, examDocument.getId());
            updatedRows = stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Mise à jour impossible", e);
        }
        return updatedRows;
    }

    /**
     * Saves an exam document (create or update).
     */
    public ExamDocument save(ExamDocument examDocument) {
        try {
            boolean isUpdate = examDocument.getId() != null && existsById(examDocument.getId());

            if (isUpdate) {
                update(examDocument);
            } else {
                long newId = insert(examDocument);
                examDocument.setId(newId);
            }

            // First clear existing associations if updating
            if (isUpdate) {
                String deleteSQL = "DELETE FROM exam_questions WHERE exam_id = ?";
                try (PreparedStatement deleteStmt = connection.prepareStatement(deleteSQL)) {
                    deleteStmt.setLong(1, examDocument.getId());
                    deleteStmt.executeUpdate();
                }
            }

            // If there are questions, associate them with the exam
            if (examDocument.getQuestions() != null && !examDocument.getQuestions().isEmpty()) {

                for (int i = 0; i < examDocument.getQuestions().size(); i++) {
                    Question question = examDocument.getQuestions().get(i);
                    question = questionRepository.save(question);
                    questionRepository.addQuestionToExam(examDocument.getId(), question.getId(), i + 1);
                }
            }

            return examDocument;
        } catch (SQLException e) {
            throw new RepositoryException("Sauvegarde impossible", e);
        }
    }

    /**
     * Deletes an exam document by its ID.
     */
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM exam_documents WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                questionRepository.deleteOrphanedQuestions(); // so questions with no documents are deleted
            }
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RepositoryException("Suppression impossible", e);
        }
    }

    /**
     * Checks if an exam document exists with the given ID.
     */
    public boolean existsById(Long id) throws SQLException {
        String sql = "SELECT 1 FROM exam_documents WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Counts the total number of exam documents.
     */
    public long count() {
        String sql = "SELECT COUNT(*) FROM exam_documents";
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

    /**
     * Counts the number of exam documents for a specific course.
     */
    public long countByCourseId(Long courseId) {
        String sql = "SELECT COUNT(*) FROM exam_documents WHERE course_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            throw new RepositoryException("Comptage impossible", e);
        }
    }

    /**
     * Closes the database connection.
     */
    public void close() {
        ConnectionManager.close();
    }
}
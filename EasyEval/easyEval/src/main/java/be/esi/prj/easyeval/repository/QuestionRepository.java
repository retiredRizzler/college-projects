package be.esi.prj.easyeval.repository;

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
 * Repository for handling Question entity persistence operations.
 * Implements direct database access for question management.
 */
public class QuestionRepository {
    private final Connection connection;
    private final DateTimeFormatter formatter;

    /**
     * Default constructor using ConnectionManager to get a connection.
     */
    public QuestionRepository() {
        connection = ConnectionManager.getConnection();
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * Constructor with connection for testing.
     *
     * @param connection Database connection to use
     */
    QuestionRepository(Connection connection) {
        this.connection = connection;
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * Finds a question by its ID.
     *
     * @param id The ID of the question to find
     * @return An Optional containing the question if found, otherwise an empty Optional
     */
    public Optional<Question> findById(Long id) {
        String sql = """
                SELECT 
                    id, text, correct_answer, created_at 
                FROM 
                    questions 
                WHERE 
                    id = ?
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Question question = new Question();
                    question.setId(rs.getLong("id"));
                    question.setText(rs.getString("text"));
                    question.setCorrectAnswer(rs.getString("correct_answer"));
                    question.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    return Optional.of(question);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error finding question by ID", e);
        }
        return Optional.empty();
    }

    /**
     * Retrieves all questions from the database.
     *
     * @return List of all questions
     */
    public List<Question> findAll() {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT id, text, correct_answer, created_at FROM questions ORDER BY created_at DESC";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Question question = new Question();
                question.setId(rs.getLong("id"));
                question.setText(rs.getString("text"));
                question.setCorrectAnswer(rs.getString("correct_answer"));
                question.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                questions.add(question);
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error retrieving all questions", e);
        }
        return questions;
    }

    /**
     * Finds questions containing the specified text.
     *
     * @param textPart Part of the text to search for
     * @return List of questions matching the search criteria
     */
    public List<Question> findByTextContaining(String textPart) {
        List<Question> questions = new ArrayList<>();
        String sql = """
                SELECT 
                    id, text, correct_answer, created_at 
                FROM 
                    questions 
                WHERE 
                    text LIKE ? 
                ORDER BY 
                    created_at DESC
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + textPart + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Question question = new Question();
                    question.setId(rs.getLong("id"));
                    question.setText(rs.getString("text"));
                    question.setCorrectAnswer(rs.getString("correct_answer"));
                    question.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    questions.add(question);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error finding questions containing text", e);
        }
        return questions;
    }

    /**
     * Finds all questions associated with a specific exam.
     *
     * @param examId The ID of the exam
     * @return List of questions belonging to the exam, ordered by their position in the exam
     */
    public List<Question> findByExamId(Long examId) {
        List<Question> questions = new ArrayList<>();
        String sql = """
                SELECT 
                    q.id, q.text, q.correct_answer, q.created_at 
                FROM 
                    questions q 
                JOIN 
                    exam_questions eq ON q.id = eq.question_id 
                WHERE 
                    eq.exam_id = ? 
                ORDER BY 
                    eq.question_order
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, examId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Question question = new Question();
                    question.setId(rs.getLong("id"));
                    question.setText(rs.getString("text"));
                    question.setCorrectAnswer(rs.getString("correct_answer"));
                    question.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    questions.add(question);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error finding questions by exam ID", e);
        }
        return questions;
    }

    /**
     * Insert a new question.
     *
     * @param question The question to insert
     * @return The ID of the inserted question
     */
    private long insert(Question question) {
        String sql = """
                INSERT INTO 
                    questions (text, correct_answer) 
                VALUES 
                    (?, ?)
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, question.getText());
            stmt.setString(2, question.getCorrectAnswer());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1); // Retourne l'ID généré
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error inserting question", e);
        }
        return -1;
    }

    /**
     * Update an existing question.
     *
     * @param question The question to update
     * @return Number of rows affected
     */
    private int update(Question question) {
        String sql = """
                UPDATE 
                    questions 
                SET 
                    text = ?, correct_answer = ? 
                WHERE 
                    id = ?
                """;
        int updatedRows = 0;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, question.getText());
            stmt.setString(2, question.getCorrectAnswer());
            stmt.setLong(3, question.getId());
            updatedRows = stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Error updating question", e);
        }
        return updatedRows;
    }

    /**
     * Saves a question to the database (create or update).
     *
     * @param question The question to save
     * @return The saved question with generated ID if it was a new entity
     */
    public Question save(Question question) {
        try {
            if (question.getId() != null && existsById(question.getId())) {
                update(question);
            } else {
                long newId = insert(question);
                question.setId(newId);
            }
            return question;
        } catch (SQLException e) {
            throw new RepositoryException("Error saving question", e);
        }
    }

    /**
     * Deletes a question by its ID.
     *
     * @param id The ID of the question to delete
     * @return true if the question was deleted, false otherwise
     */
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM questions WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RepositoryException("Error deleting question", e);
        }
    }

    /**
     * Checks if a question exists with the given ID.
     *
     * @param id The ID to check
     * @return true if a question exists with this ID, false otherwise
     * @throws SQLException if database access fails
     */
    public boolean existsById(Long id) throws SQLException {
        String sql = "SELECT 1 FROM questions WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Counts the total number of questions.
     *
     * @return The number of questions
     */
    public long count() {
        String sql = "SELECT COUNT(*) FROM questions";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RepositoryException("Error counting questions", e);
        }
    }

    /**
     * Associates an existing question with an exam.
     *
     * @param examId The ID of the exam
     * @param questionId The ID of the question
     * @param questionOrder The order of the question in the exam
     * @return true if the association is successful, false otherwise
     */
    public boolean addQuestionToExam(Long examId, Long questionId, int questionOrder) {
        String sql = """
                INSERT INTO 
                    exam_questions (exam_id, question_id, question_order) 
                VALUES 
                    (?, ?, ?)
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, examId);
            stmt.setLong(2, questionId);
            stmt.setInt(3, questionOrder);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RepositoryException("Error adding question to exam", e);
        }
    }

    /**
     * Removes a question from an exam.
     *
     * @param examId The ID of the exam
     * @param questionId The ID of the question
     * @return true if the removal is successful, false otherwise
     */
    public boolean removeQuestionFromExam(Long examId, Long questionId) {
        String sql = "DELETE FROM exam_questions WHERE exam_id = ? AND question_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, examId);
            stmt.setLong(2, questionId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RepositoryException("Error removing question from exam", e);
        }
    }

    /**
     * Updates the order of questions in an exam.
     *
     * @param examId The ID of the exam
     * @param questionIds List of question IDs in the desired order
     * @return true if the update is successful, false otherwise
     */
    public boolean updateQuestionOrder(Long examId, List<Long> questionIds) {
        String sql = "UPDATE exam_questions SET question_order = ? WHERE exam_id = ? AND question_id = ?";
        try {
            boolean autoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                for (int i = 0; i < questionIds.size(); i++) {
                    stmt.setInt(1, i + 1);  // Order starts at 1
                    stmt.setLong(2, examId);
                    stmt.setLong(3, questionIds.get(i));
                    stmt.addBatch();
                }

                int[] affectedRows = stmt.executeBatch();
                connection.commit();

                // Check if all updates were successful
                for (int rows : affectedRows) {
                    if (rows <= 0) {
                        return false;
                    }
                }

                return true;
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(autoCommit);
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error updating question order", e);
        }
    }

    /**
     * Deletes questions that aren't associated with any exam.
     * @return The number of orphaned questions deleted
     */
    public int deleteOrphanedQuestions() {
        // we need this method cause the on delete cascade is only set for exam_documents and exam_questions table
        String sql = """
                DELETE FROM questions 
                WHERE id NOT IN (
                    SELECT DISTINCT question_id FROM exam_questions
                )
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Error deleting orphaned questions", e);
        }
    }

    /**
     * Closes the database connection.
     */
    public void close() {
        ConnectionManager.close();
    }
}
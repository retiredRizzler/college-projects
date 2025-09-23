package be.esi.prj.easyeval.repository;

import be.esi.prj.easyeval.model.Submission;
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
 * Repository for handling Submission entity persistence operations.
 */
public class SubmissionRepository {
    private final Connection connection;
    private final DateTimeFormatter formatter;

    /**
     * Default constructor.
     */
    public SubmissionRepository() {
        connection = ConnectionManager.getConnection();
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * Constructor for testing.
     */
    SubmissionRepository(Connection connection) {
        this.connection = connection;
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * Finds a submission by its ID.
     */
    public Optional<Submission> findById(Long id) {
        String sql = """
                SELECT 
                    id, exam_id, student_id, submission_date 
                FROM 
                    submissions 
                WHERE 
                    id = ?
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Submission submission = new Submission();
                    submission.setId(rs.getLong("id"));
                    submission.setExamId(rs.getLong("exam_id"));
                    submission.setStudentId(rs.getString("student_id"));
                    submission.setSubmissionDate(rs.getTimestamp("submission_date").toLocalDateTime());

                    // Load student answers
                    loadStudentAnswers(submission);

                    return Optional.of(submission);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Selection impossible", e);
        }
        return Optional.empty();
    }

    /**
     * Helper method to load student answers for a submission.
     */
    private void loadStudentAnswers(Submission submission) {
        String sql = """
                SELECT 
                    question_id, answer 
                FROM 
                    student_answers 
                WHERE 
                    submission_id = ?
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, submission.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Long questionId = rs.getLong("question_id");
                    String answer = rs.getString("answer");
                    submission.setAnswer(questionId, answer);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Chargement des réponses impossible", e);
        }
    }

    /**
     * Retrieves all submissions.
     */
    public List<Submission> findAll() {
        List<Submission> submissions = new ArrayList<>();
        String sql = "SELECT id, exam_id, student_id, submission_date FROM submissions ORDER BY submission_date DESC";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Submission submission = new Submission();
                submission.setId(rs.getLong("id"));
                submission.setExamId(rs.getLong("exam_id"));
                submission.setStudentId(rs.getString("student_id"));
                submission.setSubmissionDate(rs.getTimestamp("submission_date").toLocalDateTime());
                submissions.add(submission);
            }

            // Load student answers for each submission
            for (Submission submission : submissions) {
                loadStudentAnswers(submission);
            }
        } catch (SQLException e) {
            throw new RepositoryException("Selection impossible", e);
        }
        return submissions;
    }

    /**
     * Finds all submissions for a specific exam.
     */
    public List<Submission> findByExamId(Long examId) {
        List<Submission> submissions = new ArrayList<>();
        String sql = """
                SELECT 
                    id, exam_id, student_id, submission_date 
                FROM 
                    submissions 
                WHERE 
                    exam_id = ? 
                ORDER BY 
                    submission_date DESC
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, examId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Submission submission = new Submission();
                    submission.setId(rs.getLong("id"));
                    submission.setExamId(rs.getLong("exam_id"));
                    submission.setStudentId(rs.getString("student_id"));
                    submission.setSubmissionDate(rs.getTimestamp("submission_date").toLocalDateTime());
                    submissions.add(submission);
                }
            }

            // Load student answers for each submission
            for (Submission submission : submissions) {
                loadStudentAnswers(submission);
            }
        } catch (SQLException e) {
            throw new RepositoryException("Selection impossible", e);
        }
        return submissions;
    }

    /**
     * Finds submissions for a specific student.
     */
    public List<Submission> findByStudentId(String studentId) {
        List<Submission> submissions = new ArrayList<>();
        String sql = """
                SELECT 
                    id, exam_id, student_id, submission_date 
                FROM 
                    submissions 
                WHERE 
                    student_id = ? 
                ORDER BY 
                    submission_date DESC
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Submission submission = new Submission();
                    submission.setId(rs.getLong("id"));
                    submission.setExamId(rs.getLong("exam_id"));
                    submission.setStudentId(rs.getString("student_id"));
                    submission.setSubmissionDate(rs.getTimestamp("submission_date").toLocalDateTime());
                    submissions.add(submission);
                }
            }

            // Load student answers for each submission
            for (Submission submission : submissions) {
                loadStudentAnswers(submission);
            }
        } catch (SQLException e) {
            throw new RepositoryException("Selection impossible", e);
        }
        return submissions;
    }

    /**
     * Inserts a new submission.
     */
    private long insert(Submission submission) {
        String sql = """
                INSERT INTO 
                    submissions (exam_id, student_id) 
                VALUES 
                    (?, ?)
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, submission.getExamId());
            stmt.setString(2, submission.getStudentId());
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
     * Updates an existing submission.
     */
    private int update(Submission submission) {
        String sql = """
                UPDATE 
                    submissions 
                SET 
                    exam_id = ?, student_id = ? 
                WHERE 
                    id = ?
                """;
        int updatedRows = 0;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, submission.getExamId());
            stmt.setString(2, submission.getStudentId());
            stmt.setLong(3, submission.getId());
            updatedRows = stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Mise à jour impossible", e);
        }
        return updatedRows;
    }

    /**
     * Saves the student answers for a submission.
     */
    private void saveStudentAnswers(Submission submission, boolean isUpdate) {
        // Clear existing answers if updating
        if (isUpdate) {
            String deleteSQL = """
                    DELETE FROM 
                        student_answers 
                    WHERE 
                        submission_id = ?
                    """;
            try (PreparedStatement deleteStmt = connection.prepareStatement(deleteSQL)) {
                deleteStmt.setLong(1, submission.getId());
                deleteStmt.executeUpdate();
            } catch (SQLException e) {
                throw new RepositoryException("Suppression des réponses impossible", e);
            }
        }

        // Add new answers
        if (submission.getStudentAnswers() != null && !submission.getStudentAnswers().isEmpty()) {
            String answerSQL = """
                    INSERT INTO 
                        student_answers (submission_id, question_id, answer) 
                    VALUES 
                        (?, ?, ?)
                    """;
            try (PreparedStatement answerStmt = connection.prepareStatement(answerSQL)) {
                for (var entry : submission.getStudentAnswers().entrySet()) {
                    answerStmt.setLong(1, submission.getId());
                    answerStmt.setLong(2, entry.getKey());
                    answerStmt.setString(3, entry.getValue());
                    answerStmt.addBatch();
                }
                answerStmt.executeBatch();
            } catch (SQLException e) {
                throw new RepositoryException("Insertion des réponses impossible", e);
            }
        }
    }

    /**
     * Saves a submission (create or update).
     */
    public Submission save(Submission submission) {
        try {
            boolean isUpdate = submission.getId() != null && existsById(submission.getId());
            boolean autoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            try {
                if (isUpdate) {
                    update(submission);
                } else {
                    long newId = insert(submission);
                    submission.setId(newId);
                }

                // Save student answers
                saveStudentAnswers(submission, isUpdate);

                connection.commit();
                return submission;
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(autoCommit);
            }
        } catch (SQLException e) {
            throw new RepositoryException("Sauvegarde impossible", e);
        }
    }

    /**
     * Deletes a submission by its ID.
    */
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM submissions WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RepositoryException("Suppression impossible", e);
        }
    }

    /**
     * Checks if a submission exists with the given ID.
     */
    public boolean existsById(Long id) {
        String sql = "SELECT 1 FROM submissions WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RepositoryException("Vérification d'existence impossible", e);
        }
    }

    /**
     * Counts the total number of submissions.
     */
    public long count() {
        String sql = "SELECT COUNT(*) FROM submissions";
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
     * Closes the database connection.
     */
    public void close() {
        ConnectionManager.close();
    }
}
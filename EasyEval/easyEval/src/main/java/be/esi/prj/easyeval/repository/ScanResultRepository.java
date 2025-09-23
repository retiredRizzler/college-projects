package be.esi.prj.easyeval.repository;

import be.esi.prj.easyeval.model.ScanResult;
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
 * Repository for handling ScanResult entity persistence operations.
 */
public class ScanResultRepository {
    private final Connection connection;
    private final DateTimeFormatter formatter;

    /**
     * Default constructor.
     */
    public ScanResultRepository() {
        connection = ConnectionManager.getConnection();
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * Constructor for testing.
     */
    ScanResultRepository(Connection connection) {
        this.connection = connection;
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * Finds a scan result by its ID.
     */
    public Optional<ScanResult> findById(Long id) {
        String sql = """
                SELECT 
                    id, submission_id, score, scan_date 
                FROM 
                    scan_results 
                WHERE 
                    id = ?
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ScanResult scanResult = new ScanResult();
                    scanResult.setId(rs.getLong("id"));
                    scanResult.setSubmissionId(rs.getLong("submission_id"));
                    scanResult.setScore(rs.getDouble("score"));
                    scanResult.setScanDate(rs.getTimestamp("scan_date").toLocalDateTime());

                    // Load confidence levels
                    loadConfidenceLevels(scanResult);

                    return Optional.of(scanResult);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Selection impossible", e);
        }
        return Optional.empty();
    }

    /**
     * Finds a scan result by submission ID.
     */
    public Optional<ScanResult> findBySubmissionId(Long submissionId) {
        String sql = """
                SELECT 
                    id, submission_id, score, scan_date 
                FROM 
                    scan_results 
                WHERE 
                    submission_id = ?
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, submissionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ScanResult scanResult = new ScanResult();
                    scanResult.setId(rs.getLong("id"));
                    scanResult.setSubmissionId(rs.getLong("submission_id"));
                    scanResult.setScore(rs.getDouble("score"));
                    scanResult.setScanDate(rs.getTimestamp("scan_date").toLocalDateTime());

                    // Load confidence levels
                    loadConfidenceLevels(scanResult);

                    return Optional.of(scanResult);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Selection impossible", e);
        }
        return Optional.empty();
    }

    /**
     * Helper method to load confidence levels for a scan result.
     */
    private void loadConfidenceLevels(ScanResult scanResult) {
        String sql = """
                SELECT 
                    question_id, confidence_level 
                FROM 
                    confidence_levels 
                WHERE 
                    scan_result_id = ?
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, scanResult.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Long questionId = rs.getLong("question_id");
                    double confidenceLevel = rs.getDouble("confidence_level");
                    scanResult.setConfidenceLevel(questionId, confidenceLevel);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Chargement des niveaux de confiance impossible", e);
        }
    }

    /**
     * Retrieves all scan results.
     */
    public List<ScanResult> findAll() {
        List<ScanResult> scanResults = new ArrayList<>();
        String sql = "SELECT id, submission_id, score, scan_date FROM scan_results ORDER BY scan_date DESC";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                ScanResult scanResult = new ScanResult();
                scanResult.setId(rs.getLong("id"));
                scanResult.setSubmissionId(rs.getLong("submission_id"));
                scanResult.setScore(rs.getDouble("score"));
                scanResult.setScanDate(rs.getTimestamp("scan_date").toLocalDateTime());
                scanResults.add(scanResult);
            }

            // Load confidence levels for each scan result
            for (ScanResult scanResult : scanResults) {
                loadConfidenceLevels(scanResult);
            }
        } catch (SQLException e) {
            throw new RepositoryException("Selection impossible", e);
        }
        return scanResults;
    }

    /**
     * Inserts a new scan result.
     */
    private long insert(ScanResult scanResult) {
        String sql = """
                INSERT INTO 
                    scan_results (submission_id, score) 
                VALUES 
                    (?, ?)
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, scanResult.getSubmissionId());
            stmt.setDouble(2, scanResult.getScore());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Insertion impossible", e);
        }
        return -1;
    }

    /**
     * Updates an existing scan result.
     */
    private int update(ScanResult scanResult) {
        String sql = """
                UPDATE 
                    scan_results 
                SET 
                    submission_id = ?, score = ? 
                WHERE 
                    id = ?
                """;
        int updatedRows = 0;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, scanResult.getSubmissionId());
            stmt.setDouble(2, scanResult.getScore());
            stmt.setLong(3, scanResult.getId());
            updatedRows = stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Mise à jour impossible", e);
        }
        return updatedRows;
    }

    /**
     * Saves the confidence levels for a scan result.
     */
    private void saveConfidenceLevels(ScanResult scanResult, boolean isUpdate) {
        // Clear existing confidence levels if updating
        if (isUpdate) {
            String deleteSQL = """
                    DELETE FROM 
                        confidence_levels 
                    WHERE 
                        scan_result_id = ?
                    """;
            try (PreparedStatement deleteStmt = connection.prepareStatement(deleteSQL)) {
                deleteStmt.setLong(1, scanResult.getId());
                deleteStmt.executeUpdate();
            } catch (SQLException e) {
                throw new RepositoryException("Suppression des niveaux de confiance impossible", e);
            }
        }

        // Add new confidence levels
        if (scanResult.getConfidenceLevels() != null && !scanResult.getConfidenceLevels().isEmpty()) {
            String confidenceSQL = """
                    INSERT INTO 
                        confidence_levels (scan_result_id, question_id, confidence_level) 
                    VALUES 
                        (?, ?, ?)
                    """;
            try (PreparedStatement confidenceStmt = connection.prepareStatement(confidenceSQL)) {
                for (var entry : scanResult.getConfidenceLevels().entrySet()) {
                    confidenceStmt.setLong(1, scanResult.getId());
                    confidenceStmt.setLong(2, entry.getKey());
                    confidenceStmt.setDouble(3, entry.getValue());
                    confidenceStmt.addBatch();
                }
                confidenceStmt.executeBatch();
            } catch (SQLException e) {
                throw new RepositoryException("Insertion des niveaux de confiance impossible", e);
            }
        }
    }

    /**
     * Saves a scan result (create or update).
     */
    public ScanResult save(ScanResult scanResult) {
        try {
            boolean isUpdate = scanResult.getId() != null && existsById(scanResult.getId());
            boolean autoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            try {
                if (isUpdate) {
                    update(scanResult);
                } else {
                    long newId = insert(scanResult);
                    scanResult.setId(newId);
                }

                // Save confidence levels
                saveConfidenceLevels(scanResult, isUpdate);

                connection.commit();
                return scanResult;
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
     * Deletes a scan result by its ID.
     */
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM scan_results WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RepositoryException("Suppression impossible", e);
        }
    }

    /**
     * Checks if a scan result exists with the given ID.
     */
    public boolean existsById(Long id) {
        String sql = "SELECT 1 FROM scan_results WHERE id = ?";
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
     * Counts the total number of scan results.
     */
    public long count() {
        String sql = "SELECT COUNT(*) FROM scan_results";
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
     * Gets the average score for a specific exam.
     */
    public double getAverageScoreForExam(Long examId) {
        String sql = """
                SELECT 
                    AVG(sr.score) 
                FROM 
                    scan_results sr 
                JOIN 
                    submissions s ON sr.submission_id = s.id 
                WHERE 
                    s.exam_id = ?
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, examId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
                return -1;
            }
        } catch (SQLException e) {
            throw new RepositoryException("Calcul de la moyenne impossible", e);
        }
    }

    /**
     * Closes the database connection.
     */
    public void close() {
        ConnectionManager.close();
    }
}
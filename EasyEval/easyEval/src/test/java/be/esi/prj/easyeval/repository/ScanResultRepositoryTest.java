package be.esi.prj.easyeval.repository;

import be.esi.prj.easyeval.model.ScanResult;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ScanResultRepositoryTest {
    private static Connection connection;
    private ScanResultRepository instance;
    private final ScanResult aliceScanResult;
    private final ScanResult bobScanResult;

    public ScanResultRepositoryTest() {
        LocalDateTime now = LocalDateTime.now();

        aliceScanResult = new ScanResult();
        aliceScanResult.setId(1L);
        aliceScanResult.setSubmissionId(1L);
        aliceScanResult.setScore(85.5);
        aliceScanResult.setScanDate(now);
        Map<Long, Double> aliceConfidence = new HashMap<>();
        aliceConfidence.put(1L, 0.95);
        aliceConfidence.put(2L, 0.85);
        aliceScanResult.setConfidenceLevels(aliceConfidence);

        bobScanResult = new ScanResult();
        bobScanResult.setId(2L);
        bobScanResult.setSubmissionId(2L);
        bobScanResult.setScore(65.0);
        bobScanResult.setScanDate(now);
        Map<Long, Double> bobConfidence = new HashMap<>();
        bobConfidence.put(1L, 0.75);
        bobConfidence.put(2L, 0.60);
        bobScanResult.setConfidenceLevels(bobConfidence);
    }

    @BeforeAll
    static void setupDatabase() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        try (Statement stmt = connection.createStatement()) {


            stmt.execute("""
                    CREATE TABLE courses (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL UNIQUE,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                    """);

            stmt.execute("""
                    CREATE TABLE questions (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        text TEXT NOT NULL,
                        correct_answer TEXT NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                    """);

            stmt.execute("""
                    CREATE TABLE exam_documents (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        title TEXT NOT NULL,
                        instructions TEXT,
                        course_id INTEGER NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (course_id) REFERENCES courses(id)
                    )
                    """);

            stmt.execute("""
                    CREATE TABLE submissions (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        exam_id INTEGER NOT NULL,
                        student_id TEXT NOT NULL,
                        submission_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (exam_id) REFERENCES exam_documents(id)
                    )
                    """);

            stmt.execute("""
                    CREATE TABLE scan_results (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        submission_id INTEGER NOT NULL UNIQUE,
                        score REAL NOT NULL,
                        scan_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (submission_id) REFERENCES submissions(id) ON DELETE CASCADE
                    )
                    """);

            stmt.execute("""
                    CREATE TABLE confidence_levels (
                        scan_result_id INTEGER NOT NULL,
                        question_id INTEGER NOT NULL,
                        confidence_level REAL NOT NULL,
                        PRIMARY KEY (scan_result_id, question_id),
                        FOREIGN KEY (scan_result_id) REFERENCES scan_results(id) ON DELETE CASCADE,
                        FOREIGN KEY (question_id) REFERENCES questions(id)
                    )
                    """);
        }
    }

    @BeforeEach
    void setup() throws SQLException {
        instance = new ScanResultRepository(connection);

        try (Statement stmt = connection.createStatement()) {

            stmt.execute("PRAGMA foreign_keys = ON");

            stmt.execute("DELETE FROM confidence_levels");
            stmt.execute("DELETE FROM scan_results");
            stmt.execute("DELETE FROM submissions");
            stmt.execute("DELETE FROM exam_documents");
            stmt.execute("DELETE FROM questions");
            stmt.execute("DELETE FROM courses");

            stmt.execute("""
                    INSERT INTO courses (id, name, created_at) VALUES
                    (1, 'Programming', '2024-04-08 10:00:00')
                    """);

            stmt.execute("""
                    INSERT INTO questions (id, text, correct_answer, created_at) VALUES
                    (1, 'What is Java?', 'A programming language', '2024-04-08 10:00:00'),
                    (2, 'What is HTML?', 'HyperText Markup Language', '2024-04-08 10:01:00')
                    """);

            stmt.execute("""
                    INSERT INTO exam_documents (id, title, instructions, course_id, created_at) VALUES
                    (1, 'Web Development Basics', 'Answer all questions', 1, '2024-04-08 10:00:00')
                    """);

            stmt.execute("""
                    INSERT INTO submissions (id, exam_id, student_id, submission_date) VALUES
                    (1, 1, 'Alice', '2024-04-08 10:30:00'),
                    (2, 1, 'Bob', '2024-04-08 10:45:00')
                    """);

            stmt.execute("""
                    INSERT INTO scan_results (id, submission_id, score, scan_date) VALUES
                    (1, 1, 85.5, '2024-04-08 10:35:00'),
                    (2, 2, 65.0, '2024-04-08 10:50:00')
                    """);

            stmt.execute("""
                    INSERT INTO confidence_levels (scan_result_id, question_id, confidence_level) VALUES
                    (1, 1, 0.95),
                    (1, 2, 0.85),
                    (2, 1, 0.75),
                    (2, 2, 0.60)
                    """);
        }
    }

    @AfterEach
    void cleanDatabase() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM confidence_levels");
            stmt.execute("DELETE FROM scan_results");
            stmt.execute("DELETE FROM submissions");
            stmt.execute("DELETE FROM exam_documents");
            stmt.execute("DELETE FROM questions");
            stmt.execute("DELETE FROM courses");
        }
    }

    @AfterAll
    static void closeDatabase() throws SQLException {
        connection.close();
    }

    @Test
    void testFindByIdExist() {
        System.out.println("testFindByIdExist");
        //Arrange
        Long id = 1L;
        //Action
        Optional<ScanResult> result = instance.findById(id);
        //Assert
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getSubmissionId());
        assertEquals(85.5, result.get().getScore());
        // Verify confidence levels are loaded
        assertEquals(2, result.get().getConfidenceLevels().size());
        assertEquals(0.95, result.get().getConfidenceLevels().get(1L));
        assertEquals(0.85, result.get().getConfidenceLevels().get(2L));
    }

    @Test
    void testFindByIdDoesNotExist() {
        System.out.println("testFindByIdDoesNotExist");
        //Arrange
        Long id = 100L;
        //Action
        Optional<ScanResult> result = instance.findById(id);
        //Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testFindBySubmissionId() {
        System.out.println("testFindBySubmissionId");
        //Arrange
        Long submissionId = 1L;
        //Action
        Optional<ScanResult> result = instance.findBySubmissionId(submissionId);
        //Assert
        assertTrue(result.isPresent());
        assertEquals(85.5, result.get().getScore());
        assertEquals(2, result.get().getConfidenceLevels().size());
    }

    @Test
    void testFindAll() {
        System.out.println("testFindAll");
        //Action
        List<ScanResult> results = instance.findAll();
        //Assert
        assertEquals(2, results.size());
        // Check confidence levels are loaded
        for (ScanResult result : results) {
            assertNotNull(result.getConfidenceLevels());
            assertEquals(2, result.getConfidenceLevels().size());
        }
    }

    @Test
    void testSaveNewScanResult() {
        System.out.println("testSaveNewScanResult");
        //Arrange
        // ici il faut créer d'abord une nouvelle submission sinon contrainte pas respecter
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("INSERT INTO submissions (id, exam_id, student_id, submission_date) VALUES " +
                    "(3, 1, 'Charlie', '2024-04-08 11:00:00')");
        } catch (SQLException e) {
            fail("Could not create test submission: " + e.getMessage());
        }

        ScanResult newScanResult = new ScanResult();
        newScanResult.setSubmissionId(3L);
        newScanResult.setScore(75.0);

        Map<Long, Double> confidence = new HashMap<>();
        confidence.put(1L, 0.80);
        confidence.put(2L, 0.70);
        newScanResult.setConfidenceLevels(confidence);

        //Action
        ScanResult savedScanResult = instance.save(newScanResult);

        //Assert
        assertNotNull(savedScanResult.getId());
        assertEquals(75.0, savedScanResult.getScore());

        //Verify in database
        Optional<ScanResult> fromDb = instance.findById(savedScanResult.getId());
        assertTrue(fromDb.isPresent());
        assertEquals(75.0, fromDb.get().getScore());
        assertEquals(2, fromDb.get().getConfidenceLevels().size());
        assertEquals(0.80, fromDb.get().getConfidenceLevels().get(1L));
        assertEquals(0.70, fromDb.get().getConfidenceLevels().get(2L));
    }

    @Test
    void testSaveUpdateScanResult() {
        System.out.println("testSaveUpdateScanResult");
        //Arrange
        Optional<ScanResult> scanResultOpt = instance.findById(1L);
        assertTrue(scanResultOpt.isPresent());
        ScanResult scanResult = scanResultOpt.get();

        // Update score and confidence level
        scanResult.setScore(90.0);
        Map<Long, Double> updatedConfidence = new HashMap<>(scanResult.getConfidenceLevels());
        updatedConfidence.put(1L, 0.98);
        scanResult.setConfidenceLevels(updatedConfidence);

        //Action
        ScanResult updatedScanResult = instance.save(scanResult);

        //Assert
        assertEquals(1L, updatedScanResult.getId());
        assertEquals(90.0, updatedScanResult.getScore());
        assertEquals(0.98, updatedScanResult.getConfidenceLevels().get(1L));

        //Verify in database
        Optional<ScanResult> fromDb = instance.findById(1L);
        assertTrue(fromDb.isPresent());
        assertEquals(90.0, fromDb.get().getScore());
        assertEquals(0.98, fromDb.get().getConfidenceLevels().get(1L));
    }

    @Test
    void testDeleteById() {
        System.out.println("testDeleteById");
        //Arrange
        Long id = 1L;
        //Action
        boolean result = instance.deleteById(id);
        //Assert
        assertTrue(result);
        assertFalse(instance.findById(id).isPresent());

        // Verify confidence levels are also deleted (cascade)
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM confidence_levels WHERE scan_result_id = 1")) {
            assertTrue(rs.next());
            assertEquals(0, rs.getInt(1));
        } catch (SQLException e) {
            fail("Database query failed: " + e.getMessage());
        }
    }

    @Test
    void testDeleteByIdNonExistent() {
        System.out.println("testDeleteByIdNonExistent");
        //Arrange
        Long id = 100L;
        //Action
        boolean result = instance.deleteById(id);
        //Assert
        assertFalse(result);
    }

    @Test
    void testExistsById() {
        System.out.println("testExistsById");
        //Action & Assert
        assertTrue(instance.existsById(1L));
        assertFalse(instance.existsById(100L));
    }

    @Test
    void testCount() {
        System.out.println("testCount");
        //Action
        long result = instance.count();
        //Assert
        assertEquals(2, result);
    }

    @Test
    void testGetAverageScoreForExam() {
        System.out.println("testGetAverageScoreForExam");
        //Arrange
        Long examId = 1L;
        //Action
        double result = instance.getAverageScoreForExam(examId);
        //Assert
        assertEquals(75.25, result, 0.01); // 85.5 + 65.0 / 2 = 75.25
    }
}
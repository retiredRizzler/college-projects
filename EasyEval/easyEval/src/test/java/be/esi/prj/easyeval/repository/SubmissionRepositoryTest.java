package be.esi.prj.easyeval.repository;

import be.esi.prj.easyeval.model.Submission;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SubmissionRepositoryTest {
    private static Connection connection;
    private SubmissionRepository instance;
    private final Submission aliceSubmission;
    private final Submission bobSubmission;

    public SubmissionRepositoryTest() {
        LocalDateTime now = LocalDateTime.now();

        aliceSubmission = new Submission();
        aliceSubmission.setId(1L);
        aliceSubmission.setExamId(1L);
        aliceSubmission.setStudentId("Alice");
        aliceSubmission.setSubmissionDate(now);
        Map<Long, String> aliceAnswers = new HashMap<>();
        aliceAnswers.put(1L, "A programming language");
        aliceAnswers.put(2L, "HyperText Markup Language");
        aliceSubmission.setStudentAnswers(aliceAnswers);

        bobSubmission = new Submission();
        bobSubmission.setId(2L);
        bobSubmission.setExamId(1L);
        bobSubmission.setStudentId("Bob");
        bobSubmission.setSubmissionDate(now);
        Map<Long, String> bobAnswers = new HashMap<>();
        bobAnswers.put(1L, "A coffee brand");
        bobAnswers.put(2L, "High Text Markup Language");
        bobSubmission.setStudentAnswers(bobAnswers);
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
                    CREATE TABLE student_answers (
                        submission_id INTEGER NOT NULL,
                        question_id INTEGER NOT NULL,
                        answer TEXT,
                        PRIMARY KEY (submission_id, question_id),
                        FOREIGN KEY (submission_id) REFERENCES submissions(id) ON DELETE CASCADE,
                        FOREIGN KEY (question_id) REFERENCES questions(id)
                    )
                    """);
        }
    }

    @BeforeEach
    void setup() throws SQLException {
        instance = new SubmissionRepository(connection);

        try (Statement stmt = connection.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON"); // important sinon DELETE CASCADE n'est pas pris en compte
            stmt.execute("DELETE FROM student_answers");
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
                    (1, 'Web Development Basics', 'Answer all questions', 1, '2024-04-08 10:00:00'),
                    (2, 'Advanced Programming', 'No calculators allowed', 1, '2024-04-08 10:01:00')
                    """);

            stmt.execute("""
                    INSERT INTO submissions (id, exam_id, student_id, submission_date) VALUES
                    (1, 1, 'Alice', '2024-04-08 10:30:00'),
                    (2, 1, 'Bob', '2024-04-08 10:45:00'),
                    (3, 2, 'Alice', '2024-04-08 11:00:00')
                    """);

            stmt.execute("""
                    INSERT INTO student_answers (submission_id, question_id, answer) VALUES
                    (1, 1, 'A programming language'),
                    (1, 2, 'HyperText Markup Language'),
                    (2, 1, 'A coffee brand'),
                    (2, 2, 'High Text Markup Language'),
                    (3, 1, 'Object-oriented programming language')
                    """);
        }
    }

    @AfterEach
    void cleanDatabase() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM student_answers");
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
        Optional<Submission> result = instance.findById(id);
        //Assert
        assertTrue(result.isPresent());
        assertEquals("Alice", result.get().getStudentId());
        assertEquals(1L, result.get().getExamId());
        // Verify answers are loaded
        assertEquals(2, result.get().getStudentAnswers().size());
        assertEquals("A programming language", result.get().getStudentAnswers().get(1L));
        assertEquals("HyperText Markup Language", result.get().getStudentAnswers().get(2L));
    }

    @Test
    void testFindByIdDoesNotExist() {
        System.out.println("testFindByIdDoesNotExist");
        //Arrange
        Long id = 100L;
        //Action
        Optional<Submission> result = instance.findById(id);
        //Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testFindAll() {
        System.out.println("testFindAll");
        //Action
        List<Submission> result = instance.findAll();
        //Assert
        assertEquals(3, result.size());
        // Most recent submission first (Alice's second submission)
        assertEquals("Alice", result.get(0).getStudentId());
        assertEquals(2L, result.get(0).getExamId());
        // Verify student answers are loaded
        assertNotNull(result.get(0).getStudentAnswers());
    }

    @Test
    void testFindByExamId() {
        System.out.println("testFindByExamId");
        //Arrange
        Long examId = 1L;
        //Action
        List<Submission> result = instance.findByExamId(examId);
        //Assert
        assertEquals(2, result.size());
        // Should contain Alice and Bob's submissions
        assertTrue(result.stream().anyMatch(s -> s.getStudentId().equals("Alice")));
        assertTrue(result.stream().anyMatch(s -> s.getStudentId().equals("Bob")));
    }

    @Test
    void testFindByStudentId() {
        System.out.println("testFindByStudentId");
        //Arrange
        String studentId = "Alice";
        //Action
        List<Submission> result = instance.findByStudentId(studentId);
        //Assert
        assertEquals(2, result.size());
        // All submissions should be for Alice
        assertTrue(result.stream().allMatch(s -> s.getStudentId().equals("Alice")));
        // Should include submissions for both exams
        assertTrue(result.stream().anyMatch(s -> s.getExamId() == 1L));
        assertTrue(result.stream().anyMatch(s -> s.getExamId() == 2L));
    }

    @Test
    void testSaveNewSubmission() {
        System.out.println("testSaveNewSubmission");
        //Arrange
        Submission newSubmission = new Submission();
        newSubmission.setExamId(2L);
        newSubmission.setStudentId("Charlie");

        Map<Long, String> charlieAnswers = new HashMap<>();
        charlieAnswers.put(1L, "Java is a programming language");
        newSubmission.setStudentAnswers(charlieAnswers);

        //Action
        Submission savedSubmission = instance.save(newSubmission);

        //Assert
        assertNotNull(savedSubmission.getId());
        assertEquals("Charlie", savedSubmission.getStudentId());

        //Verify in database
        Optional<Submission> fromDb = instance.findById(savedSubmission.getId());
        assertTrue(fromDb.isPresent());
        assertEquals("Charlie", fromDb.get().getStudentId());
        assertEquals(1, fromDb.get().getStudentAnswers().size());
        assertEquals("Java is a programming language", fromDb.get().getStudentAnswers().get(1L));
    }

    @Test
    void testSaveUpdateSubmission() {
        System.out.println("testSaveUpdateSubmission");
        //Arrange
        Optional<Submission> submissionOpt = instance.findById(1L);
        assertTrue(submissionOpt.isPresent());
        Submission submission = submissionOpt.get();

        // Update student answers
        Map<Long, String> updatedAnswers = new HashMap<>(submission.getStudentAnswers());
        updatedAnswers.put(1L, "Java is an OOP language");
        submission.setStudentAnswers(updatedAnswers);

        //Action
        Submission updatedSubmission = instance.save(submission);

        //Assert
        assertEquals(1L, updatedSubmission.getId());
        assertEquals("Java is an OOP language", updatedSubmission.getStudentAnswers().get(1L));

        //Verify in database
        Optional<Submission> fromDb = instance.findById(1L);
        assertTrue(fromDb.isPresent());
        assertEquals("Java is an OOP language", fromDb.get().getStudentAnswers().get(1L));
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

        // Verify student answers are also deleted (cascade)
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM student_answers WHERE submission_id = 1")) {
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
        assertEquals(3, result);
    }
}
package be.esi.prj.easyeval.repository;

import be.esi.prj.easyeval.model.Question;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class QuestionRepositoryTest {
    private static Connection connection;
    private QuestionRepository instance;
    private final Question questionJava;
    private final Question questionSQL;
    private final Question questionHTML;

    public QuestionRepositoryTest() {
        LocalDateTime now = LocalDateTime.now();
        questionJava = new Question(1L, "What is Java?", "A programming language", now);
        questionSQL = new Question(2L, "What does SQL stand for?", "Structured Query Language", now);
        questionHTML = new Question(3L, "What is HTML?", "HyperText Markup Language", now);
    }

    @BeforeAll
    static void setupDatabase() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        try (Statement stmt = connection.createStatement()) {
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
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                    """);

            stmt.execute("""
                    CREATE TABLE exam_questions (
                        exam_id INTEGER NOT NULL,
                        question_id INTEGER NOT NULL,
                        question_order INTEGER NOT NULL,
                        PRIMARY KEY (exam_id, question_id),
                        FOREIGN KEY (exam_id) REFERENCES exam_documents(id),
                        FOREIGN KEY (question_id) REFERENCES questions(id)
                    )
                    """);
        }
    }

    @BeforeEach
    void setup() throws SQLException {
        instance = new QuestionRepository(connection);
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM exam_questions");
            stmt.execute("DELETE FROM exam_documents");
            stmt.execute("DELETE FROM questions");

            stmt.execute("""
                    INSERT INTO questions (id, text, correct_answer, created_at) VALUES
                    (1, 'What is Java?', 'A programming language', '2024-04-08 10:00:00'),
                    (2, 'What does SQL stand for?', 'Structured Query Language', '2024-04-08 10:01:00'),
                    (3, 'What is HTML?', 'HyperText Markup Language', '2024-04-08 10:02:00')
                    """);

            stmt.execute("""
                    INSERT INTO exam_documents (id, title, course_id) VALUES
                    (1, 'Programming Basics', 1),
                    (2, 'Database Fundamentals', 1)
                    """);

            stmt.execute("""
                    INSERT INTO exam_questions (exam_id, question_id, question_order) VALUES
                    (1, 1, 1),
                    (1, 3, 2),
                    (2, 2, 1)
                    """);
        }
    }

    @AfterEach
    void cleanDatabase() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM exam_questions");
            stmt.execute("DELETE FROM exam_documents");
            stmt.execute("DELETE FROM questions");
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
        Optional<Question> expected = Optional.of(questionJava);
        //Action
        Optional<Question> result = instance.findById(1L);
        //Assert
        assertTrue(result.isPresent());
        assertEquals(expected.get().getId(), result.get().getId());
        assertEquals(expected.get().getText(), result.get().getText());
        assertEquals(expected.get().getCorrectAnswer(), result.get().getCorrectAnswer());
    }

    @Test
    void testFindByIdDoesNotExist() {
        System.out.println("testFindByIdDoesNotExist");
        //Arrange
        Optional<Question> expected = Optional.empty();
        //Action
        Optional<Question> result = instance.findById(100L);
        //Assert
        assertEquals(expected, result);
    }

    @Test
    void testFindAll() {
        System.out.println("testFindAll");
        //Action
        List<Question> result = instance.findAll();
        //Assert
        assertEquals(3, result.size());
        // Check order (most recent first)
        assertEquals("What is HTML?", result.get(0).getText());
        assertEquals("What does SQL stand for?", result.get(1).getText());
        assertEquals("What is Java?", result.get(2).getText());
    }

    @Test
    void testFindByTextContaining() {
        System.out.println("testFindByTextContaining");
        //Action
        List<Question> result = instance.findByTextContaining("Java");
        //Assert
        assertEquals(1, result.size());
        assertEquals("What is Java?", result.get(0).getText());
    }

    @Test
    void testFindByExamId() {
        System.out.println("testFindByExamId");
        //Action
        List<Question> result = instance.findByExamId(1L);
        //Assert
        assertEquals(2, result.size());
        assertEquals("What is Java?", result.get(0).getText());
        assertEquals("What is HTML?", result.get(1).getText());
    }

    @Test
    void testSaveNewQuestion() {
        System.out.println("testSaveNewQuestion");
        //Arrange
        Question newQuestion = new Question();
        newQuestion.setText("What is CSS?");
        newQuestion.setCorrectAnswer("Cascading Style Sheets");
        //Action
        Question savedQuestion = instance.save(newQuestion);
        //Assert
        assertNotNull(savedQuestion.getId());
        assertEquals("What is CSS?", savedQuestion.getText());

        //Verify in database
        Optional<Question> fromDb = instance.findById(savedQuestion.getId());
        assertTrue(fromDb.isPresent());
        assertEquals("What is CSS?", fromDb.get().getText());
    }

    @Test
    void testSaveUpdateQuestion() {
        System.out.println("testSaveUpdateQuestion");
        //Arrange
        Question question = instance.findById(1L).get();
        question.setText("What is Java programming?");
        //Action
        Question updatedQuestion = instance.save(question);
        //Assert
        assertEquals(1L, updatedQuestion.getId());
        assertEquals("What is Java programming?", updatedQuestion.getText());

        //Verify in database
        Optional<Question> fromDb = instance.findById(1L);
        assertTrue(fromDb.isPresent());
        assertEquals("What is Java programming?", fromDb.get().getText());
    }

    @Test
    void testDeleteById() {
        System.out.println("testDeleteById");
        //Action
        boolean result = instance.deleteById(1L);
        //Assert
        assertTrue(result);
        assertFalse(instance.findById(1L).isPresent());
    }

    @Test
    void testDeleteByIdNonExistent() {
        System.out.println("testDeleteByIdNonExistent");
        //Action
        boolean result = instance.deleteById(100L);
        //Assert
        assertFalse(result);
    }

    @Test
    void testCount() {
        System.out.println("testCount");
        //Action
        long result = instance.count();
        //Assert
        assertEquals(3, result);
    }

    @Test
    void testAddQuestionToExam() {
        System.out.println("testAddQuestionToExam");
        //Arrange
        Question newQuestion = new Question();
        newQuestion.setText("What is CSS?");
        newQuestion.setCorrectAnswer("Cascading Style Sheets");
        Question savedQuestion = instance.save(newQuestion);

        //Action
        boolean result = instance.addQuestionToExam(1L, savedQuestion.getId(), 3);

        //Assert
        assertTrue(result);
        List<Question> examQuestions = instance.findByExamId(1L);
        assertEquals(3, examQuestions.size());
        assertEquals("What is CSS?", examQuestions.get(2).getText());
    }

    @Test
    void testRemoveQuestionFromExam() {
        System.out.println("testRemoveQuestionFromExam");
        //Action
        boolean result = instance.removeQuestionFromExam(1L, 1L);

        //Assert
        assertTrue(result);
        List<Question> examQuestions = instance.findByExamId(1L);
        assertEquals(1, examQuestions.size());
        assertEquals("What is HTML?", examQuestions.get(0).getText());
    }

    @Test
    void testUpdateQuestionOrder() {
        System.out.println("testUpdateQuestionOrder");
        //Arrange
        List<Long> newOrder = Arrays.asList(3L, 1L);

        //Action
        boolean result = instance.updateQuestionOrder(1L, newOrder);

        //Assert
        assertTrue(result);
        List<Question> examQuestions = instance.findByExamId(1L);
        assertEquals(2, examQuestions.size());
        // New order should be HTML first, then Java
        assertEquals("What is HTML?", examQuestions.get(0).getText());
        assertEquals("What is Java?", examQuestions.get(1).getText());
    }
}
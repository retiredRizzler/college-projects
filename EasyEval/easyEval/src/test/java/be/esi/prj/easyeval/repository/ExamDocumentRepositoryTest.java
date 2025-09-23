package be.esi.prj.easyeval.repository;

import be.esi.prj.easyeval.model.ExamDocument;
import be.esi.prj.easyeval.model.Question;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ExamDocumentRepositoryTest {
    private static Connection connection;
    private ExamDocumentRepository instance;
    private QuestionRepository questionRepository;
    private final ExamDocument javaExam;
    private final ExamDocument sqlExam;

    public ExamDocumentRepositoryTest() {
        LocalDateTime now = LocalDateTime.now();
        javaExam = new ExamDocument(1L, "Java Basics", "Answer all questions", 1L, now);
        sqlExam = new ExamDocument(2L, "SQL Fundamentals", "No calculators allowed", 1L, now);
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
        questionRepository = new QuestionRepository(connection);
        instance = new ExamDocumentRepository(connection, questionRepository);

        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM exam_questions");
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
                    (2, 'What does SQL stand for?', 'Structured Query Language', '2024-04-08 10:01:00')
                    """);

            stmt.execute("""
                    INSERT INTO exam_documents (id, title, instructions, course_id, created_at) VALUES
                    (1, 'Java Basics', 'Answer all questions', 1, '2024-04-08 10:00:00'),
                    (2, 'SQL Fundamentals', 'No calculators allowed', 1, '2024-04-08 10:01:00')
                    """);

            stmt.execute("""
                    INSERT INTO exam_questions (exam_id, question_id, question_order) VALUES
                    (1, 1, 1),
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
        Optional<ExamDocument> result = instance.findById(id);
        //Assert
        assertTrue(result.isPresent());
        assertEquals("Java Basics", result.get().getTitle());
        assertEquals(1L, result.get().getCourseId());
        assertNotNull(result.get().getQuestions());
        assertEquals(1, result.get().getQuestions().size());
    }

    @Test
    void testFindByIdDoesNotExist() {
        System.out.println("testFindByIdDoesNotExist");
        //Arrange
        Long id = 100L;
        //Action
        Optional<ExamDocument> result = instance.findById(id);
        //Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testFindAll() {
        System.out.println("testFindAll");
        //Action
        List<ExamDocument> result = instance.findAll();
        //Assert
        assertEquals(2, result.size());
        // Most recent first
        assertEquals("SQL Fundamentals", result.get(0).getTitle());
        assertEquals("Java Basics", result.get(1).getTitle());
    }

    @Test
    void testFindByCourseId() {
        System.out.println("testFindByCourseId");
        //Arrange
        Long courseId = 1L;
        //Action
        List<ExamDocument> result = instance.findByCourseId(courseId);
        //Assert
        assertEquals(2, result.size());
    }

    @Test
    void testSaveNewExamDocument() {
        System.out.println("testSaveNewExamDocument");
        //Arrange
        ExamDocument newExam = new ExamDocument();
        newExam.setTitle("Python Basics");
        newExam.setInstructions("Complete all exercises");
        newExam.setCourseId(1L);

        // Add a new question to the exam
        Question newQuestion = new Question();
        newQuestion.setText("What is Python?");
        newQuestion.setCorrectAnswer("A programming language");

        List<Question> questions = new ArrayList<>();
        questions.add(newQuestion);
        newExam.setQuestions(questions);

        //Action
        ExamDocument savedExam = instance.save(newExam);

        //Assert
        assertNotNull(savedExam.getId());
        assertEquals("Python Basics", savedExam.getTitle());

        //Verify in database
        Optional<ExamDocument> fromDb = instance.findById(savedExam.getId());
        assertTrue(fromDb.isPresent());
        assertEquals("Python Basics", fromDb.get().getTitle());
        assertEquals(1, fromDb.get().getQuestions().size());
        assertEquals("What is Python?", fromDb.get().getQuestions().get(0).getText());
    }

    @Test
    void testSaveUpdateExamDocument() {
        System.out.println("testSaveUpdateExamDocument");
        //Arrange
        Optional<ExamDocument> examOpt = instance.findById(1L);
        assertTrue(examOpt.isPresent());
        ExamDocument exam = examOpt.get();
        exam.setTitle("Advanced Java");

        // Add a new question
        Question newQuestion = new Question();
        newQuestion.setText("What is JVM?");
        newQuestion.setCorrectAnswer("Java Virtual Machine");

        List<Question> questions = new ArrayList<>(exam.getQuestions());
        questions.add(newQuestion);
        exam.setQuestions(questions);

        //Action
        ExamDocument updatedExam = instance.save(exam);

        //Assert
        assertEquals(1L, updatedExam.getId());
        assertEquals("Advanced Java", updatedExam.getTitle());

        //Verify in database
        Optional<ExamDocument> fromDb = instance.findById(1L);
        assertTrue(fromDb.isPresent());
        assertEquals("Advanced Java", fromDb.get().getTitle());
        assertEquals(2, fromDb.get().getQuestions().size());
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
    void testCount() {
        System.out.println("testCount");
        //Action
        long result = instance.count();
        //Assert
        assertEquals(2, result);
    }

    @Test
    void testCountByCourseId() {
        System.out.println("testCountByCourseId");
        //Arrange
        Long courseId = 1L;
        //Action
        long result = instance.countByCourseId(courseId);
        //Assert
        assertEquals(2, result);
    }

    @Test
    void testExistsByIdExist() {
        System.out.println("testExistsByIdExist");
        //Arrange
        Long id = 1L;
        //Action
        boolean result = false;
        try {
            result = instance.existsById(id);
        } catch (SQLException e) {
            fail("Should not throw exception");
        }
        //Assert
        assertTrue(result);
    }

    @Test
    void testExistsByIdDoesNotExist() {
        System.out.println("testExistsByIdDoesNotExist");
        //Arrange
        Long id = 100L;
        //Action
        boolean result = true;
        try {
            result = instance.existsById(id);
        } catch (SQLException e) {
            fail("Should not throw exception");
        }
        //Assert
        assertFalse(result);
    }
}
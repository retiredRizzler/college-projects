package be.esi.prj.easyeval.repository;

import be.esi.prj.easyeval.model.Course;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CourseRepositoryTest {
    private static Connection connection;
    private CourseRepository instance;
    private final Course programmingJava;
    private final Course databases;
    private final Course algorithms;

    public CourseRepositoryTest() {
        LocalDateTime now = LocalDateTime.now();
        programmingJava = new Course(1L, "Programming Java", now);
        databases = new Course(2L, "Databases", now);
        algorithms = new Course(3L, "Algorithms", now);
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
                    CREATE TABLE exam_documents (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        course_id INTEGER NOT NULL,
                        title TEXT NOT NULL,
                        instructions TEXT,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (course_id) REFERENCES courses(id)
                    )
                    """);
        }
    }

    @BeforeEach
    void setup() throws SQLException {
        instance = new CourseRepository(connection);
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM exam_documents");
            stmt.execute("DELETE FROM courses");

            stmt.execute("""
                    INSERT INTO courses (id, name, created_at) VALUES
                    (1, 'Programming Java', '2024-04-08 10:00:00'),
                    (2, 'Databases', '2024-04-08 10:00:00'),
                    (3, 'Algorithms', '2024-04-08 10:00:00')
                    """);

            stmt.execute("""
                    INSERT INTO exam_documents (course_id, title) VALUES
                    (1, 'Midterm Exam'),
                    (1, 'Final Exam'),
                    (2, 'SQL Quiz')
                    """);
        }
    }

    @AfterEach
    void cleanDatabase() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM exam_documents");
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
        Optional<Course> expected = Optional.of(programmingJava);
        //Action
        Optional<Course> result = instance.findById(1L);
        //Assert
        assertTrue(result.isPresent());
        assertEquals(expected.get().getId(), result.get().getId());
        assertEquals(expected.get().getName(), result.get().getName());
    }

    @Test
    void testFindByIdDoesNotExist() {
        System.out.println("testFindByIdDoesNotExist");
        //Arrange
        Optional<Course> expected = Optional.empty();
        //Action
        Optional<Course> result = instance.findById(100L);
        //Assert
        assertEquals(expected, result);
    }

    @Test
    void testFindByNameExist() {
        System.out.println("testFindByNameExist");
        //Arrange
        Optional<Course> expected = Optional.of(databases);
        //Action
        Optional<Course> result = instance.findByName("Databases");
        //Assert
        assertTrue(result.isPresent());
        assertEquals(expected.get().getId(), result.get().getId());
        assertEquals(expected.get().getName(), result.get().getName());
    }

    @Test
    void testFindByNameDoesNotExist() {
        System.out.println("testFindByNameDoesNotExist");
        //Arrange
        Optional<Course> expected = Optional.empty();
        //Action
        Optional<Course> result = instance.findByName("Physics");
        //Assert
        assertEquals(expected, result);
    }

    @Test
    void testFindAll() {
        System.out.println("testFindAll");
        //Action
        List<Course> result = instance.findAll();
        //Assert
        assertEquals(3, result.size());
        // Verify sorted by name
        assertEquals("Algorithms", result.get(0).getName());
        assertEquals("Databases", result.get(1).getName());
        assertEquals("Programming Java", result.get(2).getName());
    }

    @Test
    void testFindByNameContaining() {
        System.out.println("testFindByNameContaining");
        //Action
        List<Course> result = instance.findByNameContaining("gram");
        //Assert
        assertEquals(1, result.size());
        assertEquals("Programming Java", result.get(0).getName());
    }

    @Test
    void testGetExamIds() {
        System.out.println("testGetExamIds");
        //Action
        List<Long> result = instance.getExamIds(1L);
        //Assert
        assertEquals(2, result.size());
    }

    @Test
    void testExistsById() {
        System.out.println("testExistsById");
        //Action & Assert
        assertTrue(instance.existsById(1L));
        assertFalse(instance.existsById(100L));
    }

    @Test
    void testExistsByName() {
        System.out.println("testExistsByName");
        //Action & Assert
        assertTrue(instance.existsByName("Databases"));
        assertFalse(instance.existsByName("Physics"));
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
    void testSaveNewCourse() {
        System.out.println("testSaveNewCourse");
        //Arrange
        Course newCourse = new Course("Physics");
        //Action
        Course savedCourse = instance.save(newCourse);
        //Assert
        assertNotNull(savedCourse.getId());
        assertEquals("Physics", savedCourse.getName());

        //Verify in database
        Optional<Course> fromDb = instance.findByName("Physics");
        assertTrue(fromDb.isPresent());
    }

    @Test
    void testSaveUpdateCourse() {
        System.out.println("testSaveUpdateCourse");
        //Arrange
        Course course = instance.findById(1L).get();
        course.setName("Advanced Java");
        //Action
        Course updatedCourse = instance.save(course);
        //Assert
        assertEquals(1L, updatedCourse.getId());
        assertEquals("Advanced Java", updatedCourse.getName());

        //Verify in database
        Optional<Course> fromDb = instance.findById(1L);
        assertTrue(fromDb.isPresent());
        assertEquals("Advanced Java", fromDb.get().getName());
    }

    @Test
    void testDeleteById() {
        System.out.println("testDeleteById");
        //Action
        boolean result = instance.deleteById(1L);
        //Assert
        assertTrue(result);
        assertFalse(instance.existsById(1L));
    }

    @Test
    void testDeleteByIdNonExistent() {
        System.out.println("testDeleteByIdNonExistent");
        //Action
        boolean result = instance.deleteById(100L);
        //Assert
        assertFalse(result);
    }
}
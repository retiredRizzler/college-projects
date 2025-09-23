package be.esi.prj.easyeval.viewmodel;

import be.esi.prj.easyeval.model.Course;
import be.esi.prj.easyeval.repository.CourseRepository;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CourseManagerViewModelTest {

    private CourseRepository mockRepository;
    private CourseManagerViewModel viewModel;

    @BeforeEach
    void setUp() {
        mockRepository = mock(CourseRepository.class);
        try {
            when(mockRepository.findAll()).thenReturn(Collections.emptyList());
        } catch (Exception ignored) {}
        viewModel = new CourseManagerViewModel(mockRepository);
    }

    @Test
    void fetchAllCourses_shouldPopulateList() {
        try {
            Course course = new Course("Test");
            when(mockRepository.findAll()).thenReturn(Collections.singletonList(course));

            viewModel.fetchAllCourses();
            ObservableList<Course> result = viewModel.getCourseList();

            assertEquals(1, result.size());
            assertEquals("Test", result.get(0).getName());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    void addCourse_shouldAddValidCourse() {
        try {
            when(mockRepository.findByName("Java")).thenReturn(Optional.empty());
            when(mockRepository.findAll()).thenReturn(Collections.singletonList(new Course("Java")));

            Course result = viewModel.addCourse("Java");

            assertNotNull(result);
            assertEquals("Java", result.getName());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }





    @Test
    void deleteCourse_shouldSucceedIfExists() {
        try {
            when(mockRepository.deleteById(1L)).thenReturn(true);

            boolean result = viewModel.deleteCourse(1L);

            assertTrue(result);
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    void deleteAllCourses_shouldSucceed() {
        try {
            when(mockRepository.deleteAllCourses()).thenReturn(true);

            boolean result = viewModel.deleteAllCourses();

            assertTrue(result);
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    void searchCourses_shouldReturnMatchingCourses() {
        try {
            Course course = new Course("JavaFX");
            when(mockRepository.findByNameContaining("Java")).thenReturn(Collections.singletonList(course));

            viewModel.searchCourses("Java");

            ObservableList<Course> list = viewModel.getCourseList();
            assertEquals(1, list.size());
            assertEquals("JavaFX", list.get(0).getName());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    void searchCourses_shouldReturnAllWhenEmpty() {
        try {
            Course course = new Course("Full List");
            when(mockRepository.findAll()).thenReturn(Collections.singletonList(course));

            viewModel.searchCourses("");

            ObservableList<Course> list = viewModel.getCourseList();
            assertEquals(1, list.size());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    void selectedCourse_shouldBeSetAndGetCorrectly() {
        Course course = new Course("Algo");

        viewModel.setSelectedCourse(course);

        assertEquals(course, viewModel.getSelectedCourse());
    }
}

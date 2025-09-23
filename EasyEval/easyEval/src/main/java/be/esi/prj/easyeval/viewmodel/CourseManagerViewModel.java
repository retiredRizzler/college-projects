package be.esi.prj.easyeval.viewmodel;

import be.esi.prj.easyeval.model.Course;
import be.esi.prj.easyeval.repository.CourseRepository;
import be.esi.prj.easyeval.repository.RepositoryException;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.util.List;

/**
 * ViewModel for handling Course-related operations and data binding.
 */
public class CourseManagerViewModel {
    private final CourseRepository repository;

    private final ObjectProperty<Course> selectedCourse = new SimpleObjectProperty<>();
    private final ObservableList<Course> courseList = FXCollections.observableArrayList();

    public CourseManagerViewModel() {
        this.repository = new CourseRepository();
        fetchAllCourses();
    }

    /**
     * Constructor with repository for testing.
     * @param repository The course repository to use
     */
    CourseManagerViewModel(CourseRepository repository) {
        this.repository = repository;
        fetchAllCourses();
    }

    /**
     * Fetches all courses from the repository and updates the observable list.
     */
    public void fetchAllCourses() {
        try {
            List<Course> courses = repository.findAll();
            courseList.clear();
            courseList.addAll(courses);
        } catch (RepositoryException e) {
            showErrorAlert("Error fetching courses", e.getMessage());
        }
    }

    /**
     * Creates and saves a new course.
     * @param name The name of the new course
     * @return The newly created Course object, or null if an error occurred
     */
    public Course addCourse(String name) {
        if (name == null || name.trim().isEmpty()) {
            showErrorAlert("Invalid Input", "Course name cannot be empty.");
            return null;
        }

        if (name.length() > 50) {
            showErrorAlert("Invalid Input", "Course name cannot be longer than 50 characters.");
            return null;
        }

        try {
            // Check if a course with the same name already exists
            if (repository.findByName(name).isPresent()) {
                showErrorAlert("Duplicate Course", "A course with this name already exists.");
                return null;
            }

            Course newCourse = new Course(name);
            repository.save(newCourse);

            // Refresh the course list
            fetchAllCourses();
            return newCourse;
        } catch (RepositoryException e) {
            showErrorAlert("Error saving course", e.getMessage());
            return null;
        }
    }

    /**
     * Deletes a course by its ID.
     * @param courseId The ID of the course to delete
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteCourse(Long courseId) {
        try {
            boolean success = repository.deleteById(courseId);
            if (success) {
                fetchAllCourses();
            }
            return success;
        } catch (RepositoryException e) {
            showErrorAlert("Error deleting course", e.getMessage());
            return false;
        }
    }

    public boolean deleteAllCourses() {
        try {
            boolean succes = repository.deleteAllCourses();
            if (succes) {
                fetchAllCourses();
            }
            return succes;
        } catch (RepositoryException e) {
            showErrorAlert("Error deleting all courses", e.getMessage());
            return false;
        }
    }

    /**
     * Searches for courses containing the specified text in their name.
     * @param searchText The text to search for in course names
     */
    public void searchCourses(String searchText) {
        try {
            if (searchText == null || searchText.trim().isEmpty()) {
                fetchAllCourses(); // Show all if search is empty
            } else {
                List<Course> filteredCourses = repository.findByNameContaining(searchText);
                courseList.clear();
                courseList.addAll(filteredCourses);
            }
        } catch (RepositoryException e) {
            showErrorAlert("Error searching courses", e.getMessage());
        }
    }

    /**
     * Get the observable list of courses for binding to UI controls.
     * @return Observable list of courses
     */
    public ObservableList<Course> getCourseList() {
        return courseList;
    }

    /**
     * Get the selected course property for binding.
     * @return The selected course property
     */
    public ObjectProperty<Course> selectedCourseProperty() {
        return selectedCourse;
    }

    /**
     * Get the currently selected course.
     * @return The selected course
     */
    public Course getSelectedCourse() {
        return selectedCourse.get();
    }

    /**
     * Set the selected course.
     * @param course The course to select
     */
    public void setSelectedCourse(Course course) {
        selectedCourse.set(course);
    }

    /**
     * Shows an error alert with the given title and message.
     */
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
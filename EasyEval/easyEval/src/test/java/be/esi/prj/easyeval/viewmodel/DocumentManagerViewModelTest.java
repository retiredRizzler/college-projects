package be.esi.prj.easyeval.viewmodel;

import be.esi.prj.easyeval.model.Course;
import be.esi.prj.easyeval.model.ExamDocument;
import be.esi.prj.easyeval.service.ExamDocumentService;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DocumentManagerViewModelTest {

    private ExamDocumentService mockService;
    private DocumentManagerViewModel viewModel;
    private Course dummyCourse;

    @BeforeEach
    void setUp() {
        mockService = mock(ExamDocumentService.class);
        viewModel = new DocumentManagerViewModel(mockService);
        dummyCourse = new Course();
        dummyCourse.setId(42L);
        dummyCourse.setName("Math");
    }

    @Test
    void initialize_shouldSetCurrentCourseAndFetchExams() throws Exception {
        ExamDocument exam = new ExamDocument();
        exam.setTitle("Exam 1");
        when(mockService.findExamDocumentsByCourseId(42L)).thenReturn(Collections.singletonList(exam));

        viewModel.initialize(dummyCourse);

        assertEquals(dummyCourse, viewModel.getCurrentCourse());
        assertEquals(1, viewModel.getExamList().size());
        assertEquals("Exam 1", viewModel.getExamList().get(0).getTitle());
    }

    @Test
    void createExam_shouldReturnNewExamIfValid() throws Exception {
        viewModel.initialize(dummyCourse);

        ExamDocument created = new ExamDocument();
        created.setId(1L);
        created.setTitle("Test Exam");

        when(mockService.saveExamDocument(any(ExamDocument.class))).thenReturn(created);

        ExamDocument result = viewModel.createExam("Test Exam", "Instructions");

        assertNotNull(result);
        assertEquals("Test Exam", result.getTitle());
        verify(mockService).saveExamDocument(any(ExamDocument.class));
    }


    @Test
    void deleteExam_shouldReturnTrueIfSuccess() throws Exception {
        viewModel.initialize(dummyCourse);
        when(mockService.deleteExamDocument(1L)).thenReturn(true);

        boolean result = viewModel.deleteExam(1L);

        assertTrue(result);
        verify(mockService).deleteExamDocument(1L);
    }

    @Test
    void deleteExam_shouldReturnFalseIfFails() throws Exception {
        viewModel.initialize(dummyCourse);
        when(mockService.deleteExamDocument(1L)).thenReturn(false);

        boolean result = viewModel.deleteExam(1L);

        assertFalse(result);
    }

    @Test
    void generateExamPDF_shouldReturnFile() throws Exception {
        ExamDocument exam = new ExamDocument();
        File file = new File("exam.pdf");
        when(mockService.generateExamDocument(exam)).thenReturn(file);

        File result = viewModel.generateExamPDF(exam);

        assertNotNull(result);
        assertEquals("exam.pdf", result.getName());
    }

    @Test
    void generateExamWithAnswersPDF_shouldReturnFile() throws Exception {
        ExamDocument exam = new ExamDocument();
        File file = new File("exam-answers.pdf");
        when(mockService.generateExamWithAnswers(exam)).thenReturn(file);

        File result = viewModel.generateExamWithAnswersPDF(exam);

        assertNotNull(result);
        assertEquals("exam-answers.pdf", result.getName());
    }

    @Test
    void selectedExam_shouldBeSetAndGetCorrectly() {
        ExamDocument exam = new ExamDocument();
        exam.setTitle("Selected");

        viewModel.setSelectedExam(exam);

        assertEquals(exam, viewModel.getSelectedExam());
    }
}

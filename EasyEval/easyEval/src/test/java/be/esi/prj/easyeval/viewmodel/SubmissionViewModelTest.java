package be.esi.prj.easyeval.viewmodel;

import be.esi.prj.easyeval.model.*;
import be.esi.prj.easyeval.repository.*;
import be.esi.prj.easyeval.service.ExamDocumentService;
import be.esi.prj.easyeval.utils.OCRProcessor;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SubmissionViewModelTest {

    private ExamDocumentService examDocumentService;
    private ExamDocumentRepository examDocumentRepository;
    private SubmissionRepository submissionRepository;
    private ScanResultRepository scanResultRepository;
    private CourseRepository courseRepository;
    private OCRProcessor ocrProcessor;
    private SubmissionViewModel viewModel;

    @BeforeEach
    void setup() {
        examDocumentService = mock(ExamDocumentService.class);
        examDocumentRepository = mock(ExamDocumentRepository.class);
        submissionRepository = mock(SubmissionRepository.class);
        scanResultRepository = mock(ScanResultRepository.class);
        courseRepository = mock(CourseRepository.class);
        ocrProcessor = mock(OCRProcessor.class);

        viewModel = new SubmissionViewModel(
                examDocumentService,
                examDocumentRepository,
                submissionRepository,
                scanResultRepository,
                courseRepository,
                ocrProcessor
        );
    }

    @Test
    void loadExamsForSelection_shouldPopulateExamsList() throws Exception {
        ExamDocument exam = new ExamDocument();
        exam.setId(1L);
        exam.setTitle("QCM 1");
        when(examDocumentRepository.findAll()).thenReturn(Collections.singletonList(exam));

        viewModel.loadExamsForSelection();
        ObservableList<ExamDocument> exams = viewModel.getExamsList();

        assertEquals(1, exams.size());
        assertEquals("QCM 1", exams.get(0).getTitle());
    }

    @Test
    void getCourseForExam_shouldReturnCourse() throws Exception {
        Course course = new Course("Math");
        course.setId(1L);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        Course result = viewModel.getCourseForExam(1L);

        assertNotNull(result);
        assertEquals("Math", result.getName());
    }



    @Test
    void setSelectedExam_shouldLoadSubmissions() throws Exception {
        ExamDocument exam = new ExamDocument();
        exam.setId(10L);
        exam.setTitle("Examen");
        when(submissionRepository.findByExamId(10L)).thenReturn(Collections.emptyList());

        viewModel.setSelectedExam(exam);

        assertEquals(exam, viewModel.getSelectedExam());
        assertTrue(viewModel.getSubmissionsList().isEmpty());
    }

    @Test
    void removeSubmission_shouldDeleteSubmissionAndResult() throws Exception {
        Submission submission = new Submission();
        submission.setId(100L);
        ScanResult scan = new ScanResult();
        scan.setId(200L);
        scan.setSubmissionId(100L);

        when(scanResultRepository.findBySubmissionId(100L)).thenReturn(Optional.of(scan));

        boolean result = viewModel.removeSubmission(submission);

        assertTrue(result);
        verify(scanResultRepository).deleteById(200L);
        verify(submissionRepository).deleteById(100L);
    }


    @Test
    void generateDetailedReport_shouldReturnMessageIfNoResultFound() {
        Submission submission = new Submission();
        submission.setId(123L);
        String report = viewModel.generateDetailedReport(submission);
        assertTrue(report.contains("Aucun résultat de scan"));
    }
}

package be.esi.prj.easyeval.viewmodel;

import be.esi.prj.easyeval.model.ExamDocument;
import be.esi.prj.easyeval.model.Question;
import be.esi.prj.easyeval.service.ExamDocumentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DocumentCreatorViewModelTest {

    @Mock
    private ExamDocumentService examDocumentService;

    private DocumentCreatorViewModel viewModel;

    @BeforeEach
    public void setUp() {
        viewModel = new DocumentCreatorViewModel(examDocumentService);
    }

    @Test
    public void initialize_withNewExam_shouldSetDefaultValues() {
        // Act
        viewModel.initialize(null);

        // Assert
        assertNotNull(viewModel.getCurrentExam());
        assertEquals("", viewModel.examTitleProperty().get());
        assertEquals("", viewModel.examInstructionsProperty().get());
        assertTrue(viewModel.getQuestionList().isEmpty());
        assertNull(viewModel.getSelectedQuestion());
        assertEquals("", viewModel.questionTextProperty().get());
        assertEquals("", viewModel.correctAnswerProperty().get());
    }

    @Test
    public void initialize_withExistingExam_shouldPopulateFields() {
        // Arrange
        ExamDocument exam = new ExamDocument("Test Exam", 1L);
        exam.setId(1L);
        exam.setInstructions("Test instructions");
        List<Question> questions = new ArrayList<>();
        questions.add(new Question("Q1", "A1"));
        questions.add(new Question("Q2", "A2"));
        exam.setQuestions(questions);

        // Act
        viewModel.initialize(exam);

        // Assert
        assertEquals(exam, viewModel.getCurrentExam());
        assertEquals("Test Exam", viewModel.examTitleProperty().get());
        assertEquals("Test instructions", viewModel.examInstructionsProperty().get());
        assertEquals(2, viewModel.getQuestionList().size());
        assertEquals("Q1", viewModel.getQuestionList().get(0).getText());
        assertEquals("A1", viewModel.getQuestionList().get(0).getCorrectAnswer());
        assertEquals("Q2", viewModel.getQuestionList().get(1).getText());
        assertEquals("A2", viewModel.getQuestionList().get(1).getCorrectAnswer());
    }

    @Test
    public void addQuestion_withValidInput_shouldAddToList() {
        // Arrange
        viewModel.initialize(new ExamDocument("Test Exam", 1L));
        viewModel.questionTextProperty().set("What is the capital of France?");
        viewModel.correctAnswerProperty().set("Paris");

        // Act
        Question result = viewModel.addQuestion();

        // Assert
        assertNotNull(result);
        assertEquals("What is the capital of France?", result.getText());
        assertEquals("Paris", result.getCorrectAnswer());
        assertEquals(1, viewModel.getQuestionList().size());
        assertEquals(result, viewModel.getQuestionList().get(0));
        assertEquals("", viewModel.questionTextProperty().get());
        assertEquals("", viewModel.correctAnswerProperty().get());
    }



    @Test
    public void updateQuestion_withSelectedQuestion_shouldUpdateQuestionInList() {
        // Arrange
        viewModel.initialize(new ExamDocument("Test Exam", 1L));
        Question question = new Question("Original question", "Original answer");
        viewModel.getQuestionList().add(question);
        viewModel.selectedQuestionProperty().set(question);
        viewModel.questionTextProperty().set("Updated question");
        viewModel.correctAnswerProperty().set("Updated answer");

        // Act
        Question result = viewModel.updateQuestion();

        // Assert
        assertNotNull(result);
        assertEquals("Updated question", result.getText());
        assertEquals("Updated answer", result.getCorrectAnswer());
        assertEquals(1, viewModel.getQuestionList().size());
        assertEquals(result, viewModel.getQuestionList().get(0));
        assertEquals("", viewModel.questionTextProperty().get());
        assertEquals("", viewModel.correctAnswerProperty().get());
        assertNull(viewModel.getSelectedQuestion());
    }

    @Test
    public void removeQuestion_withExistingQuestion_shouldRemoveFromList() {
        // Arrange
        viewModel.initialize(new ExamDocument("Test Exam", 1L));
        Question question1 = new Question("Q1", "A1");
        Question question2 = new Question("Q2", "A2");
        viewModel.getQuestionList().addAll(question1, question2);

        // Act
        boolean result = viewModel.removeQuestion(question1);

        // Assert
        assertTrue(result);
        assertEquals(1, viewModel.getQuestionList().size());
        assertEquals(question2, viewModel.getQuestionList().get(0));
    }

    @Test
    public void removeQuestion_withSelectedQuestion_shouldClearForm() {
        // Arrange
        viewModel.initialize(new ExamDocument("Test Exam", 1L));
        Question question = new Question("Q1", "A1");
        viewModel.getQuestionList().add(question);
        viewModel.selectedQuestionProperty().set(question);
        viewModel.questionTextProperty().set("Q1");
        viewModel.correctAnswerProperty().set("A1");

        // Act
        boolean result = viewModel.removeQuestion(question);

        // Assert
        assertTrue(result);
        assertTrue(viewModel.getQuestionList().isEmpty());
        assertNull(viewModel.getSelectedQuestion());
        assertEquals("", viewModel.questionTextProperty().get());
        assertEquals("", viewModel.correctAnswerProperty().get());
    }

    @Test
    public void removeQuestion_withNull_shouldReturnFalse() {
        // Arrange
        viewModel.initialize(new ExamDocument("Test Exam", 1L));
        Question question = new Question("Q1", "A1");
        viewModel.getQuestionList().add(question);

        // Act
        boolean result = viewModel.removeQuestion(null);

        // Assert
        assertFalse(result);
        assertEquals(1, viewModel.getQuestionList().size());
    }

    @Test
    public void clearQuestionForm_shouldResetFormFields() {
        // Arrange
        viewModel.initialize(new ExamDocument("Test Exam", 1L));
        Question question = new Question("Q1", "A1");
        viewModel.selectedQuestionProperty().set(question);
        viewModel.questionTextProperty().set("Q1");
        viewModel.correctAnswerProperty().set("A1");

        // Act
        viewModel.clearQuestionForm();

        // Assert
        assertNull(viewModel.getSelectedQuestion());
        assertEquals("", viewModel.questionTextProperty().get());
        assertEquals("", viewModel.correctAnswerProperty().get());
    }

    @Test
    public void editQuestion_shouldPopulateFormFields() {
        // Arrange
        viewModel.initialize(new ExamDocument("Test Exam", 1L));
        Question question = new Question("Q1", "A1");

        // Act
        viewModel.editQuestion(question);

        // Assert
        assertEquals(question, viewModel.getSelectedQuestion());
        assertEquals("Q1", viewModel.questionTextProperty().get());
        assertEquals("A1", viewModel.correctAnswerProperty().get());
    }

    @Test
    public void saveExam_withValidInput_shouldSaveAndReturnExam() {
        // Arrange
        ExamDocument exam = new ExamDocument("Test Exam", 1L);
        viewModel.initialize(exam);
        viewModel.examTitleProperty().set("Updated Exam");
        viewModel.examInstructionsProperty().set("Updated Instructions");
        Question question = new Question("Q1", "A1");
        viewModel.getQuestionList().add(question);

        ExamDocument savedExam = new ExamDocument("Updated Exam", 1L);
        savedExam.setId(1L);
        savedExam.setInstructions("Updated Instructions");
        List<Question> questions = new ArrayList<>();
        questions.add(new Question("Q1", "A1"));
        savedExam.setQuestions(questions);

        when(examDocumentService.saveExamDocument(any(ExamDocument.class))).thenReturn(savedExam);

        // Act
        ExamDocument result = viewModel.saveExam();

        // Assert
        assertNotNull(result);
        assertEquals(savedExam, result);
        assertEquals(savedExam, viewModel.getCurrentExam());

        verify(examDocumentService).saveExamDocument(any(ExamDocument.class));
    }
}
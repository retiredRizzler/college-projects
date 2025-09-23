package be.esi.prj.easyeval.service;

import be.esi.prj.easyeval.model.ExamDocument;
import be.esi.prj.easyeval.repository.ExamDocumentRepository;
import be.esi.prj.easyeval.utils.ExamDocumentTemplate;
import be.esi.prj.easyeval.repository.RepositoryException;

import java.io.File;

/**
 * Service class that handles operations related to ExamDocument entities,
 * including document generation and persistence.
 */
public class ExamDocumentService {
    private final ExamDocumentRepository examDocumentRepository;
    private final ExamDocumentTemplate examDocumentTemplate;

    /**
     * Default constructor.
     */
    public ExamDocumentService() {
        this.examDocumentRepository = new ExamDocumentRepository();
        this.examDocumentTemplate = new ExamDocumentTemplate();
    }

    /**
     * Constructor for testing purposes.
     *
     * @param examDocumentRepository Repository for exam documents
     * @param examDocumentTemplate Template for generating exam documents
     */
    public ExamDocumentService(ExamDocumentRepository examDocumentRepository, ExamDocumentTemplate examDocumentTemplate) {
        this.examDocumentRepository = examDocumentRepository;
        this.examDocumentTemplate = examDocumentTemplate;
    }

    /**
     * Generates a PDF document for the given exam.
     *
     * @param examDocument The exam document to generate a PDF for
     * @return The generated PDF file
     */
    public File generateExamDocument(ExamDocument examDocument) {
        return examDocumentTemplate.generatePDF(examDocument, false);
    }

    /**
     * Generates a PDF document for the given exam with answers.
     *
     * @param examDocument The exam document to generate a PDF for
     * @return The generated PDF file with answers
     */
    public File generateExamWithAnswers(ExamDocument examDocument) {
        return examDocumentTemplate.generatePDF(examDocument, true);
    }

    /**
     * Saves an exam document to the database.
     *
     * @param examDocument The exam document to save
     * @return The saved exam document with generated ID (if it was a new entity)
     * @throws RepositoryException If there was an error saving the exam document
     */
    public ExamDocument saveExamDocument(ExamDocument examDocument) {
        return examDocumentRepository.save(examDocument);
    }

    /**
     * Retrieves an exam document by its ID.
     *
     * @param id The ID of the exam document to retrieve
     * @return The exam document if found, null otherwise
     */
    public ExamDocument findExamDocumentById(Long id) {
        return examDocumentRepository.findById(id).orElse(null);
    }

    /**
     * Retrieves all exam documents for a specific course.
     *
     * @param courseId The ID of the course
     * @return A list of exam documents for the course
     */
    public java.util.List<ExamDocument> findExamDocumentsByCourseId(Long courseId) {
        return examDocumentRepository.findByCourseId(courseId);
    }

    /**
     * Deletes an exam document by its ID.
     *
     * @param id The ID of the exam document to delete
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteExamDocument(Long id) {
        return examDocumentRepository.deleteById(id);
    }

    /**
     * Closes resources associated with this service.
     */
    public void close() {
        examDocumentRepository.close();
    }
}
package be.esi.prj.easyeval.utils;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import be.esi.prj.easyeval.model.ExamDocument;
import be.esi.prj.easyeval.model.Question;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

/**
 * Utility class for generating PDF exam documents.
 * Creates professionally formatted exam sheets with or without answers.
 * Modified to support red-pen answer detection.
 */
public class ExamDocumentTemplate {
    // Layout constants
    private static final int MARGIN_LEFT = 50;
    private static final int MARGIN_RIGHT = 50;
    private static final int START_Y = 770;
    private static final int LINE_SPACING = 20;
    private static final int ANSWER_SPACING = 25; // Spacing after answer line
    private static final int HEADER_SPACING = 30;
    private static final int SECTION_SPACING = 15;
    private static final int BOTTOM_MARGIN = 50;
    private static final int PAGE_WIDTH = 595; // A4 width in points
    private static final int TEXT_WIDTH = PAGE_WIDTH - MARGIN_LEFT - MARGIN_RIGHT - 20; // Effective text width

    // Colors (in RGB, range 0-1)
    private static final float[] HEADER_COLOR = {0.2f, 0.4f, 0.8f}; // Blue for headers
    private static final float[] TEXT_COLOR = {0.1f, 0.1f, 0.1f}; // Near black for text
    private static final float[] ANSWER_COLOR = {0.0f, 0.5f, 0.0f}; // Green for answers
    private static final float[] INSTRUCTION_BG = {0.95f, 0.95f, 0.95f}; // Light gray for instruction background
    private static final float[] RED_COLOR = {1.0f, 0.0f, 0.0f}; // Red for answer hint

    // Font sizes
    private static final float TITLE_FONT_SIZE = 18;
    private static final float HEADER_FONT_SIZE = 12;
    private static final float NORMAL_FONT_SIZE = 11;
    private static final float SMALL_FONT_SIZE = 9;

    /**
     * Generates a PDF document for the given exam, with or without answers.
     * (This class was made with the help claude.ai)
     * @param exam The exam document to generate a PDF for
     * @param withAnswers Whether to include answers in the PDF
     * @return The generated PDF file
     */
    public File generatePDF(ExamDocument exam, boolean withAnswers) {
        try (PDDocument pdf = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            pdf.addPage(page);

            PDPageContentStream content = new PDPageContentStream(pdf, page);

            int y = START_Y;

            // Add header with title
            y = drawHeader(content, exam, withAnswers, y);

            // Draw a horizontal line below header
            drawHorizontalLine(content, MARGIN_LEFT, y - 10, PAGE_WIDTH - MARGIN_LEFT - MARGIN_RIGHT, 1);
            y -= 20;

            // Draw student information section - simplified with only student ID
            y = drawStudentInfoSection(content, y);
            y -= SECTION_SPACING;

            // Instructions with background
            if (exam.getInstructions() != null && !exam.getInstructions().isEmpty()) {
                y = drawInstructionsBox(content, exam.getInstructions(), y);
                y -= SECTION_SPACING;
            } else {
                // Add default instructions about using red pen
                String defaultInstructions = "INSTRUCTIONS: Veuillez écrire votre matricule (5 chiffres) et toutes vos réponses au STYLO ROUGE uniquement. " +
                        "Les réponses écrites avec une autre couleur ne seront pas détectées par le système d'évaluation automatique.";
                y = drawInstructionsBox(content, defaultInstructions, y);
                y -= SECTION_SPACING;
            }

            int questionNumber = 1;
            for (Question question : exam.getQuestions()) {
                // Check if we need a new page - estimate space needed for question and answer
                int estimatedQuestionHeight = estimateTextHeight(question.getText(), TEXT_WIDTH);
                int estimatedAnswerHeight = LINE_SPACING * 2 + ANSWER_SPACING;

                if (y - estimatedQuestionHeight - estimatedAnswerHeight < BOTTOM_MARGIN) {
                    content.close();
                    page = new PDPage(PDRectangle.A4);
                    pdf.addPage(page);
                    content = new PDPageContentStream(pdf, page);

                    // Add a simple header to continuation pages
                    y = START_Y;
                    drawContinuationHeader(content, exam.getTitle(), y);
                    y -= HEADER_SPACING;
                }

                // Draw question
                content.setNonStrokingColor(TEXT_COLOR[0], TEXT_COLOR[1], TEXT_COLOR[2]);
                String questionText = questionNumber + ". " + question.getText();
                y = drawParagraphText(content, questionText, MARGIN_LEFT, y, PDType1Font.HELVETICA_BOLD, NORMAL_FONT_SIZE);
                y -= 10; // Extra space between question and answer line

                // Draw answer line
                y = drawAnswerLine(content, y);

                // Draw answers if requested
                if (withAnswers) {
                    y -= 5; // Add extra space before the answer text
                    content.setNonStrokingColor(ANSWER_COLOR[0], ANSWER_COLOR[1], ANSWER_COLOR[2]);
                    String answerText = "Réponse correcte : " + question.getCorrectAnswer();
                    y = drawParagraphText(content, answerText, MARGIN_LEFT, y, PDType1Font.HELVETICA_OBLIQUE, SMALL_FONT_SIZE);

                    // Reset color
                    content.setNonStrokingColor(TEXT_COLOR[0], TEXT_COLOR[1], TEXT_COLOR[2]);
                }

                y -= SECTION_SPACING; // Space between questions
                questionNumber++;
            }

            // Add footer
            drawFooter(content, pdf.getNumberOfPages());

            content.close();

            String directoryPath = "generated-exam";
            File directory = new File(directoryPath);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String filename = withAnswers
                    ? "exam_with_answers_" + exam.getId() + ".pdf"
                    : "exam_" + exam.getId() + ".pdf";

            File output = new File(directory, filename);
            pdf.save(output);
            return output;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Draws the header section of the exam page.
     */
    private int drawHeader(PDPageContentStream content, ExamDocument exam, boolean withAnswers, int y) throws IOException {
        // Draw exam title
        content.setNonStrokingColor(HEADER_COLOR[0], HEADER_COLOR[1], HEADER_COLOR[2]);
        content.beginText();
        content.setFont(PDType1Font.HELVETICA_BOLD, TITLE_FONT_SIZE);
        content.newLineAtOffset(MARGIN_LEFT, y);
        String title = exam.getTitle() + (withAnswers ? " (avec réponses)" : "");
        content.showText(title);
        content.endText();
        y -= 25;

        // Draw date
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dateText = "Date: " + now.format(formatter);

        content.setNonStrokingColor(TEXT_COLOR[0], TEXT_COLOR[1], TEXT_COLOR[2]);
        content.beginText();
        content.setFont(PDType1Font.HELVETICA, SMALL_FONT_SIZE);
        content.newLineAtOffset(MARGIN_LEFT, y);
        content.showText(dateText);
        content.endText();

        // Reset text color
        content.setNonStrokingColor(TEXT_COLOR[0], TEXT_COLOR[1], TEXT_COLOR[2]);

        return y - HEADER_SPACING;
    }

    /**
     * Draws a simple header for continuation pages.
     */
    private void drawContinuationHeader(PDPageContentStream content, String title, int y) throws IOException {
        content.setNonStrokingColor(HEADER_COLOR[0], HEADER_COLOR[1], HEADER_COLOR[2]);
        content.beginText();
        content.setFont(PDType1Font.HELVETICA_BOLD, HEADER_FONT_SIZE);
        content.newLineAtOffset(MARGIN_LEFT, y);
        content.showText(title + " (suite)");
        content.endText();

        // Reset color
        content.setNonStrokingColor(TEXT_COLOR[0], TEXT_COLOR[1], TEXT_COLOR[2]);
    }

    /**
     * Draws the student information section with only student ID field.
     */
    private int drawStudentInfoSection(PDPageContentStream content, int y) throws IOException {


        // Draw form fields
        content.setNonStrokingColor(TEXT_COLOR[0], TEXT_COLOR[1], TEXT_COLOR[2]);

        // Student ID (Matricule) field
        content.beginText();
        content.setFont(PDType1Font.HELVETICA_BOLD, NORMAL_FONT_SIZE);
        content.newLineAtOffset(MARGIN_LEFT + 10, y - 25);
        content.showText("Matricule (5 chiffres): ");
        content.endText();

        // Draw a line for student ID
        drawHorizontalLine(content, MARGIN_LEFT + 150, y - 25, 100, 0.5f);

        // Reset color
        content.setNonStrokingColor(TEXT_COLOR[0], TEXT_COLOR[1], TEXT_COLOR[2]);

        return y - 50; // Return new y position after the form
    }

    /**
     * Draws a box with the exam instructions.
     */
    private int drawInstructionsBox(PDPageContentStream content, String instructions, int y) throws IOException {
        // Estimate text height needed for instructions
        int textHeight = estimateTextHeight(instructions, TEXT_WIDTH - 20); // Subtract 20 for padding
        int rectHeight = textHeight + 45; // Add more padding at top and bottom

        // Draw the background rectangle
        content.setNonStrokingColor(INSTRUCTION_BG[0], INSTRUCTION_BG[1], INSTRUCTION_BG[2]);
        roundedRect(content, MARGIN_LEFT, y - rectHeight, PAGE_WIDTH - MARGIN_LEFT - MARGIN_RIGHT, rectHeight, 8);
        content.fill();

        // Draw the instructions title
        content.setNonStrokingColor(HEADER_COLOR[0], HEADER_COLOR[1], HEADER_COLOR[2]);
        content.beginText();
        content.setFont(PDType1Font.HELVETICA_BOLD, HEADER_FONT_SIZE);
        content.newLineAtOffset(MARGIN_LEFT + 10, y - 20);
        content.showText("Instructions:");
        content.endText();

        // Draw the instructions text
        content.setNonStrokingColor(TEXT_COLOR[0], TEXT_COLOR[1], TEXT_COLOR[2]);
        int newY = drawParagraphText(content, instructions, MARGIN_LEFT + 10, y - 40, PDType1Font.HELVETICA, NORMAL_FONT_SIZE);

        // Return the lower position (either based on text or the rectangle)
        return y - rectHeight;
    }

    /**
     * Estimates the height needed for a text paragraph based on its content and width.
     */
    private int estimateTextHeight(String text, int width) {
        if (text == null || text.isEmpty()) {
            return 0;
        }

        // Approximate characters per line based on average character width
        // For PDType1Font.HELVETICA at size 11, approximately 0.5f points per character
        float avgCharWidth = 6.0f; // approximate width in points for 11pt Helvetica
        int charsPerLine = (int)(width / avgCharWidth);

        String[] paragraphs = text.split("\n");
        int lineCount = 0;

        for (String paragraph : paragraphs) {
            // Count how many lines this paragraph will take
            int paragraphLines = (int)Math.ceil((float)paragraph.length() / charsPerLine);
            lineCount += Math.max(1, paragraphLines); // At least one line per paragraph
        }

        return lineCount * (int)LINE_SPACING;
    }

    /**
     * Draws a horizontal line for the student's answer.
     */
    private int drawAnswerLine(PDPageContentStream content, int y) throws IOException {
        drawHorizontalLine(content, MARGIN_LEFT, y, TEXT_WIDTH, 0.5f);
        return y - ANSWER_SPACING;
    }

    /**
     * Draws the footer with page numbers and copyright.
     */
    private void drawFooter(PDPageContentStream content, int pageCount) throws IOException {
        // Draw footer line
        drawHorizontalLine(content, MARGIN_LEFT, 40, PAGE_WIDTH - MARGIN_LEFT - MARGIN_RIGHT, 0.5f);

        // Draw footer text
        content.beginText();
        content.setFont(PDType1Font.HELVETICA, SMALL_FONT_SIZE);
        content.setNonStrokingColor(0.5f, 0.5f, 0.5f);
        content.newLineAtOffset(MARGIN_LEFT, 25);
        content.showText("easyEval © " + LocalDateTime.now().getYear() + " - Document généré automatiquement");
        content.endText();

        // Draw page count on right
        String pageText = "Page " + pageCount;
        float textWidth = PDType1Font.HELVETICA.getStringWidth(pageText) / 1000 * 8;

        content.beginText();
        content.setFont(PDType1Font.HELVETICA, SMALL_FONT_SIZE);
        content.newLineAtOffset(PAGE_WIDTH - MARGIN_RIGHT - textWidth, 25);
        content.showText(pageText);
        content.endText();
    }

    /**
     * Improved text drawing method that properly breaks text into words and handles paragraphs.
     */
    private int drawParagraphText(PDPageContentStream content, String text, float x, float y,
                                  PDType1Font font, float fontSize) throws IOException {
        if (text == null || text.isEmpty()) {
            return (int)y;
        }

        float currentY = y;
        float lineWidth = PAGE_WIDTH - MARGIN_LEFT - MARGIN_RIGHT - (x - MARGIN_LEFT) - 10;

        // Split the text by newlines first
        String[] paragraphs = text.split("\n");

        for (String paragraph : paragraphs) {
            if (paragraph.trim().isEmpty()) {
                currentY -= LINE_SPACING;
                continue;
            }

            // Break paragraph into words
            String[] words = paragraph.split("\\s+");
            StringBuilder line = new StringBuilder();

            for (int i = 0; i < words.length; i++) {
                String word = words[i];

                // Check if adding this word would make the line too long
                String testLine = line.toString() + (line.length() > 0 ? " " : "") + word;
                float textWidth = font.getStringWidth(testLine) / 1000 * fontSize;

                if (textWidth > lineWidth && line.length() > 0) {
                    // Line is too long, print current line and start a new one
                    content.beginText();
                    content.setFont(font, fontSize);
                    content.newLineAtOffset(x, currentY);
                    content.showText(line.toString());
                    content.endText();

                    currentY -= LINE_SPACING;
                    line = new StringBuilder(word);
                } else {
                    // Add word to current line
                    if (line.length() > 0) {
                        line.append(" ");
                    }
                    line.append(word);
                }
            }

            // Print remaining line if not empty
            if (line.length() > 0) {
                content.beginText();
                content.setFont(font, fontSize);
                content.newLineAtOffset(x, currentY);
                content.showText(line.toString());
                content.endText();
                currentY -= LINE_SPACING;
            }

            // Add a small space between paragraphs
            currentY -= 5;
        }

        return (int)currentY;
    }

    /**
     * Helper method to draw a horizontal line.
     */
    private void drawHorizontalLine(PDPageContentStream content, float x, float y, float width, float thickness) throws IOException {
        content.setLineWidth(thickness);
        content.moveTo(x, y);
        content.lineTo(x + width, y);
        content.stroke();
    }

    /**
     * Helper method to draw a rounded rectangle.
     */
    private void roundedRect(PDPageContentStream content, float x, float y, float width, float height, float radius) throws IOException {
        // Start at top-left after the corner
        content.moveTo(x + radius, y + height);

        // Top edge
        content.lineTo(x + width - radius, y + height);

        // Top-right corner
        content.curveTo(
                x + width, y + height,
                x + width, y + height,
                x + width, y + height - radius
        );

        // Right edge
        content.lineTo(x + width, y + radius);

        // Bottom-right corner
        content.curveTo(
                x + width, y,
                x + width, y,
                x + width - radius, y
        );

        // Bottom edge
        content.lineTo(x + radius, y);

        // Bottom-left corner
        content.curveTo(
                x, y,
                x, y,
                x, y + radius
        );

        // Left edge
        content.lineTo(x, y + height - radius);

        // Top-left corner
        content.curveTo(
                x, y + height,
                x, y + height,
                x + radius, y + height
        );
    }

    /**
     * For testing the PDF generation.
     */
    public static void main(String[] args) {
        ArrayList<Question> questions = new ArrayList<>();
        questions.add(new Question("Que veut dire le 'U' dans 'CPU'", "Unit"));
        questions.add(new Question("En quelle année a été créer le noyau linux ?", "1991"));
        questions.add(new Question("Quel est le langage utilisé pour le développement Android natif ?", "Kotlin"));

        ExamDocument exam = new ExamDocument();
        exam.setId(1L);
        exam.setTitle("Examen de Programmation");
        exam.setInstructions("Répondez soigneusement à toutes les questions. Utilisez UNIQUEMENT UN STYLO ROUGE " +
                "pour inscrire votre matricule et toutes vos réponses. Cela est nécessaire pour la correction automatique. " +
                "Le temps alloué est de 60 minutes. Bon courage !");
        exam.setQuestions(questions);

        ExamDocumentTemplate examTemplate = new ExamDocumentTemplate();
        File pdf = examTemplate.generatePDF(exam, false);
        System.out.println("PDF generated at: " + pdf.getAbsolutePath());
    }
}
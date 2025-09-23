package be.esi.prj.easyeval.utils;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class responsible for extracting text from images using Tesseract OCR.
 * Modified version to directly extract all answers in order.
 */
public class TextExtractor {
    private ITesseract tesseract;
    private final ImageProcessor imageProcessor;
    private boolean enableDebug = false;

    // Possible paths for Tesseract data
    private static final String[] TESSDATA_PATHS = {
            "src/main/resources/be/esi/prj/easyeval/data/tessdata",
            "data/tessdata",
            "tessdata",
            System.getProperty("user.dir") + "/src/main/resources/be/esi/prj/easyeval/data/tessdata",
            System.getProperty("user.dir") + "/data/tessdata",
            System.getProperty("user.dir") + "/tessdata"
    };

    /**
     * Constructor
     * @param imageProcessor Image processor to use for graphic operations
     */
    public TextExtractor(ImageProcessor imageProcessor) {
        this.imageProcessor = imageProcessor;
        initialize();
    }

    /**
     * Initializes Tesseract OCR.
     */
    public void initialize() {
        tesseract = new Tesseract();

        // Find a valid path for tessdata
        String tessdataPath = findValidTessdataPath();
        if (tessdataPath == null) {
            System.err.println("ERROR: No valid tessdata directory found. OCR will not work properly.");
            System.err.println("Paths searched:");
            for (String path : TESSDATA_PATHS) {
                System.err.println("  - " + path);
            }
            // Set a default path even if it doesn't exist
            tessdataPath = TESSDATA_PATHS[0];
        }

        tesseract.setDatapath(tessdataPath);

        if (enableDebug) {
            System.out.println("Tesseract initialized with datapath: " + tessdataPath);
        }

        // Default configuration
        tesseract.setLanguage("fra");
        tesseract.setPageSegMode(7); // PSM_SINGLE_LINE for student ID
        resetTesseractVariables();
    }

    /**
     * Searches for a valid path for Tesseract data.
     * @return Valid path or null if none is found
     */
    private String findValidTessdataPath() {
        for (String path : TESSDATA_PATHS) {
            File dir = new File(path);
            if (dir.exists() && dir.isDirectory()) {
                // Check that it contains at least one traineddata file
                File[] trainedDataFiles = dir.listFiles((d, name) -> name.endsWith(".traineddata"));
                if (trainedDataFiles != null && trainedDataFiles.length > 0) {
                    return path;
                }
            }
        }
        return null;
    }

    /**
     * Resets Tesseract variables to their default values.
     */
    private void resetTesseractVariables() {
        tesseract.setTessVariable("tessedit_char_whitelist",
                "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789.,;:!?()-");
    }

    /**
     * Extracts the student ID from the processed image.
     * @param redTextImage Image containing only red text
     * @param matriculeZone Zone where to look for the student ID
     * @return Extracted ID or "UNKNOWN"
     */
    public String extractMatricule(BufferedImage redTextImage, Rectangle matriculeZone) {
        try {
            if (enableDebug) {
                // Extract and save the student ID zone for debugging
                BufferedImage zoneImage = imageProcessor.safeGetSubimage(
                        redTextImage,
                        matriculeZone.x,
                        matriculeZone.y,
                        matriculeZone.width,
                        matriculeZone.height);
                imageProcessor.saveDebugImage(zoneImage, "matricule_zone.png");
            }

            // Configure Tesseract for digit recognition
            tesseract.setPageSegMode(7); // PSM_SINGLE_LINE
            tesseract.setTessVariable("tessedit_char_whitelist", "0123456789"); // Limit to digits

            String matriculeText = tesseract.doOCR(imageProcessor.safeGetSubimage(
                    redTextImage,
                    matriculeZone.x,
                    matriculeZone.y,
                    matriculeZone.width,
                    matriculeZone.height)).trim();

            String cleanedText = matriculeText.replaceAll("[^0-9]", "");

            if (enableDebug) {
                System.out.println("Raw OCR text for student ID: " + matriculeText);
                System.out.println("Cleaned text: " + cleanedText);
            }

            if (cleanedText.length() >= 5) {
                return cleanedText.substring(0, 5);
            }

            // If not enough digits, look for a pattern of 5 digits
            if (cleanedText.length() < 5) {
                Pattern pattern = Pattern.compile("\\d{5}");
                Matcher matcher = pattern.matcher(matriculeText);

                if (matcher.find()) {
                    return matcher.group();
                }
            }

            return cleanedText.isEmpty() ? "UNKNOWN" : cleanedText;

        } catch (Exception e) {
            System.err.println("Error extracting student ID: " + e.getMessage());
            e.printStackTrace();
            return "UNKNOWN";
        } finally {
            resetTesseractVariables();
        }
    }

    /**
     * Extracts all answers from the questions area using the complete text.
     * @param redTextImage Processed image containing only red text
     * @param answerZone Questions/answers zone
     * @param expectedCount Expected number of questions
     * @return List of extracted answers
     */
    public List<String> extractAllAnswers(BufferedImage redTextImage, Rectangle answerZone, int expectedCount) {
        List<String> answers = new ArrayList<>();

        try {
            // Extract the questions area
            BufferedImage answerImage = imageProcessor.safeGetSubimage(
                    redTextImage,
                    answerZone.x,
                    answerZone.y,
                    answerZone.width,
                    answerZone.height);

            if (enableDebug) {
                imageProcessor.saveDebugImage(answerImage, "answer_zone.png");
            }

            tesseract.setPageSegMode(6); // PSM_SINGLE_BLOCK
            String fullText = tesseract.doOCR(answerImage).trim();

            if (enableDebug) {
                System.out.println("Raw OCR text from questions area: " + fullText);
            }

            // First, try to split by empty lines (paragraphs)
            String[] paragraphs = fullText.split("\\n\\s*\\n");

            // If we don't have enough paragraphs, try to split by lines
            if (paragraphs.length < expectedCount) {
                paragraphs = fullText.split("\\n");
            }

            if (enableDebug) {
                System.out.println("Number of segments found: " + paragraphs.length);
            }

            // Process each text segment found
            for (String paragraph : paragraphs) {
                // Clean the text to make an answer
                String cleaned = cleanAnswer(paragraph);

                // Only take non-empty answers
                if (!cleaned.isEmpty()) {
                    answers.add(cleaned);

                    if (enableDebug) {
                        System.out.println("Answer found: " + cleaned);
                    }

                    // If we have enough answers, we can stop
                    if (answers.size() >= expectedCount) {
                        break;
                    }
                }
            }

            // If we have too many answers, keep only the expected number
            if (answers.size() > expectedCount) {
                answers = answers.subList(0, expectedCount);
            }

            if (enableDebug) {
                System.out.println("Final number of answers: " + answers.size());
                for (int i = 0; i < answers.size(); i++) {
                    System.out.println("Answer " + (i+1) + ": " + answers.get(i));
                }
            }

            return answers;

        } catch (Exception e) {
            System.err.println("Error extracting answers: " + e.getMessage());
            e.printStackTrace();

            // In case of error, return a list of "XXX" answers
            List<String> defaultAnswers = new ArrayList<>();
            for (int i = 0; i < expectedCount; i++) {
                defaultAnswers.add("XXX");
            }
            return defaultAnswers;
        }
    }

    /**
     * Cleans an answer extracted by OCR.
     * @param rawAnswer Raw answer
     * @return Cleaned answer
     */
    public String cleanAnswer(String rawAnswer) {
        if (rawAnswer == null || rawAnswer.isEmpty()) {
            return "";
        }

        // Remove numbers that could be question numbers
        rawAnswer = rawAnswer.replaceAll("^\\s*\\d+\\.\\s*", "");

        // Replace non-alphanumeric characters with spaces
        String cleaned = rawAnswer.replaceAll("[^A-Za-z0-9]", " ");

        // Remove multiple spaces
        cleaned = cleaned.replaceAll("\\s+", " ").trim();

        return cleaned.toUpperCase();
    }

    /**
     * Calculates the similarity between two strings.
     * @param str1 First string
     * @param str2 Second string
     * @return Similarity value between 0.0 and 1.0
     */
    public double calculateSimilarity(String str1, String str2) {
        if (str1 == null || str2 == null) {
            return 0.0;
        }

        // Normalize strings
        String norm1 = str1.trim().toUpperCase();
        String norm2 = str2.trim().toUpperCase();

        if (norm1.equals(norm2)) {
            return 1.0;
        }

        // Calculate Levenshtein distance
        int distance = calculateLevenshteinDistance(norm1, norm2);
        int maxLength = Math.max(norm1.length(), norm2.length());

        if (maxLength == 0) {
            return 1.0;
        }

        // Convert distance to similarity (0.0 to 1.0)
        return 1.0 - ((double) distance / maxLength);
    }

    /**
     * Calculates the Levenshtein distance between two strings.
     * @param s1 First string
     * @param s2 Second string
     * @return Edit distance
     */
    private int calculateLevenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) {
            dp[i][0] = i;
        }

        for (int j = 0; j <= s2.length(); j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + cost
                );
            }
        }

        return dp[s1.length()][s2.length()];
    }

    /**
     * Enables or disables debug mode.
     * @param enabled true to enable
     */
    public void setDebugMode(boolean enabled) {
        this.enableDebug = enabled;
    }
}
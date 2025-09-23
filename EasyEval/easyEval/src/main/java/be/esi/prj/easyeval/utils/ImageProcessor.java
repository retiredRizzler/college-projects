package be.esi.prj.easyeval.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Class responsible for image processing for OCR recognition.
 * Applies transformations and filters to improve detection.
 */
public class ImageProcessor {
    // Variables for HSV red detection
    private float hueThresholdLow = 0.90f;   // Lower limit for red hue
    private float hueThresholdHigh = 0.05f;  // Upper limit for red hue
    private float saturationThreshold = 0.2f; // Threshold for saturation
    private float brightnessThreshold = 0.2f; // Threshold for brightness

    private boolean enableDebug = false;
    private boolean enableRotationCorrection = true;
    private boolean enableImageEnhancement = true;

    /**
     * Loads and prepares an image for OCR processing.
     * @param imageFile Image file to process
     * @return Prepared image
     * @throws IOException If the file cannot be read
     */
    public BufferedImage loadAndPrepareImage(File imageFile) throws IOException {
        if (!imageFile.exists() || !imageFile.canRead()) {
            throw new IOException("Unable to read image file: " + imageFile.getAbsolutePath());
        }

        // Load the image
        BufferedImage originalImage = ImageIO.read(imageFile);
        if (originalImage == null) {
            throw new IOException("Unsupported image format: " + imageFile.getAbsolutePath());
        }

        // Save the original image for debugging
        if (enableDebug) {
            saveDebugImage(originalImage, "original.png");
        }

        // Correct orientation if necessary
        if (enableRotationCorrection) {
            originalImage = correctImageOrientation(originalImage);
            if (enableDebug) {
                saveDebugImage(originalImage, "rotated.png");
            }
        }

        return originalImage;
    }

    /**
     * Divides the image into functional zones: student ID zone and questions/answers zone.
     * @param image Image to divide
     * @return Object containing the different zones
     */
    public ImageZones divideImageIntoZones(BufferedImage image) {
        int height = image.getHeight();
        int width = image.getWidth();

        // Student ID zone: upper third of the image
        Rectangle matriculeZone = new Rectangle(0, 0, width, height / 3);

        // Questions zone: lower two-thirds of the image
        Rectangle questionsZone = new Rectangle(0, height / 3, width, height * 2 / 3);

        if (enableDebug) {
            // Create a copy of the image with highlighted zones
            BufferedImage debugImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = debugImage.createGraphics();
            g.drawImage(image, 0, 0, null);

            // Draw the student ID zone in transparent green
            g.setColor(new Color(0, 255, 0, 50));
            g.fill(matriculeZone);
            g.setColor(Color.GREEN);
            g.draw(matriculeZone);
            g.drawString("Student ID Zone", 20, 30);

            // Draw the questions/answers zone in transparent blue
            g.setColor(new Color(0, 0, 255, 50));
            g.fill(questionsZone);
            g.setColor(Color.BLUE);
            g.draw(questionsZone);
            g.drawString("Questions/Answers Zone", 20, height / 3 + 30);

            g.dispose();
            saveDebugImage(debugImage, "image_zones.png");
        }

        return new ImageZones(matriculeZone, questionsZone);
    }

    /**
     * Extracts only the red text from the image using the HSV color space.
     * @param image Original image
     * @return Image containing only the red text
     */
    public BufferedImage extractRedText(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        // Create a new image with white background
        BufferedImage redTextImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = redTextImage.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        g.dispose();

        float[] hsb = new float[3];

        // Process all pixels
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color pixelColor = new Color(image.getRGB(x, y));

                // Convert RGB to HSB
                Color.RGBtoHSB(pixelColor.getRed(), pixelColor.getGreen(), pixelColor.getBlue(), hsb);
                float hue = hsb[0];            // 0-1 (0 and 1 are red)
                float saturation = hsb[1];     // 0-1
                float brightness = hsb[2];     // 0-1

                // Detect red pixels (hue close to 0 or 1)
                boolean isRed = ((hue <= hueThresholdHigh || hue >= hueThresholdLow) &&
                        saturation > saturationThreshold &&
                        brightness > brightnessThreshold);

                if (isRed) {
                    // Preserve red pixels in black (for OCR)
                    redTextImage.setRGB(x, y, Color.BLACK.getRGB());
                }
            }
        }

        if (enableDebug) {
            saveDebugImage(redTextImage, "extracted_red_text.png");
        }

        // Enhance contrast if requested
        if (enableImageEnhancement) {
            redTextImage = enhanceContrast(redTextImage);
            if (enableDebug) {
                saveDebugImage(redTextImage, "enhanced_red_text.png");
            }
        }

        return redTextImage;
    }

    /**
     * Enhances image contrast for better OCR recognition.
     * @param image Image to enhance
     * @return Image with enhanced contrast
     */
    public BufferedImage enhanceContrast(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = result.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        g.dispose();

        // Dilation to strengthen lines
        int kernelSize = 2;

        for (int y = kernelSize; y < height - kernelSize; y++) {
            for (int x = kernelSize; x < width - kernelSize; x++) {
                boolean hasBlackPixel = false;

                // Check neighboring pixels
                for (int ky = -kernelSize; ky <= kernelSize && !hasBlackPixel; ky++) {
                    for (int kx = -kernelSize; kx <= kernelSize && !hasBlackPixel; kx++) {
                        int ny = y + ky;
                        int nx = x + kx;

                        if (ny >= 0 && ny < height && nx >= 0 && nx < width) {
                            int rgb = image.getRGB(nx, ny);
                            int avg = ((rgb >> 16) & 0xFF) + ((rgb >> 8) & 0xFF) + (rgb & 0xFF);
                            avg /= 3;

                            if (avg < 128) { // Black or dark pixel
                                hasBlackPixel = true;
                            }
                        }
                    }
                }

                if (hasBlackPixel) {
                    result.setRGB(x, y, Color.BLACK.getRGB());
                }
            }
        }

        return result;
    }

    /**
     * Converts an image to grayscale.
     * @param originalImage Image to convert
     * @return Grayscale image
     */
    public BufferedImage convertToGrayscale(BufferedImage originalImage) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        BufferedImage grayImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g = grayImage.createGraphics();
        g.drawImage(originalImage, 0, 0, null);
        g.dispose();

        if (enableDebug) {
            saveDebugImage(grayImage, "grayscale_image.png");
        }

        return grayImage;
    }

    /**
     * Checks and corrects image orientation if necessary.
     * @param image Image to check
     * @return Correctly oriented image
     */
    private BufferedImage correctImageOrientation(BufferedImage image) {
        // Detect if the image is in landscape or portrait mode
        boolean isLandscape = image.getWidth() > image.getHeight();

        // If the image is in landscape mode, rotate it to portrait mode
        if (isLandscape) {
            if (enableDebug) {
                System.out.println("Orientation correction: 90 degree rotation");
            }
            return rotateImage(image, 90);
        }

        return image;
    }

    /**
     * Extracts a subimage safely respecting the image boundaries.
     * @param source Source image
     * @param x Starting X position
     * @param y Starting Y position
     * @param width Desired width
     * @param height Desired height
     * @return Extracted subimage
     */
    public BufferedImage safeGetSubimage(BufferedImage source, int x, int y, int width, int height) {
        // Check and adjust coordinates
        x = Math.max(0, x);
        y = Math.max(0, y);

        // Adjust width and height if necessary
        width = Math.min(width, source.getWidth() - x);
        height = Math.min(height, source.getHeight() - y);

        // Check that dimensions are valid
        if (width <= 0 || height <= 0) {
            // Return an empty image
            return new BufferedImage(1, 1, source.getType());
        }

        return source.getSubimage(x, y, width, height);
    }

    /**
     * Rotates an image by a specified angle.
     * @param image Image to rotate
     * @param angle Rotation angle in degrees
     * @return Rotated image
     */
    private BufferedImage rotateImage(BufferedImage image, double angle) {
        // Convert angle to radians
        double rads = Math.toRadians(angle);

        // Determine the size of the resulting image
        double sin = Math.abs(Math.sin(rads));
        double cos = Math.abs(Math.cos(rads));
        int newWidth = (int) Math.floor(image.getHeight() * sin + image.getWidth() * cos);
        int newHeight = (int) Math.floor(image.getHeight() * cos + image.getWidth() * sin);

        // Create the resulting image
        BufferedImage rotatedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = rotatedImage.createGraphics();

        // Set high rendering quality
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Transform the graphic
        AffineTransform at = new AffineTransform();
        at.translate((newWidth - image.getWidth()) / 2.0, (newHeight - image.getHeight()) / 2.0);
        at.rotate(rads, image.getWidth() / 2.0, image.getHeight() / 2.0);
        g2d.setTransform(at);

        // Draw the original image on the transformed graphic
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();

        return rotatedImage;
    }

    /**
     * Saves an image for debugging.
     * @param image Image to save
     * @param filename Filename
     */
    public void saveDebugImage(BufferedImage image, String filename) {
        if (!enableDebug) return;

        try {
            File debugDir = new File("debug-ocr");
            if (!debugDir.exists()) debugDir.mkdirs();
            ImageIO.write(image, "png", new File(debugDir, filename));
        } catch (Exception e) {
            System.err.println("Error saving debug image: " + e.getMessage());
        }
    }

    /**
     * Cleans the debug folder.
     */
    public void cleanDebugFolder() {
        if (!enableDebug) return;

        try {
            File debugDir = new File("debug-ocr");
            if (debugDir.exists()) {
                File[] files = debugDir.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.isFile()) {
                            file.delete();
                        }
                    }
                }
                System.out.println("debug-ocr folder cleaned");
            } else {
                debugDir.mkdirs();
                System.out.println("debug-ocr folder created");
            }
        } catch (Exception e) {
            System.err.println("Error cleaning debug-ocr folder: " + e.getMessage());
        }
    }

    // Getters and setters

    public void setDebugMode(boolean enabled) {
        this.enableDebug = enabled;
    }

    public void setRotationCorrection(boolean enabled) {
        this.enableRotationCorrection = enabled;
    }

    public void setImageEnhancement(boolean enabled) {
        this.enableImageEnhancement = enabled;
    }

    /**
     * Adjusts red detection parameters.
     * @param hueLow Lower limit for red hue (0.0-1.0)
     * @param hueHigh Upper limit for red hue (0.0-1.0)
     * @param saturation Threshold for saturation (0.0-1.0)
     * @param brightness Threshold for brightness (0.0-1.0)
     */
    public void setRedDetectionParameters(float hueLow, float hueHigh, float saturation, float brightness) {
        this.hueThresholdLow = hueLow;
        this.hueThresholdHigh = hueHigh;
        this.saturationThreshold = saturation;
        this.brightnessThreshold = brightness;
    }
}
package be.esi.prj.easyeval.utils;

import java.awt.Rectangle;

/**
 * Data class representing the different functional areas of an exam image.
 */
public class ImageZones {
    private final Rectangle matriculeZone;
    private final Rectangle questionsZone;

    /**
     * Constructor
     * @param matriculeZone Zone where the student ID is located
     * @param questionsZone Zone where questions and answers are located
     */
    public ImageZones(Rectangle matriculeZone, Rectangle questionsZone) {
        this.matriculeZone = matriculeZone;
        this.questionsZone = questionsZone;
    }

    /**
     * Gets the student ID zone.
     * @return Rectangle defining the zone
     */
    public Rectangle getMatriculeZone() {
        return matriculeZone;
    }

    /**
     * Gets the questions/answers zone.
     * @return Rectangle defining the zone
     */
    public Rectangle getQuestionsZone() {
        return questionsZone;
    }
}
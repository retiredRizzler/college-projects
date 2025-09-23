package model;

import java.io.Serializable;

/**
 * Shape represents the shape of a tile
 */
public enum Shape implements Serializable {
    CROSS("x"), SQUARE("■"), ROUND("●"), STAR("*"), PLUS("+"), DIAMOND("◆");

    private String shape;

    /**
     * Constructor for shape enumeration
     * @param shape the symbol for the specific shape
     */
    private Shape(String shape)
    {
        this.shape = shape;
    }

    /**
     * Getter for shape attribute
     * @return String the symbol for the specific shape
     */
    public String getShape()
    {
        return shape;
    }
}

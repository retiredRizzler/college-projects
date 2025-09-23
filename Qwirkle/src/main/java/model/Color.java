package model;

import java.io.Serializable;

/**
 * Color represents color of a tile
 */
public enum Color implements Serializable {
    BLUE("\033[34m"), RED("\033[31m"), GREEN("\033[32m"), ORANGE("\033[38;5;214m"),
    YELLOW("\033[38;5;227m"), PURPLE("\033[35m");

    private String aCode;

    /**
     * Constructor for Color enum
     * @param ansiCode the ansi code of the color
     */
    Color(String ansiCode)
    {
        this.aCode = ansiCode;
    }

    /**
     * Getter for the ansi code of a color
     * @return String the ansi code of the color
     */
    public String getColorCode()
    {
        return aCode;
    }
}

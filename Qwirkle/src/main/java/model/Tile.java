package model;

import java.io.Serializable;

/**
 * Tile represents a piece of our game
 * @param color tile's color
 * @param shape tile's shape
 */
public record Tile(Color color, Shape shape) implements Serializable {

    @Override
    public String toString() {
        return color.getColorCode() + " " + shape.getShape() + "\033[0m";
    }
}


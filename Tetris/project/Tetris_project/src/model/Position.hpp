#ifndef POSITION_HPP
#define POSITION_HPP

/**
 * @class Position
 * @brief Represents a position in a two-dimensional space.
 *
 * The Position class encapsulates the coordinates of a point in a two-dimensional space.
 */
class Position {
private:
    int x; /**< The x-coordinate of the position. */
    int y; /**< The y-coordinate of the position. */

public:
    /**
     * @brief Constructor for creating a Position object with specified coordinates.
     * @param xVal The x-coordinate of the position.
     * @param yVal The y-coordinate of the position.
     */
    Position(int xVal, int yVal) : x(xVal), y(yVal) {}

    /**
     * @brief Gets the x-coordinate of the position.
     * @return The x-coordinate.
     */
    int getX() const {
        return x;
    }

    /**
     * @brief Gets the y-coordinate of the position.
     * @return The y-coordinate.
     */
    int getY() const {
        return y;
    }

    /**
     * @brief Sets the x-coordinate of the position.
     * @param xVal The new x-coordinate.
     */
    void setX(int xVal) {
        x = xVal;
    }

    /**
     * @brief Sets the y-coordinate of the position.
     * @param yVal The new y-coordinate.
     */
    void setY(int yVal) {
        y = yVal;
    }

    /**
     * @brief Moves the position by the specified offsets.
     * @param dx The change in the x-coordinate.
     * @param dy The change in the y-coordinate.
     */
    void moveBy(int dx, int dy) {
        x += dx;
        y += dy;
    }

    /**
     * @brief Overloaded equality operator to compare two positions.
     * @param other The position to compare with.
     * @return True if the positions are equal, false otherwise.
     */
    bool operator==(const Position& other) const {
        return x == other.x && y == other.y;
    }
};

#endif // POSITION_HPP

#ifndef PIECE_H
#define PIECE_H

#include "../Position.hpp"
#include <vector>

/**
 * @class Piece
 * @brief Represents a generic Tetris piece.
 *
 * The Piece class encapsulates the behavior and properties of a Tetris piece,
 * including its shape, position, and movement operations.
 */
class Piece {
public:
    /**
     * @brief Constructor for creating a Piece object.
     * @param shape The shape of the piece defined by a vector of Position objects.
     */
    Piece(const std::vector<Position>& shape);

    /**
     * @brief Sets the position of the piece.
     * @param r Row position.
     * @param c Column position.
     */
    virtual void setPosition(int r, int c);

    /**
     * @brief Returns the absolute positions of the piece.
     * @return A vector containing absolute positions of the piece.
     */
    virtual std::vector<Position> getAbsolutePositions() const;

    /**
     * @brief Rotates the piece clockwise.
     */
    virtual void rotateClockwise();

    /**
     * @brief Rotates the piece counterclockwise.
     */
    virtual void rotateCounterClockwise();

    /**
     * @brief Moves the piece down.
     */
    virtual void moveDown();

    /**
     * @brief Moves the piece left.
     */
    virtual void moveLeft();

    /**
     * @brief Moves the piece right.
     */
    virtual void moveRight();

    /**
     * @brief Returns the row position of the piece.
     * @return Row position of the piece.
     */
    virtual int getRow();

    /**
     * @brief Returns the column position of the piece.
     * @return Column position of the piece.
     */
    virtual int getCol();

    /**
     * @brief Returns the shape of the piece.
     * @return A reference to the vector containing the shape of the piece.
     */
    std::vector<Position>& getShape();

    /**
     * @brief Returns the rotation matrix of the piece.
     * @return A 2D vector representing the rotation matrix.
     */
    std::vector<std::vector<int>> getRotationMatrix();

    /**
     * @brief Negates the given matrix.
     * @param matrix The matrix to be negated.
     */
    void negateMatrix(std::vector<std::vector<int>>& matrix);

    /**
     * @brief Overloaded equality operator to compare two pieces.
     * @param other The piece to compare with.
     * @return True if the pieces are equal, false otherwise.
     */
    virtual bool operator==(const Piece& other) const;

protected:
    std::vector<Position> shape; /**< The shape of the piece. */

private:
    int row; /**< The row position of the piece. */
    int col; /**< The column position of the piece. */

    /**
     * @brief Rotates the piece using the given rotation matrix.
     * @param rotationMatrix The rotation matrix to apply.
     */
    void rotate(const std::vector<std::vector<int>>& rotationMatrix);
};

#endif // PIECE_H

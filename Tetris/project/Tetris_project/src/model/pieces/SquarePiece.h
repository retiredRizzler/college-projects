#ifndef SQUAREPIECE_H
#define SQUAREPIECE_H

#include "Piece.h" // Include necessary headers

/**
 * @class SquarePiece
 * @brief Represents the square Tetris piece.
 *
 * The SquarePiece class is a subclass of Piece and represents the square-shaped Tetris piece.
 */
class SquarePiece : public Piece {
public:
    /**
     * @brief Default constructor for creating a SquarePiece object.
     */
    SquarePiece();

    /**
     * @brief Rotates the square piece clockwise (override from Piece).
     * This function has no effect for square pieces as they cannot be rotated.
     */
    void rotateClockwise() override;

    /**
     * @brief Rotates the square piece counter-clockwise (override from Piece).
     * This function has no effect for square pieces as they cannot be rotated.
     */
    void rotateCounterClockwise() override;

    /**
     * @brief Checks whether this SquarePiece is equal to another Piece object.
     * @param other The other Piece object to compare with.
     * @return True if the pieces are equal, false otherwise.
     */
    bool operator==(const Piece& other) const override;
};

#endif // SQUAREPIECE_H

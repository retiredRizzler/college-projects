#ifndef SPIECE_H
#define SPIECE_H

#include "Piece.h" // Include necessary headers

/**
 * @class SPiece
 * @brief Represents the "S" Tetris piece.
 *
 * The SPiece class is a subclass of Piece and represents the "S" shaped Tetris piece.
 */
class SPiece : public Piece {
public:
    /**
     * @brief Default constructor for creating an SPiece object.
     */
    SPiece();

    /**
     * @brief Checks whether this SPiece is equal to another Piece object.
     * @param other The other Piece object to compare with.
     * @return True if the pieces are equal, false otherwise.
     */
    bool operator==(const Piece& other) const override;
};

#endif // SPIECE_H

#ifndef LPIECE_H
#define LPIECE_H

#include "Piece.h" // Include necessary headers

/**
 * @class LPiece
 * @brief Represents the "L" Tetris piece.
 *
 * The LPiece class is a subclass of Piece and represents the "L" shaped Tetris piece.
 */
class LPiece : public Piece {
public:
    /**
     * @brief Default constructor for creating an LPiece object.
     */
    LPiece();

    /**
     * @brief Checks whether this LPiece is equal to another Piece object.
     * @param other The other Piece object to compare with.
     * @return True if the pieces are equal, false otherwise.
     */
    bool operator==(const Piece& other) const override;
};

#endif // LPIECE_H

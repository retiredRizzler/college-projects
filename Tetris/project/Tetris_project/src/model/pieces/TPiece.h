#ifndef TPIECE_H
#define TPIECE_H

#include "Piece.h" // Include necessary headers

/**
 * @class TPiece
 * @brief Represents the "T" Tetris piece.
 *
 * The TPiece class is a subclass of Piece and represents the "T" shaped Tetris piece.
 */
class TPiece : public Piece {
public:
    /**
     * @brief Default constructor for creating a TPiece object.
     */
    TPiece();

    /**
     * @brief Checks whether this TPiece is equal to another Piece object.
     * @param other The other Piece object to compare with.
     * @return True if the pieces are equal, false otherwise.
     */
    bool operator==(const Piece& other) const override;
};

#endif // TPIECE_H

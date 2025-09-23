#ifndef ZPIECE_H
#define ZPIECE_H

#include "Piece.h" // Include necessary headers

/**
 * @class ZPiece
 * @brief Represents the "Z" Tetris piece.
 *
 * The ZPiece class is a subclass of Piece and represents the "Z" shaped Tetris piece.
 */
class ZPiece : public Piece {
public:
    /**
     * @brief Default constructor for creating a ZPiece object.
     */
    ZPiece();

    /**
     * @brief Checks whether this ZPiece is equal to another Piece object.
     * @param other The other Piece object to compare with.
     * @return True if the pieces are equal, false otherwise.
     */
    bool operator==(const Piece& other) const override;
};

#endif // ZPIECE_H

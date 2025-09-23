#ifndef IPIECE_H
#define IPIECE_H

#include "Piece.h" // Include necessary headers

/**
 * @class IPiece
 * @brief Represents the "I" Tetris piece.
 *
 * The IPiece class is a subclass of Piece and represents the "I" shaped Tetris piece.
 */
class IPiece : public Piece {
public:
    /**
     * @brief Default constructor for creating an IPiece object.
     */
    IPiece();

    /**
     * @brief Checks whether this IPiece is equal to another Piece object.
     * @param other The other Piece object to compare with.
     * @return True if the pieces are equal, false otherwise.
     */
    bool operator==(const Piece& other) const override;
};

#endif // IPIECE_H

#ifndef JPIECE_H
#define JPIECE_H

#include "Piece.h" // Include necessary headers

/**
 * @class JPiece
 * @brief Represents the "J" Tetris piece.
 *
 * The JPiece class is a subclass of Piece and represents the "J" shaped Tetris piece.
 */
class JPiece : public Piece {
public:
    /**
     * @brief Default constructor for creating a JPiece object.
     */
    JPiece();

    /**
     * @brief Checks whether this JPiece is equal to another Piece object.
     * @param other The other Piece object to compare with.
     * @return True if the pieces are equal, false otherwise.
     */
    bool operator==(const Piece& other) const override;
};

#endif // JPIECE_H

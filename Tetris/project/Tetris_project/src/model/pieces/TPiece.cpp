#include "TPiece.h"

TPiece::TPiece() : Piece({{0, 0}, {-1, 0}, {0, 1}, {1, 0}}) {}

bool TPiece::operator==(const Piece& other) const {
    // Check if other is also a TPiece (dynamic cast or RTTI)
    const TPiece* otherTPiece = dynamic_cast<const TPiece*>(&other);
    return otherTPiece != nullptr && shape == otherTPiece->shape;
}

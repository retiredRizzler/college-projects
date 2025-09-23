#include "JPiece.h"

JPiece::JPiece() : Piece({{0, 0}, {0, 1}, {0, -1}, {-1, -1}}) {}

bool JPiece::operator==(const Piece& other) const {
    // Check if other is also a TPiece (dynamic cast or RTTI)
    const JPiece* otherJPiece = dynamic_cast<const JPiece*>(&other);
    return otherJPiece != nullptr && shape == otherJPiece->shape;
}

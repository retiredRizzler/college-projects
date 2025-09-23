#include "SPiece.h"

SPiece::SPiece() : Piece({{0, 0}, {1, 0}, {0, -1}, {-1, -1}}) {}

bool SPiece::operator==(const Piece& other) const {
    // Check if other is also a TPiece (dynamic cast or RTTI)
    const SPiece* otherSPiece = dynamic_cast<const SPiece*>(&other);
    return otherSPiece != nullptr && shape == otherSPiece->shape;
}

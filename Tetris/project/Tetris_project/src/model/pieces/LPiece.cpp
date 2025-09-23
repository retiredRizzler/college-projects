#include "LPiece.h"

LPiece::LPiece() : Piece({{0, 0}, {0, 1}, {0, -1}, {1, -1}}) {}
bool LPiece::operator==(const Piece& other) const {
    // Check if other is also a TPiece (dynamic cast or RTTI)
    const LPiece* otherLPiece = dynamic_cast<const LPiece*>(&other);
    return otherLPiece != nullptr && shape == otherLPiece->shape;
}

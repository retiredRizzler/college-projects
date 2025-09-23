#include "Piece.h"
#include "ZPiece.h"

ZPiece::ZPiece() : Piece({{0, 0}, {-1, 0}, {0, -1}, {1, -1}}) {}

bool ZPiece::operator==(const Piece& other) const {
    // Check if other is also a TPiece (dynamic cast or RTTI)
    const ZPiece* otherZPiece = dynamic_cast<const ZPiece*>(&other);
    return otherZPiece != nullptr && shape == otherZPiece->shape;
}

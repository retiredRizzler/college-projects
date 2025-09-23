#include "IPiece.h"

IPiece::IPiece() : Piece({{0, 0}, {0, 1}, {0, -1}, {0, -2}}) {}
bool IPiece::operator==(const Piece& other) const {
    // Check if other is also a TPiece (dynamic cast or RTTI)
    const IPiece* otherIPiece = dynamic_cast<const IPiece*>(&other);
    return otherIPiece != nullptr && shape == otherIPiece->shape;
}

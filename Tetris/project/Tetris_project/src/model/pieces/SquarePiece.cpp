#include "SquarePiece.h"

SquarePiece::SquarePiece() : Piece({{0, 0}, {0, 1}, {1, 0}, {1, 1}}) {
}

void SquarePiece::rotateClockwise() {
    return;
}

void SquarePiece::rotateCounterClockwise() {
    return;
}

bool SquarePiece::operator==(const Piece& other) const {
    // Check if other is also a TPiece (dynamic cast or RTTI)
    const SquarePiece* otherSquarePiece = dynamic_cast<const SquarePiece*>(&other);
    return otherSquarePiece != nullptr && shape == otherSquarePiece->shape;
}

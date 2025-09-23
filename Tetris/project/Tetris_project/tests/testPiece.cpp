#include <catch2/catch_test_macros.hpp>
#include "model/pieces/Piece.h"
#include "model/pieces/IPiece.h"
#include "model/pieces/ZPiece.h"
#include "model/pieces/JPiece.h"
#include "model/pieces/SPiece.h"
#include "model/pieces/LPiece.h"
#include "model/pieces/TPiece.h"
#include "model/pieces/SquarePiece.h"

TEST_CASE("setPosition") {
    Piece piece({{0, 0}});

    piece.setPosition(5, 3);
    REQUIRE(piece.getRow() == 5);
    REQUIRE(piece.getCol() == 3);
}

TEST_CASE("getAbsolutePositions") {
    Piece piece({{1, 2}, {4, 0}});
    piece.setPosition(3, 1);

    std::vector<Position> expectedPositions = {{4, 3}, {7, 1}};
    REQUIRE(piece.getAbsolutePositions() == expectedPositions);
}

TEST_CASE("move") {
    Piece piece({{0, 0}});

    piece.moveDown();
    REQUIRE(piece.getRow() == 1);

    piece.moveLeft();
    REQUIRE(piece.getCol() == -1);

    piece.moveRight();
    REQUIRE(piece.getCol() == 0);
}


TEST_CASE("rotateClockwise") {
    SECTION("IPiece Rotation") {
        IPiece piece;

        piece.rotateClockwise();
        std::vector<Position> expectedrotateClockwised = {{0, 0}, {1, 0}, {-1, 0}, {-2, 0}};
        REQUIRE(piece.getShape() == expectedrotateClockwised);

        piece.rotateClockwise(); // rotateClockwise again for second rotation
        expectedrotateClockwised = {{0, 0}, {0, -1}, {0, 1}, {0, 2}};
        REQUIRE(piece.getShape() == expectedrotateClockwised);

        piece.rotateClockwise();
        expectedrotateClockwised = {{0, 0}, {-1, 0}, {1, 0}, {2, 0}};
        REQUIRE(piece.getShape() == expectedrotateClockwised);


        piece.rotateClockwise(); // rotateClockwise again for fourth rotation (back to original)
        expectedrotateClockwised = {{0, 0}, {0, 1}, {0, -1}, {0, -2}};
        REQUIRE(piece.getShape() == expectedrotateClockwised);
    }

    SECTION("ZPiece rotation") {
        ZPiece piece;

        piece.rotateClockwise();
        std::vector<Position> expectedrotateClockwised = {{0, 0}, {0, 1}, {-1, 0}, {-1, -1}};
        REQUIRE(piece.getShape() == expectedrotateClockwised);

        piece.rotateClockwise();
        expectedrotateClockwised = {{0, 0}, {1, 0}, {0, 1}, {-1, 1}};
        REQUIRE(piece.getShape() == expectedrotateClockwised);

        piece.rotateClockwise();
        expectedrotateClockwised = {{0, 0}, {0, -1}, {1, 0}, {1, 1}};
        REQUIRE(piece.getShape() == expectedrotateClockwised);

        piece.rotateClockwise();
        expectedrotateClockwised = {{0, 0}, {-1, 0}, {0, -1}, {1, -1}};
        REQUIRE(piece.getShape() == expectedrotateClockwised);

    }
}







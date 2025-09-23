#include <catch2/catch_test_macros.hpp>
#include "model/GameBoard.h"
#include "model/pieces/TPiece.h"
#include "model/pieces/IPiece.h"
#include "model/pieces/ZPiece.h"
#include "model/pieces/LPiece.h"

TEST_CASE( "Test of the isInsideBoard function") {

    SECTION("testIsInsideBoard"){
        GameBoard board(5, 8);
        // Valid positions within board boundaries
        REQUIRE(board.isInsideBoard(2, 4) == true);
        REQUIRE(board.isInsideBoard(0, 0) == true);
        REQUIRE(board.isInsideBoard(4, 7) == true);
        REQUIRE(board.isInsideBoard(0, 7)  == true);

        // Invalid positions outside board boundaries
        REQUIRE(board.isInsideBoard(-1, 3) == false);
        REQUIRE(board.isInsideBoard(5, 1) == false);
        REQUIRE(board.isInsideBoard(2, 8) == false);
        REQUIRE(board.isInsideBoard(5, 8) == false);
        REQUIRE(board.isInsideBoard(0, 8) == false);
    }
}

TEST_CASE("GameBoard: setPieceAt and getPieceAt") {
}

TEST_CASE("getOccupiedPositions") {
    GameBoard board(10,20);
    SECTION("No occupied positions") {
        REQUIRE(board.getOccupiedPositions().empty() == true);
    }
    SECTION("Occupied positions") {
        std::shared_ptr<Piece> iPiece = std::make_shared<Piece>(IPiece());
        std::shared_ptr<Piece> tPiece = std::make_shared<Piece>(TPiece());
        std::shared_ptr<Piece> zPiece = std::make_shared<Piece>(ZPiece());

        board.setPieceAt(3, 3, iPiece);
        board.setPieceAt(6, 6, tPiece);
        board.setPieceAt(7, 15, zPiece);

        REQUIRE(board.getOccupiedPositions().size() == 12);
    }


}

TEST_CASE("findCompletedLines empty board") {
    GameBoard board(5, 5);

    std::vector<int> completedLines = board.findCompletedLines();

    REQUIRE(completedLines.empty());
}

TEST_CASE("findCompletedLines simple completed line") {
    GameBoard board(4, 4);
    for (int col = 0; col < board.getCols(); ++col) {
        board.setPieceAt(0, col, std::make_shared<LPiece>());
    }
    std::vector<int> completedLines = board.findCompletedLines();

    REQUIRE(completedLines.size() == 1);
    REQUIRE(completedLines[0] == 0);
}

TEST_CASE("findCompletedLines multiple completed lines") {
    GameBoard board(5, 5);
    for (int row = 0; row < 2; ++row) {
        for (int col = 0; col < board.getCols(); ++col) {
            board.setPieceAt(row, col, std::make_shared<LPiece>());
        }
    }
    std::vector<int> completedLines = board.findCompletedLines();

    REQUIRE(completedLines.size() == 2);
    REQUIRE(completedLines[0] == 0);
    REQUIRE(completedLines[1] == 1);
}

TEST_CASE("findCompletedLines no completed lines") {
    GameBoard board(5, 5);
    for (int row = 0; row < board.getRows(); ++row) {
        for (int col = 0; col < board.getCols(); ++col) {
            if (col % 2 == 0) {
                board.setPieceAt(row, col, std::make_shared<LPiece>());
            }
        }
    }

    std::vector<int> completedLines = board.findCompletedLines();

    REQUIRE(completedLines.empty());
}

TEST_CASE("findCompletedLines completed line at bottom") {
    GameBoard board(4, 4);
    for (int col = 0; col < board.getCols(); ++col) {
        board.setPieceAt(board.getRows() - 1, col, std::make_shared<LPiece>()); // Ligne complète en bas
    }

    std::vector<int> completedLines = board.findCompletedLines();

    REQUIRE(completedLines.size() == 1);
    REQUIRE(completedLines[0] == 3);
}

TEST_CASE("findCompletedLines with null pieces") {
    GameBoard board(4, 4);
    for (int row = 0; row < 2; ++row) {
        for (int col = 0; col < board.getCols(); ++col) {
            if (row == 0) {
                board.setPieceAt(row, col, std::make_shared<LPiece>());
            }
        }
    }

    std::vector<int> completedLines = board.findCompletedLines();

    REQUIRE(completedLines.size() == 1);
    REQUIRE(completedLines[0] == 0);
}

TEST_CASE("clearCompletedLines no completed lines") {
    GameBoard board(5, 5);
    for (int row = 0; row < board.getRows(); ++row) {
        for (int col = 0; col < board.getCols(); ++col) {
            if (col % 2 == 0) {
                board.setPieceAt(row, col, std::make_shared<LPiece>());
            }
        }
    }

    int numClearedLines = board.clearCompletedLines();

    REQUIRE(numClearedLines == 0);
}

TEST_CASE("clearCompletedLines single completed line") {
    GameBoard board(4, 4);
    for (int col = 0; col < board.getCols(); ++col) {
        board.setPieceAt(0, col, std::make_shared<LPiece>()); // Ligne complète en haut
    }

    int numClearedLines = board.clearCompletedLines();

    REQUIRE(numClearedLines == 1);
}

TEST_CASE("clearCompletedLines multiple completed lines") {
    GameBoard board(5, 5);
    for (int row = 0; row < 5; ++row) {
        for (int col = 0; col < board.getCols(); ++col) {
            if (col % 2 == 0 && row == 2) {
                board.setPieceAt(row, col, std::make_shared<LPiece>());
            }
            if (row==4 || row == 3) {
                board.setPieceAt(row, col, std::make_shared<LPiece>());
            }
        }
    }


    int numClearedLines = board.clearCompletedLines();

    auto previousPiecePos = board.getPieceAt(2, 2);

    REQUIRE(previousPiecePos == nullptr);
    REQUIRE(board.getPieceAt(4, 2) != nullptr);
    REQUIRE(numClearedLines == 2);

}

TEST_CASE("clearCompletedLines completed line at bottom") {
    GameBoard board(4, 4);
    for (int col = 0; col < board.getCols(); ++col) {
        board.setPieceAt(board.getRows() - 1, col, std::make_shared<LPiece>()); // Ligne complète en bas
    }

    int numClearedLines = board.clearCompletedLines();

    REQUIRE(numClearedLines == 1);
}


TEST_CASE("getPieceAt valid position with piece") {
    GameBoard board(5, 5);
    std::shared_ptr<Piece> piece = std::make_shared<LPiece>();
    board.setPieceAt(2, 3, piece);

    std::shared_ptr<Piece> retrievedPiece = board.getPieceAt(2, 3);

    REQUIRE(retrievedPiece == piece);
}

TEST_CASE("getPieceAt invalid position") {
    GameBoard board(5, 5);
    std::shared_ptr<Piece> piece = std::make_shared<LPiece>();

    std::shared_ptr<Piece> outOfBoundsPiece = board.getPieceAt(-1, 2);
    std::shared_ptr<Piece> beyondBoardPiece = board.getPieceAt(board.getRows(), 1);

    REQUIRE(outOfBoundsPiece == nullptr);
    REQUIRE(beyondBoardPiece == nullptr);
}

TEST_CASE("getPieceAt empty position") {
    GameBoard board(5, 5);

    std::shared_ptr<Piece> emptyPiece = board.getPieceAt(1, 2);

    REQUIRE(emptyPiece == nullptr);
}

TEST_CASE("setPieceAt normal placement within bounds") {

    GameBoard board(5, 5);
    std::shared_ptr<Piece> piece = std::make_shared<LPiece>();

    board.setPieceAt(2, 3, piece);

    REQUIRE(board.getPieceAt(2, 3) == piece);
    REQUIRE(piece->getRow() == 2);
    REQUIRE(piece->getCol() == 3);
}

TEST_CASE("setPieceAt overwrites existing piece") {
    GameBoard board(5, 5);
    std::shared_ptr<Piece> piece1 = std::make_shared<LPiece>();
    std::shared_ptr<Piece> piece2 = std::make_shared<LPiece>();
    board.setPieceAt(3, 1, piece1);

    board.setPieceAt(3, 1, piece2);

    REQUIRE(board.getPieceAt(3, 1) == piece2);
}

TEST_CASE("clearCompletedLines completed lines at top of board") {
    GameBoard board(5, 5);

    // Placer des pièces pour compléter les premières lignes du plateau
    for (int row = 0; row < 3; ++row) {
        for (int col = 0; col < board.getCols(); ++col) {
            board.setPieceAt(row, col, std::make_shared<LPiece>());
        }
    }

    int numClearedLines = board.clearCompletedLines();

    for (int row = 0; row < 3; ++row) {
        for (int col = 0; col < board.getCols(); ++col) {
            REQUIRE(board.getPieceAt(row, col) == nullptr);
        }
    }
    REQUIRE(numClearedLines == 3);
}
TEST_CASE("clearCompletedLines two completed lines with piece movement") {
    GameBoard board(5, 5);

    for (int col = 0; col < board.getCols(); ++col) {
        board.setPieceAt(1, col, std::make_shared<LPiece>());
        board.setPieceAt(3, col, std::make_shared<LPiece>());
    }

    int numClearedLines = board.clearCompletedLines();

    REQUIRE(board.getPieceAt(1, 0) == nullptr);
    REQUIRE(board.getPieceAt(3, 0) == nullptr);

    REQUIRE(numClearedLines == 2);
}

TEST_CASE("clearCompletedLines no completed lines2") {
    GameBoard board(5, 5);

    // Placer des pièces sans compléter aucune ligne
    board.setPieceAt(1, 1, std::make_shared<LPiece>());
    board.setPieceAt(2, 2, std::make_shared<LPiece>());

    int numClearedLines = board.clearCompletedLines();

    REQUIRE(board.getPieceAt(1, 1) != nullptr);
    REQUIRE(board.getPieceAt(2, 2) != nullptr);


    REQUIRE(numClearedLines == 0);
}
TEST_CASE("clearCompletedLines all lines completed") {
    GameBoard board(5, 5);

    // Placer des pièces pour compléter toutes les lignes
    for (int row = 0; row < board.getRows(); ++row) {
        for (int col = 0; col < board.getCols(); ++col) {
            board.setPieceAt(row, col, std::make_shared<LPiece>());
        }
    }

    int numClearedLines = board.clearCompletedLines();
    for (int row = 0; row < board.getRows(); ++row) {
        for (int col = 0; col < board.getCols(); ++col) {
            REQUIRE(board.getPieceAt(row, col) == nullptr);
        }
    }
    REQUIRE(numClearedLines == board.getRows());
}


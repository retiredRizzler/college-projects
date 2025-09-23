#include <catch2/catch_test_macros.hpp>
#include "model/TetrisModel.h"
#include "model/pieces/LPiece.h"

TEST_CASE("spawnPiece places a piece on the board at the top") {
    TetrisModel model(5, 5);

    model.spawnPiece();

    REQUIRE(model.getBoard().getPieceAt(1, 2) != nullptr);
}

TEST_CASE("TetrisModel::spawnPiece() spawns piece correctly", "[TetrisModel]") {
    // Create TetrisModel object with appropriate board size
    TetrisModel tetrisModel(10, 20); // Assuming board size is 10 rows and 20 columns

    // Call the spawnPiece() method
    tetrisModel.spawnPiece();

    // Get the current piece from the model
    auto currentPiece = tetrisModel.getState().getCurrentPiece();

    // Get the current position of the piece
    int spawnRow = 0;
    int spawnCol = tetrisModel.getBoard().getCols() / 2;

    // Check if the piece is placed at the correct position on the board
    // Assuming the piece has a shape defined, you can check if the shape's position matches the spawn position
    const std::vector<Position> &shape = currentPiece->getShape();
    bool piecePlacedCorrectly = false;
    for (const auto &pos: shape) {
        int pieceRow = spawnRow + pos.getX();
        int pieceCol = spawnCol + pos.getY();
        if (tetrisModel.getBoard().getPieceAt(pieceRow, pieceCol) == currentPiece) {
            piecePlacedCorrectly = true;
            break;
        }
    }

    // Assert that the piece is placed correctly on the board
    REQUIRE(piecePlacedCorrectly);
}

TEST_CASE("Move piece down when no collision occurs") {
    TetrisModel model(10, 20); // Crée un modèle avec une taille de plateau de 10x20
    model.spawnPiece(); // Place une pièce sur le plateau
    auto initialPiece = model.getState().getCurrentPiece();
    auto position = initialPiece->getAbsolutePositions();
    model.movePieceDown(); // Effectue un mouvement vers le bas
    auto position2 = initialPiece->getAbsolutePositions();
    REQUIRE(position != position2);
}

TEST_CASE("Move piece Left when no collision occurs") {
    TetrisModel model(10, 20); // Crée un modèle avec une taille de plateau de 10x20
    model.spawnPiece(); // Place une pièce sur le plateau
    auto initialPiece = model.getState().getCurrentPiece();
    auto position = initialPiece->getAbsolutePositions();
    model.movePieceLeft(); // Effectue un mouvement vers le bas
    auto position2 = initialPiece->getAbsolutePositions();
    REQUIRE(position != position2);
}
TEST_CASE("Move piece Right when no collision occurs") {
    TetrisModel model(10, 20); // Crée un modèle avec une taille de plateau de 10x20
    model.spawnPiece(); // Place une pièce sur le plateau
    auto initialPiece = model.getState().getCurrentPiece();
    auto position = initialPiece->getAbsolutePositions();
    model.movePieceRight(); // Effectue un mouvement vers le bas
    auto position2 = initialPiece->getAbsolutePositions();
    REQUIRE(position != position2);
}

TEST_CASE("dropPiece - no collision") {
    TetrisModel model(10, 20); // Crée un modèle avec une taille de plateau de 10x20
    model.spawnPiece(); // Place une pièce sur le plateau
    auto initialPiece = model.getState().getCurrentPiece();
    model.dropPiece(); // Effectue un mouvement vers le bas
    REQUIRE(initialPiece->getRow() == 9);
    REQUIRE(initialPiece->getCol() == 10);
}

TEST_CASE("Move piece down when collision occurs") {
    TetrisModel model(10, 20);
    model.fillBoardWithRandomPieces(); // prefilled board with random pieces
    model.dropPiece();
    REQUIRE_FALSE(model.getState().getCurrentPiece()->getRow() == 9);

}

TEST_CASE("Rotate"){
    TetrisModel model(10,20);
    model.spawnPiece();
    auto initialPiece = model.getState().getCurrentPiece();
    auto position = initialPiece->getAbsolutePositions();
    initialPiece->moveDown();
    initialPiece->rotateClockwise();
    auto position2 = initialPiece->getAbsolutePositions();
    REQUIRE(position!=position2);
}


TEST_CASE("Check fillBoardWithRandomPieces - no line completed") {
    TetrisModel tetrisModel(20, 10);
    tetrisModel.fillBoardWithRandomPieces(); // Fill the board with random pieces

    const GameBoard &board = tetrisModel.getBoard();
    bool lineComplete = true;
    // Check each row to see if any line is completely filled
    for (int row = 0; row < board.getRows(); ++row) {
        for (int col = 0; col < board.getCols(); ++col) {
            if (board.getPieceAt(row, col) == nullptr) {
                lineComplete = false;
                break;
            }
        }

    }
    REQUIRE_FALSE(lineComplete);
}
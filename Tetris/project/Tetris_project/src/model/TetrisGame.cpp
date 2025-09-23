#include "TetrisGame.h"

TetrisGame::TetrisGame(int boardRow, int boardCol) : model(boardRow, boardCol) {}

void TetrisGame::start() {
    model.registerObserver(&view);
    model.startGame();
}

void TetrisGame::fillBoardWithRandomPieces() {
    model.fillBoardWithRandomPieces();
}

void TetrisGame::end() {
//potential function for stop the game, saving score,...
}

bool TetrisGame::isGameOver() {
    return model.isGameOver();
}

void TetrisGame::movePieceDown() {
    model.movePieceDown();
    updateGame();
}

void TetrisGame::movePieceLeft() {
    model.movePieceLeft();
    updateGame();
}

void TetrisGame::movePieceRight() {
    model.movePieceRight();
    updateGame();
}

void TetrisGame::dropPiece() {
    model.dropPiece();
    updateGame();
}

void TetrisGame::rotatePiece(char dir) {
    model.rotatePiece(dir);
    updateGame();
}

void TetrisGame::updateGame() {
    model.updateGame();
    model.notifyObservers();
}

GameBoard& TetrisGame::getGameBoard() {
    return model.getBoard();
}

ConsoleView& TetrisGame::getView() {
    return view;
}

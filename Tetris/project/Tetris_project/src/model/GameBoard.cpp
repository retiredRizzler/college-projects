#include "GameBoard.h"
#include <algorithm>
#include <iostream>

GameBoard::GameBoard(int rows, int cols) : rows(rows), cols(cols) {
    board.resize(rows, std::vector<std::shared_ptr<Piece>>(cols, nullptr));
}

int GameBoard::getRows() const {
    return rows;
}

int GameBoard::getCols() const {
    return cols;
}

void GameBoard::setBoard(int row, int col) {
    this->rows = row;
    this->cols = col;
    board.resize(rows, std::vector<std::shared_ptr<Piece>>(cols, nullptr));
    // Resize each row to ensure correct number of columns
    for (int i = 0; i < rows; ++i) {
        board[i].resize(cols, nullptr);
    }
}

std::shared_ptr<Piece> GameBoard::getPieceAt(int row, int col) const {
    if (isInsideBoard(row, col)) {
        return board[row][col];
    } else {
        return nullptr;
    }
}

void GameBoard::setPieceAt(int row, int col, const std::shared_ptr<Piece>& piece) {
    // We skip this check because we need first to fit the piece within the board while spawning
    //See spawnPiece in TetrisModel
    if (!isInsideBoard(row, col)) {
       std::cerr <<  "piece is not inside board";
    }
    if (piece == nullptr) {
        throw std::runtime_error("Tried to set a nullptr piece with setPieceAt function");
    }
    piece->setPosition(row, col);
    board[row][col] = piece;
}

bool GameBoard::isInsideBoard(int row, int col) const {
    return (row >= 0 && row < rows) && (col >= 0 && col < cols);
}

std::vector<Position> GameBoard::getOccupiedPositions() const {
    std::vector<Position> occupiedPositions;

    for (int row = 0; row < rows; ++row) {
        for (int col = 0; col < cols; ++col) {
            std::shared_ptr<Piece> piece = board[row][col];
            if (piece != nullptr) {
                for (const auto& position : piece->getAbsolutePositions()) {
                    occupiedPositions.push_back(position);
                }
            }
        }
    }

    return occupiedPositions;
}

std::vector<int> GameBoard::findCompletedLines() const {
    std::vector<int> completedRows;
    for (int row = 0; row < rows; ++row) {
        bool isCompleted = true;
        for (int col = 0; col < cols; ++col) {
            // if we find nullptr on a row, it means line isn't complete, no need to keep searching
            if (board[row][col] == nullptr) {
                isCompleted = false;
                break;
            }
        }
        if (isCompleted) {
            completedRows.push_back(row);
        }
    }
    return completedRows;
}

int GameBoard::clearCompletedLines() {
    std::vector<int> completedRows = findCompletedLines();
    int linesCleared = completedRows.size(); // Number of completed lines
    if(completedRows.empty()) {
        return 0;
    }
    // Delete pieces in the completed lines
    for (int row : completedRows) {
        for (int col = 0; col < cols; ++col) {
            board[row][col].reset(); // Reset shared_ptr, releasing ownership
        }
    }
    // Shift down the remaining pieces
    for (int row : completedRows) {
        // Shift down the pieces above the completed line
        for (int i = row - 1; i >= 0; --i) {
            for (int col = 0; col < cols; ++col) {
                if (board[i][col] != nullptr) {
                board[i + linesCleared][col] = std::move(board[i][col]);// Move shared_ptr to new position
                }
            }
        }
    }
    return linesCleared;
}

std::shared_ptr<Piece>& GameBoard::at(int row, int col) {
    if (row < 0 || row >= rows || col < 0 || col >= cols) {
        throw std::out_of_range("Invalid row or column index");
    }
    return board[row][col];
}

const std::shared_ptr<Piece>& GameBoard::at(int row, int col) const {
    if (row < 0 || row >= rows || col < 0 || col >= cols) {
        throw std::out_of_range("Invalid row or column index");
    }
    return board[row][col];
}

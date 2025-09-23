#ifndef GAMEBOARD_H
#define GAMEBOARD_H

#include <vector>
#include <memory>
#include "pieces/Piece.h"

/**
 * @class GameBoard
 * @brief Represents the game board for Tetris.
 *
 * The GameBoard class represents the game board grid where Tetris pieces are placed and manipulated.
 */
class GameBoard {
private:
    int rows; /**< Number of rows in the game board. */
    int cols; /**< Number of columns in the game board. */
    std::vector<std::vector<std::shared_ptr<Piece>>> board; /**< 2D vector representing the game board. */

public:
    /**
     * @brief Constructor for creating a GameBoard object.
     * @param rows Number of rows in the game board.
     * @param cols Number of columns in the game board.
     */
    GameBoard(int rows, int cols);

    /**
     * @brief Gets the number of rows in the game board.
     * @return Number of rows.
     */
    int getRows() const;

    /**
     * @brief Gets the number of columns in the game board.
     * @return Number of columns.
     */
    int getCols() const;

    /**
     * @brief Sets the size of the game board.
     * @param row Number of rows.
     * @param col Number of columns.
     */
    void setBoard(int row, int col);

    /**
     * @brief Gets the piece at the specified position on the game board.
     * @param row Row index.
     * @param col Column index.
     * @return A shared pointer to the piece at the specified position, or nullptr if the position is empty.
     */
    std::shared_ptr<Piece> getPieceAt(int row, int col) const;

    /**
     * @brief Sets the piece at the specified position on the game board.
     * @param row Row index.
     * @param col Column index.
     * @param piece The piece to be placed at the specified position.
     */
    void setPieceAt(int row, int col, const std::shared_ptr<Piece>& piece);

    /**
     * @brief Checks if a given position is inside the game board boundaries.
     * @param row Row index.
     * @param col Column index.
     * @return True if the position is inside the game board, false otherwise.
     */
    bool isInsideBoard(int row, int col) const;

    /**
     * @brief Gets a vector containing positions occupied by pieces on the game board.
     * @return A vector containing occupied positions.
     */
    std::vector<Position> getOccupiedPositions() const;

    /**
     * @brief Checks if placing a piece at the specified position would result in a collision with existing pieces on the board.
     * @param piece The piece to be placed.
     * @param row Row index.
     * @param col Column index.
     * @return True if the piece would collide with existing pieces, false otherwise.
     */
    bool isColliding(const std::shared_ptr<Piece>& piece, int row, int col) const;

    /**
     * @brief Finds completed lines on the game board.
     * @return A vector containing indices of completed lines.
     */
    std::vector<int> findCompletedLines() const;

    /**
     * @brief Clears completed lines from the game board.
     * @return The number of lines cleared.
     */
    int clearCompletedLines();

    std::shared_ptr<Piece>& at(int row, int col);
    const std::shared_ptr<Piece>& at(int row, int col) const;

};

#endif // GAMEBOARD_H

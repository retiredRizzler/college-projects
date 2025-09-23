#ifndef TETRISMODEL_H
#define TETRISMODEL_H

#include "GameBoard.h"
#include "PieceBag.h"
#include "GameState.h"
#include "Observable.h"
#include <iostream>
#include <chrono>

/**
 * @class TetrisModel
 * @brief Represents the model of the Tetris game.
 *
 * The TetrisModel class manages the game state and logic of the Tetris game,
 * including the game board, pieces, and game state.
 */
class TetrisModel : public Observable {
public:
    /**
     * @brief Constructor for creating a TetrisModel object with a specified board size.
     * @param boardRow The number of rows in the game board.
     * @param boardCol The number of columns in the game board.
     */
    TetrisModel(int boardRow, int boardCol);

    /**
     * @brief Starts the Tetris game.
     */
    void startGame();

    /**
     * Prefills the board with random pieces
     */
    void fillBoardWithRandomPieces();

    /**
    * @brief Spawns the current piece onto the game board, ensuring it is entirely inside the board.
    */
    void spawnPiece();

    /**
     * @brief Moves the current piece right.
     */
    void movePieceRight();

    /**
     * @brief Moves the current piece left.
     */
    void movePieceLeft();

    /**
     * @brief Moves the current piece down.
     */
    void movePieceDown();

    /**
     * @brief Drops the current piece to the bottom of the game board.
     */
    void dropPiece();

    /**
     * @brief Rotates the current piece in the specified direction.
     * @param dir The direction to rotate the piece ('L' for counterclockwise, 'R' for clockwise).
     */
    void rotatePiece(char dir);

    /**
     * @brief Updates the game state.
     */
    void updateGame();

    /**
     * @brief Checks if the game is over.
     * @return True if the game is over, false otherwise.
     */
    bool isGameOver();

    /**
     * @brief Gets a reference to the game state.
     * @return A reference to the game state.
     */
    GameState& getState();

    /**
     * @brief Gets a reference to the game board.
     * @return A reference to the game board.
     */
    GameBoard& getBoard();

    /**
     * @brief Gets a reference to the piece bag.
     * @return A reference to the piece bag.
     */
    PieceBag& getBag();

private:
    GameBoard board; /**< The game board. */
    PieceBag bag; /**< The piece bag. */
    GameState state; /**< The game state. */

    int totalCompletedLine; /**< The total number of completed lines. */
    int dropScore; /**< The score obtained from dropping a piece. */
    void deleteCurrentPieceFromBoard();
    void setCurrentPieceOnBoard();
    /**
    * @brief Checks if the current piece is entirely inside the game board.
    *
    * @return true if the current piece is entirely inside the board, false otherwise.
    */
    bool pieceCompletelyInsideBoard();
    /**
     * @brief Checks if the current piece can move down.
     * @return True if the piece can move down, false otherwise.
     */
    bool canMoveDown();

    /**
     * @brief Checks if placing a piece at the specified position would result in a collision with existing pieces on the board.
     * @param piece The piece to be placed.
     * @param row Row index.
     * @param col Column index.
     * @return True if the piece would collide with existing pieces, false otherwise.
     */
    bool isColliding(const std::shared_ptr<Piece>& piece, int row, int col) const;

    /**
     * @brief Updates the score based on the number of completed lines.
     * @param completedLine The number of completed lines.
     */
    void updateScore(int completedLine);

    const int MAX_SCORE = 100000; /**< The maximum score allowed. */
    const int MAX_COMPLETED_LINES = 100; /**< The maximum number of completed lines allowed. */
    const int MAX_TIME_SECONDS = 1800; /**< The maximum time limit for the game (in seconds). */
    std::chrono::steady_clock::time_point startTime; /**< The start time of the game. */
};

#endif // TETRISMODEL_H

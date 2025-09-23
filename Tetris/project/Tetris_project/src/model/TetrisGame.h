#ifndef TETRISGAME_H
#define TETRISGAME_H

#include "TetrisModel.h" // Include necessary headers
#include "../view/ConsoleView.hpp"

/**
 * @class TetrisGame
 * @brief Represents a game of Tetris.
 *
 * The TetrisGame class manages the game logic and controls the interaction
 * between the Tetris model and the console view.
 */
class TetrisGame {
private:
    TetrisModel model; /**< The Tetris model containing the game state and logic. */
    ConsoleView view; /**< The console view for displaying the game. */

public:
    /**
     * @brief Constructor for creating a TetrisGame object with a specified board size.
     * @param boardRow The number of rows in the game board.
     * @param boardCol The number of columns in the game board.
     */
    TetrisGame(int boardRow, int boardCol);

    /**
     * @brief Starts the Tetris game.
     */
    void start();

    /**
     * @brief Fill the board with random pieces.
     */
    void fillBoardWithRandomPieces();

    /**
     * @brief Ends the Tetris game.
     */
    void end();

    /**
     * @brief Checks if the game is over.
     * @return True if the game is over, false otherwise.
     */
    bool isGameOver();

    /**
     * @brief Moves the current piece down.
     */
    void movePieceDown();

    /**
     * @brief Moves the current piece left.
     */
    void movePieceLeft();

    /**
     * @brief Moves the current piece right.
     */
    void movePieceRight();

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
     * @brief Gets a reference to the game board.
     * @return A reference to the game board.
     */
    GameBoard& getGameBoard();

    /**
     * @brief Gets a reference to the game view
     * @return A reference to the game view
     */
    ConsoleView& getView();
};

#endif // TETRISGAME_H

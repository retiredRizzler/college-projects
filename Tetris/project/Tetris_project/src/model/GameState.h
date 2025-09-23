#ifndef GAMESTATE_H
#define GAMESTATE_H

#include "pieces/Piece.h"
#include "PieceBag.h"

/**
 * @class GameState
 * @brief Represents the state of the Tetris game.
 *
 * The GameState class encapsulates the current state of the Tetris game,
 * including the player's score, current and next pieces, and current level.
 */
class GameState {
private:
    int score; /**< The player's score. */
    std::shared_ptr<Piece> currentPiece; /**< The current piece being controlled by the player. */
    int currentLevel; /**< The current level of the game. */
    std::shared_ptr<Piece> nextPiece; /**< The next piece that will appear on the game board. */

public:
    /**
     * @brief Constructor for creating a GameState object.
     * @param score The initial score of the game.
     * @param currentPiece The initial current piece.
     * @param currentLevel The initial current level.
     */
    GameState(int score, std::shared_ptr<Piece> currentPiece, int currentLevel);

    /**
     * @brief Gets the player's score.
     * @return The player's score.
     */
    int getScore() const;

    /**
     * @brief Gets the current piece being controlled by the player.
     * @return A reference to the current piece.
     */
    const std::shared_ptr<Piece>& getCurrentPiece() const;

    /**
     * @brief Gets the current level of the game.
     * @return The current level.
     */
    int getCurrentLevel() const;

    /**
     * @brief Gets the next piece that will appear on the game board.
     * @return A reference to the next piece.
     */
    const std::shared_ptr<Piece>& getNextPiece() const;

    /**
     * @brief Increments the player's score by the specified amount.
     * @param score The amount to increment the score by.
     */
    void incrementScore(int score);

    /**
     * @brief Updates the current piece with a new piece from the piece bag.
     * @param pieceBag The piece bag containing available pieces.
     */
    void updateCurrentPiece(PieceBag& pieceBag);

    /**
     * @brief Increments the current level of the game.
     */
    void incrementCurrentLevel();
};

#endif // GAMESTATE_H

#ifndef PIECEBAG_H
#define PIECEBAG_H

#include <vector>
#include <memory>
#include "pieces/Piece.h"

/**
 * @class PieceBag
 * @brief Represents a bag of Tetris pieces.
 *
 * The PieceBag class represents a bag containing Tetris pieces that are drawn randomly
 * for the player to use during the game.
 */
class PieceBag {
public:
    /**
     * @brief Constructor for creating a PieceBag object with a specified size.
     * @param size The size of the piece bag.
     */
    PieceBag(size_t size);

    /**
     * @brief Gets the contents of the piece bag.
     * @return A constant reference to the vector containing the pieces in the bag.
     */
    const std::vector<std::shared_ptr<Piece>>& getBag() const;

    /**
     * @brief Refills the piece bag with new pieces.
     *
     * After calling this method, the piece bag will contain a new set of pieces
     * to be drawn from.
     */
    void refill();

    /**
     * @brief Gets the next piece from the piece bag.
     * @return A shared pointer to the next piece in the bag.
     */
    std::shared_ptr<Piece> getNextPiece();

private:
    std::vector<std::shared_ptr<Piece>> bag; /**< The vector containing the pieces in the bag. */
    size_t bagSize; /**< The size of the piece bag. */

    /**
     * @brief Adds a random piece to the piece bag.
     * We used Gemini (google AI) to improve the "randomness"
     * This method randomly selects a Tetris piece and adds it to the piece bag.
     */
    void addRandomPiece();
};

#endif // PIECEBAG_H

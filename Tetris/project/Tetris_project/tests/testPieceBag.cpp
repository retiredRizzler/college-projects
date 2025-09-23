#include <catch2/catch_test_macros.hpp>
#include "model/PieceBag.h"

TEST_CASE("PieceBag: Constructor and getBag") {
    PieceBag bag(5);

    REQUIRE(bag.getBag().size() == 5);

    const std::vector<std::shared_ptr<Piece>>& bagContents = bag.getBag();
}

TEST_CASE("PieceBag: refill") {
    PieceBag bag(3);

    bag.refill();
    REQUIRE(bag.getBag().size() == 3);

    // Check if at least one piece is in the bag (doesn't guarantee all slots are filled)
    REQUIRE(bag.getBag().empty() == false);
}


TEST_CASE("PieceBag: getNextPiece") {
    PieceBag bag(2);

    // Get the first piece (should refill if empty)
    std::shared_ptr<Piece> firstPiece = bag.getNextPiece();
    REQUIRE(firstPiece != nullptr);


    std::shared_ptr<Piece> secondPiece = bag.getNextPiece();
    REQUIRE(secondPiece != nullptr);

    // Check if the pieces are different (doesn't guarantee unique pieces, but increases the chance)
    REQUIRE(firstPiece != secondPiece);
}



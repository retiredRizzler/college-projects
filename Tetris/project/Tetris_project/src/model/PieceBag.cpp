#include "PieceBag.h"
#include <memory>
#include <vector>
#include <random>
#include "pieces/Piece.h"
#include "pieces/IPiece.h"
#include "pieces/ZPiece.h"
#include "pieces/JPiece.h"
#include "pieces/LPiece.h"
#include "pieces/SquarePiece.h"
#include "pieces/SPiece.h"
#include "pieces/TPiece.h"

PieceBag::PieceBag(size_t size) : bagSize(size) {
    refill();
}

const std::vector<std::shared_ptr<Piece>>& PieceBag::getBag() const {
    return bag;
}

void PieceBag::refill() {
    bag.clear();
    for (size_t i = 0; i < bagSize; ++i) {
        addRandomPiece();
    }
}

std::shared_ptr<Piece> PieceBag::getNextPiece() {
    if (bag.empty()) {
        refill();
    }
    std::shared_ptr<Piece> nextPiece = bag.front();
    bag.erase(bag.begin()); // Remove the used piece from the front
    return nextPiece;
}

void PieceBag::addRandomPiece() {
    // Create a random device to seed the generator
    std::random_device rd;

    // Initialize a Mersenne Twister engine with the random seed
    static std::mt19937 generator(rd());

    // Generate a random number between 0 and 6 (inclusive)
    std::uniform_int_distribution<int> distribution(0, 6);
    int randomPiece = distribution(generator);

    switch (randomPiece) {
        case 0:
            bag.push_back(std::make_shared<IPiece>());
            break;
        case 1:
            bag.push_back(std::make_shared<SquarePiece>());
            break;
        case 2:
            bag.push_back(std::make_shared<ZPiece>());
            break;
        case 3:
            bag.push_back(std::make_shared<JPiece>());
            break;
        case 4:
            bag.push_back(std::make_shared<SPiece>());
            break;
        case 5:
            bag.push_back(std::make_shared<LPiece>());
            break;
        case 6:
            bag.push_back(std::make_shared<TPiece>());
            break;
    }
}

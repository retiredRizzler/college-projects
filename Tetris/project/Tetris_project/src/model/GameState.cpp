#include "GameState.h"

GameState::GameState(int score, std::shared_ptr<Piece> currentPiece, int currentLevel)
    : score(score), currentPiece(std::move(currentPiece)), currentLevel(currentLevel) {
}

int GameState::getScore() const {
    return score;
}

const std::shared_ptr<Piece>& GameState::getCurrentPiece() const {
    return currentPiece;
}

int GameState::getCurrentLevel() const {
    return currentLevel;
}

const std::shared_ptr<Piece>& GameState::getNextPiece() const {
    return nextPiece;
}

void GameState::updateCurrentPiece(PieceBag& pieceBag) {
    currentPiece = std::move(pieceBag.getNextPiece());
    nextPiece = pieceBag.getNextPiece(); // Update next piece
}

void GameState::incrementScore(int score) {
    this->score = this->score + score;
}

void GameState::incrementCurrentLevel() {
    currentLevel++;
}

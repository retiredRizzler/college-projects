#ifndef CONSOLEVIEW_HPP
#define CONSOLEVIEW_HPP

#include "../model/TetrisModel.h" // Include necessary headers
#include "../model/observer.h"
#include <string>
#include <iostream>
#define BLACK "\033[30m"
#define RED "\033[31m"
#define GREEN "\033[32m"
#define YELLOW "\033[33m"
#define BLUE "\033[34m"
#define MAGENTA "\033[35m"
#define CYAN "\033[36m"
#define WHITE "\033[37m"

#define BOLD "\033[1m"
#define RESET "\033[0m"
/**
 * @class ConsoleView
 * @brief Represents the console view of the Tetris game.
 *
 * The ConsoleView class is responsible for displaying the game state and board
 * in the console for the user to interact with.
 */
class ConsoleView : public Observer {
public:
    /**
     * @brief Updates the view based on changes in the observable object.
     * @param observable Pointer to the observable object.
     */
    void update(Observable* observable) override {
        auto model = dynamic_cast<TetrisModel*>(observable);
        if (model) {
            displayBoard(model);
            displayLevel(model);
            displayScore(model);
        } else {
            std::cerr << "Error: Invalid observable type!" << std::endl;
        }
    }

    /**
     * @brief Displays the welcome message when the game starts.
     */
    void displayWelcome() {
        std::cout << std::endl;
        std::cout << "\033[1;1H";

        for (int i = 0; i < 40; ++i) {
            std::cout << "\033[35m=";
        }
        std::cout << std::endl;

        std::cout << "\033[1m";
        std::cout << "  \033[32m Welcome\033[31m to \033[34mthe \033[36mTetris \033[33mgame !";
        std::cout << "\033[0m" << std::endl;

        for (int i = 0; i < 40; ++i) {
            std::cout << "\033[35m=";
        }
        std::cout << std::endl << std::endl;

        std::cout << "\033[34mType any key command to continue..." << std::endl;
        std::cin.get();
    }

    /**
 * @brief Displays the end message when the game is over.
 */
    void displayEnd() {
        std::cout << std::endl;
        std::cout << BOLD;

        for (int i = 0; i < 40; ++i) { // Assuming BOARD_COLS is defined in TetrisModel.h
            std::cout << MAGENTA << "=";
        }
        std::cout << std::endl;

        std::cout << BOLD << GREEN << "     End of the game, See you soon!" << RESET << std::endl;

        for (int i = 0; i < 40; ++i) { // Assuming BOARD_COLS is defined in TetrisModel.h
            std::cout << MAGENTA << "=";
        }
        std::cout << std::endl << std::endl;
    }

    void displayCommands() {
        std::cout << "\n\033[1mTetris Commands:\033[0m\n";
        std::cout << "r - Rotate the piece clockwise.\n";
        std::cout << "a - Rotate the piece counter-clockwise.\n";
        std::cout << "q - Move the piece left.\n";
        std::cout << "s - Move the piece down.\n";
        std::cout << "d - Move the piece right.\n";
        std::cout << "x - Drop the piece to the bottom of the board.\n";
        std::cout << "Enter your command: " << std::endl;
    }
    /**
     * @brief Displays a message
     * @param message message to display on the screen
     */
    void displayMessage(const std::string& message) {
        std::cout << RED <<  message << std::endl;
    }

    /**
     * @brief Displays the game board.
     * @param model Pointer to the TetrisModel object.
     */
    void displayBoard(TetrisModel* model) {
        std::cout << std::endl;
        const GameBoard& board = model->getBoard();
        for (int i = 0; i < board.getCols() + 2; ++i) {
            std::cout << BOLD <<  "#" << RESET;
        }
        std::cout << std::endl;
        for (int row = 0; row < board.getRows(); ++row) {
            std::cout << BOLD << "#" << RESET;
            for (int col = 0; col < board.getCols(); ++col) {
                std::shared_ptr<Piece> piece = board.getPieceAt(row, col);
                if (piece != nullptr) {
                    std::cout << "X"; // For now, print "X" to represent a piece
                } else {
                    std::cout << " ";
                }
            }
            std::cout << BOLD << "#" << RESET << std::endl;
        }
        for (int i = 0; i < board.getCols() + 2; ++i) {
            std::cout << BOLD << "#" << RESET;
        }
        std::cout << std::endl;
    }

/**
 * @brief Displays the next piece to be played.
 * @param model Pointer to the TetrisModel object.
 */
    void displayNextPiece(TetrisModel* model) {
        std::cout << "\nNext Piece:\n";

        // Get the next piece from the model
        std::shared_ptr<Piece> nextPiece = model->getState().getNextPiece();

        // Display the next piece
        if (nextPiece != nullptr) {
            const std::vector<Position>& shape = nextPiece->getShape();

            // Find dimensions of the piece
            int minX = INT_MAX, maxX = INT_MIN, minY = INT_MAX, maxY = INT_MIN;
            for (const auto& pos : shape) {
                minX = std::min(minX, pos.getX());
                maxX = std::max(maxX, pos.getX());
                minY = std::min(minY, pos.getY());
                maxY = std::max(maxY, pos.getY());
            }

            // Display the piece
            for (int y = minY; y <= maxY; ++y) {
                for (int x = minX; x <= maxX; ++x) {
                    bool occupied = false;
                    for (const auto& pos : shape) {
                        if (pos.getX() == x && pos.getY() == y) {
                            occupied = true;
                            break;
                        }
                    }
                    if (occupied) {
                        std::cout << "X"; // Occupied cell
                    } else {
                        std::cout << " "; // Empty cell
                    }
                }
                std::cout << std::endl;
            }
        } else {
            std::cout << "No next piece available." << std::endl;
        }

    }

    /**
     * @brief Displays the current level of the game.
     * @param model Pointer to the TetrisModel object.
     */
    void displayLevel(TetrisModel* model) {
        const GameState& state = model->getState();
        int level = state.getCurrentLevel();
        std::string difficulty = "Easy";
        if (level > 5) difficulty = "Medium";
        if (level > 10) difficulty = "Hard";

        std::cout << "\033[1m" << YELLOW << "Level: " << RESET << level << " (" << difficulty << ")" << std::endl;
    }

    /**
     * @brief Displays the current score of the game.
     * @param model Pointer to the TetrisModel object.
     */
    void displayScore(TetrisModel* model) {
        const GameState& state = model->getState();
        std::cout << "\033[1m" << GREEN << "Current Score: " << state.getScore() << RESET << std::endl;
    }



};

#endif // CONSOLEVIEW_HPP

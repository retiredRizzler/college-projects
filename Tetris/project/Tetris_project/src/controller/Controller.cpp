#include "Controller.h"

Controller::Controller() : game(10, 20) {}

void Controller::run() {
    int rows, cols;
    char choice;
    game.getView().displayWelcome();
    game.getView().displayMessage("Do you want to specify a board size? (y/n): ");
    std::cin >> choice;
    if (choice == 'y' || choice == 'Y') {
        askBoardSize(rows, cols);
        game.getGameBoard().setBoard(rows, cols);
    }

    char fillChoice;
    game.getView().displayMessage("Do you want to prefilled the board with random pieces? (y/n): ");
    std::cin >> fillChoice;
    if (fillChoice == 'y' || fillChoice == 'Y') {
        game.fillBoardWithRandomPieces();
    }

    game.start();
    game.getView().displayCommands();
    game.updateGame();
    while (!game.isGameOver()) {
        handleInput();
    }
    game.getView().displayEnd();
}

void Controller::askBoardSize(int& rows, int& cols) {
    //source chat.openai.com for robustness
    while (true) {
        game.getView().displayMessage("Enter the number of rows for the game board (between 5 and 30 included): ");
        if (!(std::cin >> rows)) {
            std::cerr << "Invalid input. Please enter an integer for the number of rows." << std::endl;
            std::cin.clear(); // Clear error flags
            std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n'); // Discard invalid input
            continue;
        }

        game.getView().displayMessage("Enter the number of columns for the game board (between 10 and 100): ");
        if (!(std::cin >> cols)) {
            std::cerr << "Invalid input. Please enter an integer for the number of columns." << std::endl;
            std::cin.clear(); // Clear error flags
            std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n'); // Discard invalid input
            continue;
        }

        // Check if rows and cols are within valid range (e.g., greater than 0)
        if (rows < 5 || cols < 10 || rows > 30 || cols > 100) {
            std::cerr << "Invalid input. Number of rows and columns must be included between 5 and 30. "
                         "And columns must be included between 10 and 100." << std::endl;
            continue;
        }

        // Input is valid, break out of the loop
        break;
    }
}

void Controller::handleInput() {
    // Handle user input and update game accordingly
    char userInput;
    std::cin >> userInput;

    switch (userInput) {
    case 'r':
        game.rotatePiece('r');
        break;
    case 'a':
        game.rotatePiece('l');
        break;
    case 'q':
        game.movePieceLeft();
        break;
    case 's':
        game.movePieceDown();
        break;
    case 'd':
        game.movePieceRight();
        break;
    case 'x':
        game.dropPiece();
        break;
    default:
        game.getView().displayMessage("Unknown command");
        game.getView().displayCommands();
    }
}


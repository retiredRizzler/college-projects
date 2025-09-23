#ifndef CONTROLLER_H
#define CONTROLLER_H
#include "../model/TetrisGame.h"

class Controller {

private:
    TetrisGame game;

    void askBoardSize(int& rows, int& cols);
    void handleInput();

public:
    Controller();
    void run();
};


#endif // CONTROLLER_H

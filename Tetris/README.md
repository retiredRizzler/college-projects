# Tetris Game Project

![C++](https://img.shields.io/badge/C++-00599C?style=for-the-badge&logo=c%2B%2B&logoColor=white)

## Introduction
This Tetris game project is being developed as part of the DEV4 course by Umut and Rayan from the D112 group. The project aims to implement a classic Tetris game using C++.

## Developers
- Rayan (Matricule: 58923)
- Umut (Matricule: 58620)

## Description
Tetris is a classic puzzle game where the player arranges falling tetrominoes (shapes made up of four square blocks) to form complete rows. When a row is complete, it is cleared from the board, and the player earns points. The game ends when the stack of tetrominoes reaches the top of the playing area.

## Development Status
The project is now playable and works in console mode. 

## Known Bugs
- **Ghost Piece Block**: Occasionally, when a new piece spawns, a ghost piece block may also appear. This block is a visual glitch and can be resolved by moving the piece to the right.
- **Rotation Limit for I-shaped Piece**: The I-shaped piece cannot be rotated endlessly to the left or right. After rotating the piece twice in one direction, it needs to be rotated in the opposite direction.

## Potential Improvements
We have identified a couple of potential enhancements for the game:
- **Fix Ghost Piece Block**: Find a way to fix the ghost piece bug so the user experience is increased.
- **Coloring Tetrominoes**: Consider adding colors to different types of tetrominoes to enhance visual appeal. This feature will be reserved for the graphical interface part.
- **Display Next Piece**: Implement displaying the next piece alongside the game board to provide players with additional information. This enhancement will be part of the graphical interface.

## How to Play
To play the game:
1. Run the `main` method located in the `TetrisApp` class to start the game.
2. A welcome message will appear prompting you to press any key to continue.
3. Choose whether you want to change the board size by pressing 'y' for yes or 'n' for no.
4. If you chose to modify the board size, follow the prompts to select the desired size.
5. Decide whether to start with an prefilled board by pressing 'y' for yes or 'n' for no.
6. Once setup is complete, the game will begin, and the console will display the commands needed to play.

## Development Environment
- Language: C++
- IDE: QtCreator, CLion
- Compiler: GCC 13.2
# Qwirkle Game - Java Console Implementation

![Java](https://img.shields.io/badge/Java-16-orange?style=for-the-badge&logo=java&logoColor=white)
![Console](https://img.shields.io/badge/Interface-Console-black?style=for-the-badge&logo=terminal&logoColor=white)
![MVC](https://img.shields.io/badge/Pattern-MVC-blue?style=for-the-badge&logo=architecture&logoColor=white)
![Singleton](https://img.shields.io/badge/Pattern-Singleton-green?style=for-the-badge&logo=design&logoColor=white)

## Project Description

A console-based Java implementation of the popular board game **Qwirkle**. This text-based version recreates the strategic tile-laying game where players create lines of tiles that share common attributes (color or shape) to score points. Built as a learning project to demonstrate object-oriented programming principles and design patterns.

## Game Rules

Qwirkle is a strategy game where players:
- Place tiles to create lines of matching colors or shapes
- Score points based on the length of lines created
- Complete a "Qwirkle" (line of 6 tiles) for bonus points
- Win by having the most points when all tiles are played

## Features

- **Console-based interface** - Text-based gameplay with clear visual board representation
- **Multiplayer support** - Play with 2 to 4 players in turn-based mode
- **Complete game logic** - Full rule validation and scoring system
- **Save/Load functionality** - Save your game progress and resume later
- **Real-time score tracking** - Live score calculation and display
- **Input validation** - Robust handling of player commands and moves
- **Board visualization** - ASCII representation of the game board and tiles

## Design Patterns & Architecture

This project was developed as a **first complete game implementation** focusing on object-oriented principles:

### MVC Architecture
- **Model** - Game logic, board state, and data management
- **View** - Console output and board visualization
- **Controller** - User input handling and game flow control

### Singleton Pattern
- **Tile Bag** - Implemented using Singleton pattern to ensure single instance of the tile distribution system
- Manages the 108 game tiles and their random distribution

### Object-Oriented Principles
- Clean separation of concerns
- Encapsulation of game state and rules
- Polymorphism for different game components
- Inheritance hierarchy for game entities

## Technical Implementation

### Core Components
- **Game Engine** - Handles game logic, rules, and state management
- **Tile System** - Represents game tiles with colors and shapes
- **Board Management** - Manages the game board and tile placement
- **Player Management** - Tracks player hands, scores, and turns

### Object-Oriented Design
- Clean separation of concerns between game logic and presentation
- Modular architecture for easy maintenance and testing
- Proper encapsulation of game state and rules

## Technologies Used

- **Java 16** - Core programming language and features
- **Object-Oriented Programming** - Design patterns and principles
- **Console I/O** - Text-based user interface
- **File I/O** - Game save/load functionality
- **Design Patterns** - MVC architecture and Singleton pattern

*Note: This is an academic project developed to practice object-oriented programming principles and design patterns.*


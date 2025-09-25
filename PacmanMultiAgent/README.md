# Pacman Laboratory - Rational Agents

![Python](https://img.shields.io/badge/Python-AI_Algorithms-3776AB?style=for-the-badge&logo=python&logoColor=white)

## Overview
This laboratory consists of implementing different types of rational agents for the Pacman game. These agents use artificial intelligence algorithms to efficiently navigate the maze, avoid ghosts, and eat all the pellets.

## Implemented Agents

### ReflexAgent
An agent that evaluates each possible action based on the resulting game state. This evaluation takes into account:
- Ghost proximity (penalty for being too close)
- Food proximity (reward for being close)
- Rewards for eating food
- Special handling for scared ghosts

### MinimaxAgent
An agent that uses the Minimax algorithm to anticipate several moves ahead, assuming that ghosts seek to minimize its score. The agent:
- Explores possible actions up to a defined depth
- Assumes ghosts act optimally against it
- Chooses the action that maximizes its minimum possible score

### AlphaBetaAgent
An improvement of the Minimax agent that uses alpha-beta pruning to optimize search:
- Significantly reduces computation time
- Allows exploration of greater depths
- Maintains the same behavior as Minimax

### ExpectimaxAgent
An agent that assumes ghosts move randomly rather than optimally:
- Calculates the mathematical expectation (average) of scores for ghost actions
- Is less pessimistic than Minimax when facing non-optimal adversaries
- Takes more risks when advantageous

### Enhanced Evaluation Function
An advanced evaluation function that considers several factors:
- Distance to the nearest food
- Number of remaining pellets
- Ghost positions (normal and scared)
- Presence and proximity of power capsules
# multiAgents.py
# --------------
# Licensing Information:  You are free to use or extend these projects for
# educational purposes provided that (1) you do not distribute or publish
# solutions, (2) you retain this notice, and (3) you provide clear
# attribution to UC Berkeley, including a link to http://ai.berkeley.edu.
# 
# Attribution Information: The Pacman AI projects were developed at UC Berkeley.
# The core projects and autograders were primarily created by John DeNero
# (denero@cs.berkeley.edu) and Dan Klein (klein@cs.berkeley.edu).
# Student side autograding was added by Brad Miller, Nick Hay, and
# Pieter Abbeel (pabbeel@cs.berkeley.edu).


import random, util

from game import Agent

class ReflexAgent(Agent):
    """
      A reflex agent chooses an action at each choice point by examining
      its alternatives via a state evaluation function.

      The code below is provided as a guide.  You are welcome to change
      it in any way you see fit, so long as you don't touch our method
      headers.
    """

    def get_action(self, game_state):
        """
        You do not need to change this method, but you're welcome to.

        getAction chooses among the best options according to the evaluation function.

        Just like in the previous project, getAction takes a GameState and returns
        some Directions.X for some X in the set {North, South, West, East, Stop}
        """
        # Collect legal moves and successor states  
        legal_moves = game_state.getLegalActions()

        # Choose one of the best actions
        scores = [self.evaluation_function(game_state, action) for action in legal_moves]
        best_score = max(scores)
        best_indices = [index for index in range(len(scores)) if scores[index] == best_score]
        chosen_index = random.choice(best_indices) # Pick randomly among the best

        "Add more of your code here if you want to"

        return legal_moves[chosen_index]

    def evaluation_function(self, current_game_state, action):
        """
        Design a better evaluation function here.

        The evaluation function takes in the current and proposed successor
        GameStates (pacman.py) and returns a number, where higher numbers are better.

        The code below extracts some useful information from the state, like the
        remaining food (new_food) and Pacman position after moving (new_pos).
        new_scared_times holds the number of moves that each ghost will remain
        scared because of Pacman having eaten a power pellet.

        Print out these variables to see what you're getting, then combine them
        to create a masterful evaluation function.
        """
        # Useful information you can extract from a GameState (pacman.py)
        successor_game_state = current_game_state.generatePacmanSuccessor(action)
        new_pos = successor_game_state.getPacmanPosition()
        new_food = successor_game_state.getFood()
        new_ghost_states = successor_game_state.getGhostStates()
        score = successor_game_state.getScore()
        
        
        for ghost_state in new_ghost_states:
          ghost_pos = ghost_state.getPosition()
          distance = util.manhattan_distance(ghost_pos, new_pos)
          
          if ghost_state.scaredTimer > 0:
            score += 100 / distance # plus un ghost scared est proche plus on récompense
          
          else:
            if distance < 2:
              score -= 300 # si ghost pas scared est trop proche, on punit fooort 
            elif distance < 4:
              score -= 100
              
        food_list = new_food.asList() # pour avoir direct la liste des coordonnées de chaque food      
        if food_list:
          min_food_dist = min([util.manhattan_distance(food_pos, new_pos) for food_pos in food_list])
          score += 5 / min_food_dist # on récompense plus pac est proche d'un food
          
        if current_game_state.getNumFood() > successor_game_state.getNumFood(): # getNumFood() donne le nombre de food restants et pas le nombre de food mangé
          score += 100 # ici on récompense quand il mange, on guette le nombre de food restant entre la gamestate actuelle et la gamestate suivante
              
        return score
        # Avec tout ça on a taux de winrate sur 10 parties de 70 % ou plus (sur le layout par default), donc c'est ok. 
        # Pacman reste encore souvent bloqué quand il se retrouve entre les murs du centre du grid, 
        # j'imagine qu'on pourrait lui baisser son score quand il décide de s'arrêter
        # ou encore l'encourager à ne pas revisiter les mêmes positions. 
        
        # update :  je viens de voir que dans l'énoncé du labo, comme critère d'objectif, on prenait le résultat des parties sur le layout openClassic, 
        # du coup l'implémentation actuelle est plus que suffisante (winrate de 100 % peu importe le nombre de parties lancées)

def score_evaluation_function(current_game_state):
    """
      This default evaluation function just returns the score of the state.
      The score is the same one displayed in the Pacman GUI.

      This evaluation function is meant for use with adversarial search game
      (not reflex game).
    """
    return current_game_state.getScore()

class MultiAgentSearchAgent(Agent):
    """
      This class provides some common elements to all of your
      multi-agent searchers.  Any methods defined here will be available
      to the MinimaxPacmanAgent, AlphaBetaPacmanAgent & ExpectimaxPacmanAgent.

      You *do not* need to make any changes here, but you can if you want to
      add functionality to all your adversarial search game.  Please do not
      remove anything, however.

      Note: this is an abstract class: one that should not be instantiated.  It's
      only partially specified, and designed to be extended.  Agent (game.py)
      is another abstract class.
    """

    def __init__(self, evalFn = 'better', depth = '2'):
        self.index = 0 # Pacman is always agent index 0
        self.evaluationFunction = util.lookup(evalFn, globals())
        self.depth = int(depth)

class MinimaxAgent(MultiAgentSearchAgent):
    """
      Your minimax agent (question 2)
    """

    def get_action(self, game_state):
        """
          Returns the minimax action from the current gameState using self.depth
          and self.evaluationFunction.

          Here are some method calls that might be useful when implementing minimax.

          gameState.getLegalActions(agentIndex):
            Returns a list of legal actions for an agent
            agentIndex=0 means Pacman, ghosts are >= 1

          gameState.generateSuccessor(agentIndex, action):
            Returns the successor game state after an agent takes an action

          gameState.getNumAgents():
            Returns the total number of game in the game (-> petite erreur ça retourne le nombre d'agents d'une game)
        """
        pacman_actions = game_state.getLegalPacmanActions()
        
        best_score = float("-inf")
        best_action = None
        
        for action in pacman_actions:
          game_state_successor = game_state.generateSuccessor(0, action)
          current_score = self.minimaxScore(game_state_successor, 0, 1)
          
          if current_score > best_score:
            best_score = current_score
            best_action = action
        
        return best_action
                  
    def minimaxScore(self, game_state, current_depth, agent_index):
      
      # Comme en algo 3, pour la récursivité on pose d'abord nos conditions de fin
      
      if game_state.isWin() or game_state.isLose():
        return self.evaluationFunction(game_state)
      
      if current_depth == self.depth and agent_index == 0: 
        return self.evaluationFunction(game_state)
      
      legal_actions = game_state.getLegalActions(agent_index)
      next_agent = 0 if agent_index + 1 == game_state.getNumAgents() else agent_index + 1
      next_depth = current_depth + 1 if next_agent == 0 else current_depth
      
      if agent_index == 0: # Si pacman, on veut maximiser notre score
        score = float("-inf")
        
        for action in legal_actions:
          game_state_successor = game_state.generatePacmanSuccessor(action)
          score = max(score, self.minimaxScore(game_state_successor, current_depth, next_agent))
          
      else: # pour les ghost on veut le choix qui minimise le score de pacman
        score = float("inf")
        
        for action in legal_actions: 
          game_state_successor = game_state.generateSuccessor(agent_index, action)
          score = min(score, self.minimaxScore(game_state_successor, next_depth, next_agent))
          
      return score
      
class AlphaBetaAgent(MultiAgentSearchAgent):
    """
      Your minimax agent with alpha-beta pruning (question 3)
    """

    def get_action(self, game_state):
        """
          Returns the minimax action using self.depth and self.evaluationFunction
        """
        pacman_actions = game_state.getLegalPacmanActions()
        
        best_score = float("-inf")
        best_action = None
        
        alpha = float("-inf")
        beta = float("inf")
        
        for action in pacman_actions:
          game_state_successor = game_state.generateSuccessor(0, action)
          current_score = self.minimaxScoreAlphaBeta(game_state_successor, 0, 1, alpha, beta)
          
          if current_score > best_score:
            best_score = current_score
            best_action = action
            
          alpha = max(alpha, best_score)
        
        return best_action
        
    def minimaxScoreAlphaBeta(self, game_state, current_depth, agent_index, alpha, beta):
      
      # Comme en algo 3, pour la récursivité on pose d'abord nos conditions de fin
      
      if game_state.isWin() or game_state.isLose():
        return self.evaluationFunction(game_state)
      
      if current_depth == self.depth and agent_index == 0: 
        return self.evaluationFunction(game_state)
      
      legal_actions = game_state.getLegalActions(agent_index)
      next_agent = 0 if agent_index + 1 == game_state.getNumAgents() else agent_index + 1
      next_depth = current_depth + 1 if next_agent == 0 else current_depth
      
      if agent_index == 0: # Si pacman, on veut maximiser notre score
        score = float("-inf")
        
        for action in legal_actions:
          game_state_successor = game_state.generatePacmanSuccessor(action)
          score = max(score, self.minimaxScoreAlphaBeta(game_state_successor, current_depth, next_agent, alpha, beta))

          alpha = max(alpha, score)
          
          if alpha >= beta:
            break # on élague en évitant d'explorer les autres actions si il y a un score plus élevé car les ghosts n'iront jamais choisir cette action
          
      else: # pour les ghost on veut le choix qui minimise le score de pacman
        score = float("inf")
        
        for action in legal_actions: 
          game_state_successor = game_state.generateSuccessor(agent_index, action)
          score = min(score, self.minimaxScoreAlphaBeta(game_state_successor, next_depth, next_agent, alpha, beta))
          
          beta = min(beta, score)
          
          if alpha >= beta:
            break # on élague
      
      return score  
  
      
class ExpectimaxAgent(MultiAgentSearchAgent):
    """
      Your expectimax agent (question 4)
    """

    def get_action(self, game_state):
        """
          Returns the expectimax action using self.depth and self.evaluationFunction

          All ghosts should be modeled as choosing uniformly at random from their
          legal moves.
        """
        pacman_actions = game_state.getLegalPacmanActions()
        
        best_score = float("-inf")
        best_action = None
        
        for action in pacman_actions:
          game_state_successor = game_state.generateSuccessor(0, action)
          current_score = self.expectimaxScore(game_state_successor, 0, 1)
          
          if current_score > best_score:
            best_score = current_score
            best_action = action
        
        return best_action
      
    def expectimaxScore(self, game_state, current_depth, agent_index):
      # Comme en algo 3, pour la récursivité on pose d'abord nos conditions de fin
      
      if game_state.isWin() or game_state.isLose():
        return self.evaluationFunction(game_state)
      
      if current_depth == self.depth and agent_index == 0: 
        return self.evaluationFunction(game_state)
      
      legal_actions = game_state.getLegalActions(agent_index)
      next_agent = 0 if agent_index + 1 == game_state.getNumAgents() else agent_index + 1
      next_depth = current_depth + 1 if next_agent == 0 else current_depth
      
      if agent_index == 0: # Si pacman, on veut maximiser notre score
        score = float("-inf")
        
        for action in legal_actions:
          game_state_successor = game_state.generatePacmanSuccessor(action)
          score = max(score, self.expectimaxScore(game_state_successor, current_depth, next_agent))
          
            
      else: # pour les ghosts on veut l'espérance math. pour toutes les actions d'un ghost mais pour faciliter on fait moyenne arithmétique
        score = 0
        
        for action in legal_actions: 
          game_state_successor = game_state.generateSuccessor(agent_index, action)
          score += self.expectimaxScore(game_state_successor, next_depth, next_agent)
          
          # plus d' élaguage avec alpha beta car respecte plus les conditions adversariale, ici les ghosts ont un comportement aléatoires..
          
        score = score / len(legal_actions)  
        # faire gaffe ici on veut diviser sur la somme de TOUTES les actions et par sur chacune des actions donc division se fait en dehors de la boucle
          
          
      return score    
          

def betterEvaluationFunction(current_game_tate):
    """
      Your extreme ghost-hunting, pellet-nabbing, food-gobbling, unstoppable
      evaluation function (question 5).

      DESCRIPTION: 
      this function increase or decrease the score of a game_state towards different parameters : 
      - the manhattan distance between pacman and food
      - the manhattan distance between pacman ang ghosts, if they are scared or not 
      - the remaining food
      - the remaining capsules 
    """
    
    # on va s'inspirer de ma evaluation function du reflexagent et l'adapter pour qu'elle prenne qu'un seul parametre, gamestate
    
    score = current_game_tate.getScore()
    pacman_pos = current_game_tate.getPacmanPosition()
    food_list = current_game_tate.getFood().asList() # pour avoir direct les coord de chaque food au lieu d'un grid de boolean
    ghost_states = current_game_tate.getGhostStates()
    remaining_food = len(food_list)
    capsules = current_game_tate.getCapsules()
    
    if food_list:
          min_food_dist = min([util.manhattan_distance(food_pos, pacman_pos) for food_pos in food_list])
          score += 5 / min_food_dist # on récompense plus pac est proche d'un food
          
    score -= 10 * remaining_food # on punit en fonction du nombre de nourriture qui reste (ça va forcer pac à aller chase la nourriture)
     
    for ghost_state in ghost_states:
          ghost_pos = ghost_state.getPosition()
          distance = util.manhattan_distance(ghost_pos, pacman_pos)
          
          if ghost_state.scaredTimer > 0:
            score += 200 / distance # plus un ghost scared est proche plus on récompense
          
          else:
            if distance < 2:
              score -= 500 # si ghost pas scared est trop proche, on punit fooort 
            elif distance < 4:
              score -= 100 / distance 
    
    if capsules: 
      min_capsule_dist = min([util.manhattan_distance(capsule, pacman_pos) for capsule in capsules]) # même principe qu'avec food
      score += 10 / min_capsule_dist # on encourage pac à aller chercher les capsules proches 
      
    return score
  
    # il faudrait encore implémenter une manière de récompenser pacman (ou le punir) si il se retrouve dans un endroit "confiné"    
    # voir commentaire eval function reflexagent
    # ou encore une meilleure manière de manger des food, prévoir un certain chemin en fonction de la food la plus la plus distante
    
    # remarque : sur le layout par défault, pacman reste parfoit bloqué quand pas de fantomes à coté, mais se débloque plus ou moins vite en fonctione de la depth, 
    # car plus on prévoit des coups plus on va voir qu'un ghost se rapproche, ça reste bizarre car il devrait chase la nourriture au lieu de rester aux même positions
    # on pourrait punir si il revient plusieurs fois sur une même position 
    
    # update : après debug on remarque que le problème vient du fait qu'on calcule la distance manhattan sans prendre en compte les murs
    # donc mur se trouve entre pacman et un food, la distance minimum sera biaisé car elle ne prendra pas en compte le mur qui empeche pacman d'atteindre la nourriture
    
# Abbreviation
better = betterEvaluationFunction 


package view;

import model.*;

import java.util.ArrayList;
import java.util.List;

import static model.Color.*;
import static model.Direction.*;
import static model.Shape.*;

/**
 * Represents the view of our game
 */
public class View {
    /**
     * Display the view of the game board
     * @param grid
     */
    public static void displayGrid(GridView grid)
    {
        grid.displayGrid(grid);
        System.out.println();
    }

    /**
     * Display a String on the screen.
     * @param message the String you want to display.
     */
    public static void display(String message)
    {
        System.out.println(message);
    }

    /**
     * Display the player's name and player's hand
     * @param name the name of the player
     * @param hand the player's hand
     */
    public static void displayPlayer(String name, List<Tile> hand, int score)
    {
        System.out.println();
        System.out.println("It's to \033[1m\033[36m" + name + " \033[0mto play : ");
        System.out.println();
        int i = 0;
        System.out.print("[");
        for(Tile t : hand ) {
            System.out.print( " " +i+":"+t +" ");
            i++;
        }
        System.out.println("] " + "Your current score : " + score);
    }

    /**
     * Display the commands to play the game
     */
    public static void displayHelp()
    {
        System.out.println();
        System.out.println(
                """
                        \033[4;36mQwirkle commands\033[0m
                        - o : Play one tile
                        - l : Play severeal tiles on a line
                        - m : Play plic-ploc
                        - p : Skip the round
                        - d : display the game board
                        - r : display the rules of Qwirkle
                        - s : Save the current game
                        - q : Quit the game
                        - To choose the tile(s) you want to play to have to use the index from your hand.
                        """);
        System.out.println();
    }

    /**
     *Display the rules of the Qwirkle game.
     */
    public static void displayRules()
    {
        System.out.println();
        String[] RULES = {
                "----- Qwirkle Game Rules -----",
                "Qwirkle is a tile-based game played with 108 tiles.",
                "The tiles are divided into six different shapes in six different colors.",
                "The goal of the game is to score points by creating lines of tiles that share either a shape or a color.",
                "Here are the rules for playing Qwirkle:",
                "1. Players take turns placing tiles on a game board.",
                "2. A line can be created either horizontally or vertically.",
                "3. A line can consist of one or more tiles.",
                "4. A line can only contain tiles that share either the same shape or the same color, but not both.",
                "5. Once a line is created, the player scores one point for each tile in the line.",
                "6. After scoring, the player replenishes their hand by drawing tiles from the bag.",
                "7. If a player completes a line of six tiles (a Qwirkle), they score a bonus of 6 points.",
                "8. The game continues until the bag is empty and one player has placed all their tiles.",
                "9. The player with the highest score at the end of the game wins.",
                "-------------------------------"
        };

        for (String rule : RULES) {
            System.out.println(rule);
        }
        System.out.println();
    }

    /**
     * Display the error message
     * @param message
     */
    public static void displayError(String message)
    {
        System.out.println();
        System.out.println("\033[1m\033[36mA problem occured --->\033[0m " + message + " Try again !");
        System.out.println();
    }

    public static void displayWelcome()
    {
        System.out.println("\n===================================================");
        System.out.println("\t\t     >>> \033[1m\033[36mTHE QWIRKLE GAME\033[0m <<<");
        System.out.println("===================================================");


    }

    /**
     * Display a end message on the screen with the player who won the game.
     * @param game
     */
    public static void displayEnd(Game game)
    {
        String playerName = game.getCurrentPlayerName();
        int playerScore = game.getCurrentPlayerScore();

        System.out.println();
        System.out.println("┌─────────────────────────────────────────────────┐");
        System.out.println("| Congratulations, \033[1m\033[36m" + playerName + "\033[0m!");
        System.out.println("| You won the game with a score of \033[1m\033[36m" + playerScore + "\033[0m. "
                + "Well played!");
        System.out.println("└─────────────────────────────────────────────────┘");
    }
}

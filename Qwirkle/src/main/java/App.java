import model.*;
import view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        View.displayWelcome();
        Game game;
        GridView grid;
        List<String> players;

        String ans = readString("\nDo you want to load your last game back up ? Type yes (y) or something else to start "
                + "a new game.");

        if (ans.toLowerCase().charAt(0) == 'y') {
            try {
                game = Game.getFromFile("gameSaved");
                grid = game.getGrid();
                View.display("\nYour last game back up was loaded successfully.");
                View.displayGrid(grid);
                View.display("Bag : " + Bag.getInstance().size() + " tiles left. ");

            } catch (Exception e) {
                View.displayError(e.getMessage());
                View.display("\nYou have no saved game to load, starting a new game...");
                players = enterPlayers();
                View.displayRules();
                game = new Game(players);
                grid = game.getGrid();

                View.displayPlayer(game.getCurrentPlayerName(), game.getCurrentPlayerHand(), game.getCurrentPlayerScore());
                boolean isValid = false;
                while (!isValid) {
                    try {
                        playFirst(game);
                        game.pass();
                        isValid = true;
                    } catch (QwirkleException q) {
                        View.displayError(q.getMessage());
                        View.displayPlayer(game.getCurrentPlayerName(), game.getCurrentPlayerHand(),
                                game.getCurrentPlayerScore());
                    }
                }
                View.displayHelp();
                View.displayGrid(grid);


                View.display("Bag : " + Bag.getInstance().size() + " tiles left. ");
            }
        }
        // Here we're using two times the same code, but sadly we can't put it in a private method because the instance
        // of the Game would have been null when entering the loop while(!game.IsOver()). So we have in this specific case
        // we have to re-use the same code instead of factorising it.
        else {
            players = enterPlayers();
            View.displayRules();
            game = new Game(players);
            grid = game.getGrid();

            View.displayPlayer(game.getCurrentPlayerName(), game.getCurrentPlayerHand(), game.getCurrentPlayerScore());
            boolean isValid = false;
            while (!isValid) {
                try {
                    playFirst(game);
                    game.pass();
                    isValid = true;
                } catch (QwirkleException e) {
                    View.displayError(e.getMessage());
                    View.displayPlayer(game.getCurrentPlayerName(), game.getCurrentPlayerHand(),
                            game.getCurrentPlayerScore());
                }
            }
            View.displayHelp();
            View.displayGrid(grid);


            View.display("Bag : " + Bag.getInstance().size() + " tiles left. ");
        }

        while (!game.isOver())
        {
            View.displayPlayer(game.getCurrentPlayerName(), game.getCurrentPlayerHand(), game.getCurrentPlayerScore());
            askCommandToPlayer(game, grid);
            View.displayGrid(grid);
            View.display("Bag : " + Bag.getInstance().size() + " tiles left.");
            game.pass();
        }

        game.getWinner();
        View.displayEnd(game);
    }

    /**
     * Ask the player to input a command to make a move and check if the move is valid else ask again to the current
     * player.
     * @param game Game
     */
    private static void askCommandToPlayer(Game game, GridView grid)
    {
        boolean isValid = false;
        while (!isValid) {

            try {
            String cmd = readString("Enter a command to make a move ('o', 'l', 'm', 'p', " +
                    "or any keys from your keyboard if you want to see all the commands in details) : ");
            // The string array will automatically ignore the space
            // String[] cmds = readString("Enter a command to make a move ('o', 'l', 'm', 'p', " +
            // "or any keys from your keyboard if you want to see all the commands) : ").split("\\s+");
                switch (cmd.toLowerCase().charAt(0)) {
                    case 'o' -> playOneTile(game);
                    case 'l' -> playSomeTilesOnLine(game);
                    case 'm' -> playPlicPloc(game);
                    case 'd' -> {
                        View.displayGrid(grid);
                        View.displayPlayer(game.getCurrentPlayerName(), game.getCurrentPlayerHand(),
                                game.getCurrentPlayerScore());
                        askCommandToPlayer(game, grid);
                    }
                    case 'r' -> {
                        View.displayRules();
                        View.displayPlayer(game.getCurrentPlayerName(), game.getCurrentPlayerHand(),
                                game.getCurrentPlayerScore());
                        askCommandToPlayer(game, grid);
                    }
                    case 'p' -> {
                        game.pass();
                        View.displayGrid(grid);
                        View.displayPlayer(game.getCurrentPlayerName(), game.getCurrentPlayerHand(),
                                game.getCurrentPlayerScore());
                        askCommandToPlayer(game, grid);
                    }
                    case 'q' -> {
                        View.display("You left the game :(  See you soon ! ");
                        System.exit(0);
                    }
                    case 's' -> {
                        game.write("gameSaved");
                        View.display("\nGame has been saved successfully !");
                        askCommandToPlayer(game, grid);
                    }
                    default -> {
                        View.displayHelp();
                        View.displayPlayer(game.getCurrentPlayerName(), game.getCurrentPlayerHand(),
                                game.getCurrentPlayerScore());
                        askCommandToPlayer(game, grid);
                    }
                }
                isValid = true;
            } catch (QwirkleException e) {
                View.displayError(e.getMessage());
                View.displayPlayer(game.getCurrentPlayerName(), game.getCurrentPlayerHand(),
                        game.getCurrentPlayerScore());
            } catch (StringIndexOutOfBoundsException e) {
                View.displayError("Enter a valid command please : ");
            }
        }
    }

    /**
     * Ask player indexes of tile from his hand to play some tiles on the grid
     * @param game Game
     */
    private static void playPlicPloc(Game game)
    {
        int tiles = readInt("How many tiles do you want to play ?");
        int[] tab = new int[tiles*3];
        int i = 1;
        for (int j =0; j < tab.length; j+=3) {
            tab[j] = readInt("Please enter the row of the tile " + i +
                        " from your hand that you want to play : ");
            tab[j+1] = readInt("Please enter the column of the tile " + i +
                        " from your hand that you want to play : ");
            tab[j+2] = readInt("Enter the index of the tile " + i +
                        " from your hand that you want to play : ");
            i++;
        }
        game.play(tab);

    }

    /**
     * Ask player the row and column of the grid, and indexes of his hand and try to play the move.
     * @param game Game
     */
    private static void playOneTile(Game game)
    {
        int row = readInt("Please first enter the row where you want to play your first tile : ");
        int col = readInt("Now enter the column where you want to play your first tile : ");
        int index = readInt("Enter the index of the tile " + " from your hand that you want to play : ");

        while (index < 0 || index >= 6) {
            index= readInt("Enter a correct index of the tile " + " from your hand that you want to play : ");
        }

        game.play(row, col, index);
    }

    /**
     * Ask player the row, the column of the grid of the first tile and the indexes of the tiles he want to play
     * then try to make a move on the grid
     *
     * @param game Game
     */
    private static void playSomeTilesOnLine(Game game)
    {
        int row = readInt("Please first enter the row where you want to play your first tile : ");
        int col = readInt("Now enter the column where you want to play your first tile : ");
        int[] indexes = enterTiles();
        Direction dir = enterDirection();
        game.play(row, col, dir, indexes);
    }

    /**
     * Make the first move on the grid after askin the indexes of his tiles and the direction.
     *
     * @param game Game
     */
    private static void playFirst(Game game)
    {
        int[] indexes = enterTiles();
        Direction dir = enterDirection();
        game.first(dir, indexes);
    }

    /**
     * Ask the tiles the player want to play and try to make the move
     * @return array of int: the indexes of tiles.
     */
    private static int[] enterTiles()
    {
        int tiles = readInt("How many tiles do you want to play ?");
        int[] index = new int[tiles];
        int j = 0;
        for (int i = 0; i < tiles; i++) {
            while (index[j] < 0 || index[j] >= 6) {
                index[j] = readInt("Enter a correct index of the tile " + (i + 1) +
                        " from your hand that you want to play : ");
            }
            index[j] = readInt("Enter the index of the tile " + (i + 1) +
                    " from your hand that you want to play : ");
            j++;
        }
        return index;
    }

    /**
     * Enter the direction command you want to put your tiles
     * @return Direction
     */
    private static Direction enterDirection()
    {
        String direction = readString("Enter a direction you want to play the first tile (UP: u, DOWN: d, " +
                "LEFT: l and " + "RIGHT: r) : ").toLowerCase();

        Direction dir = whichDir(direction);

        while (dir == null) {
            direction = readString("Enter a valid direction please (UP: u, DOWN: d, LEFT: l and " +
                    "RIGHT: r) : ").toLowerCase();
            dir = whichDir(direction);
        }
        return dir;
    }


    /**
     * Check in the string received in parameter which direction the user chose to put his first tile
     * @param dir the user's command
     * @return null unknown direction
     */
    private static Direction whichDir(String dir)
    {
        return switch (dir.toLowerCase().charAt(0)) {
            case 'u' -> Direction.UP;
            case 'd' -> Direction.DOWN;
            case 'l' -> Direction.LEFT;
            case 'r' -> Direction.RIGHT;
            default -> null;
        };
    }
         /*

    /**
     * Ask the user a number of players to play the game
     * @return int the number of players
     */
    private static List<String> enterPlayers()
    {
        List<String> players = new ArrayList<>();
        String ask = "Please enter a number of players between 2 and 4 : ";
        int nbPLayers = readIntMinMax(ask, 2, 4);
        for (int p = 0; p < nbPLayers; p++) {
            if (p == 0) {
                String name = readString("Please enter a name for the first player : ");
                players.add(name);
            }
            else if (p == 1) {
                String name = readString("Please enter a name for the second player : ");
                players.add(name);
            }
            else if (p == 2) {
                String name = readString("Please enter a name for the third player : ");
                players.add(name);
            }
            else if (p == 3) {
                String name = readString("Please enter a name for the fourth player : ");
                players.add(name);
            }

        }
        return players;
    }

    /**
     * Robust method to ask a String to the user
     * @param message the message to display when asking
     * @return String, the user input
     */
    private static String readString(String message)
    {
        Scanner clavier = new Scanner(System.in);
        String ans = "null";
        System.out.println(message);
        ans = clavier.next();
        while (!clavier.hasNextLine() || ans.equals("null")) {
            System.out.println("Something is wrong, try again please.");
            System.out.println(message);
            ans = clavier.next();

        }
        return ans;
    }

    /**
     * Robust method to ask an int to the user
     * @param message the message to display when asking
     * @return int, the user input
     */
    private static int readInt(String message)
    {
        Scanner clavier = new Scanner(System.in);
        System.out.println(message);
        while (!clavier.hasNextInt()) {
            clavier.next();
            System.out.println("Not a number");
            System.out.println(message);
        }
        return clavier.nextInt();
    }

    /**
     * Robust method to ask the user an int between to integer
     * @param message the message to display when asking
     * @return int, the user input
     */
    private static int readIntMinMax(String message, int min, int max)
    {
        if (min > max) {
            throw new IllegalArgumentException("Minimum is higher than maximum " + min + " " + max);
        }

        int entier = readInt(message);
        while (entier < min || entier > max) {
            System.out.println("Number of players must be included between " + min + " and " + max);
            entier = readInt(message);
        }
        return entier;
    }

}

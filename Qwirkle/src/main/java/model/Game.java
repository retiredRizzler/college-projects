package model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Game implements Serializable {
    private Grid grid;
    private Player[] player;
    private int currentPlayer;

    public Game(List<String> players)
    {
        player = new Player[players.size()];

        for (int i = 0; i < player.length; i++) {
                player[i] = new Player(players.get(i));
                player[i].refill();
            }

        grid = new Grid();
        currentPlayer = 0;
    }

    /**
     * Try to make the first move for the current player
     *
     * @param d  Direction to play the first move
     * @param is indexes of list
     */
    public void first(Direction d, int... is)
    {
        int point = grid.firstAdd(d, getTiles(is));
        player[currentPlayer].addScore(point);
        player[currentPlayer].remove(getTiles(is));
        player[currentPlayer].refill();
    }

    /**
     * Try to play a tile at a specific position for the current player
     *
     * @param row   the row of the grid
     * @param col   the column of the grid
     * @param index the index of the tile from player's hand
     */
    public void play(int row, int col, int index)
    {
        int point = grid.add(row, col, player[currentPlayer].getHand().get(index));
        player[currentPlayer].addScore(point);
        player[currentPlayer].remove(getTiles(index));
        player[currentPlayer].refill();
    }

    /**
     * Try to play several tiles in a specific position for the current player
     *
     * @param row     the row of the grid
     * @param col     the column of the grid
     * @param d       the direction you want to add your tiles
     * @param indexes the index of the tile from player's hand
     */
    public void play(int row, int col, Direction d, int... indexes)
    {
        int point = grid.add(row, col, d, getTiles(indexes));
        player[currentPlayer].remove(getTiles(indexes));
        player[currentPlayer].addScore(point);
        player[currentPlayer].refill();
    }

    /**
     * Try to play several tiles which aren't lined up for the current player
     *
     * @param is indexes of the tiles from current player's hand. First index, is the row, second is the column, third
     *           is the tile and so on.
     */
    public void play(int... is)
    {
        TileAtPosition[] tap = new TileAtPosition[is.length/3];
        int j = 0;
        // In this for loop we have to iterate it only with multiple of 3 because every 3 indexes we have a new
        // TileAtPosition object.
        for (int i = 0; i < is.length; i += 3) {
            int r = is[i];
            int c = is[i + 1];
            Tile t = player[currentPlayer].getHand().get(is[i + 2]);
            tap[j] = new TileAtPosition(r, c, t);
            j++;
        }
        int point = grid.add(tap);
        player[currentPlayer].remove(getTilesTAP(is));
        player[currentPlayer].addScore(point);
        player[currentPlayer].refill();
    }

    /**
     * Create an array of tiles based on the indexes of the player's hand with an int varargs in argument.
     * for method play(int row, int col, Direction d, int... indexes).
     * @param is indexes
     * @return an array of tiles based on player's hand indexes
     */
    private Tile[] getTiles(int... is)
    {
        Tile[] tab = new Tile[is.length];

        for (int i = 0; i < tab.length; i++) {
            tab[i] = player[currentPlayer].getHand().get(is[i]);
        }
        return tab;
    }

    /**
     * Create an array of tiles based on the indexes of the player's hand with an int varargs in argument for
     * method play(int...) -> TileAtPosition.
     * @param is indexes
     * @return an array of tiles based on player's hand indexes
     */
    private Tile[] getTilesTAP(int... is)
    {
        Tile[] tab = new Tile[is.length];

        // Loop start iterate at index 3 of the is length because the tiles are found all the 3 indexes.
        for (int i = 2; i < tab.length; i+=3) {
            tab[i] = player[currentPlayer].getHand().get(is[i]);
        }
        return tab;
    }

    /**
     * Return the name of the current player
     *
     * @return String of current player's name
     */
    public String getCurrentPlayerName() {
        return player[currentPlayer].getName();
    }

    /**
     * Current player's hand
     *
     * @return List of Tile of the current player's hand
     */
    public List<Tile> getCurrentPlayerHand()
    {
        return player[currentPlayer].getHand();
    }

    /**
     *
     * @return the score of the current player.
     */
    public int getCurrentPlayerScore()
    {
        return player[currentPlayer].getScore();
    }


    /**
     * Switch round to the next player
     */
    public void pass()
    {
        if (currentPlayer != player.length - 1) {
            currentPlayer += 1;
        } else {
            currentPlayer = 0;
        }
    }

    /**
     * Getter for grid attribute
     */
    public GridView getGrid()
    {
        return new GridView(grid);
    }

    /**
     * Control the end of the game
     * @return true players the bag is empty and players can't play anymore, or
     */
    public boolean isOver()
    {
        if (Bag.getInstance().size() != 0) {
            return false;
        }
        for (int i = 0; i < player.length; i++ ) {
            Player p = player[i];
            if (p.getHand().isEmpty()) {
                p.addScore(6);
                return true;
            }
            if (grid.isPossibleMove(p.getHand())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Calculate the highest score and determines the winner with the currentPlayer
     */
    public void getWinner()
    {
        int maxScore = player[0].getScore();
        for (int i = 0; i < player.length; i++ ) {
            Player p = player[i];
            if (p.getScore() > maxScore) {
                maxScore = p.getScore();
                currentPlayer = i;
            }
        }
    }

    /**
     * Write the game passed in argument in a file and serialize it.
     * @param fileName the name of the file
     */
    public void write(String fileName)
    {
        try {
            FileOutputStream file = new FileOutputStream(fileName.concat(".ser"));
            ObjectOutputStream out  = new ObjectOutputStream(file);

            out.writeObject(this);
            out.writeObject(Bag.getInstance());
            out.close();
            file.close();

        } catch (IOException e) {
            throw new QwirkleException(e.getMessage());
        }
    }

    /**
     * Deserialize the Game object to get from the file and return it
     * @param fileName the name of the file to get
     * @return instance of a game from the file
     */
    public static Game getFromFile(String fileName)
    {
        Game game;
        Bag bag;

        try {
            FileInputStream file = new FileInputStream(fileName.concat(".ser"));
            ObjectInputStream in = new ObjectInputStream(file);

            game = (Game)in.readObject();
            bag = (Bag)in.readObject();
            bag.getCurrentInstanceBag(bag);
            in.close();
            file.close();

        } catch (IOException | ClassNotFoundException e) {
            throw new QwirkleException(e.getMessage());
        }
        return game;
    }
}

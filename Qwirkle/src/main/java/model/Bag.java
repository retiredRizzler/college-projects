package model;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Bag represents the bag of tile
 */
public final class  Bag implements Serializable {
    private static volatile Bag instance = null;

    private List<Tile> tiles;

    /**
     * Constructor represents our game bag with its 108 tiles
     */
    private Bag()
    {
       tiles = new ArrayList();
       // In order to get the 108 tiles in our list, we have to first browse each color
       for (Color color : Color.values())
       {
           // Then browse all shape for each color
           for(Shape shape : Shape.values())
           {
               //Finally, add 3 times each combination of tiles in our list
               for (int i = 0; i<3; i++)
               {
                   tiles.add(new Tile(color, shape));
               }
           }
       }
    }

    /**
     * Method which returns an instance of Bag
     * @return instance of Bag
     */
    public static Bag getInstance()
    // source : https://fr.wikipedia.org/wiki/Singleton_(patron_de_conception)
    {
        if (Bag.instance == null)
        {
            synchronized (Bag.class)
            {
                if (Bag.instance == null)
                {
                    Bag.instance = new Bag();
                }
            }
        }
        return Bag.instance;
    }

    void getCurrentInstanceBag(Bag bag)
    {
        instance = bag;
    }


    /**
     * Return the size of our bag (to make the tests easier)
     */
    public int size()
    {
        return tiles.size();
    }

    /**
     * Picking random tiles in the bag
     * @param n the number of tiles we want to
     * @return an array of our random tiles
     */
    public Tile[] getRandomTiles(int n)
    {
        if(tiles.isEmpty())
        {
            return null;
        }

        if (tiles.size() < n) {
            n = tiles.size();
        }

        Random r = new Random();
        // Convert our tab in a temporary list, so we can easily add values in it
        List<Tile> tempList = new ArrayList();

        for(int i = 0; i<n; i++)
        {
            int randIndex = r.nextInt(tiles.size());
            tempList.add(tiles.get(randIndex));

            // Removing the tile we got from the list, so we don't pick a tile more than once
            tiles.remove(randIndex);
        }

        return tempList.toArray(new Tile[0]);
    }

}

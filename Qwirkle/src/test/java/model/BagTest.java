package model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BagTest {
    private Bag bag;
    @BeforeEach
    public void setUp() { bag = Bag.getInstance(); }

    // ??? Problem with tests: When Launching them all in once, it fails; But when launching them individually
    // it passes. it happens maybe because they all use the same bag
    @Test
    void getInstanceTest()
    {
        int act = bag.size();
        int exp = 108;
        assertEquals(exp, act);
    }

    @Test
    void getRandomTilesRemovedFromTheBagTest()
    {
        Tile[] tab = bag.getRandomTiles(5);
        int act = bag.size();
        int exp = 103; // 108 - 5 tiles
        assertEquals(exp, act);
    }

    @Test
    void getRandomTilesAddToArray()
    {
        Tile[] tab = bag.getRandomTiles(4);
        assertEquals(4,tab.length);
    }

    @Test
    void getRandomTilesNotEnoughTiles()
    {
        Tile[] tab = bag.getRandomTiles(200);
        assertEquals(null, tab);
    }

    @Test
    void getRandomTilesBagIsEmpty()
    {
        Tile[] tab = bag.getRandomTiles(108);
        Tile[] act = bag.getRandomTiles(1);
        assertEquals(null, act);
    }
}
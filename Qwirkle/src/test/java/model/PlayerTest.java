package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {


    @Test
    void refill()
    {
        Player p1 = new Player("p1");
        p1.refill();
        assertEquals(6, p1.getHand().size());
    }

    @Test
    void remove() {
        Player p1 = new Player("p1");
        p1.refill();
        p1.remove(p1.getHand().get(0), p1.getHand().get(1));
        assertEquals(4, p1.getHand().size());
    }
}
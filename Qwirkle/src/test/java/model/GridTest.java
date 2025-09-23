package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import view.View;

import static org.junit.jupiter.api.Assertions.*;
import static model.Color.*;
import static model.Direction.*;
import static model.Shape.*;
import static model.QwirkleTestUtils.*;

class GridTest {

    private Grid grid;

    @BeforeEach
    void setUp() {
        grid = new Grid();
    }

    @Test
    void firstAdd_one_tile()
    {
        var tile = new Tile(BLUE, CROSS);
        grid.firstAdd(RIGHT, tile);
        assertSame(get(grid, 0, 0), tile);
    }

    @Test
    void rules_sonia_a()
    {
        var t1 = new Tile(RED, ROUND);
        var t2 = new Tile(RED, DIAMOND);
        var t3 = new Tile(RED, PLUS);
        int score = grid.firstAdd(UP, t1, t2, t3);

        assertEquals(t1, get(grid, 0, 0));
        assertEquals(t2, get(grid, -1, 0));
        assertEquals(t3, get(grid, -2, 0));
        assertEquals(3, score);
    }

    @Test
    void rules_sonia_a_adapted_to_fail()
    {
        var t1 = new Tile(RED, ROUND);
        var t2 = new Tile(RED, DIAMOND);
        var t3 = new Tile(RED, DIAMOND);
        assertThrows(QwirkleException.class, ()->{
            grid.firstAdd(UP, t1, t2, t3);
        });
        assertNull(get(grid,0,0));
        assertNull(get(grid,-1,0));
        assertNull(get(grid,-2,0));
    }

    @Test
    void firstAdd_cannot_be_called_twice()
    {
        Tile redcross = new Tile(RED, CROSS);
        Tile reddiamond = new Tile(RED, DIAMOND);
        grid.firstAdd(UP, redcross, reddiamond);
        assertThrows(QwirkleException.class, () -> grid.firstAdd(DOWN, redcross, reddiamond));
    }

    @Test
    void firstAdd_must_be_called_first_simple()
    {
        Tile redcross = new Tile(RED, CROSS);
        assertThrows(QwirkleException.class, () -> add(grid, 0, 0, redcross));
    }

    @Test
    @DisplayName("get outside the grid should return null, not throw")
    void can_get_tile_outside_virtual_grid()
    {
        var g = new Grid();
        assertDoesNotThrow(() -> get(g, -250, 500));
        assertNull(get(g, -250, 500));
    }

    @Test
    void rules_same_tile_on_same_line()
    {
        var t1 = new Tile(RED, ROUND);
        var t2 = new Tile(RED, DIAMOND);
        var t3 = new Tile(RED, PLUS);
        grid.firstAdd(UP, t1, t2, t3);

        var t4 = new Tile (RED, SQUARE);
        var t5 = new Tile (BLUE, SQUARE);
        var t6 = new Tile (PURPLE, SQUARE);
        add(grid, 1, 0, t4);
        add(grid, 1, 1, t5);
        add(grid, 1, 2, t6);

        var tCrash = new Tile (RED, PLUS);

        assertThrows(QwirkleException.class, ()->{
            grid.add(-2, 0, tCrash);
        });
    }

    @Test
    void rules_tiles_must_be_on_same_lines_throw()
    {
        var t1 = new Tile(RED, ROUND);
        var t2 = new Tile(RED, DIAMOND);
        var t3 = new Tile(RED, PLUS);
        grid.firstAdd(UP, t1, t2, t3);

        var t4 = new Tile (RED, SQUARE);
        var t5 = new Tile (BLUE, SQUARE);
        var t6 = new Tile (PURPLE, SQUARE);
        add(grid, 1, 0,RIGHT, t4,t5,t6);

        var t7 = new Tile(BLUE, ROUND);
        add(grid, 0, 1, t7);

        var t8 = new Tile(GREEN, PLUS);
        var t9 = new Tile(GREEN, DIAMOND);
        add(grid, -2, -1, DOWN, t8,t9);

        var t10 = createTileAtPos(-3, -1, new Tile(GREEN, STAR));
        var t11 = createTileAtPos(0, 2, new Tile(PURPLE, ROUND));
        assertThrows(QwirkleException.class, ()->{
            grid.add(t10,t11);
        });
    }

    @Test
    void rules_cedric_b()
    {
        var t1 = new Tile(RED, ROUND);
        var t2 = new Tile(RED, DIAMOND);
        var t3 = new Tile(RED, PLUS);
        grid.firstAdd(UP, t1, t2, t3);

        var t4 = new Tile (RED, SQUARE);
        var t5 = new Tile (BLUE, SQUARE);
        var t6 = new Tile (PURPLE, SQUARE);

        var t7 = new Tile(PURPLE, DIAMOND);

        int score = add(grid, 1, 0, RIGHT, t4, t5, t6);
        int score1 = add(grid, 2, 2, t7);
        View.displayGrid(new GridView(grid));
        System.out.println("Score : " + score);
        System.out.println("Score1 : " + score1);
        assertEquals(t1, get(grid, 0, 0));
        assertEquals(t2, get(grid, -1, 0));
        assertEquals(t3, get(grid, -2, 0));
        assertEquals(t4, get(grid, 1, 0));
        assertEquals(t5, get(grid, 1, 1 ));
        assertEquals(t6, get(grid, 1, 2));

        assertEquals(7, score);
        assertEquals(2, score1);
    }

    @Test
    void rules_elvire_c()
    {
        var t1 = new Tile(RED, ROUND);
        var t2 = new Tile(RED, DIAMOND);
        var t3 = new Tile(RED, PLUS);
        grid.firstAdd(UP, t1, t2, t3);

        var t4 = new Tile (RED, SQUARE);
        var t5 = new Tile (BLUE, SQUARE);
        var t6 = new Tile (PURPLE, SQUARE);
        add(grid, 1, 0,RIGHT, t4,t5,t6);

        var t7 = new Tile(BLUE, ROUND);
        int score = add(grid, 0, 1, t7);
        View.displayGrid(new GridView(grid));
        System.out.println("Score : " + score);

        assertEquals(t1, get(grid, 0, 0));
        assertEquals(t2, get(grid, -1, 0));
        assertEquals(t3, get(grid, -2, 0));
        assertEquals(t4, get(grid, 1, 0));
        assertEquals(t5, get(grid, 1, 1 ));
        assertEquals(t6, get(grid, 1, 2));
        assertEquals(t7, get(grid, 0, 1));

        assertEquals(4, score);
    }

    @Test
    void rules_vincent_c()
    {
        var t1 = new Tile(RED, ROUND);
        var t2 = new Tile(RED, DIAMOND);
        var t3 = new Tile(RED, PLUS);
        grid.firstAdd(UP, t1, t2, t3);

        var t4 = new Tile (RED, SQUARE);
        var t5 = new Tile (BLUE, SQUARE);
        var t6 = new Tile (PURPLE, SQUARE);
        add(grid, 1, 0,RIGHT, t4,t5,t6);

        var t7 = new Tile(BLUE, ROUND);
        add(grid, 0, 1, t7);

        var t8 = new Tile(GREEN, PLUS);
        var t9 = new Tile(GREEN, DIAMOND);
        int score = add(grid, -2, -1, DOWN, t8,t9);
        View.displayGrid(new GridView(grid));
        System.out.println("Score : " + score);

        assertEquals(t1, get(grid, 0, 0));
        assertEquals(t2, get(grid, -1, 0));
        assertEquals(t3, get(grid, -2, 0));
        assertEquals(t4, get(grid, 1, 0));
        assertEquals(t5, get(grid, 1, 1 ));
        assertEquals(t6, get(grid, 1, 2));
        assertEquals(t7, get(grid, 0, 1));
        assertEquals(t8, get(grid, -2, -1));
        assertEquals(t9, get(grid, -1, -1));

        assertEquals(6, score);
    }

    @Test
    void rules_sonia_e()
    {
        var t1 = new Tile(RED, ROUND);
        var t2 = new Tile(RED, DIAMOND);
        var t3 = new Tile(RED, PLUS);
        grid.firstAdd(UP, t1, t2, t3);

        var t4 = new Tile (RED, SQUARE);
        var t5 = new Tile (BLUE, SQUARE);
        var t6 = new Tile (PURPLE, SQUARE);
        add(grid, 1, 0,RIGHT, t4,t5,t6);

        var t7 = new Tile(BLUE, ROUND);
        add(grid, 0, 1, t7);

        var t8 = new Tile(GREEN, PLUS);
        var t9 = new Tile(GREEN, DIAMOND);
        add(grid, -2, -1, DOWN, t8,t9);

        var t10 = createTileAtPos(-3, -1, new Tile(GREEN, STAR));
        var t11 = createTileAtPos(0, -1, new Tile(GREEN, ROUND));
        int score = grid.add(t10, t11);
        View.displayGrid(new GridView(grid));
        System.out.println("Score : " + score);


        assertEquals(t1, get(grid, 0, 0));
        assertEquals(t2, get(grid, -1, 0));
        assertEquals(t3, get(grid, -2, 0));

        assertEquals(t4, get(grid, 1, 0));
        assertEquals(t5, get(grid, 1, 1 ));
        assertEquals(t6, get(grid, 1, 2));

        assertEquals(t7, get(grid, 0, 1));

        assertEquals(t8, get(grid, -2, -1));
        assertEquals(t9, get(grid, -1, -1));

        assertEquals(t10.tile(), get(grid, -3, -1));
        assertEquals(t11.tile(), get(grid, 0, -1));

        assertEquals(7, score);
    }

    @Test
    void rules_cedric_f()
    {
        var t1 = new Tile(RED, ROUND);
        var t2 = new Tile(RED, DIAMOND);
        var t3 = new Tile(RED, PLUS);
        grid.firstAdd(UP, t1, t2, t3);

        var t4 = new Tile (RED, SQUARE);
        var t5 = new Tile (BLUE, SQUARE);
        var t6 = new Tile (PURPLE, SQUARE);
        add(grid, 1, 0,RIGHT, t4,t5,t6);

        var t7 = new Tile(BLUE, ROUND);
        add(grid, 0, 1, t7);

        var t8 = new Tile(GREEN, PLUS);
        var t9 = new Tile(GREEN, DIAMOND);
        add(grid, -2, -1, DOWN, t8,t9);

        var t10 = createTileAtPos(-3, -1, new Tile(GREEN, STAR));
        var t11 = createTileAtPos(0, -1, new Tile(GREEN, ROUND));
        grid.add(t10, t11);

        var t12 = new Tile(ORANGE, SQUARE);
        var t13 = new Tile(RED, SQUARE);
        int score = add(grid, 1, 3, DOWN, t12,t13);
        View.displayGrid(new GridView(grid));
        System.out.println("Score : " + score);

        assertEquals(t1, get(grid, 0, 0));
        assertEquals(t2, get(grid, -1, 0));
        assertEquals(t3, get(grid, -2, 0));

        assertEquals(t4, get(grid, 1, 0));
        assertEquals(t5, get(grid, 1, 1 ));
        assertEquals(t6, get(grid, 1, 2));

        assertEquals(t7, get(grid, 0, 1));

        assertEquals(t8, get(grid, -2, -1));
        assertEquals(t9, get(grid, -1, -1));

        assertEquals(t10.tile(), get(grid, -3, -1));
        assertEquals(t11.tile(), get(grid, 0, -1));

        assertEquals(t12, get(grid, 1, 3));
        assertEquals(t13, get(grid, 2, 3));

        assertEquals(6, score);
    }

    @Test
    void rules_elvire_g()
    {
        var t1 = new Tile(RED, ROUND);
        var t2 = new Tile(RED, DIAMOND);
        var t3 = new Tile(RED, PLUS);
        grid.firstAdd(UP, t1, t2, t3);

        var t4 = new Tile (RED, SQUARE);
        var t5 = new Tile (BLUE, SQUARE);
        var t6 = new Tile (PURPLE, SQUARE);
        add(grid, 1, 0,RIGHT, t4,t5,t6);

        var t7 = new Tile(BLUE, ROUND);
        add(grid, 0, 1, t7);

        var t8 = new Tile(GREEN, PLUS);
        var t9 = new Tile(GREEN, DIAMOND);
        add(grid, -2, -1, DOWN, t8,t9);

        var t10 = createTileAtPos(-3, -1, new Tile(GREEN, STAR));
        var t11 = createTileAtPos(0, -1, new Tile(GREEN, ROUND));
        grid.add(t10,t11);

        var t12 = new Tile(ORANGE, SQUARE);
        var t13 = new Tile(RED, SQUARE);
        add(grid, 1, 3, DOWN, t12,t13);

        var t14 = new Tile(YELLOW, STAR);
        var t15 = new Tile(ORANGE, STAR);
        int score = add(grid, -3, -2, LEFT, t14,t15);
        View.displayGrid(new GridView(grid));
        System.out.println("Score : " + score);
        assertEquals(3, score);

        assertEquals(t1, get(grid, 0, 0));
        assertEquals(t2, get(grid, -1, 0));
        assertEquals(t3, get(grid, -2, 0));

        assertEquals(t4, get(grid, 1, 0));
        assertEquals(t5, get(grid, 1, 1 ));
        assertEquals(t6, get(grid, 1, 2));

        assertEquals(t7, get(grid, 0, 1));

        assertEquals(t8, get(grid, -2, -1));
        assertEquals(t9, get(grid, -1, -1));

        assertEquals(t10.tile(), get(grid, -3, -1));
        assertEquals(t11.tile(), get(grid, 0, -1));

        assertEquals(t12, get(grid, 1, 3));
        assertEquals(t13, get(grid, 2, 3));

        assertEquals(t14, get(grid, -3, -2));
        assertEquals(t15, get(grid, -3, -3));
    }

    @Test
    void rules_vincent_h()
    {
        var t1 = new Tile(RED, ROUND);
        var t2 = new Tile(RED, DIAMOND);
        var t3 = new Tile(RED, PLUS);
        grid.firstAdd(UP, t1, t2, t3);

        var t4 = new Tile (RED, SQUARE);
        var t5 = new Tile (BLUE, SQUARE);
        var t6 = new Tile (PURPLE, SQUARE);
        add(grid, 1, 0,RIGHT, t4,t5,t6);

        var t7 = new Tile(BLUE, ROUND);
        add(grid, 0, 1, t7);

        var t8 = new Tile(GREEN, PLUS);
        var t9 = new Tile(GREEN, DIAMOND);
        add(grid, -2, -1, DOWN, t8,t9);

        var t10 = createTileAtPos(-3, -1, new Tile(GREEN, STAR));
        var t11 = createTileAtPos(0, -1, new Tile(GREEN, ROUND));
        grid.add(t10,t11);

        var t12 = new Tile(ORANGE, SQUARE);
        var t13 = new Tile(RED, SQUARE);
        add(grid, 1, 3, DOWN, t12,t13);

        var t14 = new Tile(YELLOW, STAR);
        var t15 = new Tile(ORANGE, STAR);
        add(grid, -3, -2, LEFT, t14,t15);

        var t16 = new Tile(ORANGE, CROSS);
        var t17 = new Tile(ORANGE, DIAMOND);
        int score = add(grid, -2, -3, DOWN, t16,t17);
        View.displayGrid(new GridView(grid));
        System.out.println("Score : " + score);
        assertEquals(3, score);

        assertEquals(t1, get(grid, 0, 0));
        assertEquals(t2, get(grid, -1, 0));
        assertEquals(t3, get(grid, -2, 0));

        assertEquals(t4, get(grid, 1, 0));
        assertEquals(t5, get(grid, 1, 1 ));
        assertEquals(t6, get(grid, 1, 2));

        assertEquals(t7, get(grid, 0, 1));

        assertEquals(t8, get(grid, -2, -1));
        assertEquals(t9, get(grid, -1, -1));

        assertEquals(t10.tile(), get(grid, -3, -1));
        assertEquals(t11.tile(), get(grid, 0, -1));

        assertEquals(t12, get(grid, 1, 3));
        assertEquals(t13, get(grid, 2, 3));

        assertEquals(t14, get(grid, -3, -2));
        assertEquals(t15, get(grid, -3, -3));

        assertEquals(t16, get(grid, -2, -3));
        assertEquals(t17, get(grid, -1, -3));
    }

    @Test
    void rules_sonia_i()
    {
        var t1 = new Tile(RED, ROUND);
        var t2 = new Tile(RED, DIAMOND);
        var t3 = new Tile(RED, PLUS);
        grid.firstAdd(UP, t1, t2, t3);

        var t4 = new Tile (RED, SQUARE);
        var t5 = new Tile (BLUE, SQUARE);
        var t6 = new Tile (PURPLE, SQUARE);
        add(grid, 1, 0,RIGHT, t4,t5,t6);

        var t7 = new Tile(BLUE, ROUND);
        add(grid, 0, 1, t7);

        var t8 = new Tile(GREEN, PLUS);
        var t9 = new Tile(GREEN, DIAMOND);
        add(grid, -2, -1, DOWN, t8,t9);

        var t10 = createTileAtPos(-3, -1, new Tile(GREEN, STAR));
        var t11 = createTileAtPos(0, -1, new Tile(GREEN, ROUND));
        grid.add(t10,t11);

        var t12 = new Tile(ORANGE, SQUARE);
        var t13 = new Tile(RED, SQUARE);
        add(grid, 1, 3, DOWN, t12,t13);

        var t14 = new Tile(YELLOW, STAR);
        var t15 = new Tile(ORANGE, STAR);
        add(grid, -3, -2, LEFT, t14,t15);

        var t16 = new Tile(ORANGE, CROSS);
        var t17 = new Tile(ORANGE, DIAMOND);
        add(grid, -2, -3, DOWN, t16,t17);

        var t18 = new Tile(YELLOW, DIAMOND);
        var t19 = new Tile(YELLOW, ROUND);
        int score = add(grid, -1, -2, DOWN, t18,t19);
        View.displayGrid(new GridView(grid));
        System.out.println("Score : " + score);
        assertEquals(10, score);


        assertEquals(t1, get(grid, 0, 0));
        assertEquals(t2, get(grid, -1, 0));
        assertEquals(t3, get(grid, -2, 0));

        assertEquals(t4, get(grid, 1, 0));
        assertEquals(t5, get(grid, 1, 1 ));
        assertEquals(t6, get(grid, 1, 2));

        assertEquals(t7, get(grid, 0, 1));

        assertEquals(t8, get(grid, -2, -1));
        assertEquals(t9, get(grid, -1, -1));

        assertEquals(t10.tile(), get(grid, -3, -1));
        assertEquals(t11.tile(), get(grid, 0, -1));

        assertEquals(t12, get(grid, 1, 3));
        assertEquals(t13, get(grid, 2, 3));

        assertEquals(t14, get(grid, -3, -2));
        assertEquals(t15, get(grid, -3, -3));

        assertEquals(t16, get(grid, -2, -3));
        assertEquals(t17, get(grid, -1, -3));

        assertEquals(t18, get(grid, -1, -2));
        assertEquals(t19, get(grid, 0, -2));

    }

    @Test
    void rules_cedric_j()
    {
        var t1 = new Tile(RED, ROUND);
        var t2 = new Tile(RED, DIAMOND);
        var t3 = new Tile(RED, PLUS);
        grid.firstAdd(UP, t1, t2, t3);

        var t4 = new Tile (RED, SQUARE);
        var t5 = new Tile (BLUE, SQUARE);
        var t6 = new Tile (PURPLE, SQUARE);
        add(grid, 1, 0,RIGHT, t4,t5,t6);

        var t7 = new Tile(BLUE, ROUND);
        add(grid, 0, 1, t7);

        var t8 = new Tile(GREEN, PLUS);
        var t9 = new Tile(GREEN, DIAMOND);
        add(grid, -2, -1, DOWN, t8,t9);

        var t10 = createTileAtPos(-3, -1, new Tile(GREEN, STAR));
        var t11 = createTileAtPos(0, -1, new Tile(GREEN, ROUND));
        grid.add(t10,t11);

        var t12 = new Tile(ORANGE, SQUARE);
        var t13 = new Tile(RED, SQUARE);
        add(grid, 1, 3, DOWN, t12,t13);

        var t14 = new Tile(YELLOW, STAR);
        var t15 = new Tile(ORANGE, STAR);
        add(grid, -3, -2, LEFT, t14,t15);

        var t16 = new Tile(ORANGE, CROSS);
        var t17 = new Tile(ORANGE, DIAMOND);
        add(grid, -2, -3, DOWN, t16,t17);

        var t18 = new Tile(YELLOW, DIAMOND);
        var t19 = new Tile(YELLOW, ROUND);
        add(grid, -1, -2, DOWN, t18,t19);

        var t20 = new Tile(RED, STAR);
        int score = add(grid, -3, 0, t20);
        View.displayGrid(new GridView(grid));
        System.out.println("Score : " + score);

        assertEquals(t1, get(grid, 0, 0));
        assertEquals(t2, get(grid, -1, 0));
        assertEquals(t3, get(grid, -2, 0));

        assertEquals(t4, get(grid, 1, 0));
        assertEquals(t5, get(grid, 1, 1 ));
        assertEquals(t6, get(grid, 1, 2));

        assertEquals(t7, get(grid, 0, 1));

        assertEquals(t8, get(grid, -2, -1));
        assertEquals(t9, get(grid, -1, -1));

        assertEquals(t10.tile(), get(grid, -3, -1));
        assertEquals(t11.tile(), get(grid, 0, -1));

        assertEquals(t12, get(grid, 1, 3));
        assertEquals(t13, get(grid, 2, 3));

        assertEquals(t14, get(grid, -3, -2));
        assertEquals(t15, get(grid, -3, -3));

        assertEquals(t16, get(grid, -2, -3));
        assertEquals(t17, get(grid, -1, -3));

        assertEquals(t18, get(grid, -1, -2));
        assertEquals(t19, get(grid, 0, -2));
        assertEquals(t20, get(grid, -3, 0));
    }

    @Test
    void rules_elvire_k()
    {
        var t1 = new Tile(RED, ROUND);
        var t2 = new Tile(RED, DIAMOND);
        var t3 = new Tile(RED, PLUS);
        grid.firstAdd(UP, t1, t2, t3);

        var t4 = new Tile (RED, SQUARE);
        var t5 = new Tile (BLUE, SQUARE);
        var t6 = new Tile (PURPLE, SQUARE);
        add(grid, 1, 0,RIGHT, t4,t5,t6);

        var t7 = new Tile(BLUE, ROUND);
        add(grid, 0, 1, t7);

        var t8 = new Tile(GREEN, PLUS);
        var t9 = new Tile(GREEN, DIAMOND);
        add(grid, -2, -1, DOWN, t8,t9);

        var t10 = createTileAtPos(-3, -1, new Tile(GREEN, STAR));
        var t11 = createTileAtPos(0, -1, new Tile(GREEN, ROUND));
        grid.add(t10,t11);

        var t12 = new Tile(ORANGE, SQUARE);
        var t13 = new Tile(RED, SQUARE);
        add(grid, 1, 3, DOWN, t12,t13);

        var t14 = new Tile(YELLOW, STAR);
        var t15 = new Tile(ORANGE, STAR);
        add(grid, -3, -2, LEFT, t14,t15);

        var t16 = new Tile(ORANGE, CROSS);
        var t17 = new Tile(ORANGE, DIAMOND);
        add(grid, -2, -3, DOWN, t16,t17);

        var t18 = new Tile(YELLOW, DIAMOND);
        var t19 = new Tile(YELLOW, ROUND);
        add(grid, -1, -2, DOWN, t18,t19);

        var t20 = new Tile(RED, STAR);
        add(grid, -3, 0, t20);

        var t21 = new Tile(BLUE, CROSS);
        var t22 = new Tile(RED, CROSS);
        var t23 = new Tile(ORANGE, CROSS);
        add(grid, 2, 1, LEFT, t21,t22,t23);


        assertEquals(t1, get(grid, 0, 0));
        assertEquals(t2, get(grid, -1, 0));
        assertEquals(t3, get(grid, -2, 0));

        assertEquals(t4, get(grid, 1, 0));
        assertEquals(t5, get(grid, 1, 1 ));
        assertEquals(t6, get(grid, 1, 2));

        assertEquals(t7, get(grid, 0, 1));

        assertEquals(t8, get(grid, -2, -1));
        assertEquals(t9, get(grid, -1, -1));

        assertEquals(t10.tile(), get(grid, -3, -1));
        assertEquals(t11.tile(), get(grid, 0, -1));

        assertEquals(t12, get(grid, 1, 3));
        assertEquals(t13, get(grid, 2, 3));

        assertEquals(t14, get(grid, -3, -2));
        assertEquals(t15, get(grid, -3, -3));

        assertEquals(t16, get(grid, -2, -3));
        assertEquals(t17, get(grid, -1, -3));

        assertEquals(t18, get(grid, -1, -2));
        assertEquals(t19, get(grid, 0, -2));
        assertEquals(t20, get(grid, -3, 0));

        assertEquals(t21, get(grid, 2, 1));
        assertEquals(t22, get(grid, 2, 0));
        assertEquals(t23, get(grid, 2, -1));
    }

    @Test
    void rules_vincent_l()
    {
        var t1 = new Tile(RED, ROUND);
        var t2 = new Tile(RED, DIAMOND);
        var t3 = new Tile(RED, PLUS);
        grid.firstAdd(UP, t1, t2, t3);

        var t4 = new Tile (RED, SQUARE);
        var t5 = new Tile (BLUE, SQUARE);
        var t6 = new Tile (PURPLE, SQUARE);
        add(grid, 1, 0,RIGHT, t4,t5,t6);

        var t7 = new Tile(BLUE, ROUND);
        add(grid, 0, 1, t7);

        var t8 = new Tile(GREEN, PLUS);
        var t9 = new Tile(GREEN, DIAMOND);
        add(grid, -2, -1, DOWN, t8,t9);

        var t10 = createTileAtPos(-3, -1, new Tile(GREEN, STAR));
        var t11 = createTileAtPos(0, -1, new Tile(GREEN, ROUND));
        grid.add(t10,t11);

        var t12 = new Tile(ORANGE, SQUARE);
        var t13 = new Tile(RED, SQUARE);
        add(grid, 1, 3, DOWN, t12,t13);

        var t14 = new Tile(YELLOW, STAR);
        var t15 = new Tile(ORANGE, STAR);
        add(grid, -3, -2, LEFT, t14,t15);

        var t16 = new Tile(ORANGE, CROSS);
        var t17 = new Tile(ORANGE, DIAMOND);
        add(grid, -2, -3, DOWN, t16,t17);

        var t18 = new Tile(YELLOW, DIAMOND);
        var t19 = new Tile(YELLOW, ROUND);
        add(grid, -1, -2, DOWN, t18,t19);

        var t20 = new Tile(RED, STAR);
        add(grid, -3, 0, t20);

        var t21 = new Tile(BLUE, CROSS);
        var t22 = new Tile(RED, CROSS);
        var t23 = new Tile(ORANGE, CROSS);
        add(grid, 2, 1, LEFT, t21,t22,t23);

        var t24 = new Tile(YELLOW, SQUARE);
        var t25 = new Tile(BLUE, SQUARE);
        add(grid, 1, 4, DOWN, t24,t25);


        assertEquals(t1, get(grid, 0, 0));
        assertEquals(t2, get(grid, -1, 0));
        assertEquals(t3, get(grid, -2, 0));

        assertEquals(t4, get(grid, 1, 0));
        assertEquals(t5, get(grid, 1, 1 ));
        assertEquals(t6, get(grid, 1, 2));

        assertEquals(t7, get(grid, 0, 1));

        assertEquals(t8, get(grid, -2, -1));
        assertEquals(t9, get(grid, -1, -1));

        assertEquals(t10.tile(), get(grid, -3, -1));
        assertEquals(t11.tile(), get(grid, 0, -1));

        assertEquals(t12, get(grid, 1, 3));
        assertEquals(t13, get(grid, 2, 3));

        assertEquals(t14, get(grid, -3, -2));
        assertEquals(t15, get(grid, -3, -3));

        assertEquals(t16, get(grid, -2, -3));
        assertEquals(t17, get(grid, -1, -3));

        assertEquals(t18, get(grid, -1, -2));
        assertEquals(t19, get(grid, 0, -2));
        assertEquals(t20, get(grid, -3, 0));

        assertEquals(t21, get(grid, 2, 1));
        assertEquals(t22, get(grid, 2, 0));
        assertEquals(t23, get(grid, 2, -1));

        assertEquals(t24, get(grid, 1, 4));
        assertEquals(t25, get(grid, 2, 4));
    }

    @Test
    void testDoubloneonAligne() {
        var tile1 = new Tile(RED, SQUARE);
        var tile2 = new Tile(RED, STAR);
        var t1 = new Tile(RED, Shape.ROUND);
        var t2 = new Tile(RED, Shape.DIAMOND);
        var t3 = new Tile(RED, Shape.PLUS);
        var t4 = new Tile(RED,DIAMOND);
        try {
            grid.firstAdd(RIGHT,t1,t2,t3,tile2,tile1);
            assertThrows(QwirkleException.class, () -> grid.add(45,50, t4));
        } catch (Exception e) {
            throw e;
        }
    }
    @Test
    void testsituationAechec(){
        var t1 = new Tile(RED, Shape.ROUND);
        var t2 = new Tile(RED, Shape.DIAMOND);
        var t3 = new Tile(RED, Shape.PLUS);
        var t4 = new Tile(BLUE,PLUS);

        try{
            assertThrows(QwirkleException.class,()->grid.firstAdd(RIGHT,t1,t2,t3,t4));
        }catch (Exception e){
            throw e ;
        }

    }
    @Test
    void testsituationBechec(){
        var t1 = new Tile(RED, Shape.ROUND);
        var t2 = new Tile(RED, Shape.DIAMOND);
        var t3 = new Tile(RED, Shape.PLUS);
        //cedrick tiles
        var t4 = new Tile(RED,Shape.SQUARE) ;
        var t5 = new Tile (BLUE , Shape.SQUARE) ;
        var t6 = new Tile(PURPLE,Shape.SQUARE) ;

        try{
            assertThrows(QwirkleException.class,()->grid.add(46,45,RIGHT,t6,t5,t4));
        }catch (Exception e){
            throw e ;
        }

    }

    @Test
    void testsituationCechec(){
        var t1 = new Tile(RED, Shape.ROUND);
        var t2 = new Tile(RED, Shape.DIAMOND);
        var t3 = new Tile(RED, Shape.PLUS);
        //cedrick tiles
        var t4 = new Tile(RED,Shape.SQUARE) ;
        var t5 = new Tile (BLUE , Shape.SQUARE) ;
        var t6 = new Tile(PURPLE,Shape.SQUARE) ;
        var t7 = new Tile(BLUE,Shape.ROUND) ;

        try{
            assertThrows(QwirkleException.class,()->grid.add(44,45,t7));
        }catch (Exception e){
            throw e ;
        }
    }
    @Test
    void testsituationDechec(){
        var t1 = new Tile(RED, Shape.ROUND);
        var t2 = new Tile(RED, Shape.DIAMOND);
        var t3 = new Tile(RED, Shape.PLUS);
        //cedrick tiles
        var t4 = new Tile(RED,Shape.SQUARE) ;
        var t5 = new Tile (BLUE , Shape.SQUARE) ;
        var t6 = new Tile(PURPLE,Shape.SQUARE) ;
        // Elvire
        var t7 = new Tile(BLUE,Shape.ROUND) ;
        //Vincent
        var t8 = new Tile(GREEN,Shape.PLUS) ;
        var t9 = new Tile (GREEN,Shape.DIAMOND) ;

        try{
            assertThrows(QwirkleException.class,()->grid.add(43,44,Direction.DOWN,t9,t8));
        }catch (Exception e){
            throw e;
        }
    }

    @Test
    void situtationEechec(){
        var t1 = new Tile(RED, Shape.ROUND);
        var t2 = new Tile(RED, Shape.DIAMOND);
        var t3 = new Tile(RED, Shape.PLUS);
        //cedrick tiles
        var t4 = new Tile(RED,Shape.SQUARE) ;
        var t5 = new Tile (BLUE , Shape.SQUARE) ;
        var t6 = new Tile(PURPLE,Shape.SQUARE) ;
        // Elvire
        var t7 = new Tile(BLUE,Shape.ROUND) ;
        //Vincent
        var t8 = new Tile(GREEN,Shape.PLUS) ;
        var t9 = new Tile (GREEN,Shape.DIAMOND) ;

        // sonia situation E

        var t10 = new Tile (GREEN , Shape.ROUND) ;
        var t11 = new Tile(RED, CROSS) ;
        TileAtPosition x = new TileAtPosition(45,44,t10);
        TileAtPosition v = new TileAtPosition(42,44,t11);

        try {
            assertThrows(QwirkleException.class,()->grid.add(x,v));
        }catch (Exception e){
            throw e;
        }

    }

    @Test
    void situationFechec(){
        var t1 = new Tile(RED, Shape.ROUND);
        var t2 = new Tile(RED, Shape.DIAMOND);
        var t3 = new Tile(RED, Shape.PLUS);
        //cedrick tiles
        var t4 = new Tile(RED,Shape.SQUARE) ;
        var t5 = new Tile (BLUE , Shape.SQUARE) ;
        var t6 = new Tile(PURPLE,Shape.SQUARE) ;
        // Elvire
        var t7 = new Tile(BLUE,Shape.ROUND) ;
        //Vincent
        var t8 = new Tile(GREEN,Shape.PLUS) ;
        var t9 = new Tile (GREEN,Shape.DIAMOND) ;

        // sonia situation E
        var t10 = new Tile (GREEN , Shape.ROUND) ;
        var t11 = new Tile(GREEN,Shape.STAR) ;

        //situation F
        var t12 = new Tile(Color.RED, Shape.SQUARE);
        var t13= new Tile(Color.YELLOW, Shape.SQUARE);
        //situation G
        var t14 = new Tile(YELLOW, Shape.STAR);
        var t15 = new Tile(ORANGE, Shape.STAR);
        // Situation H
        var t16 = new Tile(Color.ORANGE, CROSS);
        var  t17= new Tile(Color.ORANGE, Shape.DIAMOND);
        // situation x
        var tile1 = new Tile(GREEN, Shape.PLUS);
        var tile2 = new Tile(GREEN, Shape.STAR);

        //situation I
        var t18 = new Tile(Color.YELLOW, Shape.DIAMOND);
        var t19 = new Tile(Color.YELLOW, Shape.ROUND);
        var x =  new Tile(YELLOW,PLUS);
        try {
            grid.firstAdd(Direction.UP, t1, t2, t3);
            grid.add(46,45,Direction.RIGHT,t4,t5,t6) ;
            grid.add(45,46,t7);
            grid.add(43,44,Direction.DOWN,t8,t9);
            grid.add(45,44,t10);
            grid.add(42,44,t11);
            //F
            assertThrows(QwirkleException.class, () -> grid.add(46,48,DOWN,t12,t13));
        }catch (Exception e){
            throw e;
        }


    }

    @Test
    void situationGechec(){
        var t1 = new Tile(RED, Shape.ROUND);
        var t2 = new Tile(RED, Shape.DIAMOND);
        var t3 = new Tile(RED, Shape.PLUS);
        //cedrick tiles
        var t4 = new Tile(RED,Shape.SQUARE) ;
        var t5 = new Tile (BLUE , Shape.SQUARE) ;
        var t6 = new Tile(PURPLE,Shape.SQUARE) ;
        // Elvire
        var t7 = new Tile(BLUE,Shape.ROUND) ;
        //Vincent
        var t8 = new Tile(GREEN,Shape.PLUS) ;
        var t9 = new Tile (GREEN,Shape.DIAMOND) ;

        // sonia situation E
        var t10 = new Tile (GREEN , Shape.ROUND) ;
        var t11 = new Tile(GREEN,Shape.STAR) ;

        //situation F
        var t12 = new Tile(Color.RED, Shape.SQUARE);
        var t13= new Tile(Color.YELLOW, Shape.SQUARE);
        //situation G
        var t14 = new Tile(YELLOW, Shape.STAR);
        var t15 = new Tile(ORANGE, Shape.STAR);
        // Situation H
        var t16 = new Tile(Color.ORANGE, CROSS);
        var  t17= new Tile(Color.ORANGE, Shape.DIAMOND);
        // situation x
        var tile1 = new Tile(GREEN, Shape.PLUS);
        var tile2 = new Tile(GREEN, Shape.STAR);

        //situation I
        var t18 = new Tile(Color.YELLOW, Shape.DIAMOND);
        var t19 = new Tile(Color.YELLOW, Shape.ROUND);
        var x =  new Tile(YELLOW,PLUS);
        try {
            grid.firstAdd(Direction.UP, t1, t2, t3);
            grid.add(46,45,Direction.RIGHT,t4,t5,t6) ;
            grid.add(45,46,t7);
            grid.add(43,44,Direction.DOWN,t8,t9);
            grid.add(45,44,t10);
            grid.add(42,44,t11);
            //F
            grid.add(46,48,Direction.DOWN,t13,t12);
            //G


            assertThrows(QwirkleException.class, () ->   grid.add(42,43, RIGHT,t15,t14));
        }catch (Exception e){
            throw e;
        }


    }

    @Test
    void situationHechec(){
        var t1 = new Tile(RED, Shape.ROUND);
        var t2 = new Tile(RED, Shape.DIAMOND);
        var t3 = new Tile(RED, Shape.PLUS);
        //cedrick tiles
        var t4 = new Tile(RED,Shape.SQUARE) ;
        var t5 = new Tile (BLUE , Shape.SQUARE) ;
        var t6 = new Tile(PURPLE,Shape.SQUARE) ;
        // Elvire
        var t7 = new Tile(BLUE,Shape.ROUND) ;
        //Vincent
        var t8 = new Tile(GREEN,Shape.PLUS) ;
        var t9 = new Tile (GREEN,Shape.DIAMOND) ;

        // sonia situation E
        var t10 = new Tile (GREEN , Shape.ROUND) ;
        var t11 = new Tile(GREEN,Shape.STAR) ;

        //situation F
        var t12 = new Tile(Color.RED, Shape.SQUARE);
        var t13= new Tile(Color.YELLOW, Shape.SQUARE);
        //situation G
        var t14 = new Tile(YELLOW, Shape.STAR);
        var t15 = new Tile(ORANGE, Shape.STAR);
        // Situation H
        var t16 = new Tile(Color.ORANGE, CROSS);
        var  t17= new Tile(Color.ORANGE, Shape.DIAMOND);
        // situation x
        var tile1 = new Tile(ORANGE, Shape.PLUS);
        var tile2 = new Tile(ORANGE, Shape.STAR);
        try {
            grid.firstAdd(Direction.UP, t1, t2, t3);
            grid.add(46,45,Direction.RIGHT,t4,t5,t6) ;
            grid.add(45,46,t7);
            grid.add(43,44,Direction.DOWN,t8,t9);
            grid.add(45,44,t10);
            grid.add(42,44,t11);
            //F
            grid.add(46,48,Direction.DOWN,t13,t12);
            //G
            grid.add(42,43,Direction.LEFT,t14,t15);
            //H
            assertThrows(QwirkleException.class, () ->grid.add(43,42,Direction.DOWN,t16,t17,tile1,tile2));

        }catch (Exception e){
            throw e ;
        }

    }
    @Test
    void situationIechec(){
        var t1 = new Tile(RED, Shape.ROUND);
        var t2 = new Tile(RED, Shape.DIAMOND);
        var t3 = new Tile(RED, Shape.PLUS);
        //cedrick tiles
        var t4 = new Tile(RED,Shape.SQUARE) ;
        var t5 = new Tile (BLUE , Shape.SQUARE) ;
        var t6 = new Tile(PURPLE,Shape.SQUARE) ;
        // Elvire
        var t7 = new Tile(BLUE,Shape.ROUND) ;
        //Vincent
        var t8 = new Tile(GREEN,Shape.PLUS) ;
        var t9 = new Tile (GREEN,Shape.DIAMOND) ;

        // sonia situation E
        var t10 = new Tile (GREEN , Shape.ROUND) ;
        var t11 = new Tile(GREEN,Shape.STAR) ;

        //situation F
        var t12 = new Tile(Color.RED, Shape.SQUARE);
        var t13= new Tile(Color.YELLOW, Shape.SQUARE);
        //situation G
        var t14 = new Tile(YELLOW, Shape.STAR);
        var t15 = new Tile(ORANGE, Shape.STAR);
        // Situation H
        var t16 = new Tile(Color.ORANGE, CROSS);
        var  t17= new Tile(Color.ORANGE, Shape.DIAMOND);
        // situation x
        var tile1 = new Tile(GREEN, Shape.PLUS);
        var tile2 = new Tile(GREEN, Shape.STAR);

        //situation I
        var t18 = new Tile(Color.YELLOW, Shape.DIAMOND);
        var t19 = new Tile(Color.YELLOW, Shape.ROUND);
        var x =  new Tile(YELLOW,PLUS);
        try {
            grid.firstAdd(Direction.UP, t1, t2, t3);
            grid.add(46,45,Direction.RIGHT,t4,t5,t6) ;
            grid.add(45,46,t7);
            grid.add(43,44,Direction.DOWN,t8,t9);
            grid.add(45,44,t10);
            grid.add(42,44,t11);
            //F
            grid.add(46,48,Direction.DOWN,t13,t12);
            //G
            grid.add(42,43,Direction.LEFT,t14,t15);
            //H
            grid.add(43,42,Direction.DOWN,t16,t17);
            //x
            // grid.add(42,41,Direction.LEFT,tile1,tile2);
            //I
            grid.add(44,43,Direction.DOWN,t18,t19);
            assertThrows(QwirkleException.class, () -> grid.add(43,43, x));
        }catch (Exception e){
            throw e;
        }




    }

    @Test
    void situationJechec(){
        var t1 = new Tile(RED, Shape.ROUND);
        var t2 = new Tile(RED, Shape.DIAMOND);
        var t3 = new Tile(RED, Shape.PLUS);
        //cedrick tiles
        var t4 = new Tile(RED,Shape.SQUARE) ;
        var t5 = new Tile (BLUE , Shape.SQUARE) ;
        var t6 = new Tile(PURPLE,Shape.SQUARE) ;
        // Elvire
        var t7 = new Tile(BLUE,Shape.ROUND) ;
        //Vincent
        var t8 = new Tile(GREEN,Shape.PLUS) ;
        var t9 = new Tile (GREEN,Shape.DIAMOND) ;

        // sonia situation E
        var t10 = new Tile (GREEN , Shape.ROUND) ;
        var t11 = new Tile(GREEN,Shape.STAR) ;

        //situation F
        var t12 = new Tile(Color.RED, Shape.SQUARE);
        var t13= new Tile(Color.YELLOW, Shape.SQUARE);
        //situation G
        var t14 = new Tile(YELLOW, Shape.STAR);
        var t15 = new Tile(ORANGE, Shape.STAR);
        // Situation H
        var t16 = new Tile(Color.ORANGE, CROSS);
        var  t17= new Tile(Color.ORANGE, Shape.DIAMOND);
        // situation x
        var tile1 = new Tile(GREEN, Shape.PLUS);
        var tile2 = new Tile(GREEN, Shape.STAR);

        //situation I
        var t18 = new Tile(Color.YELLOW, Shape.DIAMOND);
        var t19 = new Tile(Color.YELLOW, Shape.ROUND);
        //situation J
        var t20 = new Tile(Color.RED, CROSS);
        try {
            grid.firstAdd(Direction.UP, t1, t2, t3);
            grid.add(46,45,Direction.RIGHT,t4,t5,t6) ;
            grid.add(45,46,t7);
            grid.add(43,44,Direction.DOWN,t8,t9);
            grid.add(45,44,t10);
            grid.add(42,44,t11);
            //F
            grid.add(46,48,Direction.DOWN,t13,t12);
            //G
            grid.add(42,43,Direction.LEFT,t14,t15);
            //H
            grid.add(43,42,Direction.DOWN,t16,t17);
            //x
            // grid.add(42,41,Direction.LEFT,tile1,tile2);
            //I
            grid.add(44,43,Direction.DOWN,t18,t19);
            //J

            assertThrows(QwirkleException.class, () ->  grid.add(42,45,t20));
        }catch (Exception e){
            throw e;
        }

    }


    @Test
    void situationKehec(){
        var t1 = new Tile(RED, Shape.ROUND);
        var t2 = new Tile(RED, Shape.DIAMOND);
        var t3 = new Tile(RED, Shape.PLUS);
        //cedrick tiles
        var t4 = new Tile(RED,Shape.SQUARE) ;
        var t5 = new Tile (BLUE , Shape.SQUARE) ;
        var t6 = new Tile(PURPLE,Shape.SQUARE) ;
        // Elvire
        var t7 = new Tile(BLUE,Shape.ROUND) ;
        //Vincent
        var t8 = new Tile(GREEN,Shape.PLUS) ;
        var t9 = new Tile (GREEN,Shape.DIAMOND) ;

        // sonia situation E
        var t10 = new Tile (GREEN , Shape.ROUND) ;
        var t11 = new Tile(GREEN,Shape.STAR) ;

        //situation F
        var t12 = new Tile(Color.RED, Shape.SQUARE);
        var t13= new Tile(Color.YELLOW, Shape.SQUARE);
        //situation G
        var t14 = new Tile(YELLOW, Shape.STAR);
        var t15 = new Tile(ORANGE, Shape.STAR);
        // Situation H
        var t16 = new Tile(Color.ORANGE, CROSS);
        var  t17= new Tile(Color.ORANGE, Shape.DIAMOND);
        // situation x
        var tile1 = new Tile(GREEN, Shape.PLUS);
        var tile2 = new Tile(GREEN, Shape.STAR);

        //situation I
        var t18 = new Tile(Color.YELLOW, Shape.DIAMOND);
        var t19 = new Tile(Color.YELLOW, Shape.ROUND);
        //situation J
        var t20 = new Tile(Color.RED, Shape.STAR);
        //situation K
        var t21 = new Tile(BLUE, CROSS);
        var t22 = new Tile(RED, CROSS);
        var t23 = new Tile(Color.ORANGE, CROSS);
        try {
            grid.firstAdd(Direction.UP, t1, t2, t3);
            grid.add(46,45,Direction.RIGHT,t4,t5,t6) ;
            grid.add(45,46,t7);
            grid.add(43,44,Direction.DOWN,t8,t9);
            grid.add(45,44,t10);
            grid.add(42,44,t11);
            //F
            grid.add(46,48,Direction.DOWN,t13,t12);
            //G
            grid.add(42,43,Direction.LEFT,t14,t15);
            //H
            grid.add(43,42,Direction.DOWN,t16,t17);
            //x
            // grid.add(42,41,Direction.LEFT,tile1,tile2);
            //I
            grid.add(44,43,Direction.DOWN,t18,t19);
            //J
            grid.add(42,45,t20);
            //K
            assertThrows(QwirkleException.class, () ->   grid.add(47,47 , Direction.LEFT,t21,t22,t23));

        }catch (Exception e){
            throw e ;
        }
    }

    @Test
    void situationLechec(){
        var t1 = new Tile(RED, Shape.ROUND);
        var t2 = new Tile(RED, Shape.DIAMOND);
        var t3 = new Tile(RED, Shape.PLUS);
        //cedrick tiles
        var t4 = new Tile(RED,Shape.SQUARE) ;
        var t5 = new Tile (BLUE , Shape.SQUARE) ;
        var t6 = new Tile(PURPLE,Shape.SQUARE) ;
        // Elvire
        var t7 = new Tile(BLUE,Shape.ROUND) ;
        //Vincent
        var t8 = new Tile(GREEN,Shape.PLUS) ;
        var t9 = new Tile (GREEN,Shape.DIAMOND) ;

        // sonia situation E
        var t10 = new Tile (GREEN , Shape.ROUND) ;
        var t11 = new Tile(GREEN,Shape.STAR) ;

        //situation F
        var t12 = new Tile(Color.RED, Shape.SQUARE);
        var t13= new Tile(Color.YELLOW, Shape.SQUARE);
        //situation G
        var t14 = new Tile(YELLOW, Shape.STAR);
        var t15 = new Tile(ORANGE, Shape.STAR);
        // Situation H
        var t16 = new Tile(Color.ORANGE, CROSS);
        var  t17= new Tile(Color.ORANGE, Shape.DIAMOND);
        // situation x
        var tile1 = new Tile(GREEN, Shape.PLUS);
        var tile2 = new Tile(GREEN, Shape.STAR);

        //situation I
        var t18 = new Tile(Color.YELLOW, Shape.DIAMOND);
        var t19 = new Tile(Color.YELLOW, Shape.ROUND);
        //situation J
        var t20 = new Tile(Color.RED, Shape.STAR);
        //situation K
        var t21 = new Tile(BLUE, CROSS);
        var t22 = new Tile(RED, CROSS);
        var t23 = new Tile(Color.ORANGE, CROSS);
        try {
            grid.firstAdd(Direction.UP, t1, t2, t3);
            grid.add(46,45,Direction.RIGHT,t4,t5,t6) ;
            grid.add(45,46,t7);
            grid.add(43,44,Direction.DOWN,t8,t9);
            grid.add(45,44,t10);
            grid.add(42,44,t11);
            //F
            grid.add(46,48,Direction.DOWN,t13,t12);
            //G
            grid.add(42,43,Direction.LEFT,t14,t15);
            //H
            grid.add(43,42,Direction.DOWN,t16,t17);
            //x
            // grid.add(42,41,Direction.LEFT,tile1,tile2);
            //I
            grid.add(44,43,Direction.DOWN,t18,t19);
            //J
            grid.add(42,45,t20);
            //K
            grid.add(47,46 , Direction.LEFT,t21,t22,t23);
            //situation L
            var t24 = new Tile(Color.ORANGE, Shape.SQUARE);
            var t25 = new Tile(Color.BLUE, Shape.SQUARE);

            assertThrows(QwirkleException.class, () ->grid.add(46,49,Direction.DOWN,t25,t24) );

        }catch (Exception e){
            throw e ;
        }

    }

    public static final int INITIAL_ROW = 45;
    public static final int INITIAL_COL = 45;

    @Test
    void gridInitiallyEmpty() {
        var g = new Grid();
        for (int row = -45; row < 45; row++) {
            for (int col = -45; col < 45; col++) {
                assertNull(get(g, row, col));
            }
        }
    }

    @Test
    @DisplayName("get outside the grid should return null, not throw")
    void canGetOutsideVirtualGrid() {
        var g = new Grid();
        assertDoesNotThrow(() -> get(g, -250, 500));
        assertNull(get(g, -250, 500));
    }

    // simple adds

    @Test
    void addSimpleUP() {
        var g = new Grid();
        g.firstAdd(UP, TILE_RED_CROSS, TILE_RED_DIAMOND);
        assertSame(TILE_RED_CROSS, get(g, 0, 0));
        assertSame(TILE_RED_DIAMOND, get(g, -1, 0));
        assertNull(get(g, 1, 0));
        assertNull(get(g, 0, 1));
        assertNull(get(g, 0, -1));
    }

    @Test
    void addSimpleDOWN() {
        var g = new Grid();
        g.firstAdd(DOWN, TILE_RED_CROSS, TILE_RED_DIAMOND);
        assertSame(TILE_RED_CROSS, get(g, 0, 0));
        assertSame(TILE_RED_DIAMOND, get(g, 1, 0));
        assertNull(get(g, -1, 0));
        assertNull(get(g, 0, 1));
        assertNull(get(g, 0, -1));
    }

    @Test
    void addSimpleLEFT() {
        var g = new Grid();
        g.firstAdd(LEFT, TILE_RED_CROSS, TILE_RED_DIAMOND);
        assertSame(TILE_RED_CROSS, get(g, 0, 0));
        assertSame(TILE_RED_DIAMOND, get(g, 0, -1));
        assertNull(get(g, 1, 0));
        assertNull(get(g, -1, 0));
        assertNull(get(g, 0, 1));
    }

    @Test
    void addSimpleRIGHT() {
        var g = new Grid();
        g.firstAdd(RIGHT, TILE_RED_CROSS, TILE_RED_DIAMOND);
        assertSame(TILE_RED_CROSS, get(g, 0, 0));
        assertSame(TILE_RED_DIAMOND, get(g, 0, 1));
        assertNull(get(g, 1, 0));
        assertNull(get(g, -1, 0));
        assertNull(get(g, 0, -1));
    }

    @Test
    void addSimpleDoubleShouldThrow() {
        var g = new Grid();
        for (Direction d : Direction.values()) {
            assertThrows(QwirkleException.class, () -> g.firstAdd(d, TILE_RED_CROSS, TILE_RED_CROSS));
            assertNull(get(g, 0, 0));
            assertNull(get(g, -1, 0));
            assertNull(get(g, 1, 0));
            assertNull(get(g, 0, -1));
            assertNull(get(g, 0, 1));
        }

    }

    // firstAdd must be called first

    @Test
    void addFirstCannotBeCalledTwice() {
        var g = new Grid();
        g.firstAdd(UP, TILE_RED_CROSS, TILE_RED_DIAMOND);
        assertThrows(QwirkleException.class, () -> g.firstAdd(DOWN, TILE_RED_CROSS, TILE_RED_DIAMOND));
    }

    @Test
    void addFirstMustBeCalledFirst_dir() {
        var g = new Grid();
        assertThrows(QwirkleException.class, () -> add(g, 0, 0, DOWN, TILE_RED_CROSS, TILE_RED_DIAMOND));
    }

    @Test
    void addFirstMustBeCalledFirst_tap() {
        var g = new Grid();
        assertThrows(QwirkleException.class, () -> g.add(createTileAtpos(0, 0, TILE_RED_CROSS)));
    }

    @Test
    void addFirstMustBeCalledFirst_simple() {
        var g = new Grid();
        assertThrows(QwirkleException.class, () -> add(g, 0, 0, TILE_RED_CROSS));
    }

    // neighbours

    @Test
    void aTileMustHaveNeighbours() {
        var g = new Grid();
        g.firstAdd(UP, TILE_RED_CROSS);
        assertThrows(QwirkleException.class, () -> add(g, 2, 0, TILE_RED_DIAMOND));
        assertNull(get(g, 2, 0));
    }


    // overwriting

    @Test
    void canNotAddTwiceAtTheSamePlace_equalTile() {
        var g = new Grid();
        g.firstAdd(DOWN, TILE_RED_CROSS, TILE_RED_DIAMOND);
        assertThrows(QwirkleException.class, () -> add(g, 1, 0, TILE_RED_DIAMOND_2));
        assertSame(get(g, 1, 0), TILE_RED_DIAMOND);
    }

    @Test
    void canNotAddTwiceAtTheSamePlace_differentTile_simple() {
        var g = new Grid();
        g.firstAdd(DOWN, TILE_RED_CROSS, TILE_RED_DIAMOND);
        assertThrows(QwirkleException.class, () -> add(g, 1, 0, TILE_RED_PLUS));
        assertSame(get(g, 1, 0), TILE_RED_DIAMOND);
    }

    @Test
    void canNotAddTwiceAtTheSamePlace_differentTile_dir() {
        var g = new Grid();
        g.firstAdd(DOWN, TILE_RED_CROSS, TILE_RED_DIAMOND);
        assertThrows(QwirkleException.class, () -> add(g, 0, 0, DOWN, TILE_RED_PLUS, TILE_RED_STAR));
        assertSame(get(g, 0, 0), TILE_RED_CROSS);
        assertSame(get(g, 1, 0), TILE_RED_DIAMOND);
    }

    @Test
    void canNotAddTwiceAtTheSamePlace_differentTile_taps() {
        var g = new Grid();
        g.firstAdd(DOWN, TILE_RED_CROSS, TILE_RED_DIAMOND);
        TileAtPosition tap1 = createTileAtpos(0, 0, TILE_RED_PLUS);
        TileAtPosition tap2 = createTileAtpos(1, 0, TILE_RED_STAR);
        assertThrows(QwirkleException.class, () -> g.add(tap1, tap2));
        assertSame(TILE_RED_CROSS, get(g, 0, 0));
        assertSame(TILE_RED_DIAMOND, get(g, 1, 0));
    }


    // alignment
    @Test
    void canNotAddInDifferentLines() {
        var g = new Grid();
        g.firstAdd(UP, TILE_RED_CROSS);
        var tap1 = createTileAtpos(0, 1, TILE_RED_DIAMOND);
        var tap2 = createTileAtpos(1, 0, TILE_RED_STAR);
        assertThrows(QwirkleException.class, () -> g.add(tap1, tap2));
        assertSame(TILE_RED_CROSS, get(g, 0, 0));
        assertNull(get(g, 0, 1));
        assertNull(get(g, 1, 0));
    }

    // must share common trait
    @Test
    void canNotAddIfNoCommonTrait_tap() {
        var g = new Grid();
        g.firstAdd(UP, TILE_RED_CROSS);
        var tap1 = createTileAtpos(0, 1, TILE_YELLOW_DIAMOND);
        assertThrows(QwirkleException.class, () -> g.add(tap1));
        assertSame(TILE_RED_CROSS, get(g, 0, 0));
        assertNull(get(g, 0, 1));
        assertNull(get(g, 1, 0));
    }

    @Test
    void canNotAddIfNoCommonTrait_simple() {
        var g = new Grid();
        g.firstAdd(UP, TILE_RED_CROSS);
        assertThrows(QwirkleException.class, () -> add(g, 0, 1, TILE_YELLOW_DIAMOND));
        assertSame(TILE_RED_CROSS, get(g, 0, 0));
        assertNull(get(g, 0, 1));
        assertNull(get(g, 1, 0));
    }

    @Test
    void canNotAddIfNoCommonTrait_dir() {
        var g = new Grid();
        g.firstAdd(UP, TILE_RED_CROSS);
        assertThrows(QwirkleException.class, () -> add(g, 0, 1, LEFT, TILE_YELLOW_DIAMOND));
        assertSame(TILE_RED_CROSS, get(g, 0, 0));
        assertNull(get(g, 0, 1));
        assertNull(get(g, 1, 0));
    }

    @Test
    void canNotCompleteToALineWithDifferentTraits_simple() {
        var g = new Grid();
        g.firstAdd(RIGHT, TILE_RED_CROSS, TILE_RED_STAR, TILE_RED_DIAMOND);

        add(g, 1, 0, TILE_RED_DIAMOND_2);
        add(g, 2, 0, TILE_RED_PLUS);

        add(g, 1, 2, TILE_GREEN_DIAMOND);
        add(g, 2, 2, TILE_YELLOW_DIAMOND);

        // the "hole" in 2, 1 can never be filled because 2, 0 and 2, 2 share no trait
        for (var color : Color.values()) {
            for (var shape : Shape.values()) {
                assertThrows(QwirkleException.class, () -> add(g, 2, 1, new Tile(color, shape)));
                assertNull(get(g, 2, 1));
            }
        }
    }

    @Test
    void canNotCompleteToALineWithDifferentTraits_dir() {
        var g = new Grid();
        g.firstAdd(RIGHT, TILE_RED_CROSS, TILE_RED_STAR, TILE_RED_DIAMOND);

        add(g, 1, 0, TILE_RED_DIAMOND_2);
        add(g, 2, 0, TILE_RED_PLUS);

        add(g, 1, 2, TILE_GREEN_DIAMOND);
        add(g, 2, 2, TILE_YELLOW_DIAMOND);
        View.displayGrid(new GridView(g));

        // the "hole" in 2, 1 can never be filled because 2, 0 and 2, 2 share no trait
        for (var color : Color.values()) {
            for (var shape : Shape.values()) {
                for (Direction dir : Direction.values()) {
                    assertThrows(QwirkleException.class, () -> add(g, 2, 1, dir, new Tile(color, shape)));
                    assertNull(get(g, 2, 1));
                }
            }
        }
    }

    @Test
    void canNotCompleteToALineWithDifferentTraits_tap() {
        var g = new Grid();
        g.firstAdd(RIGHT, TILE_RED_CROSS, TILE_RED_STAR, TILE_RED_DIAMOND);

        add(g, 1, 0, TILE_RED_DIAMOND_2);
        add(g, 2, 0, TILE_RED_PLUS);

        add(g, 1, 2, TILE_GREEN_DIAMOND);
        add(g, 2, 2, TILE_YELLOW_DIAMOND);

        // the "hole" in 2, 1 can never be filled because 2, 0 and 2, 2 share no trait
        for (var color : Color.values()) {
            for (var shape : Shape.values()) {
                assertThrows(QwirkleException.class, () -> g.add(createTileAtpos(2, 1, new Tile(color, shape))));
                assertNull(get(g, 2, 1));
            }
        }
    }

    // never identical
    @Test
    void canNotCompleteToALineWithIdenticalTiles_simple() {
        var g = new Grid();
        g.firstAdd(RIGHT, TILE_RED_CROSS, TILE_RED_STAR, TILE_RED_DIAMOND);

        add(g, 1, 0, TILE_RED_SQUARE);
        add(g, 2, 0, TILE_RED_PLUS);

        add(g, 1, 2, TILE_RED_ROUND);
        add(g, 2, 2, TILE_RED_PLUS_2);
        View.displayGrid(new GridView(g));

        // the "hole" in 2, 1 can never be filled because 2, 0 and 2, 2 are identical
        for (var color : Color.values()) {
            for (var shape : Shape.values()) {
                assertThrows(QwirkleException.class, () -> add(g, 2, 1, new Tile(color, shape)));
                assertNull(get(g, 2, 1));
            }
        }
    }

    @Test
    void canNotCompleteToALineWithIdenticalTiles_tap() {
        var g = new Grid();
        g.firstAdd(RIGHT, TILE_RED_CROSS, TILE_RED_STAR, TILE_RED_DIAMOND);

        add(g, 1, 0, TILE_RED_SQUARE);
        add(g, 2, 0, TILE_RED_PLUS);

        add(g, 1, 2, TILE_RED_ROUND);
        add(g, 2, 2, TILE_RED_PLUS_2);

        // the "hole" in 2, 1 can never be filled because 2, 0 and 2, 2 are identical
        for (var color : Color.values()) {
            for (var shape : Shape.values()) {
                assertThrows(QwirkleException.class, () -> g.add(createTileAtpos(2, 1, new Tile(color, shape))));
                assertNull(get(g, 2, 1));
            }
        }
    }

    @Test
    void canNotCompleteToALineWithIdenticalTiles_dir() {
        var g = new Grid();
        g.firstAdd(RIGHT, TILE_RED_CROSS, TILE_RED_STAR, TILE_RED_DIAMOND);

        add(g, 1, 0, TILE_RED_SQUARE);
        add(g, 2, 0, TILE_RED_PLUS);

        add(g, 1, 2, TILE_RED_ROUND);
        add(g, 2, 2, TILE_RED_PLUS_2);

        // the "hole" in 2, 1 can never be filled because 2, 0 and 2, 2 are identical
        for (var color : Color.values()) {
            for (var shape : Shape.values()) {
                // there is only one tile but let's try to add it in all directions anyway
                for (Direction direction : Direction.values()) {
                    assertThrows(QwirkleException.class, () -> add(g, 2, 1, direction, new Tile(color, shape)));
                    assertNull(get(g, 2, 1));
                }
            }
        }
    }

    // various other tests, pertaining to filling existing holes
    @Test
    void canCompleteToALineLeftRight() {
        var g = new Grid();
        g.firstAdd(RIGHT, TILE_RED_CROSS, TILE_RED_STAR, TILE_RED_DIAMOND);

        add(g, 1, 0, TILE_GREEN_CROSS);
        add(g, 2, 0, TILE_YELLOW_CROSS);

        add(g, 1, 2, TILE_GREEN_DIAMOND);
        add(g, 2, 2, TILE_YELLOW_DIAMOND);

        add(g, 2, 1, TILE_YELLOW_PLUS);
        assertSame(TILE_YELLOW_PLUS, get(g, 2, 1));

    }

    @Test
    void canCompleteToALineLeftRightUpDown() {
        var g = new Grid();
        g.firstAdd(RIGHT, TILE_RED_CROSS, TILE_RED_PLUS, TILE_RED_DIAMOND);

        add(g, 1, 0, TILE_GREEN_CROSS);
        add(g, 2, 0, TILE_YELLOW_CROSS);

        add(g, 1, 2, TILE_GREEN_DIAMOND);
        add(g, 2, 2, TILE_YELLOW_DIAMOND);

        add(g, 2, 1, TILE_YELLOW_PLUS);
        add(g, 1, 1, TILE_GREEN_PLUS);
        assertSame(TILE_GREEN_PLUS, get(g, 1, 1));
    }

    @Test
    @DisplayName("Complete a line leaving holes during intermediary steps")
    void canCompleteALine_Left_Middle_Right() {
        var g = new Grid();
        g.firstAdd(RIGHT, TILE_RED_CROSS, TILE_RED_PLUS, TILE_RED_DIAMOND);

        add(g, 1, 0, TILE_GREEN_CROSS);
        add(g, 2, 0, TILE_YELLOW_CROSS);

        add(g, 1, 2, TILE_GREEN_DIAMOND);
        add(g, 2, 2, TILE_YELLOW_DIAMOND);

        TileAtPosition plus_left = createTileAtpos(2, -1, TILE_YELLOW_PLUS);
        TileAtPosition round_center = createTileAtpos(2, 1, TILE_YELLOW_ROUND);
        TileAtPosition star_right = createTileAtpos(2, 3, TILE_YELLOW_STAR);
        assertDoesNotThrow(() -> {
            g.add(plus_left, star_right, round_center); // make sur having the center tile last does not throw.
        });
        assertAtCorrectPosition(g, plus_left);
        assertAtCorrectPosition(g, round_center);
        assertAtCorrectPosition(g, star_right);
    }

    @Test
    @DisplayName("Complete a line leaving holes during intermediary steps")
    void canCompleteALine_Left2_Left() {
        var g = new Grid();
        g.firstAdd(RIGHT, TILE_RED_CROSS, TILE_RED_PLUS, TILE_RED_DIAMOND);

        add(g, 1, 0, TILE_GREEN_CROSS);
        add(g, 2, 0, TILE_YELLOW_CROSS);

        add(g, 1, 2, TILE_GREEN_DIAMOND);
        add(g, 2, 2, TILE_YELLOW_DIAMOND);

        TileAtPosition plus_left_left = createTileAtpos(2, -2, TILE_YELLOW_PLUS);
        TileAtPosition round_left = createTileAtpos(2, -1, TILE_YELLOW_ROUND);
        View.displayGrid(new GridView(g));
        assertDoesNotThrow(() -> {
            g.add(plus_left_left, round_left); // make sur having the "left" tile after the "left left" tile does not throw
        });
        assertAtCorrectPosition(g, plus_left_left);
        assertAtCorrectPosition(g, round_left);
    }


    @Test
    void canNotCompleteALine_leaving_a_hole() {
        var g = new Grid();
        g.firstAdd(RIGHT, TILE_RED_CROSS, TILE_RED_PLUS, TILE_RED_DIAMOND);

        add(g, 1, 0, TILE_GREEN_CROSS);
        add(g, 2, 0, TILE_YELLOW_CROSS);

        add(g, 1, 2, TILE_GREEN_DIAMOND);
        add(g, 2, 2, TILE_YELLOW_DIAMOND);

        TileAtPosition plus_left = createTileAtpos(2, -1, TILE_YELLOW_PLUS);
        TileAtPosition star_right = createTileAtpos(2, 3, TILE_YELLOW_STAR);
        View.displayGrid(new GridView(g));
        assertThrows(QwirkleException.class, () -> {
            g.add(plus_left, star_right);
        });
        assertNull(get(g, 2, -1));
        assertNull(get(g, 2, 3));
    }

    // private methods

    private int add(Grid g, int row, int col, Tile tile) {
        return g.add(INITIAL_ROW + row, INITIAL_COL + col, tile);
    }

    private int add(Grid g, int row, int col, Direction d, Tile... line) {
        return g.add(INITIAL_ROW + row, INITIAL_COL + col, d, line);

    }

    private Tile get(Grid g, int row, int col) {
        return g.get(INITIAL_ROW + row, INITIAL_COL + col);
    }

    private TileAtPosition createTileAtpos(int row, int col, Tile tile) {
        return new TileAtPosition(INITIAL_ROW + row, INITIAL_COL + col, tile);
    }


    private void assertAtCorrectPosition(Grid g, TileAtPosition tileAtPosition) {
        int row = tileAtPosition.row();
        int col = tileAtPosition.col();
        assertSame(tileAtPosition.tile(), g.get(row, col));
    }

}
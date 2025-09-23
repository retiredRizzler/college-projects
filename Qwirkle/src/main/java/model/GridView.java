package model;

import view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the view of our board
 */
public class GridView {
    private Grid grid;
    public GridView(Grid grid)
    {
        this.grid = grid;
    }
    Tile get(int row, int col)
    {
        return grid.get(row, col);
    }
    boolean isEmpty()
    {
        return grid.isEmpty();
    }

    public void displayGrid(GridView grid)
    {
        if (isEmpty()) {
            return  ;
        }
        List<Integer> lR = listRow();
        List<Integer> lC = listCol();
        int beginRow = Collections.min(lR);
        int beginCol = Collections.min(lC);
        int lastRow = Collections.max(lR);
        int lastCol = Collections.max(lC);

        for (int r = beginRow; r <= lastRow; r++) {
            System.out.println();
            System.out.print( r + " |");
            for (int c = beginCol; c <= lastCol; c++) {
                if (get(r, c) != null) {
                    System.out.print(grid.get(r, c) + "  ");
                } else {
                    System.out.print("    ");
                }
            }
        }
        System.out.println();
        System.out.println();

        System.out.print("    ");
        for (int c = beginCol; c <= lastCol; c++) {
                System.out.print(c + "  ");
        }
        System.out.println();
    }

    /**
     *
     * @return List of integer which represents all the rows occupied by at least one tile
     */
    private List<Integer> listRow()
    {
        List<Integer> lR = new ArrayList();
        for (int r = 0; r < 91; r++) {
            for (int c = 0; c < 91; c++) {
                if (get(r, c) != null) {
                        lR.add(r);
                }
            }
        }
        return lR;
    }

    /**
     *
     * @return List of integer which represents all the column occupied by at least one tile
     */
    private List<Integer> listCol()
    {
        List<Integer> lC = new ArrayList();
        for (int r = 0; r < 91; r++) {
            for (int c = 0; c < 91; c++) {
                if (get(r, c) != null) {
                        lC.add(c);

                }
            }
        }
        return lC;
    }
}

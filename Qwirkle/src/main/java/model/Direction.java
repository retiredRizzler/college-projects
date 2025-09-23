package model;

/**
 * Direction represents the direction we chose to put our tiles on the game board
 */
public enum Direction {
    LEFT(0,-1), RIGHT(0,1), UP(-1,0), DOWN(1,0);

    private int deltaRow;
    public int getDeltaRow() {return deltaRow;}

    private int deltaCol;
    public int getDeltaCol() {return deltaCol;}

    Direction(int row, int col)
    {
        this.deltaRow = row;
        this.deltaCol = col;
    }

    /**
     * Return the opposite from the current direction
     */
    public Direction opposite()
    {
        if (this == Direction.UP) return Direction.DOWN;
        if (this == Direction.DOWN) return Direction.UP;
        if (this == Direction.LEFT) return Direction.RIGHT;
        return Direction.LEFT;
    }
}

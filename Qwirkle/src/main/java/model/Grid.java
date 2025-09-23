package model;

import view.View;

import java.io.Serializable;
import java.util.List;

/**
 * model.Grid represents our game board
 */
public class Grid implements Serializable {
    private final Tile[][] tiles;
    private boolean isEmpty;

    public Grid()
    {
        this.isEmpty = true;
        this.tiles = new Tile[91][91];
    }

    /**
     * Return the tile of a specific position on the board
     */
    public Tile get(int row, int col)
    {
       if (!isPositionValid(row, col)) {
           return null;
       }
       return tiles[row][col];
    }

    /**
     * This method returns isEmpty attribute
     */
    public boolean isEmpty()
    {
         return isEmpty;
    }

    /**
     * Method is used to make the first add by respecting the rules
     * @param d the direction we chose to put our tiles
     * @param line the tile(s) we want to add to the grid
     */
    public int firstAdd(Direction d, Tile... line)
    {
        if (!isEmpty()) {
            throw new QwirkleException("The grid is not empty");
        }

        if (!tilesMatchEachOther(line)) {
            throw new QwirkleException("Tiles you pick from your hand don't match : either not the same shape " +
                    "or not the same color, or you picked two times the same tile or more. ");
        }

        int row = tiles.length/2; // 45
        int col = tiles[0].length/2; // 45
        tiles[row][col] = line[0];
        int nbPoint = 1;
        isEmpty = false;

        for (int i = 1; i<line.length; i++) {
                tiles[row + d.getDeltaRow()] [col + d.getDeltaCol()] = line[i];
                nbPoint++;
                row += d.getDeltaRow();
                col += d.getDeltaCol();
            }
        return nbPoint == 6 ? 12 : nbPoint ;
    }

    /**
     * Adding a tile to a specific position on the grid
     * @param row the row of the grid
     * @param col the column of the grid
     * @param tile the tile you want to add
     */
    public int add(int row, int col, Tile tile)
    {
        if (areRulesValid(row, col, tile)) {
            tiles[row][col] = tile;
        }
        return score(row, col);
    }

    /**
     * Adding tile(s) to a specific position in a specific direction on the grid
     * @param row the row of the grid
     * @param col the column of the grid
     * @param d the direction
     * @param line the tile(s) you want to add
     */
    public int add(int row, int col, Direction d, Tile... line)
    {
        // Check first if the tiles we want to add match each other
        if (!tilesMatchEachOther(line)) {
                throw new QwirkleException("Tiles you picked from your hand don't match : either not the same shape " +
                        "or not the same color or you picked two times the same tile or more. ");
        }

        int fRow = row, rRow = row, sRow = row, nRow = row;
        int fCol = col, rCol = col, sCol = col, nCol = col;
        int score= 0;

        try {
            for (int i = 0; i < line.length; i++) {
                areRulesValid(row, col, line[i]);
                tiles[row][col] = line[i];
                row += d.getDeltaRow();
                col += d.getDeltaCol();
                }
            } catch (QwirkleException e) {
            // Remove all the tiles added in this move if one tile doesn't respect rules.
            for (int i = 0; i < line.length; i++) {
                if (get(fRow, fCol) == null) {
                    for (int j = 0; j < line.length; j++) {
                        tiles[rRow][rCol] = null;
                        rRow += d.getDeltaRow();
                        rCol += d.getDeltaCol();
                    }
                }
                fRow += d.getDeltaRow();
                fCol += d.getDeltaCol();
            }
            throw new QwirkleException(e.getMessage());
        }

        score += score(nRow, nCol);
        for (int i = 0; i < line.length; i++) {
            //Checking if a tile have the same row or same column of the first tile, so we don't count several times
            //the same line in the score.
            if (nRow == sRow && nCol != sCol) {
                score += getColScore(nRow, nCol);
            }
            if (nCol == sCol && nRow != sRow) {
                score += getRowScore(nRow, nCol);
            }
            nRow += d.getDeltaRow();
            nCol += d.getDeltaCol();
        }
        return score;
    }

    public int add(TileAtPosition... line)
    {
        int score = 0;
        // There is a specific rule about that if we had a tile, and we want to add a second tile or more,
        // this/these tile(s) must be on the same line (same row or same column) as the first tile we add.
        if (!areTilesOnSameLine(line)) {
           throw new QwirkleException("Each tile you want to add must be on the same line. ");
        }
        try {
            for (TileAtPosition t : line) {
                add(t.row(), t.col(), t.tile());
            }
        } catch (QwirkleException e) {
            // Remove all the tiles added in this move if one tile doesn't respect rules.
            for (TileAtPosition t : line) {
                if (get(t.row(), t.col()) == null) {
                    for (TileAtPosition tap : line) {
                        tiles[tap.row()][tap.col()] = null;
                    }
                }
            }
            throw new QwirkleException(e.getMessage());
        }

        int iRow = line[0].row();
        int iCol = line[0].col();
        score += score(iRow, iCol);
        for (int i = 0; i<line.length; i++){
            int nRow = line[i].row();
            int nCol = line[i].col();
            if (iRow == nRow && iCol != nCol) {
                score += getColScore(nRow, nCol);
            }
            if (iCol == nCol && iRow != nRow) {
                score += getRowScore(nRow, nCol);
            }
        }
        return score;
    }

    /**
     * Check if there is at least two same tiles on the same row or column given in argument.
     * @param row
     * @param col
     * @return true if a same tile as the tile in argument is found.
     */
    private boolean sameTilesOnSameLine(int row, int col)
    {
            if (tiles[row][col + 1] != null && tiles[row][col - 1] != null) {

                for (int j = 0; j < tiles[0].length; j++) {
                    if(tiles[row][j] != null) {
                        Tile rowTile = tiles[row][j];
                        // Check for same tile in the same row
                        int countRow = 0;
                        for (int c = 0; c < tiles[0].length; c++) {
                            if(tiles[row][c] != null) {
                                if (!eitherSameShapeOrSameColor(tiles[row][c], rowTile)) {
                                    countRow++;
                                    if (countRow >= 2) {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (tiles[row + 1][col] != null && tiles[row - 1][col] != null) {
                for (int i = 0; i < tiles.length; i++) {
                    if (tiles[i][col] != null) {
                        Tile colTile = tiles[i][col];
                        // Check for same tile in the same column
                        int countCol = 0;
                        for (int r = 0; r < tiles.length; r++) {
                            if (tiles[r][col] != null) {
                                if (eitherSameShapeOrSameColor(tiles[r][col], colTile)) {
                                    countCol++;
                                    if (countCol >= 2) {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        return false;
    }

    /**
     * Calculate the total score from a given position
     * @param row the initial row
     * @param col the initial column
     * @return int, the score number
     */
    private int score(int row, int col)
    {
        int rowScore = getRowScore(row, col);
        int colScore = getColScore(row, col);

        return colScore == 0 && rowScore == 0 ? 1 : rowScore + colScore;
    }

    /**
     * Calculate the tile score from a raw in the grid from a position given in parameter (from left to right).
     * @param row
     * @param col
     * @return int the total of points
     */
    private int getRowScore(int row, int col)
    {
        Direction[] dir = {Direction.LEFT, Direction.RIGHT};
        int rowScore = 1;
        int total = 0;
        for (Direction d : dir) {
            int nRow = row + d.getDeltaRow();
            int nCol = col + d.getDeltaCol();
            if (tiles[nRow][nCol] != null) {
                while (isPositionValid(nRow, nCol)) {
                    if (tiles[nRow][nCol] == null) {break;}
                    rowScore++;
                    // If after going on one direction there is already six tiles, it means the player put six tiles
                    // in a raw (Qwirkle) so no need to check the opposite direction.
                    if (rowScore == 6) {
                        return 12;
                    }
                    nRow += d.getDeltaRow();
                    nCol += d.getDeltaCol();
                }
                total += rowScore;
                rowScore = 0;
            }
        }
        // If there is 6 tiles in a raw it is a Qwirkle so the player scores 12 points
        return total == 6 ? 12 : total;
    }

    /**
     * Calculate the score from a column on the grid from a position given in parameter (from up to down).
     * @param row
     * @param col
     * @return int the total score
     */
    private int getColScore(int row, int col)
    {
        Direction[] dir = {Direction.UP, Direction.DOWN};
        int colScore = 1;
        int total = 0;
        for (Direction d : dir) {
            int nRow = row + d.getDeltaRow();
            int nCol = col + d.getDeltaCol();
            if (tiles[nRow][nCol] != null) {
                while (isPositionValid(nRow, nCol)) {
                    if (tiles[nRow][nCol] == null) {break;}
                    colScore++;
                    // If after going on one direction there is already six tiles, it means the player put six tiles
                    // in a raw (Qwirkle) so no need to check the opposite direction.
                    if (colScore == 6) {
                        return 12;
                    }
                    nRow += d.getDeltaRow();
                    nCol += d.getDeltaCol();
                }
                total += colScore;
                colScore = 0;
            }
        }
        // If there is 6 tiles in a raw it is a Qwirkle so the player scores 12 points
        return total == 6 ? 12 : total;
    }

    /**
     * Check if all the tiles of a varargs have the same row or the same column
     * @param line our tile(s)
     * @return true if all the tiles have the same row or the same column
     */
    private boolean areTilesOnSameLine(TileAtPosition... line)
    {
        for ( int k = 1; k<line.length; k++) {
            if (!(line[0].row() == line[k].row()) && !(line[0].col() == line[k].col())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if tiles match each other in a Tile varargs
     * @param line the tile(s) we want to check
     * @return false if one tile doesn't match
     */
    private boolean tilesMatchEachOther(Tile... line)
    {
        for (int k = 1; k<line.length; k++) {
            if (!eitherSameShapeOrSameColor(line[0], line[k])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if the rules when you want to add a tile to a specific positions are valid
     * @param row row of the grid
     * @param col column of the grid
     * @param tile the tile you want to check if it's valid
     * @return true if all the rules are respected
     */
    private boolean areRulesValid(int row, int col, Tile tile)
    {
        if(!isPositionValid(row, col)) {
            throw  new QwirkleException("The position is not on the grid");
        }

        if (tiles[row][col] != null) {
            throw new QwirkleException("The position is already occupied by a tile");
        }

        if(sameTilesOnSameLine(row, col)) {
            tiles[row][col] = null;
            throw new QwirkleException("There are 2 times the same tile (or more) on the same line. ");
        }

        if(arePositionsAroundFree(row, col)){
            throw new QwirkleException("The positions around the tile aren't occupied by at least one tile");
        }

        Direction[] dir = Direction.values();
        for (Direction d : dir) {

            int nRow = row + d.getDeltaRow(); // next row
            int nCol = col + d.getDeltaCol(); // next col
            Tile nTile = tiles[nRow][nCol]; // next position

            while (isPositionValid(nRow, nCol)) {
                // If there is no tile at next position, break the loop and pass to next direction
                if (nTile == null)
                    break;

                if (!eitherSameShapeOrSameColor(tile, nTile)) {
                    throw new QwirkleException("Tiles don't match : either not the same shape or not the same color, " +
                            "or you chose at least two times the same tile. ");
                }


                nRow += d.getDeltaRow();
                nCol += d.getDeltaCol();
                nTile = tiles[nRow][nCol];
            }
        }
            return true;
    }

    /**
     * Check if the positions around the tile we want to add are free; useful to check if it is valid to add a tile
     * at a specific position on the grid.
     * @return false if there is at least one tile around
     */
    private boolean arePositionsAroundFree(int row, int col)
    {
        Direction[] dir = Direction.values();

        for (Direction d : dir) {
            if (!isPositionValid(row + d.getDeltaRow(), col + d.getDeltaCol())) {
                continue;
            }
            // If at least one position around is occupied by a tile
            if (tiles[row + d.getDeltaRow()] [col + d.getDeltaCol()] != null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if the position entered in parameters is on the grid
     * @return true if the position exists on the grid
     */
    private boolean isPositionValid (int row, int col)
    {
        return row >= 0 && row < tiles.length
                &&
                col >= 0 && col < tiles.length;
    }

    /**
     * Return true either if tiles have the same shape or the same color but not both
     */
    private boolean eitherSameShapeOrSameColor(Tile tile0, Tile tile1)
    {
        return tile0.shape().equals(tile1.shape())
                ^
                ((tile0.color().equals(tile1.color())
                        || ((tile1.color() == Color.GREEN) && !tile0.shape().equals(tile1.shape()))));

    }

    /**
     * Protected method to use in Game class for isOver method in order to know if a player can't play tiles anymore
     * @param hand player's hand
     * @return true if there is one possible move which the player can make.
     */
    boolean isPossibleMove(List<Tile> hand)
    {
        if (hand.isEmpty()) {
            return false;
        }

        for (int i = 0; i< tiles.length; i++) {
            for (int j = 0; j< tiles[0].length; j++) {
                if (arePositionsAroundFree(i, j)) {continue;}
                for (Tile t : hand) {
                    try {
                        areRulesValid(i, j, t);
                        return true;
                    } catch (QwirkleException e) {}
                }
            }
        }
        return false;
    }

}


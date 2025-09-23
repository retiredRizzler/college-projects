package model;

import java.io.Serializable;

public record TileAtPosition(int row, int col, Tile tile) implements Serializable {}

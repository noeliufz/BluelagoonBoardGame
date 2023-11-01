package BlueLagoonGame.board;

import BlueLagoonGame.piece.Piece;

/**
 * This class represents the tiles on board, including three types:
 * sea tiles, stone tiles (to distribute resources and statuettes), land tiles.
 */

public class Tile {
    // all the tiles should be one of the types
    public enum TileType {
        SEA_TILE,
        STONE_TILE,
        LAND_TILE;
    }

    private boolean isOccupied;
    private Piece occupier;
    private final TileType type;

    public Tile(TileType type) {
        this.type = type;
        this.isOccupied = false;
        this.occupier = null;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public Piece getOccupier() {
        return occupier;
    }

    public TileType getType() {
        return type;
    }

    // place a piece on the tile
    public void placePiece(Piece occupier) {
        this.occupier = occupier;
        this.isOccupied = true;
    }

    // take away pieces on the tiles after on phase is over
    public void reset() {
        this.occupier = null;
        this.isOccupied = false;
    }

}

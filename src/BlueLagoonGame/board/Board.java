package BlueLagoonGame.board;

import BlueLagoonGame.Position;
import BlueLagoonGame.piece.Piece;

import java.util.*;


public class Board {
    private final int size;
    private final Map<Piece.PieceType, Set<Position>> resStatPosMap;
    private final Tile[][] tileMatrix;
    private final Position[] stoneTilePositions;
    private final Set<Position> seaTilePositions;
    private final Island[] islands;

    /**
     * Create a board given the size, island information and stone positions
     *
     * @param size:               the size of the board
     * @param islands:            all the islands' information on this board
     * @param stoneTilePositions: all the tile positions of the board
     */
    public Board(int size, Island[] islands, Position[] stoneTilePositions) {
        // create the tile matrix based on the size
        this.size = size;
        tileMatrix = new Tile[size][size];
        // store the stone positions
        this.stoneTilePositions = stoneTilePositions;
        // create a map to store resources positions
        resStatPosMap = new HashMap<>();
        for (var type : Piece.PieceType.getResAndStatType()) {
            resStatPosMap.put(type, new HashSet<>());
        }
        // store the islands
        this.islands = islands;

        /*
        Create island tiles then the stone tiles and finally remained nonnull valid positions
        to create sea tiles
         */
        // create island tiles
        for (var island : islands) {
            for (var pos : island.positions()) {
                tileMatrix[pos.getX()][pos.getY()] = new Tile(Tile.TileType.LAND_TILE);
            }
        }
        // create stone tiles
        for (var pos : stoneTilePositions) {
            tileMatrix[pos.getX()][pos.getY()] = new Tile(Tile.TileType.STONE_TILE);
        }
        // create sea tiles
        Set<Position> seaTiles = new HashSet<>();
        for (int row = 0; row < size; row++) {
            // if null, then add the tiles with the sea type
            for (int col = 0; col < size; col++) {
                // even number row cannot exceed the width
                if (row % 2 == 0 && col < size - 1 && tileMatrix[row][col] == null) {
                    tileMatrix[row][col] = new Tile(Tile.TileType.SEA_TILE);
                    seaTiles.add(new Position(row, col));
                } else if (row % 2 == 1 && tileMatrix[row][col] == null) {
                    tileMatrix[row][col] = new Tile(Tile.TileType.SEA_TILE);
                    seaTiles.add(new Position(row, col));
                }
            }
        }
        seaTilePositions = seaTiles;
    }

    public Map<Piece.PieceType, Set<Position>> getResStatPosMap() {
        return resStatPosMap;
    }

    public Tile[][] getTileMatrix() {
        return tileMatrix;
    }

    public Position[] getStoneTilePositions() {
        return stoneTilePositions;
    }

    public Set<Position> getSeaTilePositions() {
        return seaTilePositions;
    }

    public Island[] getIslands() {
        return islands;
    }

    public Tile getTile(Position position) {
        return tileMatrix[position.getX()][position.getY()];
    }

    public int getSize() {
        return size;
    }

    /**
     * Given a position get all the positions on this board;
     *
     * @param position: the given position
     * @return a set of adjacent positions
     */
    public Set<Position> getAdjacentPositions(Position position) {
        Set<Position> adjacentPositions = new HashSet<>();
        Position[] temporaryPositions;
        int x = position.getX();
        int y = position.getY();
        // get adjacent positions first in terms of even row and odd row
        if (position.getX() % 2 == 0) {
            temporaryPositions = new Position[]{
                    new Position(x - 1, y), new Position(x - 1, y + 1),
                    new Position(x, y - 1), new Position(x, y + 1),
                    new Position(x + 1, y), new Position(x + 1, y + 1)
            };
        } else {
            temporaryPositions = new Position[]{
                    new Position(x - 1, y - 1), new Position(x - 1, y),
                    new Position(x, y - 1), new Position(x, y + 1),
                    new Position(x + 1, y - 1), new Position(x + 1, y)
            };
        }
        // check if the positions are on board
        for (var pos : temporaryPositions) {
            if (isPositionsOnBoard(pos)) {
                adjacentPositions.add(pos);
            }
        }
        return adjacentPositions;
    }

    /**
     * Given a position check if the position is on board
     *
     * @param position: the given position
     * @return if the positions is on board
     */
    public boolean isPositionsOnBoard(Position position) {
        // if the positions is off board return false
        if (position.getX() < 0 || position.getY() < 0 || position.getX() >= size) {
            return false;
        }
        // different cases for even and odd rows
        if (position.getX() % 2 == 0 && position.getY() >= size - 1) {
            return false;
        } else if (position.getX() % 2 == 1 && position.getY() >= size) {
            return false;
        }
        // else return true
        return true;
    }


}

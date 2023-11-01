package BlueLagoonGame.piece;

import java.util.HashSet;
import java.util.Set;

public class Piece {
    public enum PieceType {
        SETTLER,
        VILLAGE,
        STATUETTE,
        COCONUT,
        WATER,
        PRECIOUS_STONE,
        BAMBOO;

        public static Set<PieceType> getResType() {
            Set<PieceType> set = new HashSet<>();
            set.add(PieceType.COCONUT);
            set.add(PieceType.WATER);
            set.add(PieceType.BAMBOO);
            set.add(PieceType.PRECIOUS_STONE);
            return set;
        }

        public static Set<PieceType> getResAndStatType() {
            Set<PieceType> set = new HashSet<>(getResType());
            set.add(PieceType.STATUETTE);
            return set;
        }

        public static Set<PieceType> getPlayerPieceType() {
            Set<PieceType> set = new HashSet<>();
            set.add(SETTLER);
            set.add(VILLAGE);
            return set;
        }

        public static boolean isResources(Piece piece) {
            return piece.pieceType != SETTLER && piece.pieceType != VILLAGE;
        }
    }

    public enum Colour {
        PINK(0),
        ORANGE(1),
        BLUE(2),
        YELLOW(3);
        private final int id;

        Colour(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

    private final Colour colour;
    private final PieceType pieceType;

    public Piece(PieceType pieceType) {
        this.colour = null;
        this.pieceType = pieceType;
    }

    public Piece(PieceType pieceType, Colour colour) {
        this.colour = colour;
        this.pieceType = pieceType;
    }

    public PieceType getPieceType() {
        return pieceType;
    }

    public Colour getColour() {
        return colour;
    }
}

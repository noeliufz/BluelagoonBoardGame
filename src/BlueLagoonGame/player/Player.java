package BlueLagoonGame.player;

import BlueLagoonGame.Position;
import BlueLagoonGame.piece.Piece;

import java.util.*;

public class Player {
    private final Piece.Colour colour;
    private final int settlerNum;
    private final int villageNum;
    private final int id;
    private final Map<Piece.PieceType, Integer> resourceAndStatuetteNumMap;
    private final Map<Piece.PieceType, Set<Position>> piecePositionMap;
    private final Map<Piece.PieceType, Set<Position>> validMoveMap;
    private Score calculateScore;
    private int totalScore;

    public Player(int id, int settlerNum) {
        this.id = id;
        this.colour = Piece.Colour.values()[id];
        this.settlerNum = settlerNum;
        this.villageNum = 5;
        // create picked up resources and statuettes number map
        resourceAndStatuetteNumMap = new HashMap<>();
        for (var type : Piece.PieceType.getResAndStatType()) {
            resourceAndStatuetteNumMap.put(type, 0);
        }
        calculateScore = new Score();
        // create all pieces' position map and an empty valid move map
        piecePositionMap = new HashMap<>();
        validMoveMap = new HashMap<>();
        for (var type : Piece.PieceType.getPlayerPieceType()) {
            piecePositionMap.put(type, new HashSet<>());
            validMoveMap.put(type, new HashSet<>());
        }
    }

    public Piece.Colour getColour() {
        return colour;
    }

    public int getId() {
        return id;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public Map<Piece.PieceType, Integer> getResourceAndStatuetteNumMap() {
        return resourceAndStatuetteNumMap;
    }

    public Map<Piece.PieceType, Set<Position>> getPiecePositionMap() {
        return piecePositionMap;
    }

    public Map<Piece.PieceType, Set<Position>> getValidMoves() {
        return validMoveMap;
    }

    public Score getCalculateScore() {
        return calculateScore;
    }

    public int getRemainedPieceNum(Piece.PieceType type) {
        int originalNum = switch (type) {
            case SETTLER -> settlerNum;
            case VILLAGE -> villageNum;
            default -> -1;
        };
        return originalNum - piecePositionMap.get(type).size();
    }

    public int getResourceNum(Piece.PieceType type) {
        return resourceAndStatuetteNumMap.get(type);
    }


    public void setTotalScore(int score) {
        totalScore = score;
    }
    public String toString() {
        // playerStatement = "p ", playerId, " ", score, " ", coconut, " ", bamboo, " ", water, " ", preciousStone, " ", statuette, " S", {" ", coordinate}, " T", {" ", coordinate}, ";"
        String str;
        String playerId = Integer.toString(this.id);
        String score = Integer.toString(this.totalScore);
        String coconut = Integer.toString(this.resourceAndStatuetteNumMap.get(Piece.PieceType.COCONUT));
        String bamboo = Integer.toString(this.resourceAndStatuetteNumMap.get(Piece.PieceType.BAMBOO));
        String water = Integer.toString(this.resourceAndStatuetteNumMap.get(Piece.PieceType.WATER));
        String preciousStone = Integer.toString(this.resourceAndStatuetteNumMap.get(Piece.PieceType.PRECIOUS_STONE));
        String statuette = Integer.toString(this.resourceAndStatuetteNumMap.get(Piece.PieceType.STATUETTE));
        String sCor = " S";
        if (this.piecePositionMap.get(Piece.PieceType.SETTLER) != null) {
            // sort the arraylist first
            ArrayList<Position> settlerPosArr = new ArrayList<>(this.piecePositionMap.get(Piece.PieceType.SETTLER));
            settlerPosArr.sort(new Position.PositionComparator());
            for (Position pos : settlerPosArr) {
                sCor += " ";
                sCor += pos.toString();
            }
        }
        String tCor = " T";
        if (this.piecePositionMap.get(Piece.PieceType.VILLAGE) != null) {
            // sort the arraylist first
            ArrayList<Position> villagePosArr = new ArrayList<>(this.piecePositionMap.get(Piece.PieceType.VILLAGE));
            villagePosArr.sort(new Position.PositionComparator());
            for (Position pos : villagePosArr) {
                tCor += " ";
                tCor += pos.toString();
            }
        }
        str = "p " + playerId + " " + score + " " + coconut + " " + bamboo + " " + water + " " + preciousStone + " " + statuette + sCor + tCor + ";";
        return str;
    }


    public boolean hasValidMoves() {
        return !(validMoveMap.get(Piece.PieceType.SETTLER).size() + validMoveMap.get(Piece.PieceType.VILLAGE).size() == 0);
    }
}

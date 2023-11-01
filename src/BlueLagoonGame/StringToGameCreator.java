package BlueLagoonGame;

import BlueLagoonGame.board.Island;
import BlueLagoonGame.piece.Piece;
import BlueLagoonGame.piece.Piece.PieceType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StringToGameCreator {
    String[] status;

    public StringToGameCreator(String gameString) {
        // stats[0] for gameArrangementStatement = "a ", boardHeight, " ", numPlayers, ";"
        // stats[1] for currentStateStatement = "c ", playerId, " ", phase, ";"
        // stats[2] for islandStatement = "i ", bonus, {" ", coordinate}, ";"
        // stats[3] for stonesStatement = "s", {" ", coordinate}, ";"
        // stats[4] for unclaimedResourcesAndStatuettesStatement = "r C", {" ", coordinate}, " B", {" ", coordinate}, " W", {" ", coordinate}, " P", {" ", coordinate}, " S", {" ", coordinate}, ";"
        // stats[5] for playerStatement = "p ", playerId, " ", score, " ", coconut, " ", bamboo, " ", water, " ", preciousStone, " ", statuette, " S", {" ", coordinate}, " T", {" ", coordinate}, ";"
        String[] statsToArrange = gameString.split("; ");
        String[] stats = new String[6];
        stats[0] = statsToArrange[0];
        stats[1] = statsToArrange[1];
        int index = 3;
        StringBuilder sb = new StringBuilder();
        sb.append(statsToArrange[2]);
        while (statsToArrange[index].charAt(0) == 'i') {
            sb.append("; ");
            sb.append(statsToArrange[index]);
            index++;
        }
        stats[2] = sb.toString();
        stats[3] = statsToArrange[index];
        stats[4] = statsToArrange[index + 1];

        index = index + 2;
        StringBuilder sbPlayer = new StringBuilder();
        sbPlayer.append(statsToArrange[index]);
        index = index + 1;
        while (index < statsToArrange.length) {
            sbPlayer.append("; ");
            sbPlayer.append(statsToArrange[index]);
            index++;
        }
        sbPlayer.deleteCharAt(sbPlayer.length() - 1);
        stats[5] = sbPlayer.toString();
        status = stats;
    }

    public int getBoardSize() {
        return Integer.parseInt(status[0].split("\\s")[1]);
    }

    public int getPlayerNum() {
        return Integer.parseInt(status[0].split("\\s")[2]);
    }

    public int getPlayerTurn() {
        return Integer.parseInt(status[1].split("\\s")[1]);
    }

    public BlueLagoonGame.GamePhase getGamePhase() {
        return switch (status[1].split("\\s")[2]) {
            case "E" -> BlueLagoonGame.GamePhase.EXPLORATION_PHASE;
            case "S" -> BlueLagoonGame.GamePhase.SETTLEMENT_PHASE;
            default -> null;
        };
    }

    public Island[] getIslands() {
        // status[2] for islandStatement = "i ", bonus, {" ", coordinate}, ";"
        String[] islandsString = status[2].split("; |;");
        List<Island> islands = new ArrayList<>();
        for (String island : islandsString) {
            String[] islandSplitString = island.split("\\s");
            // create an arraylist to store the positions of the island
            int islandBonus = Integer.parseInt(islandSplitString[1]);
            ArrayList<Position> pos = new ArrayList<>();
            for (int i = 2; i < islandSplitString.length; i++) {
                pos.add(Position.parsePosition(islandSplitString[i]));
            }
            Position[] positions = pos.toArray(new Position[0]);
            islands.add(new Island(positions, islandBonus));
        }
        return islands.toArray(new Island[0]);
    }

    public Position[] getStonePositions() {
        // status[3] for stonesStatement = "s", {" ", coordinate}, ";"
        Position[] stonePositions = new Position[32];
        String[] stoneSplitString = status[3].split("\\s");
        for (int i = 1; i < stoneSplitString.length; i++) {
            stonePositions[i - 1] = (Position.parsePosition(stoneSplitString[i]));
        }
        return stonePositions;
    }

    public void setUnclaimedRes(BlueLagoonGame game) {
        // status[4] for unclaimedResourcesAndStatuettesStatement = "r C", {" ", coordinate}, " B", {" ", coordinate}, " W", {" ", coordinate}, " P", {" ", coordinate}, " S", {" ", coordinate}, ";"
        String[] unclaimedResString = status[4].split("\\s");
        int i = 1;
        Piece.PieceType resType = null;
        while (i < unclaimedResString.length) {
            if (unclaimedResString[i].matches("C")) {
                resType = Piece.PieceType.COCONUT;
            } else if (unclaimedResString[i].matches("B")) {
                resType = Piece.PieceType.BAMBOO;
            } else if (unclaimedResString[i].matches("W")) {
                resType = Piece.PieceType.WATER;
            } else if (unclaimedResString[i].matches("P")) {
                resType = Piece.PieceType.PRECIOUS_STONE;
            } else if (unclaimedResString[i].matches("S")) {
                resType = Piece.PieceType.STATUETTE;
            } else if (unclaimedResString[i].matches("[0-9]*,[0-9]*")) {
                Position pos = Position.parsePosition(unclaimedResString[i]);
                game.getBoard().getTileMatrix()[pos.getX()][pos.getY()].placePiece(new Piece(resType));
                game.getBoard().getResStatPosMap().get(resType).add(pos);
            }
            i++;
        }
    }

    public void setPlayer(BlueLagoonGame game) {
        String[] playersStats = status[5].split("; |;");
        for (String player : playersStats) {
            String[] playerString = player.split("\\s");
            // 1: playerId 2: score 3: coconut 4: bamboo 5: water 6: preciousStone 7: statuettes 8~ : S {" ", coordinate}  T {" ", coordinate}
            int id = Integer.parseInt(playerString[1]);
            int score = Integer.parseInt(playerString[2]);
            int coconut = Integer.parseInt(playerString[3]);
            int bamboo = Integer.parseInt(playerString[4]);
            int water = Integer.parseInt(playerString[5]);
            int preciousStone = Integer.parseInt(playerString[6]);
            int statuettes = Integer.parseInt(playerString[7]);

            Set<Position> settlerPositions = new HashSet<>();
            Set<Position> villagePositions = new HashSet<>();
            // 2: score
            game.getPlayers()[id].setTotalScore(score);
            // 3-6: resources
            game.getPlayers()[id].getResourceAndStatuetteNumMap().put(Piece.PieceType.COCONUT, coconut);
            game.getPlayers()[id].getResourceAndStatuetteNumMap().put(Piece.PieceType.BAMBOO, bamboo);
            game.getPlayers()[id].getResourceAndStatuetteNumMap().put(Piece.PieceType.WATER, water);
            game.getPlayers()[id].getResourceAndStatuetteNumMap().put(Piece.PieceType.PRECIOUS_STONE, preciousStone);
            // 7: statuette
            game.getPlayers()[id].getResourceAndStatuetteNumMap().put(Piece.PieceType.STATUETTE, statuettes);

            // 8~
            int i = 8;
            PieceType pieceType = null;
            while (i < playerString.length) {
                if (playerString[i].matches("S")) {
                    pieceType = PieceType.SETTLER;
                } else if (playerString[i].matches("T")) {
                    pieceType = PieceType.VILLAGE;
                } else if (playerString[i].matches("[0-9]*,[0-9]*")) {
                    Position pos = Position.parsePosition(playerString[i]);
                    if (pieceType == PieceType.SETTLER) {
                        // place the piece on board
                        game.getBoard().getTileMatrix()[pos.getX()][pos.getY()].placePiece(new Piece(PieceType.SETTLER, game.getPlayers()[id].getColour()));
                        game.getPlayers()[id].getPiecePositionMap().get(PieceType.SETTLER).add(pos);
                        settlerPositions.add(pos);
                    } else if (pieceType == PieceType.VILLAGE) {
                        game.getBoard().getTileMatrix()[pos.getX()][pos.getY()].placePiece(new Piece(PieceType.VILLAGE, game.getPlayers()[id].getColour()));
                        game.getPlayers()[id].getPiecePositionMap().get(PieceType.VILLAGE).add(pos);
                        villagePositions.add(pos);
                    }
                }
                i++;
            }
            game.getPlayers()[id].getPiecePositionMap().put(PieceType.SETTLER, settlerPositions);
            game.getPlayers()[id].getPiecePositionMap().put(PieceType.VILLAGE, villagePositions);
        }
    }

}

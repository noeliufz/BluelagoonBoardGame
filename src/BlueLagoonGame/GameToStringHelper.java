package BlueLagoonGame;

import BlueLagoonGame.board.Island;
import BlueLagoonGame.player.Player;
import BlueLagoonGame.piece.Piece.*;

import java.util.ArrayList;

/**
 * This class is to transform a game instance to a separate string
 *
 * @author Fangzhou Liu
 */
public class GameToStringHelper {
    BlueLagoonGame game;

    public GameToStringHelper(BlueLagoonGame game) {
        this.game = game;
    }

    // game to string: arrangement string
    public StringBuilder toArrangementStr() {
        // gameArrangementStatement = "a ", boardHeight, " ", numPlayers, ";"
        StringBuilder sb = new StringBuilder();
        String boardHeight = Integer.toString(game.getBoard().getSize());
        String numPlayers = Integer.toString(game.getPlayers().length);
        sb.append("a ");
        sb.append(boardHeight);
        sb.append(" ");
        sb.append(numPlayers);
        sb.append(";");
        return sb;
    }

    // game to string: to current status string
    public StringBuilder toCurrentStateStr() {

        // currentStateStatement = "c ", playerId, " ", phase, ";"
        StringBuilder sb = new StringBuilder();
        String playId = Integer.toString(game.getCurrentPlayer().getId());
        String phase = switch (game.getPhase()) {
            case EXPLORATION_PHASE:
                yield "E";
            case SETTLEMENT_PHASE:
                yield "S";
        };
        sb.append("c ");
        sb.append(playId);
        sb.append(" ");
        sb.append(phase);
        sb.append(";");
        return sb;
    }

    // game to string: island string
    public String[] toIslandStr() {
        // islandStatement = "i ", bonus, {" ", coordinate}, ";"
        ArrayList<String> str = new ArrayList<>();
        for (Island island : game.getBoard().getIslands()) {
            str.add(island.toString());
        }
        return str.toArray(new String[0]);
    }

    // game to string: stone string
    public StringBuilder toStonesStr() {
        // stonesStatement = "s", {" ", coordinate}, ";"
        StringBuilder sb = new StringBuilder();
        sb.append("s");
        for (Position tile : game.getBoard().getStoneTilePositions()) {
            sb.append(" ");
            sb.append(tile.toString());
        }
        sb.append(";");
        return sb;
    }

    // game to string: unclaimed resources string
    public StringBuilder toUnclaimedResStr() {
        ArrayList<Position> coconutPosArr = new ArrayList<>(game.getBoard().getResStatPosMap().get(PieceType.COCONUT));
        coconutPosArr.sort(new Position.PositionComparator());
        ArrayList<Position> bambooPosArr = new ArrayList<>(game.getBoard().getResStatPosMap().get(PieceType.BAMBOO));
        bambooPosArr.sort(new Position.PositionComparator());
        ArrayList<Position> waterPosArr = new ArrayList<>(game.getBoard().getResStatPosMap().get(PieceType.WATER));
        waterPosArr.sort(new Position.PositionComparator());
        ArrayList<Position> prestonePosArr = new ArrayList<>(game.getBoard().getResStatPosMap().get(PieceType.PRECIOUS_STONE));
        prestonePosArr.sort(new Position.PositionComparator());
        ArrayList<Position> statuettePosArr = new ArrayList<>(game.getBoard().getResStatPosMap().get(PieceType.STATUETTE));
        statuettePosArr.sort(new Position.PositionComparator());

        //unclaimedResourcesAndStatuettesStatement = "r C", {" ", coordinate}, " B", {" ", coordinate}, " W", {" ", coordinate}, " P", {" ", coordinate}, " S", {" ", coordinate}, ";"
        StringBuilder sb = new StringBuilder();
        sb.append("r C");
        if (coconutPosArr != null) {
            for (Position pos : coconutPosArr) {
                sb.append(" ");
                sb.append(pos.toString());
            }
        }
        sb.append(" B");
        if (bambooPosArr != null) {
            for (Position pos : bambooPosArr) {
                sb.append(" ");
                sb.append(pos.toString());
            }
        }
        sb.append(" W");
        if (waterPosArr != null) {
            for (Position pos : waterPosArr) {
                sb.append(" ");
                sb.append(pos.toString());
            }
        }
        sb.append(" P");
        if (prestonePosArr != null) {
            for (Position pos : prestonePosArr) {
                sb.append(" ");
                sb.append(pos.toString());
            }
        }
        sb.append(" S");
        if (statuettePosArr != null) {
            for (Position pos : statuettePosArr) {
                sb.append(" ");
                sb.append(pos.toString());
            }
        }
        sb.append(";");
        return sb;
    }

    // game to string: player string
    public String[] toPlayerStr() {
        // playerStatement = "p ", playerId, " ", score, " ", coconut, " ", bamboo, " ", water, " ", preciousStone, " ", statuette, " S", {" ", coordinate}, " T", {" ", coordinate}, ";"
        ArrayList<String> str = new ArrayList<>();
        for (Player player : game.getPlayers()) {
            str.add(player.toString());
        }
        return str.toArray(new String[0]);
    }

    @Override
    public String toString() {
        // gameState = gameArrangementStatement, " ", currentStateStatement, {" ", islandStatement}, " ", stonesStatement, " ", unclaimedResourcesAndStatuettesStatement, {" ", playerStatement}
        StringBuilder sb = new StringBuilder();
        sb.append(toArrangementStr());
        sb.append(" ");
        sb.append(toCurrentStateStr());

        for (String islandStatement : toIslandStr()) {
            sb.append(" ");
            sb.append(islandStatement);
        }

        sb.append(" ");
        sb.append(toStonesStr());
        sb.append(" ");
        sb.append(toUnclaimedResStr());
        for (String playerStatement : toPlayerStr()) {
            sb.append(" ");
            sb.append(playerStatement);
        }
        return sb.toString();
    }
}



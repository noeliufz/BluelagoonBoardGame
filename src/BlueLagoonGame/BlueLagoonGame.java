package BlueLagoonGame;

import BlueLagoonGame.board.Board;
import BlueLagoonGame.board.Tile;
import BlueLagoonGame.piece.Piece;
import BlueLagoonGame.player.Player;


import java.util.*;

public class BlueLagoonGame {

    public enum GamePhase {
        EXPLORATION_PHASE,
        SETTLEMENT_PHASE;

        public String toString() {
            return switch (this) {
                case EXPLORATION_PHASE -> "EXPLORATION PHASE";
                case SETTLEMENT_PHASE -> "SETTLEMENT PHASE";
            };
        }
    }

    public static final String DEFAULT_GAME = "a 13 2; c 0 E; i 6 0,0 0,1 0,2 0,3 1,0 1,1 1,2 1,3 1,4 2,0 2,1; i 6 0,5 0,6 0,7 1,6 1,7 1,8 2,6 2,7 2,8 3,7 3,8; i 6 7,12 8,11 9,11 9,12 10,10 10,11 11,10 11,11 11,12 12,10 12,11; i 8 0,9 0,10 0,11 1,10 1,11 1,12 2,10 2,11 3,10 3,11 3,12 4,10 4,11 5,11 5,12; i 8 4,0 5,0 5,1 6,0 6,1 7,0 7,1 7,2 8,0 8,1 8,2 9,0 9,1 9,2; i 8 10,3 10,4 11,0 11,1 11,2 11,3 11,4 11,5 12,0 12,1 12,2 12,3 12,4 12,5; i 10 3,3 3,4 3,5 4,2 4,3 4,4 4,5 5,3 5,4 5,5 5,6 6,3 6,4 6,5 6,6 7,4 7,5 7,6 8,4 8,5; i 10 5,8 5,9 6,8 6,9 7,8 7,9 7,10 8,7 8,8 8,9 9,7 9,8 9,9 10,6 10,7 10,8 11,7 11,8 12,7 12,8; s 0,0 0,5 0,9 1,4 1,8 1,12 2,1 3,5 3,7 3,10 3,12 4,0 4,2 5,9 5,11 6,3 6,6 7,0 7,8 7,12 8,2 8,5 9,0 9,9 10,3 10,6 10,10 11,0 11,5 12,2 12,8 12,11; r C B W P S; p 0 0 0 0 0 0 0 S T; p 1 0 0 0 0 0 0 S T;";
    private final Player[] players;
    private final Board board;
    private GamePhase phase;
    private int playerTurn;

    // create a game from string, not considering the unclaimed resources and player pieces
    public BlueLagoonGame(String gameString) {
        StringToGameCreator creator = new StringToGameCreator(gameString);
        int playerNum = creator.getPlayerNum();
        players = new Player[playerNum];
        int settlerNum = switch (playerNum) {
            case 4 -> 20;
            case 3 -> 25;
            case 2 -> 30;
            default -> 0;
        };
        for (int i = 0; i < players.length; i++) {
            players[i] = new Player(i, settlerNum);
        }
        board = new Board(creator.getBoardSize(), creator.getIslands(), creator.getStonePositions());
        playerTurn = creator.getPlayerTurn();
        phase = creator.getGamePhase();
        creator.setUnclaimedRes(this);
        creator.setPlayer(this);
    }

    public Board getBoard() {
        return board;
    }

    public Player[] getPlayers() {
        return players;
    }

    public Player getCurrentPlayer() {
        return players[playerTurn];
    }

    public GamePhase getPhase() {
        return phase;
    }

    public int getPlayerTurn() {
        return playerTurn;
    }

    public void setPhase(GamePhase phase) {
        this.phase = phase;
    }

    // randomly distribute resources on stone tiles
    public void distributeResources() {
        // clear pieces on stones first
        for (var type : Piece.PieceType.getResAndStatType()) {
            board.getResStatPosMap().get(type).clear();
        }
        // create a new set of pieces to place on board
        List<Piece> resources = new ArrayList<>();
        for (var type : Piece.PieceType.getResType()) {
            for (int i = 0; i < 6; i++) {
                resources.add(new Piece(type));
            }
        }
        for (int i = 0; i < 8; i++) {
            resources.add(new Piece(Piece.PieceType.STATUETTE));
        }

        Random random = new Random();
        for (var position : board.getStoneTilePositions()) {
            int index = random.nextInt(resources.size());
            Piece piece = resources.get(index);
            board.getTile(position).placePiece(piece);
            board.getResStatPosMap().get(piece.getPieceType()).add(position);
            resources.remove(index);
        }
    }

    public void generateValidMoves(Player player) {
        // clear valid moves first
        player.getValidMoves().get(Piece.PieceType.SETTLER).clear();
        player.getValidMoves().get(Piece.PieceType.VILLAGE).clear();

        // visited all sea tiles, for Exploration phase, settler can be placed on the sea
        if (player.getRemainedPieceNum(Piece.PieceType.SETTLER) != 0 && phase == GamePhase.EXPLORATION_PHASE) {
            for (var position : board.getSeaTilePositions()) {
                if (!board.getTile(position).isOccupied()) {
                    player.getValidMoves().get(Piece.PieceType.SETTLER).add(position);
                }
            }
        }
        // store the tile positions to visit
        Set<Position> toVisit = new HashSet<>();
        // visit all the positions of the player, including settlers and villages and get their adjacent positions
        for (var positionSet : player.getPiecePositionMap().values()) {
            for (var position : positionSet) {
                toVisit.addAll(board.getAdjacentPositions(position));
            }
        }

        for (var adjacent : toVisit) {
            Tile currentTile = board.getTile(adjacent);
            if (player.getRemainedPieceNum(Piece.PieceType.SETTLER) != 0) {
                if (!currentTile.isOccupied()) {
                    player.getValidMoves().get(Piece.PieceType.SETTLER).add(adjacent);
                } else if (Piece.PieceType.isResources(currentTile.getOccupier())) {
                    player.getValidMoves().get(Piece.PieceType.SETTLER).add(adjacent);
                }
            }

            // generate village valid move
            // villages cannot be placed on the sea
            // villages cannot be placed during Settlement phase
            if (player.getRemainedPieceNum(Piece.PieceType.VILLAGE) != 0 && phase != GamePhase.SETTLEMENT_PHASE && !currentTile.getType().equals(Tile.TileType.SEA_TILE)) {
                if (!currentTile.isOccupied()) {
                    player.getValidMoves().get(Piece.PieceType.VILLAGE).add(adjacent);
                } else if (Piece.PieceType.isResources(currentTile.getOccupier())) {
                    player.getValidMoves().get(Piece.PieceType.VILLAGE).add(adjacent);
                }
            }
        }
    }

    public boolean isMoveValid(Player player, Piece.PieceType pieceType, Position position) {
        generateValidMoves(player);
        return player.getValidMoves().get(pieceType).contains(position);
    }

    public boolean isPhaseOver() {
        int claimedResources = 0;
        int validMoves = 0;
        for (var player : players) {
            // calculate all the resources claimed
            this.generateValidMoves(player);
            for (var type : Piece.PieceType.getResType()) {
                claimedResources += player.getResourceNum(type);
            }

            //calculate valid moves for all users
            for (var type : Piece.PieceType.getPlayerPieceType()) {
                validMoves += player.getValidMoves().get(type).size();
            }
        }

        // if all the resources have been collected, phase over
        // if there is no valid moves for all users, phase over
        return claimedResources == 24 || validMoves == 0;
    }

    public void placePieces(Player player, Piece.PieceType pieceType, Position position) {
        // add to player piece position map
        player.getPiecePositionMap().get(pieceType).add(position);
        // if there is a resource on board, pick it up and update the number map
        Tile tile = board.getTile(position);
        if (tile.isOccupied()) {
            Piece.PieceType resourceType = tile.getOccupier().getPieceType();
            board.getResStatPosMap().get(resourceType).remove(position);
            player.getResourceAndStatuetteNumMap().put(resourceType, player.getResourceNum(resourceType) + 1);
        }
        tile.placePiece(new Piece(pieceType, player.getColour()));
    }

    public void calculateTotalIslandScore() {
        for (var player : players) {
            // calculate how many islands the player has placed his pieces
            int placedIsland = 0;
            for (var island : board.getIslands()) {
                // get intersections of the island positions and the player settler or village positions
                // if not empty, it means there are pieces placed on the island, increment the number of placedIsland
                var pieceMap = player.getPiecePositionMap();
                Set<Position> commonPosition = new HashSet<>(pieceMap.get(Piece.PieceType.SETTLER));
                commonPosition.addAll(pieceMap.get(Piece.PieceType.VILLAGE));
                commonPosition.retainAll(Arrays.asList(island.positions()));
                if (!commonPosition.isEmpty()) {
                    placedIsland++;
                }
            }
            if (placedIsland >= 8) {
                player.getCalculateScore().setIslandScore(20);
            } else if (placedIsland == 7) {
                player.getCalculateScore().setIslandScore(10);
            } else {
                player.getCalculateScore().setIslandScore(0);
            }
        }
    }

    public void calculateIslandLinksScore() {
        for (var player : players) {
            // get all the positions where pieces placed on the sea to find the longest chain
            var pieceMap = player.getPiecePositionMap();
            Set<Position> playerAllPieces = new HashSet<>(pieceMap.get(Piece.PieceType.SETTLER));
            playerAllPieces.addAll(pieceMap.get(Piece.PieceType.VILLAGE));
            // create an arraylist to store all positions already visited
            Set<Position> visited = new HashSet<>();
            int maxLinkedIslandNum = 0;
            for (var position : playerAllPieces) {
                // if the position has not been visited, go on the search
                if (!visited.contains(position)) {
                    // store positions of one chain that linked all the pieces
                    Set<Position> chain = new HashSet<>();
                    // create an arraylist to store positions to visit for next step
                    List<Position> toVisit = new ArrayList<>();
                    visited.add(position);
                    toVisit.add(position);
                    // get the chain of linked pieces
                    while (!toVisit.isEmpty()) {
                        Position currentPosition = toVisit.get(0);
                        toVisit.remove(0);
                        visited.add(currentPosition);
                        if (playerAllPieces.contains(currentPosition)) {
                            chain.add(currentPosition);
                        }
                        var adjacentPositions = board.getAdjacentPositions(currentPosition);
                        for (var adjacentPosition : adjacentPositions) {
                            if (playerAllPieces.contains(adjacentPosition) && !visited.contains(adjacentPosition)) {
                                toVisit.add(adjacentPosition);
                            }
                        }
                    }
                    // count how many island is intersected with the linked chain of the player
                    int playerLinkedIslandNum = 0;
                    for (var island : board.getIslands()) {
                        List<Position> islandPositions = new ArrayList<>(List.of(island.positions()));
                        islandPositions.retainAll(chain);
                        if (!islandPositions.isEmpty()) {
                            // if the chain has the intersection of one island, increment the playerLinkedIslandNUm
                            playerLinkedIslandNum++;
                        }
                    }
                    // store the maximum number of the linked island quantities
                    maxLinkedIslandNum = Integer.max(maxLinkedIslandNum, playerLinkedIslandNum);
                }
            }
            // for each linked island, get 5 points
            player.getCalculateScore().setLinkScore(maxLinkedIslandNum * 5);
        }
    }

    public void calculateIslandMajoritiesScore() {
        // create an array to store the scores for each player
        int[] majorityScore = new int[players.length];
        Arrays.fill(majorityScore, 0);
        for (var island : board.getIslands()) {
            // store pieces number for each player of the current island
            int[] playerPieceNum = new int[players.length];
            for (int i = 0; i < players.length; i++) {
                var pieceMap = players[i].getPiecePositionMap();
                // create position set of the current player's all pieces
                var playerAllPieces = new ArrayList<>(pieceMap.get(Piece.PieceType.SETTLER));
                playerAllPieces.addAll(pieceMap.get(Piece.PieceType.VILLAGE));
                // get intersections of the current island
                playerAllPieces.retainAll(Arrays.asList(island.positions()));
                playerPieceNum[i] = playerAllPieces.size();
            }
            // find the max index
            int max = Integer.MIN_VALUE;
            var maxIndex = new ArrayList<Integer>();
            for (int i = 0; i < players.length; i++) {
                int value = playerPieceNum[i];
                if (value > max) {
                    max = value;
                    maxIndex.clear();
                    maxIndex.add(i);
                } else if (value == max) {
                    maxIndex.add(i);
                }
            }
            if (max != 0) {
                // update scores
                for (var index : maxIndex) {
                    majorityScore[index] += island.bonus() / maxIndex.size();
                }
            }
            // store the scores back to players' score
            for (int i = 0; i < players.length; i++) {
                players[i].getCalculateScore().setMajorityScore(majorityScore[i]);
            }
        }
    }

    public void calculateResourcesAndStatuettesScore() {
        for (var player : players) {
            int matchingScore = 0;
            int additionalScore;
            int statuetteScore;
            // calculate matching resources score
            int type = 1;
            var resNumMap = player.getResourceAndStatuetteNumMap();
            var score = player.getCalculateScore();
            for (var entry : resNumMap.entrySet()) {
                if (entry.getKey() != Piece.PieceType.STATUETTE) {
                    switch (entry.getValue()) {
                        case 4, 5, 6 -> matchingScore += 20;
                        case 3 -> matchingScore += 10;
                        case 2 -> matchingScore += 5;
                    }
                    // calculate additional score if the player has all 4 types of resources
                    type = type * entry.getValue();
                }
            }
            // if the type is not 0, it means the player has all 4 types of resources
            additionalScore = type == 0 ? 0 : 10;
            // calculate statuette score, for each statuette the player collects 4 points
            statuetteScore = resNumMap.get(Piece.PieceType.STATUETTE) * 4;

            score.setResourceScore(matchingScore);
            score.setResourceAdditionalScore(additionalScore);
            score.setStatuetteScore(statuetteScore);
        }
    }

    public void calculateScore() {
        this.calculateIslandLinksScore();
        this.calculateTotalIslandScore();
        this.calculateIslandMajoritiesScore();
        this.calculateResourcesAndStatuettesScore();
        for (var player : players) {
            player.getCalculateScore().updateTotalScore();
        }
    }

    public void applyUpdateScore() {
        // update the calculated end phase score for players
        for (var player : players) {
            player.setTotalScore(player.getTotalScore() + player.getCalculateScore().getTotalScore());
        }
    }

    public void updatePlayerTurnAndPhase() {
        for (Player player : players) {
            generateValidMoves(player);
        }
        int step = 0;
        int i = playerTurn;
        while (step != 4) {
            // set the next player to 0 if the player is the last player
            if (i == players.length - 1) {
                i = 0;
            } else {
                i++;
            }
            if (players[i].hasValidMoves()) {
                playerTurn = i;
                break;
            }
            step++;
        }
    }

    public void phaseOver() {
        // calculate score and save
        calculateScore();
        applyUpdateScore();
        // update all details to end exploration phase
        if (this.getPhase() == GamePhase.EXPLORATION_PHASE) {
            for (var player : players) {
                var pieceMap = player.getPiecePositionMap();
                // clear player collected resource value
                for (var type : Piece.PieceType.getResAndStatType()) {
                    player.getResourceAndStatuetteNumMap().put(type, 0);
                }
                // clear player settler
                for (var position : pieceMap.get(Piece.PieceType.SETTLER)) {
                    board.getTile(position).reset();
                }
                pieceMap.get(Piece.PieceType.SETTLER).clear();

                // remove villages on stone tiles
                Set<Position> set = new HashSet<>(pieceMap.get(Piece.PieceType.VILLAGE));
                set.retainAll(List.of(board.getStoneTilePositions()));
                pieceMap.get(Piece.PieceType.VILLAGE).removeAll(set);
                for (var position : set) {
                    board.getTile(position).reset();
                }
            }
        }
    }

    public String getGameResultString() {
        StringBuilder sb = new StringBuilder();
        int winnerScore = Integer.MIN_VALUE;
        ArrayList<Player> winners = new ArrayList<>();
        for (Player player : players) {
            if (player.getTotalScore() > winnerScore) {
                winnerScore = player.getTotalScore();
                winners.clear();
                winners.add(player);
            } else if (player.getTotalScore() == winnerScore) {
                winners.add(player);
            }
        }

        sb.append("Player ");

        if (winners.size() == 1) {
            sb.append(winners.get(0).getId() + 1);
            sb.append(" wins.\nScore: ");
            sb.append(winnerScore);
        } else {
            for (int i = 0; i < winners.size(); i++) {
                sb.append(winners.get(i).getId() + 1);
                if (i != winners.size() - 1) {
                    sb.append(" and ");
                }
            }
            sb.append("win.\nScore: ");
            sb.append(winnerScore);
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        GameToStringHelper helper = new GameToStringHelper(this);
        return helper.toString();
    }

    public static void main(String[] args) {
        BlueLagoonGame game = new BlueLagoonGame("a 13 2; c 1 S; i 5 0,1 0,2 0,3 0,4 1,1 1,5 2,0 2,5 3,0 3,6 4,0 4,5 5,1 5,5 6,1 6,2 6,3 6,4; i 5 0,8 0,9 0,10 1,8 1,11 2,7 2,11 3,8 3,11 4,8 4,9 4,10; i 7 8,8 8,9 8,10 9,8 9,11 10,7 10,11 11,8 11,11 12,8 12,9 12,10; i 7 10,0 10,1 10,4 10,5 11,0 11,2 11,3 11,4 11,6 12,0 12,1 12,4 12,5; i 9 2,2 2,3 3,2 3,4 4,2 4,3; i 9 2,9; i 9 6,6 6,7 6,8 6,9 6,10 6,11 7,6 8,0 8,1 8,2 8,3 8,4 8,5; i 9 10,9; s 0,1 0,4 0,10 2,2 2,3 2,9 2,11 3,0 3,2 3,4 3,6 4,2 4,3 4,10 6,1 6,4 6,6 6,11 8,0 8,5 8,8 8,10 10,0 10,5 10,7 10,9 10,11 11,3 12,1 12,4 12,8 12,10; r C 2,2 2,3 4,10 10,7 B 0,10 3,4 6,11 8,8 10,11 W 3,2 10,0 12,8 12,10 P 2,11 3,6 6,1 12,4 S 0,1 0,4 2,9 3,0 8,0 8,10 11,3 12,1; p 0 90 2 1 1 2 0 S 4,2 4,3 5,2 5,4 6,2 6,4 6,5 6,6 7,5 8,5 9,5 10,5 T 6,3; p 1 113 0 0 1 0 0 S 7,6 7,7 8,6 9,7 10,8 10,9 11,6 11,7 11,8 12,6 12,7 T 9,8;");
        game.calculateResourcesAndStatuettesScore();
    }
}

package BlueLagoonGame.board;

import BlueLagoonGame.Position;

public record Island(Position[] positions, int bonus) {
    /**
     * An island of certain bonus and all the positions on board
     *
     * @param positions: all the positions of this island
     * @param bonus:     the bonus of the island
     */
    public Island {
    }

    @Override
    public String toString() {
        //islandStatement = "i ", bonus, {" ", coordinate}, ";"
        StringBuilder sb = new StringBuilder();
        sb.append("i ");
        sb.append(bonus);
        for (Position pos : positions) {
            sb.append(" ");
            sb.append(pos.toString());
        }
        sb.append(";");
        return sb.toString();
    }
}

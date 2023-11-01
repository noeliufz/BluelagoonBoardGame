package BlueLagoonGame;

import java.util.Comparator;
import java.util.Objects;

public class Position {
    private final int x;
    private final int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public static Position parsePosition(String str) {
        String[] strSplit = str.split(",");
        return new Position(Integer.parseInt(strSplit[0]), Integer.parseInt(strSplit[1]));
    }

    public static class PositionComparator implements Comparator<Position> {

        @Override
        public int compare(Position o1, Position o2) {
            if (o1.getX() < o2.getX()) {
                return -1;
            } else if (o1.getX() > o2.getX()) {
                return 1;
            } else {
                // if x is the same, compare y
                return Integer.compare(o1.getY(), o2.getY());
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Position)) {
            return false;
        }
        Position other = (Position) obj;
        return x == other.getX() && y == other.getY();
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return x + "," + y;
    }

}

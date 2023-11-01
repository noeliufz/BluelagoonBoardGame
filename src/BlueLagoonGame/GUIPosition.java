package BlueLagoonGame;

/**
 * This class is to convert a board coordinate to a real position on a JavaFx window.
 *
 * @author Fangzhou Liu
 */

public class GUIPosition {
    private final double x;
    private final double y;

    public GUIPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x + 20;
    }

    public double getY() {
        return y + 30;
    }

    public int getIntX() {
        int newX = (int) x + 5;
        return newX;
    }

    public int getIntY() {
        int newY = (int) y + 15;
        return newY;
    }

    public static GUIPosition parseFromBoardPosition(int xin, int yin, double side) {
        // odd line
        if (xin % 2 == 0) {
            double GUIX = 2 * (yin + 1) * side * Math.sqrt(3) / 2;
            double GUIY = (1.5 * xin + 1) * side;
            return new GUIPosition(GUIX, GUIY);
        } else if (xin % 2 == 1) {
            double GUIX = (2 * yin + 1) * side * Math.sqrt(3) / 2;
            double GUIY = (1.5 * xin + 1) * side;
            return new GUIPosition(GUIX, GUIY);
        }
        return null;
    }
}

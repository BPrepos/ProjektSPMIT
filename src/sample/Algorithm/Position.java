package sample.Algorithm;

public class Position {
    private int y;
    private int x;

    public Position(int x, int y) {
        this.y = y;
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    public int calculateDistance(Position nextPosition) {
        int x = nextPosition.getX();
        int y = nextPosition.getY();

        double distance = (Math.pow(this.x-x, 2)+ Math.pow(this.y-y, 2));
        return (int) distance;
    }

    public String toString() {
        return "(" + x + "," + y + ")";
    }
}

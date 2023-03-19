package org.example.Models;

public class Position{
    private int x;
    private int y;
    private Chessman chessman;

    public Position(int x, int y, Chessman chessman) {
        this.x = x;
        this.y = y;
        this.chessman = chessman;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Chessman getChessman() {
        return chessman;
    }

    public void setChessman(Chessman chessman) {
        this.chessman = chessman;
    }
}

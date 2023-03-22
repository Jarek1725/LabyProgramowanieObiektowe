package org.example.Models;

import java.util.HashMap;

public class Position {
    private int x;
    private int y;
    private Chessman chessman;

    private static final HashMap<Integer, String> xPositionDict = new HashMap<Integer, String>() {{
        put(0, "A");
        put(1, "B");
        put(2, "C");
        put(3, "D");
        put(4, "E");
        put(5, "F");
        put(6, "G");
        put(7, "H");
    }};

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

    @Override
    public String toString() {
        return xPositionDict.get(y) + (x+1);
    }
}

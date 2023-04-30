package org.example.Models;

import java.util.HashMap;

public class Position {
    private int x;
    private int y;
    private ChessmanAdapter chessmanAdapter;

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

    public Position(int x, int y, ChessmanAdapter chessmanAdapter) {
        this.x = x;
        this.y = y;
        this.chessmanAdapter = chessmanAdapter;
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

    public ChessmanAdapter getChessman() {
        return chessmanAdapter;
    }

    public void setChessman(ChessmanAdapter chessmanAdapter) {
        this.chessmanAdapter = chessmanAdapter;
    }

    @Override
    public String toString() {
        return xPositionDict.get(y) + (x+1);
    }
}

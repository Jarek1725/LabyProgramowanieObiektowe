package org.example.Models;

public abstract class Chessman {
    private final String name;
    private final String symbol;
    private final boolean isWhite;
    private Position position;

    public abstract boolean canMove(Position newPosition);

    public Chessman(String name, String symbol, boolean isWhite, Position position) {
        this.name = name;
        this.symbol = symbol;
        this.isWhite = isWhite;
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    public boolean isWhite() {
        return isWhite;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

}

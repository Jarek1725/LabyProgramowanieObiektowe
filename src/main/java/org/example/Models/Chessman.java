package org.example.Models;

public abstract class Chessman {
    private final String name;
    private final String symbol;
    private final boolean isWhite;

    public Chessman(String name, String symbol, boolean isWhite) {
        this.name = name;
        this.symbol = symbol;
        this.isWhite = isWhite;
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
}

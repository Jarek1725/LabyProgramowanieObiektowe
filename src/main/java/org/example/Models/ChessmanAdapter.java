package org.example.Models;

import java.util.List;

public abstract class ChessmanAdapter implements ChessmanFunctions{
    private final String name;
    private final String symbol;
    private final boolean isWhite;

    public ChessmanAdapter(String name, String symbol, boolean isWhite) {
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

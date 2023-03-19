package org.example.Models;

import java.util.List;

public class Rook extends Chessman {
    public Rook(boolean isWhite) {
        super("Rook", isWhite ? "\u265C" : "\u2656", isWhite);
    }

    @Override
    public boolean canMove(Position from, Position to) {
        return false;
    }

    @Override
    public List<String> getAvailableMoves(Position from) {
        return null;
    }
}

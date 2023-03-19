package org.example.Models;

import java.util.List;

public class King extends Chessman {
    public King(boolean isWhite) {
        super("King", isWhite ? "\u265A" : "\u2654", isWhite);
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


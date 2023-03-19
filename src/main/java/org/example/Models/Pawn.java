package org.example.Models;

import java.util.List;

public class Pawn extends Chessman {
    public Pawn(boolean isWhite) {
        super("Pawn", isWhite ? "\u265F" : "\u2659", isWhite);
    }

    @Override
    public boolean canMove(Position from, Position to) {
        return true;
    }

    @Override
    public List<String> getAvailableMoves(Position from) {
        return List.of("A6", "A5");
    }
}

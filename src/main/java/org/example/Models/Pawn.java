package org.example.Models;

public class Pawn extends Chessman {
    public Pawn(boolean isWhite, Position position) {
        super("Pawn", isWhite ? "\u265F" : "\u2659", isWhite, position);
    }

    @Override
    public boolean canMove(Position newPosition) {
        return false;
    }
}

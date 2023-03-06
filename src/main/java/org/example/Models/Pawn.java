package org.example.Models;

public class Pawn extends Chessman {
    public Pawn(boolean isWhite) {
        super("Pawn", isWhite ? "\u265F" : "\u2659", isWhite);
    }
}

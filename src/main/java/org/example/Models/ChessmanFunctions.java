package org.example.Models;

import java.util.List;

public interface ChessmanFunctions {
    public boolean canMove(Position from, Position to);
    public abstract List<String> getAvailableMoves(Position from, Board board);
}

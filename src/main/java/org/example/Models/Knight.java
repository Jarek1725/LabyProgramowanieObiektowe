package org.example.Models;

import java.util.Arrays;
import java.util.List;

public class Knight extends Chessman {
    public Knight(boolean isWhite) {
        super("Knight", isWhite ? "\u265e" : "\u2658", isWhite);
    }

    @Override
    public boolean canMove(Position from, Position to) {
        return ((Math.abs(from.getY() - to.getY()) == 1 && Math.abs(from.getX() - to.getX()) == 2)
                ||
                (Math.abs(from.getX() - to.getX()) == 1 && Math.abs(from.getY() - to.getY()) == 2)
        ) && CommonServices.isPositionEmptyOrEnemy(from, to);
    }

    @Override
    public List<String> getAvailableMoves(Position from, Board board) {
        return Arrays.stream(board.getPositions())
                .flatMap(Arrays::stream)
                .filter(to -> canMove(from, to))
                .map(Position::toString)
                .toList();
    }
}

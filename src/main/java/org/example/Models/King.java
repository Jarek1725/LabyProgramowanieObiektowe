package org.example.Models;

import java.util.ArrayList;
import java.util.Arrays;
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
    public List<String> getAvailableMoves(Position from, Board board) {
        List<String> result = new ArrayList<>();
        int x = from.getX();
        int y = from.getY();
        Arrays.stream(board.getPositions())
                .flatMap(Arrays::stream)
                .filter(to -> (Math.abs(to.getX() - x) <= 1) && (Math.abs(to.getY() - y) <= 1) && to.getChessman() == null)
                .forEach(to -> result.add(to.toString()));
        return result;
    }

}


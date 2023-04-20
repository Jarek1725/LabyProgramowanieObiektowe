package org.example.Models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class King extends ChessmanAdapter {
    public King(boolean isWhite) {
        super("King", isWhite ? "\u265A" : "\u2654", isWhite);
    }

    @Override
    public boolean canMove(Position from, Position to) {
        if ((Math.abs(to.getX() - from.getX()) <= 1) && (Math.abs(to.getY() - from.getY()) <= 1)) {
            if(to.getChessman() != null ){
                return to.getChessman().isWhite() != from.getChessman().isWhite();
            } else{
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> getAvailableMoves(Position from, Board board) {
        List<String> result = new ArrayList<>();
        int x = from.getX();
        int y = from.getY();
        Arrays.stream(board.getPositions())
                .flatMap(Arrays::stream)
                .filter(to -> canMove(from, to) && !board.isChessmanBetweenPositions(from, to))
                .forEach(to -> result.add(to.toString()));
        return result;
    }

}


package org.example.Models;

import java.util.Arrays;
import java.util.List;

public class Rook extends ChessmanAdapter {
    public Rook(boolean isWhite) {
        super("Rook", isWhite ? "\u265C" : "\u2656", isWhite);
    }

    @Override
    public boolean canMove(Position from, Position to) {
        return CommonServices.moveHorizontalOrVertical(from, to) && CommonServices.isPositionEmptyOrEnemy(from, to);
    }

    @Override
    public List<String> getAvailableMoves(Position from, Board board) {
        return Arrays.stream(board.getPositions())
                .flatMap(Arrays::stream)
                .filter(to -> canMove(from, to) && !board.isChessmanBetweenPositions(from, to))
                .map(Position::toString)
                .toList();
    }

}

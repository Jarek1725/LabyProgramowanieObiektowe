package org.example.Models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.List;

public class Bishop extends ChessmanAdapter {
    @JsonCreator
    public Bishop(@JsonProperty("white")boolean isWhite) {
        super("Bishop", isWhite ? "\u265d" : "\u2657", isWhite);
    }

    @Override
    public boolean canMove(Position from, Position to) {
        return CommonServices.moveAcross(from, to) && CommonServices.isPositionEmptyOrEnemy(from, to);
    }

    @Override
    public List<String> getAvailableMoves(Position from, Board board) {
        return Arrays.stream(board.getPositions())
                .flatMap(Arrays::stream)
                .filter(to -> !board.isChessmanBetweenPositions(from, to) && canMove(from, to))
                .map(Position::toString)
                .toList();
    }

}

package org.example.Models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.List;

public class Queen extends ChessmanAdapter {

    @JsonCreator
    public Queen(@JsonProperty("white")boolean isWhite) {
        super("Queen", isWhite ? "\u265b" : "\u2655", isWhite);
    }

    @Override
    public boolean canMove(Position from, Position to) {
        return (CommonServices.moveHorizontalOrVertical(from, to) || CommonServices.moveAcross(from, to))
        && CommonServices.isPositionEmptyOrEnemy(from, to);
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

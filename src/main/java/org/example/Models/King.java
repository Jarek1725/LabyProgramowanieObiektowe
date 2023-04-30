package org.example.Models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class King extends ChessmanAdapter {
    @JsonCreator
    public King(@JsonProperty("white")boolean isWhite) {
        super("King", isWhite ? "\u265A" : "\u2654", isWhite);
    }


    @JsonIgnore
    private boolean hasMoved = false;

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

    public boolean isHasMoved() {
        return hasMoved;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

}


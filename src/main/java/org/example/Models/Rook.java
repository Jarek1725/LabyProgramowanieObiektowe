package org.example.Models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Rook extends ChessmanAdapter {

    @JsonCreator
    public Rook(@JsonProperty("white")boolean isWhite) {
        super("Rook", isWhite ? "\u265C" : "\u2656", isWhite);
    }

    @Override
    public boolean canMove(Position from, Position to) {
        return CommonServices.moveHorizontalOrVertical(from, to) && CommonServices.isPositionEmptyOrEnemy(from, to);
    }

    @JsonIgnore
    private boolean hasMoved = false;

    @Override
    public List<String> getAvailableMoves(Position from, Board board) {

        return Arrays.stream(board.getPositions())
                .flatMap(Arrays::stream)
                .filter(to -> canMove(from, to) && !board.isChessmanBetweenPositions(from, to))
                .map(Position::toString)
                .collect(Collectors.toList());

    }


    public boolean isCastlingMovePossible(Position from, Board board) {
        if (hasMoved) {
            return false;
        }
        if (from.getChessman().isWhite()) {
            if (!board.isWhiteKingMoves()) {
                if (from.getY() == 0) {
                    if (!board.isChessmanBetweenPositions(from, board.getPositions()[0][4])) {
                        if(!board.canOpponentStandOnThisPosition(
                                List.of(
                                board.getPositions()[0][2],
                                board.getPositions()[0][3])
                        )){
                            return true;
                        }
                    }
                } else{
                    if(!board.isChessmanBetweenPositions(from, board.getPositions()[0][4])){
                        if(!board.canOpponentStandOnThisPosition(
                                List.of(
                                        board.getPositions()[0][5],
                                        board.getPositions()[0][6]
                                )
                        )){
                            return true;
                        }
                    }
                }
            }} else{
                if(!board.isBlackKingMoves()){
                    if (from.getY() == 0) {
                        if (!board.isChessmanBetweenPositions(from, board.getPositions()[7][4])) {
                            if(!board.canOpponentStandOnThisPosition(
                                    List.of(
                                            board.getPositions()[7][2],
                                            board.getPositions()[7][3])
                            )){
                                return true;
                            }
                        }
                    } else{
                        if(!board.isChessmanBetweenPositions(from, board.getPositions()[7][4])){
                            if(!board.canOpponentStandOnThisPosition(
                                    List.of(
                                            board.getPositions()[7][5],
                                            board.getPositions()[7][6]
                                    )
                            )){
                                return true;
                            }
                        }
                    }
                }
            }
        return false;
    }


    public boolean isHasMoved() {
        return hasMoved;
    }


    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

}

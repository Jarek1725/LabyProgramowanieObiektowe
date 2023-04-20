package org.example.Models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Pawn extends ChessmanAdapter {
    public Pawn(boolean isWhite) {
        super("Pawn", isWhite ? "\u265F" : "\u2659", isWhite);
    }

    @Override
    public boolean canMove(Position from, Position to) {
        if (to.getChessman() == null || from.getChessman().isWhite() != to.getChessman().isWhite()) {
            if (!from.getChessman().isWhite()) {
                if ((from.getX() == 6 && (to.getX() == 4 || to.getX() == 5) && from.getY() == to.getY()) && to.getChessman() == null) {
                    return true;
                } else if (from.getX() - to.getX() == 1 && from.getY() == to.getY() && to.getChessman() == null) {
                    return true;
                } else return to.getChessman() != null && to.getChessman().isWhite() && from.getX() == to.getX() + 1 && (from.getY() == to.getY() - 1 || from.getY() == to.getY() + 1);
            } else {
                if ((from.getX() == 1 && (to.getX() == 3 || to.getX() == 2) && from.getY() == to.getY()) && to.getChessman() == null) {
                    return true;
                } else if(to.getX() - from.getX() == 1 && from.getY() == to.getY() && to.getChessman() == null){
                    return true;
                } else return to.getChessman() != null && !to.getChessman().isWhite() && from.getX() == to.getX() - 1 && (from.getY() == to.getY() - 1 || from.getY() == to.getY() + 1);
            }
        }
        return false;
    }

    private boolean canEnPassantCapture(Position from, Position to, Board board) {
        Position enPassantTarget = board.getEnPassantTarget();
        if (enPassantTarget == null) {
            return false;
        }

        int dx = Math.abs(from.getY() - to.getY());
        int dy = to.getX() - from.getX();
        int direction = (isWhite()) ? 1 : -1;

        return dx == 1 && dy == direction && enPassantTarget.equals(to);
    }

    @Override
    public List<String> getAvailableMoves(Position from, Board board) {
        List<String> standardMoves = Arrays.stream(board.getPositions())
                .flatMap(Arrays::stream)
                .filter(to -> canMove(from, to) && !board.isChessmanBetweenPositions(from, to))
                .map(Position::toString)
                .toList();

        List<String> enPassantCaptureMoves = Arrays.stream(board.getPositions())
                .flatMap(Arrays::stream)
                .filter(to -> canEnPassantCapture(from, to, board))
                .map(Position::toString)
                .toList();

        List<String> allMoves = new ArrayList<>();
        allMoves.addAll(standardMoves);
        allMoves.addAll(enPassantCaptureMoves);

        return allMoves;
    }}

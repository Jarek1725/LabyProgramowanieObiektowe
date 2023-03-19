package org.example.Models;

import java.util.*;

public class Board {
    private final Position[][] positions = new Position[8][8];
    private boolean isWhiteTurn = false;
    private boolean isEnd = false;

    private final Scanner scanner = new Scanner(System.in);

    public boolean makeMove(Position from, Position to) {
        if (from.getChessman().canMove(from, to) && from.getChessman().isWhite() == isWhiteTurn) {
            to.setChessman(from.getChessman());
            from.setChessman(null);
            isWhiteTurn = !isWhiteTurn;
            drawBoard(Collections.emptyList());
            return true;
        } else
            drawBoard(List.of("Invalid move"));
        return false;
    }

    public void startGame() {
        drawBoard(Collections.emptyList());

        System.out.print("Select chessman to move, for example 'A2': ");
        String positionStartCoords = scanner.nextLine();
        Position positionStart = getChessmanAtPosition(positionStartCoords);
        if (positionStart.getChessman() == null) {
            drawBoard(List.of("No chessman at this position"));
            return;
        } else {
            List<String> availableMoves = positionStart.getChessman().getAvailableMoves(positionStart);
            if (availableMoves.isEmpty()) {
                drawBoard(List.of("No available moves"));
                return;
            } else {
                drawBoard(List.of("Available moves: " + availableMoves.stream().reduce((s, s2) -> s + ", " + s2).get()));
                System.out.print("Select position to move to, for example 'A3': ");
                String positionEndCoords = scanner.nextLine();
                Position positionEnd = getChessmanAtPosition(positionEndCoords);
                if (availableMoves.contains(positionEndCoords)) {
                    makeMove(positionStart, positionEnd);
                } else {
                    drawBoard(List.of("Invalid move"));
                }
            }
        }

//        makeMove(getChessmanAtPosition(positionStart), getChessmanAtPosition("A5"));
    }

    private List<String> getAvailableMoves(Position positionStart) {
        return positionStart.getChessman().getAvailableMoves(positionStart);
    }

    public Position getChessmanAtPosition(String position) {
        int x = Character.toUpperCase(position.charAt(0)) - 65;
        int y = Integer.parseInt(position.substring(1)) - 1;
        return Arrays.stream(positions).filter(row -> row[y].getX() == y).findFirst().get()[x];
    }


    public Board() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (i == 1) {
                    positions[i][j] = new Position(i, j, new Pawn(true));
                } else if (i == 6)
                    positions[i][j] = new Position(i, j, new Pawn(false));
                else
                    positions[i][j] = new Position(i, j, null);
            }
        }

        positions[0][4] = new Position(0, 4, new King(true));
        positions[7][3] = new Position(0, 4, new King(false));

        positions[0][0] = new Position(0, 0, new Rook(true));
        positions[0][7] = new Position(0, 0, new Rook(true));
        positions[7][0] = new Position(0, 0, new Rook(false));
        positions[7][7] = new Position(0, 0, new Rook(false));
    }

    public void drawBoard(List<String> additionalInformation) {
        List<String> informationToDraw = new ArrayList<>(additionalInformation);
        informationToDraw.add(isWhiteTurn ? "White turn" : "Black turn");
        System.out.println();
        System.out.println("--------------------------------------------------------");
        System.out.println();
        System.out.println("   A\u2003B\u2003C\u2003D\u2003E\u2003F\u2003G\u2003H");
        for (int i = 0; i < 8; i++) {
            System.out.print((i + 1) + "  ");
            for (int j = 0; j < 8; j++) {
                if (positions[i][j].getChessman() == null) {
                    System.out.print("\u2003 ");
                } else {
                    System.out.print(positions[i][j].getChessman().getSymbol() + " ");
                }
            }
            System.out.printf(" |   ");
            if (i < informationToDraw.size()) {
                System.out.print(informationToDraw.get(i));
            }
            System.out.println();
        }
    }

    public boolean isEnd() {
        return isEnd;
    }

    public void setEnd(boolean end) {
        isEnd = end;
    }
}

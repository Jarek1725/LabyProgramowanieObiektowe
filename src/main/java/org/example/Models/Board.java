package org.example.Models;

import java.util.*;

public class Board {
    private final Position[][] positions = new Position[8][8];
    private boolean isWhiteTurn = true;
    private boolean isEnd = false;
    private final Scanner scanner = new Scanner(System.in);

    public void makeMove(Position from, Position to) {
        to.setChessman(from.getChessman());
        from.setChessman(null);
        isWhiteTurn = !isWhiteTurn;
        drawBoard(Collections.emptyList());
    }

    public void startGame() {
        drawBoard(Collections.emptyList());
        Position positionStart = null;
        while (!isEnd) {
            positionStart = selectChessman();
            if (positionStart != null) {
                operationsOnSelectedChessman(positionStart);
            } else {
                drawBoard(List.of("Invalid chessman"));
            }
        }
    }

    public boolean isChessmanBetweenPositions(Position from, Position to) {
        int x1 = from.getX();
        int y1 = from.getY();
        int x2 = to.getX();
        int y2 = to.getY();

        if (x1 == x2) {
            int yMin = Math.min(y1, y2);
            int yMax = Math.max(y1, y2);
            for (int i = yMin + 1; i < yMax; i++) {
                if (positions[x1][i].getChessman() != null) {
                    return true;
                }
            }
        }
        if (y1 == y2) {
            int xMin = Math.min(x1, x2);
            int xMax = Math.max(x1, x2);
            for (int i = xMin + 1; i < xMax; i++) {
                if (positions[i][y1].getChessman() != null) {
                    return true;
                }
            }
        }

        if (Math.abs(x1 - x2) == Math.abs(y1 - y2)) {
            int xDirection = (x2 > x1) ? 1 : -1;
            int yDirection = (y2 > y1) ? 1 : -1;
            int x = x1 + xDirection;
            int y = y1 + yDirection;

            while (x >= 0 && x < positions.length && y >= 0 && y < positions[x].length && x != x2 && y != y2) {
                if (positions[x][y].getChessman() != null) {
                    return true;
                }
                x += xDirection;
                y += yDirection;
            }
        }
        return false;
    }

    private void operationsOnSelectedChessman(Position positionStart) {
        List<String> availableMoves = positionStart.getChessman().getAvailableMoves(positionStart, this);
        if (availableMoves.isEmpty()) {
            drawBoard(List.of("No available moves"));
        } else {
            drawBoard(List.of("Available moves: " + availableMoves.stream().reduce((s, s2) -> s + ", " + s2).get()));
            selectPositionEnd(availableMoves, positionStart);
        }
    }

    private Position selectChessman() {
        System.out.print("Select chessman to move, for example 'A2': ");
        String positionStartCoords = scanner.nextLine();
        return isSelectedChessmanValid(positionStartCoords) ? getChessmanAtPosition(positionStartCoords) : null;
    }

    private boolean isSelectedChessmanValid(String positionStartCoords) {
        return getChessmanAtPosition(positionStartCoords) != null
                && getChessmanAtPosition(positionStartCoords).getChessman() != null
                && !isEnd
                && getChessmanAtPosition(positionStartCoords).getChessman().isWhite() == isWhiteTurn;
    }

    private void selectPositionEnd(List<String> availableMoves, Position positionStart) {
        System.out.print("Select position to move to, for example 'A3': ");
        String positionEndCoords = scanner.nextLine();
        Position chessmanAtPosition = getChessmanAtPosition(positionEndCoords);
        if (availableMoves.contains(positionEndCoords)) {
            makeMove(positionStart, chessmanAtPosition);
        } else {
            drawBoard(List.of("Invalid move"));
        }
    }

    private List<String> getAvailableMoves(Position positionStart) {
        return positionStart.getChessman().getAvailableMoves(positionStart, this);
    }

    public Position getChessmanAtPosition(String position) {
        try {
            int x = Character.toUpperCase(position.charAt(0)) - 65;
            int y = Integer.parseInt(position.substring(1)) - 1;
            return Arrays.stream(positions).filter(row -> row[y].getX() == y).findFirst().get()[x];
        } catch (Exception e) {
            drawBoard(List.of("Invalid position"));
        }
        ;
        return null;
    }


    public Board() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (i == 1) {
                    positions[i][j] = new Position(i, j, new Pawn(true));
                } else if (i == 6) {
                    positions[i][j] = new Position(i, j, new Pawn(false));
                } else
                    positions[i][j] = new Position(i, j, null);
            }
        }

        positions[0][4] = new Position(0, 4, new King(true));
        positions[7][3] = new Position(7, 3, new King(false));

        positions[0][0] = new Position(0, 0, new Rook(true));
        positions[0][7] = new Position(0, 7, new Rook(true));
        positions[7][0] = new Position(7, 0, new Rook(false));
        positions[7][7] = new Position(7, 7, new Rook(false));

        positions[0][3] = new Position(0, 3, new Queen(true));
        positions[7][4] = new Position(7, 4, new Queen(false));

        positions[0][1] = new Position(0, 1, new Knight(true));
        positions[0][6] = new Position(0, 6, new Knight(true));
        positions[7][6] = new Position(7, 6, new Knight(false));
        positions[7][1] = new Position(7, 1, new Knight(false));

        positions[0][2] = new Position(0, 2, new Bishop(true));
        positions[0][5] = new Position(0, 5, new Bishop(true));
        positions[7][5] = new Position(7, 5, new Bishop(false));
        positions[7][2] = new Position(7, 2, new Bishop(false));
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

    public Position[][] getPositions() {
        return positions;
    }
}

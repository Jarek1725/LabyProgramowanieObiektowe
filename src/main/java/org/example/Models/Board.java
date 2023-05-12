package org.example.Models;

import java.util.*;

public class Board {
    private final Position[][] positions = new Position[8][8];
    private boolean isWhiteTurn = true;
    private boolean isEnd = false;
    private final Scanner scanner = new Scanner(System.in);
    private Position enPassantTarget;
    private boolean whiteKingMoves;
    private boolean blackKingMoves;
    private List<String> properMoves = new ArrayList<>();
    private String selectedPosition;
    private String lastMovedTo;

    public boolean isWhiteKingMoves() {
        return whiteKingMoves;
    }

    public void setWhiteKingMoves(boolean whiteKingMoves) {
        this.whiteKingMoves = whiteKingMoves;
    }

    public boolean isBlackKingMoves() {
        return blackKingMoves;
    }

    public void setBlackKingMoves(boolean blackKingMoves) {
        this.blackKingMoves = blackKingMoves;
    }

    public void makeMove(Position from, Position to) {
        if (getEnPassantTarget() != null) {
            if (to.getX() == getEnPassantTarget().getX() && to.getY() == getEnPassantTarget().getY()) {
                if (from.getChessman().isWhite()) {
                    positions[getEnPassantTarget().getX() - 1][getEnPassantTarget().getY()].setChessman(null);
                } else {
                    positions[getEnPassantTarget().getX() + 1][getEnPassantTarget().getY()].setChessman(null);
                }
            }
        }
        if (from.getChessman() instanceof Pawn) {
            if (from.getX() == 1 && to.getX() == 3) {
                enPassantTarget = positions[from.getX() + 1][from.getY()];
            } else if (from.getX() == 6 && to.getX() == 4) {
                enPassantTarget = positions[from.getX() - 1][from.getY()];
            } else {
                enPassantTarget = null;
            }
        } else {
            enPassantTarget = null;
        }

        if (from.getChessman() instanceof King || from.getChessman() instanceof Rook) {
            if (from.getChessman() instanceof King) {
                ((King) from.getChessman()).setHasMoved(true);
                if (from.getChessman().isWhite()) {
                    whiteKingMoves = true;
                } else {
                    blackKingMoves = true;
                }
            } else {
                ((Rook) from.getChessman()).setHasMoved(true);
            }
        }


        boolean kingIfCastled = createKingIfCastled(from, to);
        if (!kingIfCastled) {
            to.setChessman(from.getChessman());
            from.setChessman(null);
        }

//        upgradePawn(to);

//        drawBoard(Collections.emptyList());
    }

    private boolean createKingIfCastled(Position from, Position to) {
        if (to.getChessman() instanceof King) {
            if (!whiteKingMoves) {
                if (to.getX() == 0) {
                    if (from.getY() == 0) {
                        positions[0][2].setChessman(new King(true));
                        positions[0][3].setChessman(new Rook(true));
                    } else {
                        positions[0][6].setChessman(new King(true));
                        positions[0][5].setChessman(new Rook(true));
                    }
                }
                from.setChessman(null);
                to.setChessman(null);
                setWhiteKingMoves(true);
                return true;
            }
        }
        if (to.getChessman() instanceof King) {
            if (!blackKingMoves) {
                if (to.getX() == 7) {
                    if (from.getY() == 0) {
                        positions[7][2].setChessman(new King(false));
                        positions[7][3].setChessman(new Rook(false));
                    } else {
                        positions[7][6].setChessman(new King(false));
                        positions[7][5].setChessman(new Rook(false));
                    }
                }
                setBlackKingMoves(true);
                from.setChessman(null);
                to.setChessman(null);
                return true;
            }
        }
        return false;
    }

    public String isCheck() {
        Position kingPosition = Arrays.stream(positions)
                .flatMap(Arrays::stream)
                .filter(position -> position.getChessman() instanceof King && position.getChessman().isWhite() == isWhiteTurn)
                .findFirst()
                .orElse(null);
        boolean availableMovesForPosition = canOpponentStandOnThisPosition(List.of(kingPosition));
        boolean canKingMove = !getAvailableMoves(kingPosition).isEmpty();
        if (availableMovesForPosition && !canKingMove) {
            return "Checkmate";
        } else if (availableMovesForPosition) {
            return "Check";
        }
        return null;
    }

    public void startGame() {
//        drawBoard(Collections.emptyList());
        Position positionStart;
        while (!isEnd) {
            Position kingPosition = Arrays.stream(positions)
                    .flatMap(Arrays::stream)
                    .filter(position -> position.getChessman() instanceof King && position.getChessman().isWhite() == isWhiteTurn)
                    .findFirst()
                    .orElse(null);
            boolean availableMovesForPosition = canOpponentStandOnThisPosition(List.of(kingPosition));
            boolean canKingMove = !getAvailableMoves(kingPosition).isEmpty();
            if (availableMovesForPosition && !canKingMove) {
                drawBoard(List.of("Checkmate"));
                isEnd = true;
            } else if (availableMovesForPosition) {
                drawBoard(List.of("Check"));
            }
            positionStart = selectChessman();
            if (positionStart != null) {
                operationsOnSelectedChessman(positionStart);
            } else {
                drawBoard(List.of("Invalid chessman"));
            }
        }
    }

    private void upgradePawn(Position position) {
        ChessmanAdapter chessman = position.getChessman();
        int x = position.getX();

        if (!(chessman instanceof Pawn)) {
            return;
        }

        if (chessman.isWhite() && x == 7 || !chessman.isWhite() && x == 0) {
            System.out.print("Choose chessman to upgrade: (Queen | Rook | Bishop | Knight) ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "Queen":
                    chessman = new Queen(chessman.isWhite());
                    break;
                case "Rook":
                    chessman = new Rook(chessman.isWhite());
                    break;
                case "Bishop":
                    chessman = new Bishop(chessman.isWhite());
                    break;
                case "Knight":
                    chessman = new Knight(chessman.isWhite());
                    break;
                default:
                    System.out.println("Invalid chessman");
                    upgradePawn(position);
                    return;
            }

            position.setChessman(chessman);
        }
    }


    public boolean canOpponentStandOnThisPosition(List<Position> tos) {
        return Arrays.stream(positions)
                .flatMap(Arrays::stream)
                .filter(position -> position.getChessman() != null && isWhiteTurn != position.getChessman().isWhite())
                .anyMatch(e -> tos.stream().anyMatch(toss -> getAvailableMoves(e).contains(toss.toString())));
    }


    public boolean isChessmanBetweenPositions(Position from, Position to) {
        int x1 = from.getX();
        int y1 = from.getY();
        int x2 = to.getX();
        int y2 = to.getY();

        if (x1 == x2) {
            return isChessmanBetweenPositionsInColumn(x1, y1, y2);
        }
        if (y1 == y2) {
            return isChessmanBetweenPositionsInRow(y1, x1, x2);
        }
        if (Math.abs(x1 - x2) == Math.abs(y1 - y2)) {
            return isChessmanBetweenPositionsInDiagonal(x1, y1, x2, y2);
        }
        return false;
    }

    private boolean isChessmanBetweenPositionsInColumn(int x, int y1, int y2) {
        return isChessmanBetweenPositionsInLine(x, y1, y2, true);
    }

    private boolean isChessmanBetweenPositionsInRow(int y, int x1, int x2) {
        return isChessmanBetweenPositionsInLine(y, x1, x2, false);
    }

    private boolean isChessmanBetweenPositionsInLine(int fixedCoordinate, int start, int end, boolean isColumn) {
        int min = Math.min(start, end);
        int max = Math.max(start, end);
        for (int i = min + 1; i < max; i++) {
            if (isColumn) {
                if (positions[fixedCoordinate][i].getChessman() != null) {
                    return true;
                }
            } else {
                if (positions[i][fixedCoordinate].getChessman() != null) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isChessmanBetweenPositionsInDiagonal(int x1, int y1, int x2, int y2) {
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
        return false;
    }

    public void operationsOnSelectedChessman(Position positionStart) {
        List<String> availableMoves = positionStart.getChessman().getAvailableMoves(positionStart, this);
        if (positionStart.getChessman() instanceof Rook) {
            if (((Rook) positionStart.getChessman()).isCastlingMovePossible(positionStart, this)) {
                if (isWhiteTurn) {
                    availableMoves.add(positions[0][4].toString());
                } else {
                    availableMoves.add(positions[7][4].toString());
                }
            }
        }
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

    public List<String> userSelectedChessman(String position) {
        Position position1 = isSelectedChessmanValid(position) ? getChessmanAtPosition(position) : null;
        List<String> additionalInformation = new ArrayList<>();
        if (position1 != null) {
            List<String> availableMoves = position1.getChessman().getAvailableMoves(position1, this);
            if (position1.getChessman() instanceof Rook) {
                if (((Rook) position1.getChessman()).isCastlingMovePossible(position1, this)) {
                    if (isWhiteTurn) {
                        availableMoves.add(positions[0][4].toString());
                    } else {
                        availableMoves.add(positions[7][4].toString());
                    }
                }
            }
            if (availableMoves.isEmpty()) {
                return Collections.emptyList();
            } else {
                additionalInformation = List.of("Available moves: " + availableMoves.stream().reduce((s, s2) -> s + ", " + s2).get());
                setProperMoves(availableMoves);
                properMoves = availableMoves;
            }
        }
        return additionalInformation;
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
        positions[7][4] = new Position(7, 4, new King(false));

        positions[0][0] = new Position(0, 0, new Rook(true));
        positions[0][7] = new Position(0, 7, new Rook(true));
        positions[7][0] = new Position(7, 0, new Rook(false));
        positions[7][7] = new Position(7, 7, new Rook(false));

        positions[0][3] = new Position(0, 3, new Queen(true));
        positions[7][3] = new Position(7, 3, new Queen(false));

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

    public Position getEnPassantTarget() {
        return enPassantTarget;
    }

    public void setEnPassantTarget(Position enPassantTarget) {
        this.enPassantTarget = enPassantTarget;
    }


    public boolean isWhiteTurn() {
        return isWhiteTurn;
    }

    public void setWhiteTurn(boolean whiteTurn) {
        isWhiteTurn = whiteTurn;
    }

    public void setEnd(boolean end) {
        isEnd = end;
    }

    public Scanner getScanner() {
        return scanner;
    }

    public List<String> getProperMoves() {
        return properMoves;
    }

    public void setProperMoves(List<String> properMoves) {
        this.properMoves = properMoves;
    }

    public String getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(String selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    public Position stringToPosition(String position) {
        return getChessmanAtPosition(position);
    }

    public String getLastMovedTo() {
        return lastMovedTo;
    }

    public void setLastMovedTo(String lastMovedTo) {
        this.lastMovedTo = lastMovedTo;
    }
}

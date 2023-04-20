package org.example.Models;

public class CommonServices {
    public static boolean isPositionEmptyOrEnemy(Position from, Position to) {
        return to.getChessman() == null || to.getChessman().isWhite() != from.getChessman().isWhite();
    }

    public static boolean moveHorizontalOrVertical(Position from, Position to) {
        return from.getY() == to.getY() || from.getX() == to.getX();
    }

    public static boolean moveAcross(Position from, Position to) {
        return Math.abs(from.getX() - to.getX()) == Math.abs(from.getY() - to.getY());
    }
}

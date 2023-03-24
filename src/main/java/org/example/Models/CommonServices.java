package org.example.Models;

public class CommonServices {
    public static boolean isPositionEmptyOrEnemy(Position from, Position to){
        return to.getChessman() == null || to.getChessman().isWhite() != from.getChessman().isWhite();
    }
}

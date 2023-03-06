package org.example.Models;

public record Position(int xCoordinate, int yCoordinate) {
    public Position {
        if (xCoordinate < 0 || xCoordinate > 7) {
            throw new IllegalArgumentException("X coordinate must be between 0 and 7");
        }
        if (yCoordinate < 0 || yCoordinate > 7) {
            throw new IllegalArgumentException("Y coordinate must be between 0 and 7");
        }
    }
}

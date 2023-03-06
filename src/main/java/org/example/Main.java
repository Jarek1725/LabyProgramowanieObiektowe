package org.example;

import org.example.Models.Pawn;
import org.example.Models.Position;

import java.awt.*;
import java.io.Console;

public class Main {
    public static void main(String[] args) {
        Pawn pawn = new Pawn(true);
        Position position = new Position(1, 1, pawn);
        System.out.println(position.chessman().getSymbol());
    }
}

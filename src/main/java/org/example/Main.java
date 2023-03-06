package org.example;

import org.example.Models.Pawn;
import org.example.Models.Position;

public class Main {
    public static void main(String[] args) {
        Pawn pawn = new Pawn(true, new Position(0, 0));



        System.out.println(pawn.getSymbol());
    }
}

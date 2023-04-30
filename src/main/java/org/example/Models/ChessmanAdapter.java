package org.example.Models;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;



@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "name")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Knight.class, name = "Knight"),
        @JsonSubTypes.Type(value = Bishop.class, name = "Bishop"),
        @JsonSubTypes.Type(value = Pawn.class, name = "Pawn"),
        @JsonSubTypes.Type(value = King.class, name = "King"),
        @JsonSubTypes.Type(value = Queen.class, name = "Queen"),
        @JsonSubTypes.Type(value = Rook.class, name = "Rook"),
})

public abstract class ChessmanAdapter implements ChessmanFunctions{
    private final String name;
    private final String symbol;
    private final boolean isWhite;

    public ChessmanAdapter(String name, String symbol, boolean isWhite) {
        this.name = name;
        this.symbol = symbol;
        this.isWhite = isWhite;
    }


    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    public boolean isWhite() {
        return isWhite;
    }
}

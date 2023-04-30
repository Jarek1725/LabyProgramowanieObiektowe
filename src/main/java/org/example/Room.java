package org.example;

import org.example.Models.Board;
import org.java_websocket.WebSocket;

import java.util.HashSet;
import java.util.Set;

public class Room {
    private int roomNumber;
    private WebSocket whitePlayer;
    private WebSocket blackPlayer;
    private Board board;

    public Room(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public void broadcast(String message) {
        whitePlayer.send(message);
        blackPlayer.send(message);
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public WebSocket getWhitePlayer() {
        return whitePlayer;
    }

    public void setWhitePlayer(WebSocket whitePlayer) {
        this.whitePlayer = whitePlayer;
    }

    public WebSocket getBlackPlayer() {
        return blackPlayer;
    }

    public void setBlackPlayer(WebSocket blackPlayer) {
        this.blackPlayer = blackPlayer;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }
}

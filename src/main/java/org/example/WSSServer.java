package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.example.Models.Board;
import org.example.Models.GameInfo;
import org.example.Models.Position;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WSSServer extends WebSocketServer {
    private Map<Integer, Room> rooms;
    ObjectMapper objectMapper = new ObjectMapper();


    public WSSServer(InetSocketAddress address) {
        super(address);
        rooms = new HashMap<>();
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("New connection from " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("Closed connection to " + conn.getRemoteSocketAddress() + " with exit code " + code);
        removeClientFromRoom(conn);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("Message from client: " + message);

        if (message.startsWith("joinRoom:")) {
            int roomNumber = Integer.parseInt(message.split(":")[1]);
            try {
                joinRoom(conn, roomNumber);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Client " + conn.getRemoteSocketAddress() + " joined room " + roomNumber);
        } else if (message.startsWith("selectedChessman:")) {
            String selectedChessman = message.split(":")[1];
            Room room = this.getRoomViaPlayer(conn);
            if (room != null) {
                if (room.getBoard().isWhiteTurn()) {
                    if (conn.equals(room.getWhitePlayer())) {
                        List<String> availableMoves = room.getBoard().userSelectedChessman(selectedChessman);
                        GameInfo gameInfo = new GameInfo();
                        gameInfo.setPositions(room.getBoard().getPositions());
                        List<String> additionalInfo = new ArrayList<>();
                        additionalInfo.add("Select destination");
                        if (availableMoves.isEmpty()) {
                            gameInfo.setWrongSelection(true);
                        } else {
                            additionalInfo.addAll(availableMoves);
                            room.getBoard().setSelectedPosition(selectedChessman);
                        }
                        gameInfo.setGameInfo(additionalInfo);
                        gameInfo.setYourTurn(true);
                        String jsonString = null;
                        try {
                            jsonString = objectMapper.writeValueAsString(gameInfo);
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                        room.getWhitePlayer().send(jsonString);
                    }
                }
            }
        } else if (message.startsWith("selectedPositionToMove:")) {
            String selectedPosition = message.split(":")[1];
            Room room = this.getRoomViaPlayer(conn);
            if (room != null) {
                if (room.getBoard().isWhiteTurn()) {
                    if (conn.equals(room.getWhitePlayer())) {
                        if (room.getBoard().getProperMoves().contains(selectedPosition)) {

                            Position chessmanAtPosition = room.getBoard().getChessmanAtPosition(room.getBoard().getSelectedPosition());
                            room.getBoard().makeMove(chessmanAtPosition, room.getBoard().getChessmanAtPosition(selectedPosition));
                            GameInfo gameInfo = new GameInfo();
                            gameInfo.setPositions(room.getBoard().getPositions());
                            List<String> additionalInfo = new ArrayList<>();
                            additionalInfo.add("Waiting for opponent");
                            gameInfo.setGameInfo(additionalInfo);
                            gameInfo.setYourTurn(false);
                            String jsonString = null;
                            try {
                                jsonString = objectMapper.writeValueAsString(gameInfo);
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }
                            room.getWhitePlayer().send(jsonString);

                            gameInfo.setYourTurn(true);
                            gameInfo.setSelecting(true);
                            jsonString = null;
                            try {
                                jsonString = objectMapper.writeValueAsString(gameInfo);
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }
                            room.getBlackPlayer().send(jsonString);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("An error occurred on connection " + conn.getRemoteSocketAddress() + ":" + ex);
    }

    @Override
    public void onStart() {
        System.out.println("Server started successfully");
    }

    private void joinRoom(WebSocket conn, int roomNumber) throws JsonProcessingException {
        Room room = rooms.get(roomNumber);
        if (room == null) {
            room = new Room(roomNumber);
            rooms.put(roomNumber, room);
            room.setWhitePlayer(conn);
            room.setBoard(new Board());
            GameInfo gameInfo = new GameInfo();
            gameInfo.setPositions(room.getBoard().getPositions());
            List<String> additionalInfo = new ArrayList<>();
            additionalInfo.add("Waiting for player...");
            gameInfo.setGameInfo(additionalInfo);
            gameInfo.setYourTurn(false);
            String jsonString = objectMapper.writeValueAsString(gameInfo);
            room.getWhitePlayer().send(jsonString);
        } else {
            room.setBlackPlayer(conn);
            GameInfo gameInfo = new GameInfo();
            gameInfo.setPositions(room.getBoard().getPositions());
            List<String> additionalInfo = new ArrayList<>();
            additionalInfo.add("Starting game");
            gameInfo.setGameInfo(additionalInfo);
            gameInfo.setYourTurn(false);
            String jsonString = objectMapper.writeValueAsString(gameInfo);
            room.getBlackPlayer().send(jsonString);
            gameInfo.setYourTurn(true);
            gameInfo.setSelecting(true);
            jsonString = objectMapper.writeValueAsString(gameInfo);
            room.getWhitePlayer().send(jsonString);
        }
    }

    private Room getRoomViaPlayer(WebSocket player) {
        for (Room room : rooms.values()) {
            if (room.getWhitePlayer() != null) {
                boolean equals = room.getWhitePlayer().equals(player);
                if (equals) {
                    return room;
                }
            }

            if (room.getBlackPlayer() != null) {
                boolean equals = room.getBlackPlayer().equals(player);
                if (equals) {
                    return room;
                }
            }
        }
        return null;
    }

    private void removeClientFromRoom(WebSocket conn) {
        for (Room room : rooms.values()) {
            if (room.getWhitePlayer() != null) {
                boolean equals = room.getWhitePlayer().equals(conn);
                if (equals) {
                    room.setWhitePlayer(null);
                    return;
                }
            }

            if (room.getBlackPlayer() != null) {
                boolean equals = room.getBlackPlayer().equals(conn);
                if (equals) {
                    room.setBlackPlayer(null);
                    return;
                }
            }

        }
    }
}

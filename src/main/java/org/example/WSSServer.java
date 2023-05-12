package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.Models.*;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
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
            handleJoinRoomMessage(conn, message);
        } else if (message.startsWith("selectedChessman:")) {
            handleSelectedChessmanMessage(conn, message);
        } else if (message.startsWith("selectedPositionToMove:")) {
            handleSelectedPositionToMoveMessage(conn, message);
        } else if (message.startsWith("upgradePawn:")) {
            handleUpgradePawn(conn, message);
        }
    }

    private void handleUpgradePawn(WebSocket conn, String message) {
        Room room = getRoomViaPlayer(conn);
        String pawnUpgradedTo = message.split(":")[1];
        ChessmanAdapter chessman = room.getBoard().getChessmanAtPosition(room.getBoard().getLastMovedTo()).getChessman();
        switch (pawnUpgradedTo) {
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
                sendMessageToUpgradePawn(conn, room);
                return;
        }

        room.getBoard().getChessmanAtPosition(room.getBoard().getLastMovedTo()).setChessman(chessman);
        room.getBoard().setWhiteTurn(!room.getBoard().isWhiteTurn());

        GameInfo gameInfoForCurrentPlayer = createGameInfoForOpponent(room);
        String action = room.getBoard().getSelectedPosition() + " -> " + room.getBoard().getLastMovedTo();
        gameInfoForCurrentPlayer.getGameInfo().add(action);
        gameInfoForCurrentPlayer.getGameInfo().add("Upgraded pawn to: " + pawnUpgradedTo);
        sendGameInfoToCurrentPlayer(conn, room, gameInfoForCurrentPlayer);

        GameInfo gameInfoForOpponent = createGameInfoForCurrentPlayer(room);
        gameInfoForCurrentPlayer.getGameInfo().add(action);
        sendGameInfoToOpponent(conn, room, gameInfoForOpponent);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("An error occurred on connection " + conn.getRemoteSocketAddress() + ":" + ex);
    }

    @Override
    public void onStart() {
        System.out.println("Server started successfully");
    }

    private void handleJoinRoomMessage(WebSocket conn, String message) {
        int roomNumber = Integer.parseInt(message.split(":")[1]);
        try {
            joinRoom(conn, roomNumber);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Client " + conn.getRemoteSocketAddress() + " joined room " + roomNumber);
    }

    private void handleSelectedChessmanMessage(WebSocket conn, String message) {
        String selectedChessman = message.split(":")[1];
        Room room = getRoomViaPlayer(conn);
        if (room != null && isPlayerInRoom(conn, room)) {
            List<String> availableMoves = room.getBoard().userSelectedChessman(selectedChessman);
            GameInfo gameInfo = createGameInfo(room, availableMoves);
            room.getBoard().setSelectedPosition(selectedChessman);
            sendGameInfoToCurrentPlayer(conn, room, gameInfo);
        }
    }

    private void handleSelectedPositionToMoveMessage(WebSocket conn, String message) {
        String selectedPosition = message.split(":")[1];
        Room room = getRoomViaPlayer(conn);
        if (room != null && isPlayerInRoom(conn, room)) {
            if (room.getBoard().getProperMoves().contains(selectedPosition)) {
                executeMoveAndUpdatePlayers(conn, room, selectedPosition);
            } else {
                sendWrongMoveInformation(conn, room);
            }
        }
    }

    private void sendWrongMoveInformation(WebSocket conn, Room room) {
        GameInfo gameInfoForCurrentPlayer = createGameInfoForOpponent(room);
        gameInfoForCurrentPlayer.setWrongMove(true);
        gameInfoForCurrentPlayer.setGameInfo(new ArrayList<>());
        sendGameInfoToCurrentPlayer(conn, room, gameInfoForCurrentPlayer);
    }

    private void sendMessageToUpgradePawn(WebSocket conn, Room room) {
        GameInfo gameInfoForCurrentPlayer = createGameInfoForCurrentPlayer(room);
        gameInfoForCurrentPlayer.setUpgradePawn(true);
        sendGameInfoToCurrentPlayer(conn, room, gameInfoForCurrentPlayer);
    }

    private void executeMoveAndUpdatePlayers(WebSocket conn, Room room, String selectedPosition) {
        Position chessmanAtPosition = room.getBoard().getChessmanAtPosition(room.getBoard().getSelectedPosition());
        room.getBoard().makeMove(chessmanAtPosition, room.getBoard().getChessmanAtPosition(selectedPosition));

        Position position = room.getBoard().getChessmanAtPosition(selectedPosition);
        room.getBoard().setLastMovedTo(selectedPosition);
        if (position.getChessman() instanceof Pawn) {
            if (position.getChessman().isWhite() && position.getX() == 7 || !position.getChessman().isWhite() && position.getX() == 0) {
                sendMessageToUpgradePawn(conn, room);
                return;
            }
        }


        room.getBoard().setWhiteTurn(!room.getBoard().isWhiteTurn());

        GameInfo gameInfoForCurrentPlayer = createGameInfoForOpponent(room);
        String action = room.getBoard().getSelectedPosition() + " -> " + selectedPosition;
        gameInfoForCurrentPlayer.getGameInfo().add(action);
        if(room.getBoard().isCheck()!=null){
            gameInfoForCurrentPlayer.getGameInfo().add(room.getBoard().isCheck());
        }
        sendGameInfoToCurrentPlayer(conn, room, gameInfoForCurrentPlayer);

        GameInfo gameInfoForOpponent = createGameInfoForCurrentPlayer(room);
        gameInfoForCurrentPlayer.getGameInfo().add(action);
        sendGameInfoToOpponent(conn, room, gameInfoForOpponent);

    }

    private GameInfo createGameInfo(Room room, List<String> availableMoves) {
        GameInfo gameInfo = new GameInfo();
        gameInfo.setPositions(room.getBoard().getPositions());
        List<String> additionalInfo = new ArrayList<>();
        additionalInfo.add("Select destination");
        if (availableMoves.isEmpty()) {
            gameInfo.setWrongSelection(true);
        } else {
            additionalInfo.addAll(availableMoves);
        }
        gameInfo.setGameInfo(additionalInfo);
        gameInfo.setYourTurn(true);
        return gameInfo;
    }

    private GameInfo createGameInfoForCurrentPlayer(Room room) {
        GameInfo gameInfo = new GameInfo();
        gameInfo.setPositions(room.getBoard().getPositions());
        List<String> additionalInfo = new ArrayList<>();
        gameInfo.setGameInfo(additionalInfo);
        gameInfo.setYourTurn(false);
        return gameInfo;
    }

    private GameInfo createGameInfoForOpponent(Room room) {
        GameInfo gameInfo = new GameInfo();
        gameInfo.setPositions(room.getBoard().getPositions());
        List<String> additionalInfo = new ArrayList<>();
        gameInfo.setGameInfo(additionalInfo);
        gameInfo.setYourTurn(true);
        gameInfo.setSelectingPositionToMove(true);
        return gameInfo;
    }

    private void sendGameInfoToCurrentPlayer(WebSocket conn, Room room, GameInfo gameInfo) {
        String jsonString;
        try {
            jsonString = objectMapper.writeValueAsString(gameInfo);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        if (room.getBoard().isWhiteTurn()) {
            room.getWhitePlayer().send(jsonString);
        } else {
            room.getBlackPlayer().send(jsonString);
        }
    }

    private void sendGameInfoToOpponent(WebSocket conn, Room room, GameInfo gameInfo) {
        String jsonString;
        try {
            jsonString = objectMapper.writeValueAsString(gameInfo);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        if (!room.getBoard().isWhiteTurn()) {
            room.getWhitePlayer().send(jsonString);
        } else {
            room.getBlackPlayer().send(jsonString);
        }
    }

    private boolean isPlayerInRoom(WebSocket conn, Room room) {
        return room.getWhitePlayer().equals(conn) || room.getBlackPlayer().equals(conn);
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
            gameInfo.setGameInfo(additionalInfo);
            gameInfo.setYourTurn(false);
            String jsonString = objectMapper.writeValueAsString(gameInfo);
            room.getWhitePlayer().send(jsonString);
        } else {
            room.setBlackPlayer(conn);
            GameInfo gameInfo = new GameInfo();
            gameInfo.setPositions(room.getBoard().getPositions());
            gameInfo.setGameInfo(new ArrayList<>());
            gameInfo.setYourTurn(false);
            String jsonString = objectMapper.writeValueAsString(gameInfo);
            room.getBlackPlayer().send(jsonString);
            gameInfo.setYourTurn(true);
            gameInfo.setSelectingPositionToMove(true);
            gameInfo.getGameInfo().add("Start game");
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
        Room roomToBeUpdated = null;
        for (Room room : rooms.values()) {
            if (room.getWhitePlayer() != null && room.getWhitePlayer().equals(conn)) {
                room.setWhitePlayer(null);
                roomToBeUpdated = room;
                break;
            } else if (room.getBlackPlayer() != null && room.getBlackPlayer().equals(conn)) {
                room.setBlackPlayer(null);
                roomToBeUpdated = room;
                break;
            }
        }

        if (roomToBeUpdated != null) {
            WebSocket remainingPlayer = roomToBeUpdated.getWhitePlayer() != null ? roomToBeUpdated.getWhitePlayer() : roomToBeUpdated.getBlackPlayer();
            if (remainingPlayer != null) {
                GameInfo gameInfo = new GameInfo();
                gameInfo.setYourTurn(false);
                gameInfo.setPositions(roomToBeUpdated.getBoard().getPositions());
                gameInfo.setOpponentLeft(true);
                gameInfo.setGameInfo(new ArrayList<>());
                String jsonString;
                try {
                    jsonString = objectMapper.writeValueAsString(gameInfo);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                remainingPlayer.send(jsonString);
            }
            rooms.remove(roomToBeUpdated.getRoomNumber());
        }
    }

}


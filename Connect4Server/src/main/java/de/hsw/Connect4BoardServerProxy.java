package de.hsw;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Hashtable;

public class Connect4BoardServerProxy implements Runnable {

    private final Socket socket;
    private final IConnect4Board connect4Board;
    private final RpcReader reader;
    private final RpcWriter writer;

    private final Hashtable<String, IConnect4Player> connect4Players = new Hashtable<>();

    private final String PROTOCOL = "" +
            "1. Join Game ; " +
            "2. Leave Game ; " +
            "3. Get Board State ; " +
            "4. Make Move ; " +
            "5. Is Game Over ; " +
            "6. Get Winner ; " +
            "7. Get Winning Pieces ; " +
            "8. Reset Board ; " +
            "(Tech.: 0. End Connection)";
    private final String PROTOCOL_MESSAGE_TEMPLATE = "[SERVER][%s]: %s";

    public Connect4BoardServerProxy(Socket socket, IConnect4Board connect4Board) throws IOException {
        this.socket = socket;
        this.connect4Board = connect4Board;
        reader = new RpcReader(new InputStreamReader(socket.getInputStream()));
        writer = new RpcWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    @Override
    public void run() {
        long threadId = Thread.currentThread().getId();
        try {
            System.err.printf("[%d][SERVER]: Connection to %s:%d opened successfully.\n", threadId, socket.getInetAddress().getHostAddress(), socket.getPort());

            while (!socket.isClosed()) {
                writer.writeString(generateProtocolMessage("PROTOCOL", PROTOCOL));
                int option = reader.readInt();

                switch (option) {
                    case 0 -> endConnection();
                    case 1 -> joinGame();
                    case 2 -> leaveGame();
                    case 3 -> getBoardState();
                    case 4 -> makeMove();
                    case 5 -> isGameOver();
                    case 6 -> getWinner();
                    case 7 -> getWinningPieces();
                    case 8 -> resetBoard();
                    default -> writer.writeString(generateProtocolMessage("ERROR", "Invalid option: " + option));
                }
            }

            System.err.printf("[%d][SERVER]: Connection to %s:%d closed successfully.\n", threadId, socket.getInetAddress().getHostAddress(), socket.getPort());
        } catch (IOException | ClassNotFoundException e) {
            System.err.println(generateProtocolMessage("ERROR", "An error occurred: " + e.getMessage()));
        }
    }

    // Option 0 - End Connection
    private void endConnection() throws IOException {
        writer.writeString(generateProtocolMessage("INFO", "Closing connection."));
        socket.close();
    }

    // Option 1 - Join Game
    private void joinGame() throws IOException, ClassNotFoundException {
        writer.writeString(generateProtocolMessage("INFO", "Joining the game."));

        IConnect4Player joiningConnect4Player = getConnect4Player();

        boolean isJoinSuccessful = connect4Board.joinGame(joiningConnect4Player);
        writer.writeString(generateProtocolMessage("INFO", "Sending join status."));
        writer.writeBoolean(isJoinSuccessful);
    }

    // Option 2 - Leave Game
    private void leaveGame() throws IOException, ClassNotFoundException {
        writer.writeString(generateProtocolMessage("INFO", "Leaving the game."));

        IConnect4Player leavingConnect4Player = getConnect4Player();

        boolean isLeaveSuccessful = connect4Board.leaveGame(leavingConnect4Player);
        writer.writeString(generateProtocolMessage("INFO", "Sending leave status."));
        writer.writeBoolean(isLeaveSuccessful);
    }

    // Option 3 - Get Board State
    private void getBoardState() throws IOException {
        writer.writeString(generateProtocolMessage("INFO", "Sending the board state."));
        writer.writeCharArray(connect4Board.getBoardState());
    }

    // Option 4 - Make Move
    private void makeMove() throws IOException, ClassNotFoundException {
        IConnect4Player connect4Player = getConnect4Player();

        writer.writeString(generateProtocolMessage("INFO", "Please select a column."));
        int column = reader.readInt();

        boolean isMoveSuccessful = connect4Board.makeMove(column, connect4Player);
        writer.writeString(generateProtocolMessage("INFO", "Sending move status."));
        writer.writeBoolean(isMoveSuccessful);
    }

    // Option 5 - Is Game Over
    private void isGameOver() throws IOException {
        writer.writeString(generateProtocolMessage("INFO", "Checking for game over."));
        writer.writeBoolean(connect4Board.isGameOver());
    }

    // Option 6 - Get Winner
    private void getWinner() throws IOException {
        writer.writeString(generateProtocolMessage("INFO", "Sending winning char."));
        writer.writeChar(connect4Board.getWinner());
    }

    // Option 7 - Get Winning Pieces
    private void getWinningPieces() throws IOException {
        writer.writeString(generateProtocolMessage("INFO", "Sending winning pieces."));
        writer.writeObject(connect4Board.getWinningPieces());
    }

    // Option 8 - ResetBoard
    private void resetBoard() throws IOException {
        writer.writeString(generateProtocolMessage("INFO", "Resetting the board."));

        IConnect4Player playingConnect4Player = getConnect4Player();

        boolean isResetSuccessful = connect4Board.resetBoard(playingConnect4Player);
        writer.writeString(generateProtocolMessage("INFO", "Sending reset status."));
        writer.writeBoolean(isResetSuccessful);
    }

    private IConnect4Player getConnect4Player() throws IOException {
        writer.writeString(generateProtocolMessage("INFO", "Please provide your player id."));
        String playerId = reader.readString();

        IConnect4Player connect4Player = connect4Players.get(playerId);

        if (connect4Player == null) {
            writer.writeString(generateProtocolMessage("INFO", "Player unknown. Please provide your IP and PORT."));
            String IP = reader.readString();    // IP
            int PORT = reader.readInt();        // PORT

            Socket clientSocket = new Socket(IP, PORT);

            Connect4PlayerClientProxy connect4PlayerClientProxy = new Connect4PlayerClientProxy(clientSocket);
            connect4Players.put(playerId, connect4PlayerClientProxy);
        }

        return connect4Players.get(playerId);
    }

    private String generateProtocolMessage(String status, String message) {
        return String.format(PROTOCOL_MESSAGE_TEMPLATE, status, message);
    }
}

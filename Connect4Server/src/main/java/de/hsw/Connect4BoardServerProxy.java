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

    public Connect4BoardServerProxy(Socket socket, IConnect4Board connect4Board) throws IOException {
        this.socket = socket;
        this.connect4Board = connect4Board;
        reader = new RpcReader(new InputStreamReader(socket.getInputStream()));
        writer = new RpcWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    @Override
    public void run() {
        try {
            System.err.printf("[SERVER]: Connection to %s:%d opened successfully.\n", socket.getInetAddress().getHostAddress(), socket.getPort());

            while (!socket.isClosed()) {
                writer.writeString("0 | [SERVER - PROTOCOL]: 1. Join Game ; 2. Leave Game ; 3. Get Board State ; 4. Make Move ; 5. Is Game Over ; 6. Get Winner ; 7. Get Winning Pieces ; 8. Reset Board ; (Tech.: 0. End Connection)");
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
                    default -> writer.writeString("99 | [SERVER - PROTOCOL - ERROR]: Invalid option: " + option);
                }
            }

            System.err.printf("[SERVER]: Connection to %s:%d closed successfully.\n", socket.getInetAddress().getHostAddress(), socket.getPort());
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[SERVER]: An error occurred: " + e.getMessage());
        }
    }

    // Option 0 - End Connection
    private void endConnection() throws IOException {
        writer.writeString("0 | [SERVER - PROTOCOL]: Closing connection.");
        socket.close();
    }

    // Option 1 - Join Game
    private void joinGame() throws IOException, ClassNotFoundException {
        writer.writeString("0 | [SERVER - PROTOCOL]: Joining the game.");

        IConnect4Player joiningConnect4Player = getConnect4Player();

        boolean isJoinSuccessful = connect4Board.joinGame(joiningConnect4Player);
        writer.writeString("0 | [SERVER - PROTOCOL]: Sending join status.");
        writer.writeBoolean(isJoinSuccessful);
    }

    // Option 2 - Leave Game
    private void leaveGame() throws IOException, ClassNotFoundException {
        writer.writeString("0 | [SERVER - PROTOCOL]: Leaving the game.");

        IConnect4Player leavingConnect4Player = getConnect4Player();

        boolean isLeaveSuccessful = connect4Board.leaveGame(leavingConnect4Player);
        writer.writeString("0 | [SERVER - PROTOCOL]: Sending leave status.");
        writer.writeBoolean(isLeaveSuccessful);
    }

    // Option 3 - Get Board State
    private void getBoardState() throws IOException {
        writer.writeString("0 | [SERVER - PROTOCOL]: Sending the board state.");
        writer.writeCharArray(connect4Board.getBoardState());
    }

    // Option 4 - Make Move
    private void makeMove() throws IOException, ClassNotFoundException {
        IConnect4Player connect4Player = getConnect4Player();

        writer.writeString("0 | [SERVER - PROTOCOL]: Please select a column.");
        int column = reader.readInt();

        boolean isMoveSuccessful = connect4Board.makeMove(column, connect4Player);
        writer.writeString("0 | [SERVER - PROTOCOL]: Sending move status.");
        writer.writeBoolean(isMoveSuccessful);
    }

    // Option 5 - Is Game Over
    private void isGameOver() throws IOException {
        writer.writeString("0 | [SERVER - PROTOCOL]: Checking for game over.");
        writer.writeBoolean(connect4Board.isGameOver());
    }

    // Option 6 - Get Winner
    private void getWinner() throws IOException {
        writer.writeString("0 | [SERVER - PROTOCOL]: Sending winning char.");
        writer.writeChar(connect4Board.getWinner());
    }

    // Option 7 - Get Winning Pieces
    private void getWinningPieces() throws IOException {
        writer.writeString("0 | [SERVER - PROTOCOL]: Sending winning pieces.");
        writer.writeObject(connect4Board.getWinningPieces());
    }

    // Option 8 - ResetBoard
    private void resetBoard() throws IOException {
        writer.writeString("0 | [SERVER - PROTOCOL]: Resetting the board.");

        IConnect4Player playingConnect4Player = getConnect4Player();

        boolean isResetSuccessful = connect4Board.resetBoard(playingConnect4Player);
        writer.writeString("0 | [SERVER - PROTOCOL]: Sending reset status.");
        writer.writeBoolean(isResetSuccessful);
    }

    private IConnect4Player getConnect4Player() throws IOException {
        writer.writeString("0 | [SERVER - PROTOCOL]: Please provide your player id.");
        String playerId = reader.readString();

        IConnect4Player connect4Player = connect4Players.get(playerId);

        if (connect4Player == null) {
            writer.writeString("0 | [SERVER - PROTOCOL]: Player unknown. Please provide your IP and PORT.");
            String IP = reader.readString();    // IP
            int PORT = reader.readInt();        // PORT

            Socket clientSocket = new Socket(IP, PORT);

            Connect4PlayerClientProxy connect4PlayerClientProxy = new Connect4PlayerClientProxy(clientSocket);
            connect4Players.put(playerId, connect4PlayerClientProxy);
        }

        return connect4Players.get(playerId);
    }
}

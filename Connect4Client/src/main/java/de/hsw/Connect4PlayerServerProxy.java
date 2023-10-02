package de.hsw;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Connect4PlayerServerProxy implements Runnable {

    private final Socket socket;
    private final IConnect4Player connect4Player;
    private final RpcReader reader;
    private final RpcWriter writer;

    public Connect4PlayerServerProxy(Socket socket, IConnect4Player connect4Player) throws IOException {
        this.socket = socket;
        this.connect4Player = connect4Player;
        reader = new RpcReader(new InputStreamReader(socket.getInputStream()));
        writer = new RpcWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    @Override
    public void run() {
        try {
            System.err.printf("[CLIENT]: Connection to %s:%d opened successfully.\n", socket.getInetAddress().getHostAddress(), socket.getPort());

            while (!socket.isClosed()) {
                writer.writeString("0 | [CLIENT - PROTOCOL]: 1. Get Player ID ; 2. Get Player Name ; 3. Set Player Char ; 4. Get Player Char ; 5. Make Move ; 6. Receive Board State ; 7. Receive Opponents ; 8. Game Result ; (Tech.: 0. End Connection)");
                int option = reader.readInt();

                switch (option) {
                    case 0 -> endConnection();
                    case 1 -> getPlayerId();
                    case 2 -> getPlayerName();
                    case 3 -> setPlayerChar();
                    case 4 -> getPlayerChar();
                    case 5 -> makeMove();
                    case 6 -> receiveBoardState();
                    case 7 -> receiveOpponents();
                    case 8 -> gameResult();
                    default -> writer.writeString("99 | [CLIENT - PROTOCOL - ERROR]: Invalid option: " + option);
                }
            }

            System.err.printf("[CLIENT]: Connection to %s:%d closed successfully.\n", socket.getInetAddress().getHostAddress(), socket.getPort());
        } catch (IOException e) {
            System.err.println("[CLIENT]: An error occurred: " + e.getMessage());
        }
    }

    // Option 0 - End Connection
    private void endConnection() throws IOException {
        writer.writeString("0 | [CLIENT - PROTOCOL]: Closing connection.");
        socket.close();
    }

    // Option 1 - Get Player ID
    private void getPlayerId() throws IOException {
        writer.writeString("0 | [CLIENT - PROTOCOL]: Sending player id.");
        writer.writeString(connect4Player.getPlayerId());
    }

    // Option 2 - Get Name
    private void getPlayerName() throws IOException {
        writer.writeString("0 | [CLIENT - PROTOCOL]: Sending player name.");
        writer.writeString(connect4Player.getPlayerName());
    }

    // Option 3 - Set Player Char
    private void setPlayerChar() throws IOException {
        writer.writeString("0 | [CLIENT - PROTOCOL]: Please provide the player char.");
        char playerSymbol = reader.readChar();
        connect4Player.setPlayerChar(playerSymbol);
    }

    // Option 4 - Get Player Char
    private void getPlayerChar() throws IOException {
        writer.writeString("0 | [CLIENT - PROTOCOL]: Sending player char.");
        writer.writeChar(connect4Player.getPlayerChar());
    }

    // Option 5 - Make Move
    private void makeMove() throws IOException {
        connect4Player.makeMove();
    }

    // Option 6 - Receive Board State
    private void receiveBoardState() throws IOException {
        writer.writeString("0 | [CLIENT - PROTOCOL]: Please provide the board state.");
        char[][] boardState = reader.readCharArray();
        connect4Player.receiveBoardState(boardState);
    }

    // Option 7 - Receive Opponents
    private void receiveOpponents() throws IOException {
        writer.writeString("0 | [CLIENT - PROTOCOL]: Please provide the opponents list.");
        String[] opponents = reader.readStringArray();
        connect4Player.receiveOpponents(opponents);
    }

    // Option 8 - Game Result
    private void gameResult() throws IOException {
        writer.writeString("0 | [CLIENT - PROTOCOL]: Please provide the game result.");
        char gameResult = reader.readChar();
        connect4Player.gameResult(gameResult);
    }
}

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

    private boolean running = true;

    public Connect4PlayerServerProxy(Socket socket, IConnect4Player connect4Player) throws IOException {
        this.socket = socket;
        this.connect4Player = connect4Player;
        reader = new RpcReader(new InputStreamReader(socket.getInputStream()));
        writer = new RpcWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    @Override
    public void run() {
        try {
            while (running) {
                writer.writeString("0 | [PROTOCOL]: 1. Get Player ID ; 2. Get Player Name ; 3. Set PlayerSymbol ; 4. Get Player Symbol ; 5. Make Move ; 6. Receive Board State ; 7. Receive Opponents ; 8. Game Result ; (Tech.: 0. End Connection)");
                int option = reader.readInt();

                switch (option) {
                    case 0 -> endConnection();
                    case 1 -> getPlayerId();
                    case 2 -> getPlayerName();
                    case 3 -> setPlayerSymbol();
                    case 4 -> getPlayerSymbol();
                    case 5 -> makeMove();
                    case 6 -> receiveBoardState();
                    case 7 -> receiveOpponents();
                    case 8 -> gameResult();
                    default -> writer.writeString("99 | [PROTOCOL ERROR]: Invalid Option :" + option);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Option 0 - End Connection
    private void endConnection() throws IOException {
        running = false;
        socket.close();
    }

    // Option 1 - Get Player ID
    private void getPlayerId() throws IOException {
        writer.writeInt(connect4Player.getPlayerId());
    }

    // Option 2 - Get Name
    private void getPlayerName() throws IOException {
        writer.writeString(connect4Player.getPlayerName());
    }

    // Option 3 - Set Player Symbol
    private void setPlayerSymbol() throws IOException {
        writer.writeString("0 | [PROTOCOL]: Please provide the Player Symbol!");
        char playerSymbol = reader.readChar();
        connect4Player.setPlayerSymbol(playerSymbol);
    }

    // Option 4 - Get Player Symbol
    private void getPlayerSymbol() throws IOException {
        writer.writeChar(connect4Player.getPlayerSymbol());
    }

    // Option 5 - Make Move
    private void makeMove() throws IOException {
        connect4Player.makeMove();
    }

    // Option 6 - Receive Board State
    private void receiveBoardState() throws IOException {
        writer.writeString("0 | [PROTOCOL]: Please provide the Board State!");
        char[][] boardState = reader.readCharArray();
        connect4Player.receiveBoardState(boardState);
    }

    // Option 7 - Receive Opponents
    private void receiveOpponents() throws IOException {
        writer.writeString("0 | [PROTOCOL]: Please provide the Opponents List!");
        String[] opponents = reader.readStringArray();
        connect4Player.receiveOpponents(opponents);
    }

    // Option 8 - Game Result
    private void gameResult() throws IOException {
        writer.writeString("0 | [PROTOCOL]: Please provide the Game Result");
        char gameResult = reader.readChar();
        connect4Player.gameResult(gameResult);
    }
}

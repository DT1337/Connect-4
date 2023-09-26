package de.hsw;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Connect4PlayerClientProxy implements IConnect4Player {

    private final RpcReader reader;
    private final RpcWriter writer;

    public Connect4PlayerClientProxy(Socket socket) throws IOException {
        reader = new RpcReader(new InputStreamReader(socket.getInputStream()));
        writer = new RpcWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    // Option 0 - End Connection
    public void endConnection() throws IOException {
        selectOption(0);
        reader.readString(); // 0 | [PROTOCOL]: Closing connection.
    }

    // Option 1 - Get Player ID
    @Override
    public int getPlayerId() throws IOException {
        selectOption(1);
        reader.readString(); // 0 | [PROTOCOL]: Sending player id.
        return reader.readInt();
    }

    // Option 2 - Get Player Name
    @Override
    public String getPlayerName() throws IOException {
        selectOption(2);
        reader.readString(); // 0 | [PROTOCOL]: Sending player name.
        return reader.readString();
    }

    // Option 3 - Set Player Char
    @Override
    public void setPlayerChar(char playerSymbol) throws IOException {
        selectOption(3);
        reader.readString(); // 0 | [PROTOCOL]: Please provide the player char.
        writer.writeChar(playerSymbol);
    }

    // Option 4 - Get Player Char
    @Override
    public char getPlayerChar() throws IOException {
        selectOption(4);
        reader.readString(); // 0 | [PROTOCOL]: Sending player char.
        return reader.readChar();
    }

    // Option 5 - Make Move
    @Override
    public void makeMove() throws IOException {
        selectOption(5);
    }

    // Option 6 - Receive Board State
    @Override
    public void receiveBoardState(char[][] boardState) throws IOException {
        selectOption(6);
        reader.readString(); // 0 | [PROTOCOL]: Please provide the board state.
        writer.writeCharArray(boardState);
    }

    // Option 7 - Receive Opponents
    @Override
    public void receiveOpponents(String[] opponents) throws IOException {
        selectOption(7);
        reader.readString(); // 0 | [PROTOCOL]: Please provide the opponents list.
        writer.writeStringArray(opponents);
    }

    // Option 8 - Game Result
    @Override
    public void gameResult(char winnerSymbol) throws IOException {
        selectOption(8);
        reader.readString(); // 0 | [PROTOCOL]: Please provide the game result.
        writer.writeChar(winnerSymbol);
    }

    private void selectOption(int option) throws IOException {
        reader.readString(); // 0 | [PROTOCOL]: 1. Get Player ID ; 2. Get Player Name ; 3. Set Player Char ; 4. Get Player Char ; 5. Make Move ; 6. Receive Board State ; 7. Receive Opponents ; 8. Game Result ; (Tech.: 0. End Connection)
        writer.writeInt(option);
    }
}

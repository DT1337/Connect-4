package de.hsw;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

public class Connect4BoardClientProxy implements IConnect4Board {

    private final RpcReader reader;
    private final RpcWriter writer;

    private final Hashtable<IConnect4Player, Integer> connect4Players = new Hashtable<>();

    public Connect4BoardClientProxy(Socket socket) throws IOException {
        reader = new RpcReader(new InputStreamReader(socket.getInputStream()));
        writer = new RpcWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    // Option 0 - End Connection
    public void endConnection() throws IOException {
        selectOption(0);
        reader.readString(); // 0 | [PROTOCOL]: Closing connection.
    }

    // Option 1 - Join Game
    @Override
    public boolean joinGame(IConnect4Player joiningConnect4Player) throws IOException {
        selectOption(1);
        reader.readString(); // 0 | [PROTOCOL]: Joining the game.
        sendConnect4Player(joiningConnect4Player);
        return reader.readBoolean();
    }

    // Option 2 - Leave Game
    @Override
    public boolean leaveGame(IConnect4Player leavingConnect4Player) throws IOException {
        selectOption(2);
        reader.readString(); // 0 | [PROTOCOL]: Leaving the game.
        sendConnect4Player(leavingConnect4Player);
        return reader.readBoolean();
    }

    // Option 3 - Get Board State
    @Override
    public char[][] getBoardState() throws IOException {
        selectOption(3);
        reader.readString(); // 0 | [PROTOCOL]: Sending the board state.
        return reader.readCharArray();
    }

    // Option 4 - Make Move
    @Override
    public boolean makeMove(int column, IConnect4Player playingConnect4Player) throws IOException {
        selectOption(4);

        sendConnect4Player(playingConnect4Player);

        reader.readString(); // 0 | [PROTOCOL]: Please select a column.
        writer.writeInt(column);

        return reader.readBoolean();
    }

    // Option 5 - Is Game Over
    @Override
    public boolean isGameOver() throws IOException {
        selectOption(5);
        reader.readString(); // 0 | [PROTOCOL]: Checking for game over.
        return reader.readBoolean();
    }

    // Option 6 - Get Winner
    @Override
    public char getWinner() throws IOException {
        selectOption(6);
        reader.readString(); // 0 | [PROTOCOL]: Sending winning char.
        return reader.readChar();
    }

    // Option 7 - Get Winning Pieces
    public int[][] getWinningPieces() throws IOException, ClassNotFoundException {
        selectOption(7);
        reader.readString(); // 0 | [PROTOCOL]: Sending winning pieces.
        return (int[][]) reader.readObject();
    }

    // Option 8 - Reset Board
    @Override
    public boolean resetBoard(IConnect4Player playingConnect4Player) throws IOException {
        selectOption(8);
        reader.readString(); // 0 | [PROTOCOL]: Resetting the board.
        sendConnect4Player(playingConnect4Player);
        return reader.readBoolean();
    }

    private void sendConnect4Player(IConnect4Player playingConnect4Player) throws IOException {
        reader.readString(); // 0 | [PROTOCOL]: Please provide your player id.
        writer.writeInt(playingConnect4Player.getPlayerId());

        Integer playerId = connect4Players.get(playingConnect4Player);

        if (playerId == null) {
            connect4Players.put(playingConnect4Player, playingConnect4Player.getPlayerId());

            ServerSocket serverSocket = new ServerSocket(0);

            writer.writeString(InetAddress.getLocalHost().getHostAddress());    // IP
            writer.writeInt(serverSocket.getLocalPort());                       // PORT

            Socket clientSocket = serverSocket.accept();

            Connect4PlayerServerProxy connect4PlayerServerProxy = new Connect4PlayerServerProxy(clientSocket, playingConnect4Player);
            Thread connect4PlayerServerProxyThread = new Thread(connect4PlayerServerProxy);
            connect4PlayerServerProxyThread.start();
        }
    }

    private void selectOption(int option) throws IOException {
        reader.readString(); // 0 | [PROTOCOL]: 1. Join Game ; 2. Leave Game ; 3. Get Board State ; 4. Make Move ; 5. Is Game Over ; 6. Get Winner ; 7. Get Winning Pieces ; 8. Reset Board ; (Tech.: 0. End Connection)
        writer.writeInt(option);
    }
}

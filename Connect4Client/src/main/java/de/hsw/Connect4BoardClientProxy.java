package de.hsw;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

public class Connect4BoardClientProxy implements IConnect4Board {

    private final Socket socket;
    private final RpcReader reader;
    private final RpcWriter writer;

    private final Hashtable<IConnect4Player, Integer> connect4Players = new Hashtable<>();

    public Connect4BoardClientProxy(Socket socket) throws IOException {
        this.socket = socket;
        reader = new RpcReader(new InputStreamReader(socket.getInputStream()));
        writer = new RpcWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    // Option 0 - End Connection
    public void endConnection() throws IOException {
        selectOption(0);
    }

    // Option 1 - Join Game
    @Override
    public boolean joinGame(IConnect4Player joiningConnect4Player) throws IOException {
        selectOption(1);
        sendConnect4Player(joiningConnect4Player);
        return reader.readBoolean();
    }

    // Option 2 - Leave Game
    @Override
    public boolean leaveGame(IConnect4Player leavingConnect4Player) throws IOException {
        selectOption(2);
        sendConnect4Player(leavingConnect4Player);
        return reader.readBoolean();
    }

    // Option 3 - Get Board State
    @Override
    public char[][] getBoardState() throws IOException {
        selectOption(3);
        return reader.readCharArray();
    }

    // Option 4 - Make Move
    @Override
    public boolean makeMove(int column, IConnect4Player playingConnect4Player) throws IOException {
        selectOption(4);

        sendConnect4Player(playingConnect4Player);

        reader.readString(); // 0 | [PROTOCOL]: Please select a Column!
        writer.writeInt(column);

        return reader.readBoolean();
    }

    // Option 5 - Is Game Over
    @Override
    public boolean isGameOver() throws IOException {
        selectOption(5);
        return reader.readBoolean();
    }

    // Option 6 - Get Winner
    @Override
    public char getWinner() throws IOException {
        selectOption(6);
        return reader.readChar();
    }

    // Option 7 - Get Winning Pieces
    public int[][] getWinningPieces() throws IOException, ClassNotFoundException {
        selectOption(7);
        return (int[][]) reader.readObject();
    }

    // Option 8 - Reset Board
    @Override
    public boolean resetBoard(IConnect4Player playingConnect4Player) throws IOException {
        selectOption(8);
        sendConnect4Player(playingConnect4Player);
        return reader.readBoolean();
    }

    private void sendConnect4Player(IConnect4Player playingConnect4Player) throws IOException {
        reader.readString(); // 0 | [PROTOCOL]: Please provide your Player ID!
        writer.writeInt(playingConnect4Player.getPlayerId());

        Integer playerId = connect4Players.get(playingConnect4Player);

        if (playerId == null) {
            connect4Players.put(playingConnect4Player, playingConnect4Player.getPlayerId());

            ServerSocket serverSocket = new ServerSocket(0);

            writer.writeString(InetAddress.getLocalHost().getHostAddress()); // IP
            writer.writeInt(serverSocket.getLocalPort()); // PORT

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

package de.hsw;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Connect4Server {

    private final ServerSocket serverSocket;

    private Connect4Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    private void startServer() {
        try {
            Connect4Board connect4Board = new Connect4Board();

            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();
                Connect4BoardServerProxy connect4BoardServerProxy = new Connect4BoardServerProxy(clientSocket, connect4Board);
                Thread connect4BoardServerProxyThread = new Thread(connect4BoardServerProxy);
                connect4BoardServerProxyThread.start();
            }
        } catch (IOException e) {
            System.out.println("Error while starting Server: " + e.getMessage());
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println("[SERVER]");
        System.out.println("[SERVER]: Welcome to Connect 4!");

        ServerSocket serverSocket = new ServerSocket(7171);

        Connect4Server connect4Server = new Connect4Server(serverSocket);
        connect4Server.startServer();
    }
}

package de.hsw;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Connect4Server {
    public static void main(String[] args) throws IOException {
        System.out.println("Server");
        ServerSocket serverSocket = new ServerSocket(7171);
        Connect4Board connect4Board = new Connect4Board();

        while (!serverSocket.isClosed()) {
            Socket clientSocket = serverSocket.accept();
            Connect4BoardServerProxy connect4BoardServerProxy = new Connect4BoardServerProxy(clientSocket, connect4Board);
            Thread connect4BoardServerProxyThread = new Thread(connect4BoardServerProxy);
            connect4BoardServerProxyThread.start();
        }
    }
}

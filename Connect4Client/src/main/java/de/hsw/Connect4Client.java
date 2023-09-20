package de.hsw;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Connect4Client {
    public static void main(String[] args) throws IOException {
        System.out.println("Client");
        System.out.println("Welcome to Connect 4");

        Scanner scanner = new Scanner(System.in);
        System.out.print("Please input your Name: ");
        Connect4Player connect4Player = new Connect4Player(scanner.next());

        Socket socket = new Socket("localhost", 7171);
        Connect4BoardClientProxy connect4BoardClientProxy = new Connect4BoardClientProxy(socket);

        while (socket.isConnected()) {
            System.out.print("""
                    0. (Tech.: End Connection)
                    1. Join Game
                    2. Leave Game
                    3. Get Board State
                    4. Make Move
                    5. Is Game Over
                    6. Get Winner
                    7. Reset Board
                    >\s""");
            int option = scanner.nextInt();

            switch (option) {
                case 0 -> {
                    connect4BoardClientProxy.endConnection();
                }
                case 1 -> connect4BoardClientProxy.joinGame(connect4Player);
                case 2 -> connect4BoardClientProxy.leaveGame(connect4Player);
                case 3 -> connect4BoardClientProxy.getBoardState();
                case 4 -> {
                    System.out.println("Please provide a column number: ");
                    int column = scanner.nextInt();
                    connect4BoardClientProxy.makeMove(--column, connect4Player);
                }
                case 5 -> connect4BoardClientProxy.isGameOver();
                case 6 -> connect4BoardClientProxy.getWinner();
                case 7 -> connect4BoardClientProxy.resetBoard(connect4Player);
                default -> System.out.println("Invalid option (" + option + ")");
            }
        }
    }
}

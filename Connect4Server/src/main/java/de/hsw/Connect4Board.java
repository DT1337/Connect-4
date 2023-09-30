package de.hsw;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Arrays;

public class Connect4Board implements IConnect4Board {

    private final IConnect4Player[] connect4Players;
    private final String[] opponents;
    private final ArrayDeque<Character> availablePlayerChars = new ArrayDeque<>();
    private int currentPlayerIndex = 0;

    private final char[][] connect4Board;
    private final int rows;
    private final int cols;

    public Connect4Board() {
        this(2, 6, 7);
    }

    public Connect4Board(int playerCount) {
        this(playerCount, 6, 7);
    }

    public Connect4Board(int rows, int cols) {
        this(2, rows, cols);
    }

    public Connect4Board(int playerCount, int rows, int cols) {
        // Ensure that the playerCount 8 at maximum
        if (playerCount > 8) {
            throw new IllegalArgumentException("The maximum number of players is 8.");
        }

        // Ensure rows and columns meet the minimum requirements
        if (rows < 6 || cols < 7) {
            throw new IllegalArgumentException("The board size must be at least 6x7.");
        }

        // Set up the board
        this.rows = rows;
        this.cols = cols;
        this.connect4Board = new char[rows][cols];

        // Initialize the board with empty spaces
        for (char[] row : connect4Board) {
            Arrays.fill(row, '-');
        }

        // Set up the player list
        this.connect4Players  = new IConnect4Player[playerCount];
        this.opponents = new String[playerCount];
        Arrays.fill(opponents, "");

        if (playerCount == 2) {
            availablePlayerChars.addAll(Arrays.asList('X', 'O'));
        } else {
            for (int i = 0; i < playerCount; i++) {
                availablePlayerChars.add((char) (65 + i));
            }
        }
    }

    @Override
    public boolean joinGame(IConnect4Player joiningConnect4Player) throws IOException {
        for (IConnect4Player connect4Player : connect4Players) {
            if (connect4Player == joiningConnect4Player) {
                return false;
            }
        }

        for (int i = 0; i < connect4Players.length; i++) {
            if (connect4Players[i] == null) {
                connect4Players[i] = joiningConnect4Player;

                String playerName = joiningConnect4Player.getPlayerName();
                opponents[i] = playerName == null ? "" : playerName;

                joiningConnect4Player.setPlayerChar(getAvailablePlayerChars());

                updateClientOpponents();
                updateClientBoards();
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean leaveGame(IConnect4Player leavingConnect4Player) throws IOException {
        for (int i = 0; i < connect4Players.length; i++) {
            if (connect4Players[i] == leavingConnect4Player) {
                connect4Players[i] = null;
                opponents[i] = "";
                availablePlayerChars.add(leavingConnect4Player.getPlayerChar());

                updateClientOpponents();
                updateClientBoards();

                return true;
            }
        }

        return false;
    }

    @Override
    public char[][] getBoardState() {
        return connect4Board;
    }

    @Override
    public boolean makeMove(int column, IConnect4Player playingConnect4Player) throws IOException {
        // check whether the game is over and if so notify the players
        if (isGameOver()) {
            sendPlayersGameResult();
            return false;
        }

        // check if de.hsw.IConnect4Player is a joined the game
        if (!Arrays.asList(connect4Players).contains(playingConnect4Player)) {
            return false;
        }

        // check if it's their turn
        if (connect4Players[currentPlayerIndex] != playingConnect4Player) {
            return false;
        }

        // calculate the played position by choosing the lowest possible row in the selected column
        int row = getAvailableRow(column);
        if (row == -1) {
            return false;
        }

        // make move and update board
        connect4Board[row][column] = playingConnect4Player.getPlayerChar();
        updateClientBoards();

        // check whether the game is over and if so notify the players
        if (isGameOver()) {
            sendPlayersGameResult();
            return false;
        }

        // calculate next playerIndex
        currentPlayerIndex = (++currentPlayerIndex) % connect4Players.length;

        // notify the opponent player that it's their turn
        IConnect4Player opponentConnect4Player = connect4Players[currentPlayerIndex];
        if (opponentConnect4Player != null) {
            opponentConnect4Player.makeMove();
        }

        return true;
    }

    @Override
    public boolean isGameOver() {
        // Check for horizontal win
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols - 3; col++) {
                if (connect4Board[row][col] != '-' && connect4Board[row][col] == connect4Board[row][col + 1] &&
                        connect4Board[row][col] == connect4Board[row][col + 2] && connect4Board[row][col] == connect4Board[row][col + 3]) {
                    return true;
                }
            }
        }

        // Check for vertical win
        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows - 3; row++) {
                if (connect4Board[row][col] != '-' && connect4Board[row][col] == connect4Board[row + 1][col] &&
                        connect4Board[row][col] == connect4Board[row + 2][col] && connect4Board[row][col] == connect4Board[row + 3][col]) {
                    return true;
                }
            }
        }

        // Check for diagonal win (top-left to bottom-right)
        for (int row = 0; row < rows - 3; row++) {
            for (int col = 0; col < cols - 3; col++) {
                if (connect4Board[row][col] != '-' && connect4Board[row][col] == connect4Board[row + 1][col + 1] &&
                        connect4Board[row][col] == connect4Board[row + 2][col + 2] && connect4Board[row][col] == connect4Board[row + 3][col + 3]) {
                    return true;
                }
            }
        }

        // Check for diagonal win (top-right to bottom-left)
        for (int row = 0; row < rows - 3; row++) {
            for (int col = 3; col < cols; col++) {
                if (connect4Board[row][col] != '-' && connect4Board[row][col] == connect4Board[row + 1][col - 1] &&
                        connect4Board[row][col] == connect4Board[row + 2][col - 2] && connect4Board[row][col] == connect4Board[row + 3][col - 3]) {
                    return true;
                }
            }
        }

        // If no winner is found, check for a tie (full connect4Board)
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (connect4Board[row][col] == '-') {
                    return false; // There are empty spaces, the game is not over
                }
            }
        }

        // If no winner and no empty spaces, the game is a tie
        return true;
    }

    @Override
    public char getWinner() {
        int[][] winningPieces = getWinningPieces();
        // If no winner is found, return '-' (empty space) to indicate no winner yet
        if (winningPieces == null) {
            return '-';
        }

        return connect4Board[winningPieces[0][0]][winningPieces[0][1]];
    }

    @Override
    public int[][] getWinningPieces() {
        // Check for horizontal win
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols - 3; col++) {
                char current = connect4Board[row][col];
                if (current != '-' && current == connect4Board[row][col + 1] &&
                        current == connect4Board[row][col + 2] && current == connect4Board[row][col + 3]) {
                    return new int[][]{{row, col}, {row, col + 1}, {row, col + 2}, {row, col + 3}};
                }
            }
        }

        // Check for vertical win
        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows - 3; row++) {
                char current = connect4Board[row][col];
                if (current != '-' && current == connect4Board[row + 1][col] &&
                        current == connect4Board[row + 2][col] && current == connect4Board[row + 3][col]) {
                    return new int[][]{{row, col}, {row + 1, col}, {row + 2, col}, {row + 3, col}};
                }
            }
        }

        // Check for diagonal win (top-left to bottom-right)
        for (int row = 0; row < rows - 3; row++) {
            for (int col = 0; col < cols - 3; col++) {
                char current = connect4Board[row][col];
                if (current != '-' && current == connect4Board[row + 1][col + 1] &&
                        current == connect4Board[row + 2][col + 2] && current == connect4Board[row + 3][col + 3]) {
                    return new int[][]{{row, col}, {row + 1, col + 1}, {row + 2, col + 2}, {row + 3, col + 3}};
                }
            }
        }

        // Check for diagonal win (top-right to bottom-left)
        for (int row = 0; row < rows - 3; row++) {
            for (int col = 3; col < cols; col++) {
                char current = connect4Board[row][col];
                if (current != '-' && current == connect4Board[row + 1][col - 1] &&
                        current == connect4Board[row + 2][col - 2] && current == connect4Board[row + 3][col - 3]) {
                    return new int[][]{{row, col}, {row + 1, col - 1}, {row + 2, col - 2}, {row + 3, col - 3}};
                }
            }
        }

        // If no winner is found, return null to indicate no winner yet
        return null;
    }

    @Override
    public boolean resetBoard(IConnect4Player playingConnect4Player) throws IOException {
        if (!Arrays.asList(connect4Players).contains(playingConnect4Player)) {
            return false;
        }

        // Replace all values in the connect4Board with '-' (empty)
        for (char[] chars : connect4Board) {
            Arrays.fill(chars, '-');
        }

        // Reset current player
        currentPlayerIndex = 0;

        updateClientBoards();

        return true;
    }

    private int getAvailableRow(int column) {
        // Return the lowest possible row in the provided column (default Connect 4 behaviour)
        for (int row = connect4Board.length-1; row >= 0; row--) {
            if (connect4Board[row][column] == '-') {
                return row;
            }
        }

        // Return -1 if all rows in the provided column are already placed
        return -1;
    }

    private void updateClientBoards() throws IOException {
        for (IConnect4Player connect4Player : connect4Players) {
            if (connect4Player != null) {
                connect4Player.receiveBoardState(connect4Board);
            }
        }
    }

    private void updateClientOpponents() throws IOException {
        for (IConnect4Player connect4Player : connect4Players) {
            if (connect4Player != null) {
                connect4Player.receiveOpponents(opponents);
            }
        }
    }

    private void sendPlayersGameResult() throws IOException {
        for (IConnect4Player connect4Player : connect4Players) {
            if (connect4Player != null) {
                connect4Player.gameResult(getWinner());
            }
        }
    }

    private char getAvailablePlayerChars() {
        return availablePlayerChars.pop();
    }
}

package de.hsw;

public class Connect4Player implements IConnect4Player {

    private final String playerName;
    private final int playerId;
    private char playerSymbol;
    private char[][] boardState;
    private String[] opponents;

    public Connect4Player(String name) {
        this.playerName = name;
        this.playerId = name.hashCode();
    }

    @Override
    public int getPlayerId() {
        return playerId;
    }

    @Override
    public String getPlayerName() {
        return playerName;
    }

    @Override
    public void setPlayerSymbol(char playerSymbol) {
        this.playerSymbol = playerSymbol;
    }

    @Override
    public char getPlayerSymbol() {
        return playerSymbol;
    }

    @Override
    public void makeMove() {
        System.out.println("It's your turn!");
    }

    @Override
    public void receiveBoardState(char[][] boardState) {
        this.boardState = boardState;

        StringBuilder boardString = new StringBuilder();
        boardString.append("\n");

        // Opponents
        boardString.append(opponents[0]);
        for (int i = 1; i < opponents.length; i++) {
            boardString.append(" vs. ");
            boardString.append(opponents[i]);
        }

        // Column Header
        boardString.append("\n");
        for (int i = 1; i <= boardState[0].length; i++) {
            boardString.append("   ").append(i);
        }
        boardString.append("\n");

        // Connect 4 Board
        for (char[] chars : boardState) {
            for (char aChar : chars) {
                boardString.append(" | ").append(aChar);
            }
            boardString.append(" |\n");
        }

        System.out.println(boardString);
    }

    @Override
    public void receiveOpponents(String[] opponents) {
        this.opponents = opponents;
    }

    @Override
    public void gameResult(char winnerSymbol) {
        System.out.println("Game is over and the winner is: " + winnerSymbol);
    }
}

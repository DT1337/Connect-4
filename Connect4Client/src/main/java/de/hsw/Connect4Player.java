package de.hsw;

public class Connect4Player implements IConnect4Player {

    private final Connect4PlayerUIController connect4PlayerUIController;
    private final String playerName;
    private final int playerId;
    private char playerChar;

    public Connect4Player(Connect4PlayerUIController connect4PlayerUIController, String name) {
        this.connect4PlayerUIController = connect4PlayerUIController;
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
    public void setPlayerChar(char playerChar) {
        this.playerChar = playerChar;
    }

    @Override
    public char getPlayerChar() {
        return playerChar;
    }

    @Override
    public void makeMove() {
        connect4PlayerUIController.setStatusLabel("It's your turn!");
    }

    @Override
    public void receiveBoardState(char[][] boardState) {
        connect4PlayerUIController.updateBoardState(boardState);
    }

    @Override
    public void receiveOpponents(String[] opponents) {
        connect4PlayerUIController.updateOpponents(opponents);
    }

    @Override
    public void gameResult(char winnerChar) {
        connect4PlayerUIController.setWinner(winnerChar);
    }
}

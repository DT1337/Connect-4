package de.hsw;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class Connect4PlayerTest {

    private Connect4Player connect4Player;
    private Connect4PlayerUIController mockController;

    @Before
    public void setUp() {
        mockController = Mockito.mock(Connect4PlayerUIController.class);
        connect4Player = new Connect4Player(mockController, "Player 1");
    }

    @Test
    public void testGetPlayerId() {
        assertNotNull(connect4Player.getPlayerId());
    }

    @Test
    public void testGetPlayerName() {
        assertEquals("Player 1", connect4Player.getPlayerName());
    }

    @Test
    public void testSetPlayerChar() {
        connect4Player.setPlayerChar('X');
        assertEquals('X', connect4Player.getPlayerChar());
    }

    @Test
    public void testMakeMove() {
        connect4Player.makeMove();
        Mockito.verify(mockController, Mockito.times(1)).setStatusLabel("It's your turn!");
    }

    @Test
    public void testReceiveBoardState() {
        char[][] boardState = new char[6][7];
        connect4Player.receiveBoardState(boardState);
        Mockito.verify(mockController, Mockito.times(1)).updateBoardState(boardState);
    }

    @Test
    public void testReceiveOpponents() {
        String[] opponents = {"Player 2", "Player 3"};
        connect4Player.receiveOpponents(opponents);
        Mockito.verify(mockController, Mockito.times(1)).updateOpponents(opponents);
    }

    @Test
    public void testGameResult() {
        char winnerSymbol = 'X';
        connect4Player.gameResult(winnerSymbol);
        Mockito.verify(mockController, Mockito.times(1)).setWinner(winnerSymbol);
    }
}

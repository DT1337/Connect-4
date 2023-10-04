import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import de.hsw.*;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class Connect4BoardTest {

    private Connect4Board connect4Board;
    private IConnect4Player player1;
    private IConnect4Player player2;
    private IConnect4Player player3;

    @Before
    public void setUp() throws IOException {
        connect4Board = new Connect4Board();

        player1 = mock(IConnect4Player.class);
        player2 = mock(IConnect4Player.class);
        player3 = mock(IConnect4Player.class);

        when(player1.getPlayerChar()).thenReturn('X');
        when(player2.getPlayerChar()).thenReturn('O');
        when(player3.getPlayerChar()).thenReturn(' ');
    }

    @Test
    public void testJoinGame() throws IOException {
        assertTrue(connect4Board.joinGame(player1));
        assertTrue(connect4Board.joinGame(player2));
        assertFalse(connect4Board.joinGame(player1)); // Player 1 is already joined the game
    }

    @Test
    public void testLeaveGame() throws IOException {
        connect4Board.joinGame(player1);
        connect4Board.joinGame(player2);

        assertTrue(connect4Board.leaveGame(player1));
        assertTrue(connect4Board.leaveGame(player2));
        assertFalse(connect4Board.leaveGame(player1)); // Player 1 is already left the game
    }

    @Test
    public void testRGetBoardState() throws IOException {
        connect4Board.joinGame(player1);
        connect4Board.joinGame(player2);

        connect4Board.makeMove(0, player1);
        connect4Board.makeMove(1, player2);

        char[][] expectedBoard = {
                {'-', '-', '-', '-', '-', '-', '-'},
                {'-', '-', '-', '-', '-', '-', '-'},
                {'-', '-', '-', '-', '-', '-', '-'},
                {'-', '-', '-', '-', '-', '-', '-'},
                {'-', '-', '-', '-', '-', '-', '-'},
                {'X', 'O', '-', '-', '-', '-', '-'},
        };

        assertArrayEquals(expectedBoard, connect4Board.getBoardState());
    }

    @Test
    public void testMakeMove() throws IOException {
        connect4Board.joinGame(player1);
        connect4Board.joinGame(player2);

        assertTrue(connect4Board.makeMove(0, player1));
        char[][] boardState = connect4Board.getBoardState();
        assertEquals(player1.getPlayerChar(), boardState[5][0]); // Valid move

        assertTrue(connect4Board.makeMove(0, player2));
        boardState = connect4Board.getBoardState();
        assertEquals(player2.getPlayerChar(), boardState[4][0]); // Valid move

        for (int i=0; i<3; i++) {
            connect4Board.makeMove(2, player1);
            connect4Board.makeMove(2, player2);
        }
        assertFalse(connect4Board.makeMove(2, player1)); // Invalid move, column full

        assertFalse(connect4Board.makeMove(69, player1)); // Invalid move, column out of range
        assertFalse(connect4Board.makeMove(0, null)); // Invalid move, player is null
        assertFalse(connect4Board.makeMove(1, player3)); // Invalid move, player not joined
    }

    @Test
    public void testIsGameOver() throws IOException {
        connect4Board.joinGame(player1);
        connect4Board.joinGame(player2);

        assertFalse(connect4Board.isGameOver());

        // Simulate a winning condition
        connect4Board.makeMove(0, player1);
        connect4Board.makeMove(1, player2);
        connect4Board.makeMove(0, player1);
        connect4Board.makeMove(1, player2);
        connect4Board.makeMove(0, player1);
        connect4Board.makeMove(1, player2);
        connect4Board.makeMove(0, player1);

        assertTrue(connect4Board.isGameOver());
    }

    @Test
    public void testGetWinner() throws IOException {
        connect4Board.joinGame(player1);
        connect4Board.joinGame(player2);

        assertEquals('-', connect4Board.getWinner());

        // Simulate a winning condition
        connect4Board.makeMove(0, player1);
        connect4Board.makeMove(1, player2);
        connect4Board.makeMove(0, player1);
        connect4Board.makeMove(1, player2);
        connect4Board.makeMove(0, player1);
        connect4Board.makeMove(1, player2);
        connect4Board.makeMove(0, player1);

        assertEquals(player1.getPlayerChar(), connect4Board.getWinner());
    }

    @Test
    public void testResetBoard() throws IOException {
        connect4Board.joinGame(player1);
        connect4Board.joinGame(player2);

        connect4Board.makeMove(0, player1);
        connect4Board.makeMove(1, player2);

        assertTrue(connect4Board.resetBoard(player1));

        char[][] boardState = connect4Board.getBoardState();
        for (char[] row : boardState) {
            for (char cell : row) {
                assertEquals('-', cell); // All cells should be reset to empty
            }
        }
    }
}

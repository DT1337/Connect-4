package de.hsw;

import java.io.IOException;

public interface IConnect4Board {

    /**
     * Join the Connect4 Game
     *
     * @param joiningConnect4Player The connect4Player joining the game.
     * @return True if the joining was successful, false otherwise.
     * @throws IOException if there is a network-related error
     */
    boolean joinGame(IConnect4Player joiningConnect4Player) throws IOException;

    /**
     * Leave the Connect4 Game
     *
     * @param leavingConnect4Player The connect4Player leaving the game.
     * @return True if the leaving was successful, false otherwise.
     * @throws IOException if there is a network-related error
     */
    boolean leaveGame(IConnect4Player leavingConnect4Player) throws IOException;

    /**
     * Get the current state of the Connect 4 board.
     *
     * @return A 2D array representing the current board state.
     *         The array should have dimensions 6x7 for a standard Connect 4 board or custom dimensions.
     *         It can contain values like 'X' for player 1, 'O' for player 2 (, ...), or '-' for an empty slot.
     * @throws IOException if there is a network-related error.
     */
    char[][] getBoardState() throws IOException;

    /**
     * Attempt to make a move on the Connect 4 board.
     *
     * @param column The column where the player wants to drop their token (0-6 for a standard board or custom values).
     * @param playingConnect4Player The connect4Player making the move.
     * @return True if the move is valid and successful, false otherwise.
     * @throws IOException if there is a network-related error.
     */
    boolean makeMove(int column, IConnect4Player playingConnect4Player) throws IOException;

    /**
     * Check if the game is over.
     *
     * @return True if the game is over (either a player has won or it's a draw), false otherwise.
     * @throws IOException if there is a network-related error.
     */
    boolean isGameOver() throws IOException;

    /**
     * Get the winner of the game.
     *
     * @return The symbol ('X' or 'O' (, ...)) of the winning player, or '-' if there is no winner.
     * @throws IOException if there is a network-related error.
     */
    char getWinner() throws IOException;

    /**
     * Reset the Connect 4 board to its initial state.
     *
     * @throws IOException if there is a network-related error.
     */
    void resetBoard(IConnect4Player playingConnect4Player) throws IOException;
}

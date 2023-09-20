package de.hsw;

import java.io.IOException;
import java.io.Serializable;

public interface IConnect4Player extends Serializable {

    /**
     * Get the unique ID of the player.
     *
     * @return The player's ID.
     * @throws IOException if there is a network-related error.
     */
    int getPlayerId() throws IOException;

    /**
     * Get the Name of the player.
     *
     * @return The player's Name.
     * @throws IOException if there is a network-related error.
     */
    String getPlayerName() throws IOException;

    /**
     * Set the Char of the player ('X' or 'O' (, ...))
     *
     * @param playerChar The player's Char
     * @throws IOException if there is a network-related error.
     */
    void setPlayerChar(char playerChar) throws IOException;

    /**
     * Get the Char of the player ('X' or 'O' (, ...))
     *
     * @return playerChar The player's Char
     * @throws IOException if there is a network-related error.
     */
    char getPlayerChar() throws IOException;

    /**
     * Notify the player that it's their turn to make a move.
     *
     * @throws IOException if there is a network-related error.
     */
    void makeMove() throws IOException;

    /**
     * Receive the current state of the Connect 4 board from the server.
     *
     * @param boardState A 2D array representing the current board state.
     *                   The array should have dimensions 6x7 for a standard Connect 4 board or custom dimensions.
     *                   It contains values like 'X' for player 1, 'O' for player 2 (, ...), or '-' for an empty slot.
     * @throws IOException if there is a network-related error.
     */
    void receiveBoardState(char[][] boardState) throws IOException;

    /**
     * Receive the current list of opponent names from the server.
     *
     * @param opponents The list of the opponent names.
     * @throws IOException if there is a network-related error.
     */
    void receiveOpponents(String[] opponents) throws IOException;

    /**
     * Notify the player that the game is over and provide the result.
     *
     * @param winnerSymbol The symbol ('X' or 'O' (, ...)) of the winning player, or '-' if it's a draw.
     * @throws IOException if there is a network-related error.
     */
    void gameResult(char winnerSymbol) throws IOException;
}

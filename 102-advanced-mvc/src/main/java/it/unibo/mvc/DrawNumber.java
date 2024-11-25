package it.unibo.mvc;

/**
 * This class models a draw number game.
 */
public interface DrawNumber {

    /**
     * Resets the game.
     */
    void reset();

    /**
     * Guesses a number.
     * 
     * @param n the guess
     * @return the result of the guess
     */
    DrawResult attempt(int n);
}

package com.comp2042.core;

import com.comp2042.input.InputEventListener;
import com.comp2042.logic.workflow.ViewData;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;

/**
 * Defines UI-side callbacks that respond to game state changes and bindings.
 * Enables initialization, refresh, score/level bindings, visual effects, and
 * game over handling.
 *
 * @author Eashwar
 * @version 1.0
 */
public interface GameEventListener {
    /**
     * Initializes the game view with the current board and active brick data.
     *
     * @param boardMatrix the background matrix for the board
     * @param brick the active brick and preview/hold information
     */
    void initGameView(int[][] boardMatrix, ViewData brick);

    /**
     * Refreshes the rendered background from the updated board matrix.
     *
     * @param board the updated background matrix
     */
    void refreshGameBackground(int[][] board);

    /**
     * Registers the input event listener for handling user interactions.
     *
     * @param eventListener the input event listener to register
     */
    void setEventListener(InputEventListener eventListener);

    /**
     * Binds the score property to UI elements.
     *
     * @param integerProperty the score property
     */
    void bindScore(IntegerProperty integerProperty);

    /**
     * Binds the high score property to UI elements.
     *
     * @param integerProperty the high score property
     */
    void bindHighScore(IntegerProperty integerProperty);

    /**
     * Binds the lines cleared property to UI elements.
     *
     * @param integerProperty the lines cleared property
     */
    void bindLinesCleared(IntegerProperty integerProperty);

    /**
     * Binds the numeric level property to UI elements.
     *
     * @param integerProperty the level value property
     */
    void bindLevelValue(IntegerProperty integerProperty);

    BooleanProperty getIsGameOver();

    /**
     * Binds the level property to UI elements (semantic representation).
     *
     * @param integerProperty the level property
     */
    void bindLevel(IntegerProperty integerProperty);

    /**
     * Triggers a pulse effect for blocks after a hard drop.
     *
     * @param brickShape the landed brick matrix
     * @param xPosition the x-position of the brick
     * @param yPosition the y-position of the brick
     */
    void pulseLandedBlocks(int[][] brickShape, int xPosition, int yPosition);

    /**
     * Handles game-over sequence and visuals.
     *
     * @param newHighScore whether the game ended with a new high score
     */
    void gameOver(boolean newHighScore);
}

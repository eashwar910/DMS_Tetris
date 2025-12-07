package com.comp2042.core;

import com.comp2042.logic.workflow.ClearRow;
import com.comp2042.logic.workflow.Score;
import com.comp2042.logic.workflow.ViewData;

/**
 * Defines the contract for a Tetris game board, covering brick movement,
 * rotation, holding and spawning, merging into the background, row clearing,
 * and access to view and score data.
 *
 * @author Eashwar
 * @version 1.0
 */
public interface Board {

    /**
     * Moves the active brick one row downward if possible.
     *
     * @return true if the brick moved successfully; false if blocked or locked
     */
    boolean moveBrickDown();

    /**
     * Moves the active brick one column to the left if possible.
     *
     * @return true if the brick moved successfully; false otherwise
     */
    boolean moveBrickLeft();

    /**
     * Moves the active brick one column to the right if possible.
     *
     * @return true if the brick moved successfully; false otherwise
     */
    boolean moveBrickRight();

    /**
     * Rotates the active brick counterclockwise if possible.
     *
     * @return true if the brick rotated successfully; false otherwise
     */
    boolean rotateLeftBrick();

    /**
     * Holds the active brick or swaps with the held brick according to rules.
     *
     * @return true if a hold or swap occurred; false otherwise
     */
    boolean holdBrick();

    /**
     * Spawns a new active brick.
     *
     * @return true if a new brick was created; false if spawn failed
     */
    boolean createNewBrick();

    int[][] getBoardMatrix();

    ViewData getViewData();

    /**
     * Merges the active brick into the board background, locking its cells.
     */
    void mergeBrickToBackground();

    /**
     * Clears completed rows and returns the details of the operation.
     *
     * @return summary information about cleared rows
     */
    ClearRow clearRows();

    Score getScore();

    /**
     * Resets the board state and starts a new game.
     */
    void newGame();

    /**
     * Clears the held brick slot.
     */
    void clearHold();
}

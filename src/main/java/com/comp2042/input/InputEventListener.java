package com.comp2042.input;

import com.comp2042.logic.workflow.DownData;
import com.comp2042.logic.workflow.ViewData;
import com.comp2042.events.MoveEvent;

/**
 * Defines callbacks for handling user or system input events that affect
 * game state, movement, rotation, holding, dropping, and new game creation.
 *
 * @author Eashwar
 * @version 1.0
 */
public interface InputEventListener {

    /**
     * Handles moving the active brick down.
     *
     * @param event the move event
     * @return outcome data and updated view
     */
    DownData onDownEvent(MoveEvent event);

    /**
     * Handles moving the active brick left.
     *
     * @param event the move event
     * @return updated view data
     */
    ViewData onLeftEvent(MoveEvent event);

    /**
     * Handles moving the active brick right.
     *
     * @param event the move event
     * @return updated view data
     */
    ViewData onRightEvent(MoveEvent event);

    /**
     * Handles rotating the active brick.
     *
     * @param event the move event
     * @return updated view data
     */
    ViewData onRotateEvent(MoveEvent event);

    /**
     * Handles holding or swapping the active brick.
     *
     * @param event the move event
     * @return updated view data
     */
    ViewData onHoldEvent(MoveEvent event);

    /**
     * Handles a hard drop of the active brick.
     *
     * @param event the move event
     * @return outcome data and updated view
     */
    DownData onHardDropEvent(MoveEvent event);

    /**
     * Creates and initializes a new game session.
     */
    void createNewGame();
}

package com.comp2042.core;

import com.comp2042.events.EventSource;
import com.comp2042.events.MoveEvent;
import com.comp2042.input.InputEventListener;
import com.comp2042.logic.workflow.ClearRow;
import com.comp2042.logic.workflow.DownData;
import com.comp2042.logic.workflow.ViewData;
import com.comp2042.core.GameModeHandler.GameMode;

/**
 * Coordinates user input, game board state updates, scoring, and UI events.
 * Acts as the mediator between input events, core logic, and the GUI layer.
 *
 * @author Eashwar
 * @version 1.0
 */
public class GameController implements InputEventListener {

    private Board board = new GameBoard(Constants.BOARD_ROWS, Constants.BOARD_COLS);

    private final GameEventListener viewGuiController;

    /**
     * Creates a controller and initializes the game and UI bindings.
     *
     * @param c the UI event listener to receive game updates
     */
    public GameController(GameEventListener c) {
        viewGuiController = c;
        board.createNewBrick();
        viewGuiController.setEventListener(this);
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());
        viewGuiController.bindScore(board.getScore().scoreProperty());
        viewGuiController.bindHighScore(board.getScore().highScoreProperty());
        viewGuiController.bindLinesCleared(board.getScore().linesClearedProperty());
        viewGuiController.bindLevelValue(board.getScore().levelProperty());
        viewGuiController.bindLevel(board.getScore().levelProperty());
        board.getScore().setMode(GameMode.NORMAL);
    }

    @Override
    /**
     * Handles down movement event, merging and clearing rows when needed,
     * updating the score, and notifying the UI of changes.
     *
     * @param event the move event source and context
     * @return drop outcome and refreshed view information
     */
    public DownData onDownEvent(MoveEvent event) {
        boolean canMove = board.moveBrickDown();
        ClearRow clearRow = null;
        if (!canMove) {
            board.mergeBrickToBackground();
            clearRow = board.clearRows();
            if (clearRow.getLinesRemoved() > 0) {
                board.getScore().add(clearRow.getScoreBonus());
                board.getScore().addLines(clearRow.getLinesRemoved());
            }
            if (board.createNewBrick()) {
                boolean newHigh = board.getScore().scoreProperty().get() > 0 &&
                        board.getScore().scoreProperty().get() == board.getScore().highScoreProperty().get();
                viewGuiController.gameOver(newHigh);
            }

            viewGuiController.refreshGameBackground(board.getBoardMatrix());

        } else {
            if (event.getEventSource() == EventSource.USER) {
                board.getScore().add(1);
            }
        }
        return new DownData(clearRow, board.getViewData());
    }

    @Override
    /**
     * Moves the active brick left and returns updated view data.
     *
     * @param event the move event
     * @return current view data after the move
     */
    public ViewData onLeftEvent(MoveEvent event) {
        board.moveBrickLeft();
        return board.getViewData();
    }

    @Override
    /**
     * Moves the active brick right and returns updated view data.
     *
     * @param event the move event
     * @return current view data after the move
     */
    public ViewData onRightEvent(MoveEvent event) {
        board.moveBrickRight();
        return board.getViewData();
    }

    @Override
    /**
     * Rotates the active brick counterclockwise and returns updated view data.
     *
     * @param event the move event
     * @return current view data after rotation
     */
    public ViewData onRotateEvent(MoveEvent event) {
        board.rotateLeftBrick();
        return board.getViewData();
    }

    @Override
    /**
     * Holds or swaps the active brick and returns updated view data.
     *
     * @param event the move event
     * @return current view data after hold
     */
    public ViewData onHoldEvent(MoveEvent event) {
        board.holdBrick();
        return board.getViewData();
    }


    @Override
    /**
     * Resets the board and refreshes the background for a new game.
     */
    public void createNewGame() {
        board.newGame();
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
    }

    // method to set game mode
    /**
     * Sets the active game mode for scoring and progression.
     *
     * @param mode the game mode to apply
     */
    public void setMode(GameMode mode) {
        board.getScore().setMode(mode);
    }

    /**
     * Clears the UI hold box by resetting the board's hold state.
     */
    public void clearHoldBox() { if (board != null) board.clearHold(); }

    @Override
    /**
     * Performs a hard drop by moving the brick down until blocked, updates
     * scoring and UI effects, and processes row clears and spawn.
     *
     * @param event the move event
     * @return drop outcome data and refreshed view information
     */
    public DownData onHardDropEvent(MoveEvent event) { // created method to handle hard drop event
        int dropped = 0;
        while (board.moveBrickDown())
        {
            dropped++;
        }

        // capture brick info before merging for pulse effect
        ViewData landedBrick = board.getViewData();
        int[][] brickShape = landedBrick.getBrickData();
        int xPos = landedBrick.getxPosition();
        int yPos = landedBrick.getyPosition();

        board.mergeBrickToBackground();
        ClearRow clearRow = board.clearRows();

        if (clearRow.getLinesRemoved() > 0)
        {
            board.getScore().add(clearRow.getScoreBonus());
            board.getScore().addLines(clearRow.getLinesRemoved());
        }

        if (dropped > 0)
        {
            board.getScore().add(dropped * 2);
        }

        if (board.createNewBrick())
        {
            boolean newHigh = board.getScore().scoreProperty().get() > 0 &&
                    board.getScore().scoreProperty().get() == board.getScore().highScoreProperty().get();
            viewGuiController.gameOver(newHigh);
        }

        viewGuiController.refreshGameBackground(board.getBoardMatrix());

        // trigger pulse effect for hard drop
        viewGuiController.pulseLandedBlocks(brickShape, xPos, yPos);

        return new DownData(clearRow, board.getViewData());
    }
}

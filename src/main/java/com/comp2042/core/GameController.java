package com.comp2042.core;

import com.comp2042.events.EventSource;
import com.comp2042.events.MoveEvent;
import com.comp2042.input.InputEventListener;
import com.comp2042.logic.workflow.ClearRow;
import com.comp2042.logic.workflow.DownData;
import com.comp2042.logic.workflow.ViewData;
import com.comp2042.ui.GuiController;

public class GameController implements InputEventListener {

    private Board board = new GameBoard(Constants.BOARD_ROWS, Constants.BOARD_COLS);

    private final GuiController viewGuiController;

    public GameController(GuiController c) {
        viewGuiController = c;
        board.createNewBrick();
        viewGuiController.setEventListener(this);
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());
        viewGuiController.bindScore(board.getScore().scoreProperty());
        viewGuiController.bindHighScore(board.getScore().highScoreProperty());
        viewGuiController.bindLinesCleared(board.getScore().linesClearedProperty());
        viewGuiController.bindLevelValue(board.getScore().levelProperty());
        viewGuiController.bindLevel(board.getScore().levelProperty());
    }

    @Override
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
    public ViewData onLeftEvent(MoveEvent event) {
        board.moveBrickLeft();
        return board.getViewData();
    }

    @Override
    public ViewData onRightEvent(MoveEvent event) {
        board.moveBrickRight();
        return board.getViewData();
    }

    @Override
    public ViewData onRotateEvent(MoveEvent event) {
        board.rotateLeftBrick();
        return board.getViewData();
    }

    @Override
    public ViewData onHoldEvent(MoveEvent event) {
        board.holdBrick();
        return board.getViewData();
    }


    @Override
    public void createNewGame() {
        board.newGame();
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
    }


    @Override
    public DownData onHardDropEvent(MoveEvent event) { // created method to handle hard drop event
        int dropped = 0;
        while (board.moveBrickDown())
        {
            dropped++;
        }

        board.mergeBrickToBackground();
        ClearRow clearRow = board.clearRows();

        if (clearRow.getLinesRemoved() > 0)
        {
            board.getScore().add(clearRow.getScoreBonus());
            board.getScore().addLines(clearRow.getLinesRemoved());
        }

        if (dropped > 0)
        {
            board.getScore().add(dropped);
        }

        if (board.createNewBrick())
        {
            boolean newHigh = board.getScore().scoreProperty().get() > 0 &&
                    board.getScore().scoreProperty().get() == board.getScore().highScoreProperty().get();
            viewGuiController.gameOver(newHigh);
        }

        viewGuiController.refreshGameBackground(board.getBoardMatrix());
        return new DownData(clearRow, board.getViewData());
    }
}

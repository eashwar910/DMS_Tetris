package com.comp2042.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.comp2042.core.GameModeHandler.GameMode;
import com.comp2042.events.EventSource;
import com.comp2042.events.EventType;
import com.comp2042.events.MoveEvent;
import com.comp2042.input.InputEventListener;
import com.comp2042.logic.workflow.DownData;
import com.comp2042.logic.workflow.ViewData;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

class GameControllerTest {

    // dummy view
    private static class dummy implements GameEventListener {
        int[][] lastBoard;
        ViewData lastView;
        InputEventListener eventListener;
        int refreshCount;
        boolean gameOverCalled;
        boolean gameOverNewHigh;
        int[][] lastPulseShape;
        int lastPulseX;
        int lastPulseY;
        IntegerProperty scoreProp = new SimpleIntegerProperty(0);
        IntegerProperty highScoreProp = new SimpleIntegerProperty(0);
        IntegerProperty linesProp = new SimpleIntegerProperty(0);
        IntegerProperty levelProp = new SimpleIntegerProperty(1);
        BooleanProperty isGameOver = new SimpleBooleanProperty(false);

        @Override
        public void initGameView(int[][] boardMatrix, ViewData brick) {

            // captures initial board and view data
            this.lastBoard = boardMatrix;
            this.lastView = brick;
        }

        @Override
        public void refreshGameBackground(int[][] board) {

            // records refreshes and last board state
            this.lastBoard = board;
            refreshCount++;
        }

        @Override
        public void setEventListener(InputEventListener eventListener) {

            // stores controller listener
            this.eventListener = eventListener;
        }

        @Override
        public void bindScore(IntegerProperty integerProperty) {

            // binds score
            this.scoreProp = integerProperty;
        }

        @Override
        public void bindHighScore(IntegerProperty integerProperty) {

            // binds high score
            this.highScoreProp = integerProperty;
        }

        @Override
        public void bindLinesCleared(IntegerProperty integerProperty) {

            // binds lines cleared
            this.linesProp = integerProperty;
        }

        @Override
        public void bindLevelValue(IntegerProperty integerProperty) {

            // binds level value
            this.levelProp = integerProperty;
        }

        @Override
        public BooleanProperty getIsGameOver() {

            // exposes game over property
            return isGameOver;
        }

        @Override
        public void bindLevel(IntegerProperty integerProperty) {

            // binds level
            this.levelProp = integerProperty;
        }

        @Override
        public void pulseLandedBlocks(int[][] brickShape, int xPosition, int yPosition) {

            // captures pulse parameters
            this.lastPulseShape = brickShape;
            this.lastPulseX = xPosition;
            this.lastPulseY = yPosition;
        }

        @Override
        public void gameOver(boolean newHighScore) {

            // marks game over and flag
            gameOverCalled = true;
            gameOverNewHigh = newHighScore;
            isGameOver.set(true);
        }
    }

    private GameController newController(dummy view) {
        // create controller for dummy
        return new GameController(view);
    }

    // test for on down method
    @Test
    void onDownEvent() {

        // user down should add one point and return downdata
        dummy view = new dummy();
        GameController controller = newController(view);

        int beforeScore = view.scoreProp.get();
        DownData data = controller.onDownEvent(new MoveEvent(EventType.DOWN, EventSource.USER));
        // data should not be null
        assertNotNull(data);
        int afterScore = view.scoreProp.get();
        // score should have increased by 1
        assertEquals(beforeScore + 1, afterScore);
        assertNotNull(data.getViewData());
    }

    // test for left method
    @Test
    void onLeftEvent() {

        // left should decrement x
        dummy view = new dummy();
        GameController controller = newController(view);

        ViewData before = view.lastView;
        ViewData after = controller.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER));

        // x value should be 1 less than earlier
        assertEquals(before.getxPosition() - 1, after.getxPosition());
        assertEquals(before.getyPosition(), after.getyPosition());
    }

    // test for right method
    @Test
    void onRightEvent() {

        // right should increment x
        dummy view = new dummy();
        GameController controller = newController(view);

        ViewData before = view.lastView;
        ViewData after = controller.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER));

        // x value should be 1 more than earlier
        assertEquals(before.getxPosition() + 1, after.getxPosition());
        assertEquals(before.getyPosition(), after.getyPosition());
    }

    // test for rotate method (up)
    @Test
    void onRotateEvent() {

        // rotate returns a valid view
        dummy view = new dummy();
        GameController controller = newController(view);

        ViewData vd = controller.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.USER));
        // vd should not be null
        assertNotNull(vd);
    }

    // test for hold method
    @Test
    void onHoldEvent() {

        // hold returns a valid view
        dummy view = new dummy();
        GameController controller = newController(view);

        ViewData vd = controller.onHoldEvent(new MoveEvent(EventType.HOLD, EventSource.USER));
        // vd should not be null
        assertNotNull(vd);
    }

    // test for new game method
    @Test
    void createNewGame() {

        // new game should refresh and clear board
        dummy view = new dummy();
        GameController controller = newController(view);

        int refreshBefore = view.refreshCount;
        controller.createNewGame();
        // refresh count should have incremented by 1
        assertTrue(view.refreshCount > refreshBefore);

        int sum = java.util.Arrays.stream(view.lastBoard).flatMapToInt(java.util.Arrays::stream).sum();
        assertEquals(0, sum);
    }

    // test for set game mode method
    @Test
    void setMode() {

        // mode can be changed without breaking bindings
        dummy view = new dummy();

        GameController controller = newController(view);
        // high score property should not be null
        assertNotNull(view.highScoreProp);
        controller.setMode(GameMode.TIMED);
        assertNotNull(view.highScoreProp);
    }

    // test for clear hold box method
    @Test
    void clearHoldBox() {

        // clear hold runs and does not toggle game over
        dummy view = new dummy();
        GameController controller = newController(view);

        controller.clearHoldBox();
        // game over flag should be false
        assertFalse(view.isGameOver.get());
    }

    // test for hard drop method
    @Test
    void onHardDropEvent() {

        // hard drop should trigger pulse and increase or keep score
        dummy view = new dummy();
        GameController controller = newController(view);

        int beforeScore = view.scoreProp.get();
        DownData data = controller.onHardDropEvent(new MoveEvent(EventType.HARD_DROP, EventSource.USER));
        // data, viewdata and pulse shape cannot be null
        assertNotNull(data);
        assertNotNull(data.getViewData());
        assertNotNull(view.lastPulseShape);

        int afterScore = view.scoreProp.get();
        // score and refresh count should have increased
        assertTrue(afterScore >= beforeScore);
        assertTrue(view.refreshCount > 0);
    }
}

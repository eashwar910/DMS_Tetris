package com.comp2042.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.comp2042.input.InputEventListener;
import com.comp2042.logic.workflow.ViewData;
import com.comp2042.ui.OverlayManager;

import javafx.animation.Animation;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.Label;

class GameLoopManagerTest {

    // start javafx toolkit once for label/timeline usage
    @BeforeAll
    static void initFx() {
        try { Platform.startup(() -> {}); }
        catch (IllegalStateException ignored) {}
    }

    // dummy view to build a lightweight game controller
    static class dummy implements GameEventListener {

        InputEventListener listener;
        IntegerProperty score = new SimpleIntegerProperty(0);
        IntegerProperty high = new SimpleIntegerProperty(0);
        IntegerProperty lines = new SimpleIntegerProperty(0);
        IntegerProperty level = new SimpleIntegerProperty(1);
        int[][] lastBoard;
        ViewData lastBrick;

        @Override
        public void initGameView(int[][] boardMatrix, ViewData brick) { lastBoard = boardMatrix; lastBrick = brick; }

        @Override
        public void refreshGameBackground(int[][] board) { lastBoard = board; }

        @Override
        public void setEventListener(InputEventListener eventListener) { listener = eventListener; }

        @Override
        public void bindScore(IntegerProperty integerProperty) { score = integerProperty; }

        @Override
        public void bindHighScore(IntegerProperty integerProperty) { high = integerProperty; }

        @Override
        public void bindLinesCleared(IntegerProperty integerProperty) { lines = integerProperty; }

        @Override
        public void bindLevelValue(IntegerProperty integerProperty) { level = integerProperty; }

        @Override
        public BooleanProperty getIsGameOver() { return new javafx.beans.property.SimpleBooleanProperty(false); }

        @Override
        public void bindLevel(IntegerProperty integerProperty) {}

        @Override
        public void pulseLandedBlocks(int[][] brickShape, int xPosition, int yPosition) {}

        @Override
        public void gameOver(boolean newHighScore) {}

    }

    // gui to capture upside down toggle
    static class dummyGui extends com.comp2042.ui.GuiController {
        boolean upside;
        @Override public void setUpsideDownMode(boolean enable) { this.upside = enable; }
    }

    // test for setup game mode handler method
    @Test
    void setupModeHandler() {

        // verifies mode handler toggles upside down state and timed label visibility
        dummyGui gui = new dummyGui();
        GameLoopManager glm = new GameLoopManager(gui);

        Label timer = new Label();
        OverlayManager overlay = new OverlayManager(null, null, null, null);
        glm.setupModeHandler(timer, overlay);
        assertNotNull(glm.getModeHandler());

        glm.getModeHandler().startUpsideDown();
        // gui should be toggled for upside down mode
        assertTrue(gui.upside);

        glm.getModeHandler().startNormal();
        // gui should be toggled for normal mode
        assertFalse(gui.upside);

        glm.getModeHandler().startTimed();
        // gui should be toggled for timed mode
        assertTrue(timer.isVisible());
    }

    // test for setup timeline method
    @Test
    void setupTimeline() {

        // timeline should be created paused with correct cycle count and interval
        dummyGui gui = new dummyGui();
        GameLoopManager glm = new GameLoopManager(gui);
        glm.setupTimeline();

        Timeline tl = glm.getTimeLine();
        assertNotNull(tl);
        assertTrue(tl.getStatus() != Animation.Status.RUNNING);
        assertEquals(Animation.INDEFINITE, tl.getCycleCount());
        double ms = tl.getKeyFrames().get(0).getTime().toMillis();
        assertEquals(Constants.FALL_INTERVAL_MS, ms);
    }

    // test for isplaying value method
    @Test
    void isPlaying() {

        // isPlaying should reflect pause/game over flags
        dummyGui gui = new dummyGui();
        GameLoopManager glm = new GameLoopManager(gui);

        // flag should be true
        assertTrue(glm.isPlaying());
        glm.getIsPause().set(true);
        // flag should be false - paused
        assertFalse(glm.isPlaying());
        glm.getIsPause().set(false);
        glm.getIsGameOver().set(true);
        // flag should be false - game over
        assertFalse(glm.isPlaying());
        glm.getIsGameOver().set(false);
        // flag should be true - not paused or game over
        assertTrue(glm.isPlaying());
    }

    // test for get game mode method
    @Test
    void getModeHandler() {

        // mode handler should be non-null after setup
        dummyGui gui = new dummyGui();
        GameLoopManager glm = new GameLoopManager(gui);
        glm.setupModeHandler(new Label(), new OverlayManager(null, null, null, null));
        assertNotNull(glm.getModeHandler());
    }

    // test for get timeline method
    @Test
    void getTimeLine() {

        // timeline should be accessible after setup
        dummyGui gui = new dummyGui();
        GameLoopManager glm = new GameLoopManager(gui);
        // time line should be null before setup
        assertNull(glm.getTimeLine());
        glm.setupTimeline();
        // timeline should not be null after setup
        assertNotNull(glm.getTimeLine());
    }

    // test get is pause method
    @Test
    void getIsPause() {

        // pause property should be writable and readable
        dummyGui gui = new dummyGui();
        GameLoopManager glm = new GameLoopManager(gui);
        assertFalse(glm.getIsPause().get());

        glm.getIsPause().set(true);
        assertTrue(glm.getIsPause().get());
    }

    // test is game over method
    @Test
    void getIsGameOver() {

        // game over property should be writable and readable
        dummyGui gui = new dummyGui();
        GameLoopManager glm = new GameLoopManager(gui);
        assertFalse(glm.getIsGameOver().get());

        glm.getIsGameOver().set(true);
        assertTrue(glm.getIsGameOver().get());
    }

    // test update fall interval method
    @Test
    void updateFallInterval() {

        // updates timeline keyframe duration based on level
        dummyGui gui = new dummyGui();
        GameLoopManager glm = new GameLoopManager(gui);
        glm.setupTimeline();

        glm.updateFallInterval(1);
        double ms1 = glm.getTimeLine().getKeyFrames().get(0).getTime().toMillis();
        assertEquals(Constants.FALL_INTERVAL_MS, ms1);

        glm.updateFallInterval(5);
        double ms5 = glm.getTimeLine().getKeyFrames().get(0).getTime().toMillis();
        double base = Constants.BASE_TIME;
        double dec = Constants.TIME_DECREMENT;
        double t = Math.pow(Math.max(0.0, base - ((5 - 1) * dec)), Math.max(0, 5 - 1));
        double expected = Math.max(Constants.MIN_FALL_INTERVAL_MS, t * 1000.0);
        assertEquals(expected, ms5);

        // if running and not paused or game over, it should continue running
        glm.getTimeLine().play();
        assertEquals(Animation.Status.RUNNING, glm.getTimeLine().getStatus());
        glm.updateFallInterval(2);
        assertEquals(Animation.Status.RUNNING, glm.getTimeLine().getStatus());
    }
}

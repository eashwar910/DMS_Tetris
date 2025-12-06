package com.comp2042.core;

import javafx.application.Platform;
import javafx.scene.control.Label;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameModeHandlerTest {

    // start javafx toolkit once to use label and timelines
    @BeforeAll
    static void initFx() {
        try { Platform.startup(() -> {}); }
        catch (IllegalStateException ignored) {}
    }

    // test for get mode method
    @Test
    void getMode() {

        // mode should change based on start methods
        Label timer = new Label();
        // line to hold the game mode (ai suggestion fix)
        final java.util.concurrent.atomic.AtomicReference<GameModeHandler.GameMode> last = new java.util.concurrent.atomic.AtomicReference<>();
        // game mode default - normal
        GameModeHandler h = new GameModeHandler(timer, () -> {}, m -> last.set(m));
        assertEquals(GameModeHandler.GameMode.NORMAL, h.getMode());
        assertEquals(GameModeHandler.GameMode.BOTTOMS_UP, h.getMode());
        h.startNormal();
        assertEquals(GameModeHandler.GameMode.NORMAL, h.getMode());
        h.startTimed();
        assertEquals(GameModeHandler.GameMode.TIMED, h.getMode());
        assertEquals(GameModeHandler.GameMode.TIMED, last.get());
    }

    // test for start normal mode method
    @Test
    void startNormal() {

        // normal should hide timer and notify mode change
        Label timer = new Label();
        timer.setVisible(true);
        final java.util.concurrent.atomic.AtomicReference<GameModeHandler.GameMode> last = new java.util.concurrent.atomic.AtomicReference<>();
        GameModeHandler h = new GameModeHandler(timer, () -> {}, m -> last.set(m));

        // timer should not be visible and mode should be normal
        h.startNormal();
        assertFalse(timer.isVisible());
        assertEquals(GameModeHandler.GameMode.NORMAL, last.get());
    }

    // test start upside down method
    @Test
    void startUpsideDown() {

        // bottoms up should hide timer and notify mode change
        Label timer = new Label();
        timer.setVisible(true);
        final java.util.concurrent.atomic.AtomicReference<GameModeHandler.GameMode> last = new java.util.concurrent.atomic.AtomicReference<>();
        GameModeHandler h = new GameModeHandler(timer, () -> {}, m -> last.set(m));

        h.startUpsideDown();
        assertFalse(timer.isVisible());
        assertEquals(GameModeHandler.GameMode.BOTTOMS_UP, last.get());
    }

    // test for start timed mode method
    @Test
    void startTimed() {

        // timed should show timer, set starting text and notify mode change
        Label timer = new Label();
        final java.util.concurrent.atomic.AtomicReference<GameModeHandler.GameMode> last = new java.util.concurrent.atomic.AtomicReference<>();
        GameModeHandler h = new GameModeHandler(timer, () -> {}, m -> last.set(m));

        h.startTimed();
        assertTrue(timer.isVisible());
        assertEquals("02:00:00", timer.getText());
        assertEquals(GameModeHandler.GameMode.TIMED, last.get());
    }

    // test for pause method
    @Test
    void pause() {

        // pause should be callable when timed is running
        Label timer = new Label();
        GameModeHandler h = new GameModeHandler(timer, () -> {}, m -> {});
        h.startTimed();
        h.pause();
    }

    // test for resume method
    @Test
    void resume() {

        // resume should be callable when timer is visible
        Label timer = new Label();
        GameModeHandler h = new GameModeHandler(timer, () -> {}, m -> {});
        h.startTimed();
        h.pause();
        h.resume();
        assertTrue(timer.isVisible());
    }

    // test for stop method
    @Test
    void stop() {

        // stop should allow starting timed again cleanly
        Label timer = new Label();
        GameModeHandler h = new GameModeHandler(timer, () -> {}, m -> {});
        h.startTimed();
        h.stop();
        h.startTimed();
        assertTrue(timer.isVisible());
        assertEquals("02:00:00", timer.getText());
    }

    // test for restart method
    @Test
    void restartForNewGame() {

        // restart in timed keeps timer visible; normal hides timer
        Label timer = new Label();
        GameModeHandler h = new GameModeHandler(timer, () -> {}, m -> {});

        h.startTimed();
        h.restartForNewGame();
        assertTrue(timer.isVisible());

        h.startNormal();
        h.restartForNewGame();
        assertFalse(timer.isVisible());
    }
}

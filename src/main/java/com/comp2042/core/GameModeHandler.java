package com.comp2042.core;

import java.util.function.Consumer;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.util.Duration;

/**
 * Handles switching between game modes, manages timed countdowns, and
 * communicates mode changes to interested consumers.
 *
 * @author Eashwar
 * @version 1.0
 */
public final class GameModeHandler {

    public enum GameMode { NORMAL, TIMED, BOTTOMS_UP } // replaced tetro with upside down mode

    private final Label timerLabel;
    private final Runnable onTimeUp;
    private final Consumer<GameMode> onModeChanged;

    private Timeline modeTimer;
    private GameMode current = GameMode.NORMAL;

    /**
     * Creates a handler for managing game modes and timers.
     *
     * @param timerLabel label used for displaying countdown when timed mode
     * @param onTimeUp callback invoked when the countdown reaches zero
     * @param onModeChanged consumer notified on mode changes
     */
    public GameModeHandler(Label timerLabel,
                           Runnable onTimeUp,
                           Consumer<GameMode> onModeChanged) {
        this.timerLabel = timerLabel;
        this.onTimeUp = onTimeUp;
        this.onModeChanged = onModeChanged;
    }

    // method to get current game mode
    public GameMode getMode() { return current; }

    // method to start the normal game mode
    /**
     * Activates normal mode without timers and hides the countdown label.
     */
    public void startNormal() {
        current = GameMode.NORMAL;
        if (onModeChanged != null) onModeChanged.accept(current);
        stop();
        if (timerLabel != null) timerLabel.setVisible(false);
    }

    // method to start bottoms up mode
    /**
     * Activates bottoms-up mode and hides the countdown label.
     */
    public void startUpsideDown() {
        current = GameMode.BOTTOMS_UP;
        if (onModeChanged != null) onModeChanged.accept(current);
        stop();
        if (timerLabel != null) timerLabel.setVisible(false);
    }

    // method to start the timed mode
    /**
     * Activates timed mode, displays the countdown label, and starts a timer.
     */
    public void startTimed() {
        current = GameMode.TIMED;
        if (onModeChanged != null) onModeChanged.accept(current);
        if (timerLabel != null) {
            timerLabel.setVisible(true);
            timerLabel.setText("02:00:00");
        }
        restartForNewGame();
    }

    // pasue resume and stop game methods
    /**
     * Pauses the active countdown timer if present.
     */
    public void pause() { if (modeTimer != null) modeTimer.pause(); }
    /**
     * Resumes the countdown timer when the label is visible.
     */
    public void resume() { if (modeTimer != null && timerLabel != null && timerLabel.isVisible()) modeTimer.play(); }
    /**
     * Stops and clears the countdown timer if present.
     */
    public void stop() { if (modeTimer != null) { modeTimer.stop(); modeTimer = null; } }

    // restart method
    /**
     * Restarts timing setup for a new game based on the current mode.
     */
    public void restartForNewGame() {
        stop();
        if (current == GameMode.TIMED) startCountdown(120_000);
        else if (timerLabel != null) timerLabel.setVisible(false);
    }

    // countdown for timed mode
    /**
     * Starts a countdown timer for timed mode.
     *
     * @param durationMs duration in milliseconds
     */
    private void startCountdown(long durationMs) {
        final long start = System.currentTimeMillis();
        modeTimer = new Timeline(new KeyFrame(Duration.millis(50), ae -> {
            long remaining = Math.max(0, durationMs - (System.currentTimeMillis() - start));
            long mm = remaining / 60000;
            long ss = (remaining % 60000) / 1000;
            long cs = (remaining % 1000) / 10;
            if (timerLabel != null) {
                timerLabel.setText(String.format("%02d:%02d:%02d", mm, ss, cs));
            }
            if (remaining == 0) {
                stop();
                if (onTimeUp != null) onTimeUp.run();
            }
        }));
        modeTimer.setCycleCount(Animation.INDEFINITE);
        modeTimer.play();
    }
}

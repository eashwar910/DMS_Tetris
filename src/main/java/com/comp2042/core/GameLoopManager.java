package com.comp2042.core;

import com.comp2042.events.EventSource;
import com.comp2042.events.EventType;
import com.comp2042.events.MoveEvent;
import com.comp2042.ui.GuiController;
import com.comp2042.ui.OverlayManager;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Label;
import javafx.util.Duration;

/**
 * Manages the main game loop, timing, and mode changes, coordinating
 * periodic drop events and UI interactions.
 *
 * @author Eashwar
 * @version 1.0
 */
public class GameLoopManager {
    private final GuiController guiController;
    private Timeline timeLine;
    private GameModeHandler modeHandler;
    private final BooleanProperty isPause = new SimpleBooleanProperty();
    private final BooleanProperty isGameOver = new SimpleBooleanProperty();

    /**
     * Constructs the loop manager connected to the GUI controller.
     *
     * @param guiController the GUI controller for dispatching events
     */
    public GameLoopManager(GuiController guiController) {
        this.guiController = guiController;
    }

    /**
     * Configures the game mode handler, wiring timer and overlay callbacks
     * and sending the mode changes to the renderer and controller.
     *
     * @param timerLabel label to display timer or mode information
     * @param overlayManager manager handling overlays such as game over
     */
    public void setupModeHandler(Label timerLabel, OverlayManager overlayManager) {
        this.modeHandler = new GameModeHandler(
                timerLabel,
                () -> overlayManager.gameOver(false),
                mode -> {
                    // logic to toggle renderer mode based on game mode
                    boolean isUpsideDown = (mode == GameModeHandler.GameMode.BOTTOMS_UP);
                    guiController.setUpsideDownMode(isUpsideDown);

                    if (guiController.getEventListener() instanceof GameController gc) {
                        gc.setMode(mode);
                    }
                }
        );
    }

    /**
     * Initializes the timeline that triggers periodic down events
     * based on the current fall interval.
     */
    public void setupTimeline() {
        this.timeLine = new Timeline(new KeyFrame(
                Duration.millis(Constants.FALL_INTERVAL_MS),
                ae -> guiController.moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        this.timeLine.setCycleCount(Animation.INDEFINITE);
        this.timeLine.pause();
    }

    /**
     * Indicates whether the game is currently running (not paused or over).
     *
     * @return true if active, false if paused or game over
     */
    public boolean isPlaying() {
        return !isPause.get() && !isGameOver.get();
    }

    public GameModeHandler getModeHandler() {
        return modeHandler;
    }

    public Timeline getTimeLine() {
        return timeLine;
    }

    public BooleanProperty getIsPause() {
        return isPause;
    }

    public BooleanProperty getIsGameOver() {
        return isGameOver;
    }

    /**
     * Updates the fall interval according to the level and restarts the
     * timeline when appropriate.
     *
     * @param level current game level
     */
    public void updateFallInterval(int level) {
        double ms;
        if (level <= 1) {
            ms = Constants.FALL_INTERVAL_MS;
        } else {
            double base = Constants.BASE_TIME;
            double dec = Constants.TIME_DECREMENT;
            double t = Math.pow(Math.max(0.0, base - ((level - 1) * dec)), Math.max(0, level - 1));
            ms = Math.max(Constants.MIN_FALL_INTERVAL_MS, t * 1000.0);
        }
        boolean wasRunning = timeLine != null && timeLine.getStatus() == Animation.Status.RUNNING;
        if (timeLine != null) {
            timeLine.stop();
            timeLine.getKeyFrames().setAll(new KeyFrame(
                    Duration.millis(ms),
                    ae -> guiController.moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
            ));
            timeLine.setCycleCount(Animation.INDEFINITE);
            if (wasRunning && !isPause.get() && !isGameOver.get()) {
                timeLine.play();
            }
        }
    }
}

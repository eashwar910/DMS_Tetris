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

public class GameLoopManager {
    private final GuiController guiController;
    public Timeline timeLine;
    public GameModeHandler modeHandler;
    public final BooleanProperty isPause = new SimpleBooleanProperty();
    public final BooleanProperty isGameOver = new SimpleBooleanProperty();

    public GameLoopManager(GuiController guiController) {
        this.guiController = guiController;
    }

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

    public void setupTimeline() {
        this.timeLine = new Timeline(new KeyFrame(
                Duration.millis(Constants.FALL_INTERVAL_MS),
                ae -> guiController.moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        this.timeLine.setCycleCount(Animation.INDEFINITE);
        this.timeLine.pause();
    }

    public boolean isPlaying() {
        return !isPause.get() && !isGameOver.get();
    }// created wrappers for methods from mode handler (fix later)

    public void startNormalMode() {
        modeHandler.startNormal();
    }// wrapper for new mode (fix later)

    public void startUpsideDownMode() {
        modeHandler.startUpsideDown();
    }

    public void startTimedMode() {
        modeHandler.startTimed();
    }

    public void pauseModeTimer() {
        modeHandler.pause();
    }

    public void resumeModeTimer() {
        modeHandler.resume();
    }

    public void stopModeTimer() {
        modeHandler.stop();
    }

    public GameModeHandler.GameMode getCurrentMode() {
        return modeHandler.getMode();
    }

    public void restartCurrentModeTimer() {
        modeHandler.restartForNewGame();
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
package com.comp2042.ui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public final class OverlayManager {

    private final Region startOverlay;
    private final Region helpOverlay;
    private final Region pauseOverlay;
    private final Region gameOverOverlay;

    private GuiController controller;
    private DynamicStartScreen dynamicStartScreen;
    private PauseOverlay pauseScreen;
    private GameOverOverlay gameOverPanel;
    private GridPane gamePanel;
    private Button playButton;
    private Button helpButton;
    private Button closeHelpButton;
    private MusicManager musicManager;
    private boolean helpFromStart = false;

    public OverlayManager(Region startOverlay, Region helpOverlay, Region pauseOverlay, Region gameOverOverlay) {
        this.startOverlay = startOverlay;
        this.helpOverlay = helpOverlay;
        this.pauseOverlay = pauseOverlay;
        this.gameOverOverlay = gameOverOverlay;
    }

    public void bindOverlayFill(Region overlay) {

        // transfer the scene high and width calculation methods to overlay manager
        // if scene exists, bind overlay to scene width and height
        // if secne doesnt exist, bind overlay to pane width and hight
        if (overlay == null) return;
        Platform.runLater(() -> {
            if (overlay.getScene() != null) {
                overlay.prefWidthProperty().bind(overlay.getScene().widthProperty());
                overlay.prefHeightProperty().bind(overlay.getScene().heightProperty());
            } else if (overlay.getParent() instanceof Pane) {
                Pane parent = (Pane) overlay.getParent();
                overlay.prefWidthProperty().bind(parent.widthProperty());
                overlay.prefHeightProperty().bind(parent.heightProperty());
            }
        });
    }

    // centralized overlay setup workflow
    // refactored to reduce Cognitive Complexity
    public void setup(GuiController controller,
                      MusicManager musicManager,
                      DynamicStartScreen dynamicStartScreen,
                      GridPane gamePanel,
                      Button playButton,
                      Button raceButton,
                      Button mineButton,
                      Button helpButton,
                      Button closeHelpButton,
                      PauseOverlay pauseScreen,
                      GameOverOverlay gameOverPanel) {

        this.controller = controller;
        this.musicManager = musicManager;
        this.dynamicStartScreen = dynamicStartScreen;
        this.gamePanel = gamePanel;
        this.playButton = playButton;
        this.helpButton = helpButton;
        this.closeHelpButton = closeHelpButton;
        this.pauseScreen = pauseScreen;
        this.gameOverPanel = gameOverPanel;

        if (gameOverOverlay != null) gameOverOverlay.setVisible(false);

        // call setup functions
        setupGameOverPanel();
        setupStartOverlay();
        setupGameButtons(playButton, raceButton, mineButton);
        setupHelpOverlay(helpButton, closeHelpButton);
        setupPauseOverlay();

        if (gameOverOverlay != null) {
            bindOverlayFill(gameOverOverlay);
            hide(gameOverOverlay);
        }
    }

    //defining centralised methods
    // gameover panel setup method
    private void setupGameOverPanel() {
        if (gameOverPanel != null)
        {
            // direct musicManager calls
            gameOverPanel.setRestartEventHandler(e -> { playClick(); newGame(e); });
            gameOverPanel.setExitEventHandler(e -> { playClick(); quitGame(); });
        }
    }

    // start overlay setup
    private void setupStartOverlay() {
        if (startOverlay != null) {
            controller.getIsPause().set(true);
            bindOverlayFill(startOverlay);
            show(startOverlay);
            if (dynamicStartScreen != null) dynamicStartScreen.start();

            // Direct music call
            if (musicManager != null) musicManager.startStartLoop("start_screen.mp3");

            controller.setBrickPanelVisible(false);
        }
    }

    // game button setup method
    private void setupGameButtons(Button playButton, Button raceButton, Button mineButton) {
        if (playButton != null) playButton.setOnAction(e -> { playClick(); startNormalGame(); });
        if (raceButton != null) raceButton.setOnAction(e -> { playClick(); startTimedGame(); });
        if (mineButton != null) mineButton.setOnAction(e -> { playClick(); startUpsideDownGame(); });
    }

    // help overlay setup method
    private void setupHelpOverlay(Button helpButton, Button closeHelpButton) {
        if (helpOverlay != null) {
            bindOverlayFill(helpOverlay);
            hide(helpOverlay);
        }

        if (helpButton != null) {
            helpButton.setOnAction(e -> {
                playClick();
                helpFromStart = startOverlay != null && startOverlay.isVisible();
                show(helpOverlay);
            });
        }

        if (closeHelpButton != null) {
            closeHelpButton.setOnAction(e -> {
                playClick();
                hide(helpOverlay);
                if (helpFromStart && startOverlay != null) {
                    show(startOverlay);
                    helpFromStart = false;
                }
            });
        }
    }

    // pause overlay setup method
    private void setupPauseOverlay() {
        if (pauseScreen != null && pauseOverlay != null) {
            bindOverlayFill(pauseOverlay);
            hide(pauseOverlay);
            pauseScreen.prefWidthProperty().bind(pauseOverlay.widthProperty());
            pauseScreen.prefHeightProperty().bind(pauseOverlay.heightProperty());
            pauseScreen.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

            // direct music/click calls
            pauseScreen.setResumeHandler(e -> { playClick(); resumeGame(); });
            pauseScreen.setQuitHandler(e -> { playClick(); goToMainMenu(); });
            pauseScreen.setNewGameHandler(e -> { playClick(); newGame(e); });
        }
    }

    // show and hide overlay method definition
    private void show(Region overlay) {
        if (overlay != null)
        {
            overlay.setVisible(true);
            overlay.toFront();
            if (overlay == startOverlay) {
                controller.setBrickPanelVisible(false);
            }
        }
    }
    private void hide(Region overlay) {
        if (overlay != null)
        {
            overlay.setVisible(false);
        }
    }

    // start game method with proper overlay flow
    // changed to start normal game method
    public void startNormalGame() {
        prepareForStart();
        showCountdownThen(() -> {
            controller.startNormalMode();
            if (musicManager != null) musicManager.playLoopFromStart();

            if (controller.getEventListener() != null) controller.getEventListener().createNewGame();
            controller.getIsGameOver().set(false);

            if (controller.getTimeLine() != null) controller.getTimeLine().play();
            controller.getIsPause().set(false);
            controller.setBrickPanelVisible(true);

            if (gamePanel != null) gamePanel.requestFocus();
        });
    }

    // start the timed game mode
    public void startTimedGame() {
        prepareForStart();
        showCountdownThen(() -> {
            controller.startTimedMode();
            if (musicManager != null) musicManager.playLoopFromStart();

            // fixed game being stuck at game over after new high score
            // fixed game resuming after going to main menu from pause screen
            if (controller.getEventListener() != null) controller.getEventListener().createNewGame();
            controller.getIsGameOver().set(false);

            if (controller.getTimeLine() != null) controller.getTimeLine().play();
            controller.getIsPause().set(false);
            controller.setBrickPanelVisible(true);

            if (gamePanel != null) gamePanel.requestFocus();
        });
    }

    // start upside down game mode
    public void startUpsideDownGame() {
        prepareForStart();
        showCountdownThen(() -> {
            controller.startUpsideDownMode();
            if (musicManager != null) musicManager.playLoopFromStart();

            if (controller.getEventListener() != null) controller.getEventListener().createNewGame();
            controller.getIsGameOver().set(false);

            if (controller.getTimeLine() != null) controller.getTimeLine().play();
            controller.getIsPause().set(false);
            controller.setBrickPanelVisible(true);

            if (gamePanel != null) gamePanel.requestFocus();
        });
    }

    // resume game
    public void resumeGame() {
        controller.getIsPause().set(false);
        if (controller.getTimeLine() != null) controller.getTimeLine().play();
        controller.resumeModeTimer();

        if (musicManager != null) musicManager.resume();

        hide(pauseOverlay);
        if (gamePanel != null) gamePanel.requestFocus();
    }

    // method for go to main menu button (replacement for quit game button)
    private void goToMainMenu() {
        hide(pauseOverlay);
        if (startOverlay != null) {
            show(startOverlay);
            controller.getIsPause().set(true);
            if (controller.getTimeLine() != null) controller.getTimeLine().pause();
            if (dynamicStartScreen != null) dynamicStartScreen.start();
            controller.setBrickPanelVisible(false);
            controller.stopModeTimer();

            if (musicManager != null) {
                musicManager.stop();
                musicManager.startStartLoop("start_screen.mp3");
            }
        }
    }

    // toggle pause method moved
    public void togglePause() {
        if (!controller.getIsPause().get()) {
            controller.getIsPause().set(true);
            if (controller.getTimeLine() != null) controller.getTimeLine().pause();
            controller.pauseModeTimer();

            if (musicManager != null) musicManager.pause();

            show(pauseOverlay);
        } else {
            resumeGame();
        }
    }

    public void quitGame() {
        Platform.exit();
        System.exit(0);
    }

    // new game method
    public void newGame(ActionEvent actionEvent) {
        if (controller.getTimeLine() != null) controller.getTimeLine().stop();
        if (gameOverPanel != null) gameOverPanel.setVisible(false);

        hide(gameOverOverlay);
        hide(pauseOverlay);
        controller.getEventListener().createNewGame();
        controller.restartCurrentModeTimer();

        if (musicManager != null) {
            musicManager.stopFx();
            musicManager.restart();
        }

        if (gamePanel != null) gamePanel.requestFocus();
        if (startOverlay != null && startOverlay.isVisible())
        {
            if (controller.getTimeLine() != null) controller.getTimeLine().pause();
            controller.pauseModeTimer();
        }
        else
        {
            if (controller.getTimeLine() != null) controller.getTimeLine().play();
            controller.resumeModeTimer();
        }
        controller.getIsPause().set(false);
        controller.getIsGameOver().set(false);
    }

    // refactored to reduce Cognitive Complexity
    // game over overlay
    public void gameOver(boolean newHighScore) {
        stopRunningGame();

        controller.getIsPause().set(true);
        controller.getIsGameOver().set(true);
        show(gameOverOverlay);

        if (gameOverPanel != null) {
            configureGameOverScreen(newHighScore);
            gameOverPanel.setVisible(true);
        }
    }

    // split the loops to methods
    // metho to stop running game and other entities that its bound to
    private void stopRunningGame() {
        if (controller.getTimeLine() != null) controller.getTimeLine().stop();
        controller.stopModeTimer();
        if (musicManager != null) musicManager.stop();
    }

    // game over screen choosing method
    private void configureGameOverScreen(boolean newHighScore) {
        if (newHighScore)
        {
            setupHighScoreGameOver();
        }
        else
        {
            setupStandardGameOver();
        }
    }

    // new high score method
    private void setupHighScoreGameOver() {
        gameOverPanel.setHighScoreMode();
        if (musicManager != null) musicManager.playOnce("high_score.mp3");
        gameOverPanel.setRestartEventHandler(e -> { playClick(); newGame(e); });
        gameOverPanel.setExitEventHandler(e -> handleHighScoreExit());
    }

    // normal game over method
    private void setupStandardGameOver() {
        gameOverPanel.setDefaultMode();
        if (musicManager != null) musicManager.playOnce("game_over.mp3");
        gameOverPanel.setRestartEventHandler(e -> { playClick(); newGame(e); });
        gameOverPanel.setExitEventHandler(e -> { playClick(); quitGame(); });
    }

    // high score exit menu method
    private void handleHighScoreExit() {
        playClick();
        hide(gameOverOverlay);
        if (startOverlay != null) {
            show(startOverlay);
            controller.getIsPause().set(true);
            if (controller.getTimeLine() != null) controller.getTimeLine().pause();
            if (dynamicStartScreen != null) dynamicStartScreen.start();
            if (musicManager != null) musicManager.startStartLoop("start_screen.mp3");
        }
    }

    // start game error handling method centralized
    private void prepareForStart() {
        controller.gameModeTransition();
        if (startOverlay != null) {
            hide(startOverlay);
            if (dynamicStartScreen != null) dynamicStartScreen.stop();
            if (musicManager != null) musicManager.stopStart();
        }
        if (helpOverlay != null) hide(helpOverlay);
        if (controller.getEventListener() != null) controller.getEventListener().createNewGame();
        controller.getIsGameOver().set(false);
    }

    // show countdown method
    private void showCountdownThen(Runnable onFinish) {
        Region anchor = startOverlay != null ?
                startOverlay : (helpOverlay != null ? helpOverlay : pauseOverlay);
        Pane parent = anchor != null && anchor.getParent() instanceof Pane ?
                (Pane) anchor.getParent() : null;
        if (parent == null) { onFinish.run(); return;
        }

        StackPane overlay = new StackPane();
        overlay.setMouseTransparent(true);
        overlay.prefWidthProperty().bind(parent.widthProperty());
        overlay.prefHeightProperty().bind(parent.heightProperty());

        Label label = new Label("3");
        label.getStyleClass().add("titleClass");
        label.setScaleX(0.4);
        label.setScaleY(0.4);
        overlay.getChildren().add(label);
        StackPane.setAlignment(label, Pos.CENTER);

        parent.getChildren().add(overlay);

        Runnable play1 = () -> playNumber(label, "1", () -> finishCountdown(parent, overlay, onFinish));
        Runnable play2 = () -> playNumber(label, "2", play1);
        Runnable play3 = () -> playNumber(label, "3", play2);
        play3.run();
    }

    // animation for coutndown
    private void playNumber(Label label, String text, Runnable next) {
        label.setText(text);
        label.setScaleX(0.4);
        label.setScaleY(0.4);
        ScaleTransition st = new ScaleTransition(Duration.millis(500), label);
        st.setInterpolator(Interpolator.EASE_OUT);
        st.setFromX(0.4);
        st.setFromY(0.4);
        st.setToX(1.6);
        st.setToY(1.6);
        st.setOnFinished(e -> next.run());
        st.play();
    }

    private void finishCountdown(Pane parent, StackPane overlay, Runnable onFinish) {
        parent.getChildren().remove(overlay);
        onFinish.run();
    }

    // helper to clean up code
    private void playClick() {
        if (musicManager != null) musicManager.playClick();
    }
}
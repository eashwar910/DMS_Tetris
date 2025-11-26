package com.comp2042.ui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

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
    public void setup(GuiController controller,
                      DynamicStartScreen dynamicStartScreen,
                      GridPane gamePanel,
                      Button playButton,
                      Button helpButton,
                      Button closeHelpButton,
                      PauseOverlay pauseScreen,
                      GameOverOverlay gameOverPanel) {

        this.controller = controller;
        this.dynamicStartScreen = dynamicStartScreen;
        this.gamePanel = gamePanel;
        this.playButton = playButton;
        this.helpButton = helpButton;
        this.closeHelpButton = closeHelpButton;
        this.pauseScreen = pauseScreen;
        this.gameOverPanel = gameOverPanel;

        if (gameOverOverlay != null) gameOverOverlay.setVisible(false);

        if (gameOverPanel != null) {
            gameOverPanel.setRestartEventHandler(this::newGame);
            gameOverPanel.setExitEventHandler(e -> quitGame());
        }

        if (startOverlay != null) {
            controller.getIsPause().set(true);
            bindOverlayFill(startOverlay);
            show(startOverlay);
            if (dynamicStartScreen != null) dynamicStartScreen.start();
        }

        if (playButton != null) playButton.setOnAction(e -> startGame());

        if (helpOverlay != null) {
            bindOverlayFill(helpOverlay);
            hide(helpOverlay);
        }

        if (helpButton != null) {
            helpButton.setOnAction(e -> {
                helpFromStart = startOverlay != null && startOverlay.isVisible();
                show(helpOverlay);
            });
        }

        if (closeHelpButton != null) {
            closeHelpButton.setOnAction(e -> {
                hide(helpOverlay);
                if (helpFromStart && startOverlay != null) {
                    show(startOverlay);
                    helpFromStart = false;
                }
            });
        }

        if (pauseScreen != null && pauseOverlay != null) {
            bindOverlayFill(pauseOverlay);
            hide(pauseOverlay);
            pauseScreen.prefWidthProperty().bind(pauseOverlay.widthProperty());
            pauseScreen.prefHeightProperty().bind(pauseOverlay.heightProperty());
            pauseScreen.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            pauseScreen.setResumeHandler(e -> resumeGame());
            pauseScreen.setQuitHandler(e -> quitGame());
            pauseScreen.setNewGameHandler(this::newGame);
        }

        if (gameOverOverlay != null) {
            bindOverlayFill(gameOverOverlay);
            hide(gameOverOverlay);
        }
    }

    // show and hide overlay method definition
    private void show(Region overlay) {
        if (overlay != null)
        {
            overlay.setVisible(true); overlay.toFront();
        }
    }
    private void hide(Region overlay) {
        if (overlay != null)
        {
            overlay.setVisible(false);
        }
    }

    // start game method with proper overlay flow
    public void startGame() {
        if (startOverlay != null) {
            hide(startOverlay);
            if (dynamicStartScreen != null) dynamicStartScreen.stop();
        }

        if (helpOverlay != null) hide(helpOverlay);

        // fixed game being stuck at game over after new high score
        if (controller.getIsGameOver().get() || (startOverlay != null && startOverlay.isVisible())) {
            if (controller.getEventListener() != null) controller.getEventListener().createNewGame();
            controller.getIsGameOver().set(false);
        }

        if (controller.getTimeLine() != null) controller.getTimeLine().play();
        controller.getIsPause().set(false);
        if (gamePanel != null) gamePanel.requestFocus();
    }

    // resume game
    public void resumeGame() {
        controller.getIsPause().set(false);
        if (controller.getTimeLine() != null) controller.getTimeLine().play();
        hide(pauseOverlay);
        if (gamePanel != null) gamePanel.requestFocus();
    }

    // toggle pause method moved
    public void togglePause() {
        if (!controller.getIsPause().get()) {
            controller.getIsPause().set(true);
            if (controller.getTimeLine() != null) controller.getTimeLine().pause();
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

        if (gamePanel != null) gamePanel.requestFocus();
        if (startOverlay != null && startOverlay.isVisible())
        {
            if (controller.getTimeLine() != null) controller.getTimeLine().pause();
        }
        else
        {
            if (controller.getTimeLine() != null) controller.getTimeLine().play();
        }
        controller.getIsPause().set(false);
        controller.getIsGameOver().set(false);
    }

    // game over overlay
    public void gameOver(boolean newHighScore) {
        if (controller.getTimeLine() != null) controller.getTimeLine().stop();

        controller.getIsPause().set(true);
        controller.getIsGameOver().set(true);
        show(gameOverOverlay);

        if (gameOverPanel != null)
        {
            if (newHighScore)
            {
                gameOverPanel.setHighScoreMode();
                gameOverPanel.setRestartEventHandler(this::newGame);
                gameOverPanel.setExitEventHandler(e -> {
                    hide(gameOverOverlay);
                    if (startOverlay != null)
                    {
                        show(startOverlay);
                        controller.getIsPause().set(true);
                        if (controller.getTimeLine() != null) controller.getTimeLine().pause();
                        if (dynamicStartScreen != null) dynamicStartScreen.start();
                    }
                });
            }
            else
             {
                gameOverPanel.setDefaultMode();
                gameOverPanel.setRestartEventHandler(this::newGame);
                gameOverPanel.setExitEventHandler(e -> quitGame());
            }
            gameOverPanel.setVisible(true);
        }
    }
}

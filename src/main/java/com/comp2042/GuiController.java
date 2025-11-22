package com.comp2042;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.effect.Reflection;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.util.Duration;
import javafx.animation.Animation;

public class GuiController implements Initializable {

    private GameRenderer gameRenderer;

    @FXML
    private GridPane gamePanel;

    @FXML
    private Group groupNotification;

    @FXML
    private GridPane brickPanel;

    @FXML
    private GameOverOverlay gameOverPanel;

    @FXML
    private Label scoreLabel;

    @FXML
    private PauseOverlay pauseScreen;

    @FXML
    private AnchorPane groupPause;

    @FXML
    private javafx.scene.layout.StackPane gameBoardContainer;

    @FXML
    private javafx.scene.layout.BorderPane gameBoard;

    @FXML
    private GridPane nextBrickPanel1;

    @FXML
    private GridPane nextBrickPanel2;

    @FXML
    private GridPane nextBrickPanel3;

    @FXML
    private GridPane holdBrickPanel;

    @FXML
    private javafx.scene.layout.StackPane gameOverOverlay;

    @FXML
    private AnchorPane startOverlay;

    @FXML
    private javafx.scene.control.Button playButton;

    @FXML
    private javafx.scene.control.Button helpButton;

    @FXML
    private AnchorPane helpOverlay;

    @FXML
    private javafx.scene.control.Button closeHelpButton;

    @FXML
    private Label highScoreLabel;

    private InputEventListener eventListener;

    private Timeline timeLine;

    private final BooleanProperty isPause = new SimpleBooleanProperty();

    private final BooleanProperty isGameOver = new SimpleBooleanProperty();

    // scene co ordinates for efficient brick positioning
    private final DoubleProperty gamePanelSceneX = new SimpleDoubleProperty();

    private final DoubleProperty gamePanelSceneY = new SimpleDoubleProperty();

    private boolean helpFromStart = false;

    private OverlayManager overlayManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // initialize renderer
        gameRenderer = new GameRenderer(gamePanel, brickPanel, nextBrickPanel1, nextBrickPanel2, nextBrickPanel3, holdBrickPanel, gamePanelSceneX, gamePanelSceneY);

        Font.loadFont(getClass().getClassLoader().getResource(Constants.FONT_DIGITAL).toExternalForm(), Constants.FONT_SIZE);
        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();

        // removed eventlistener and reaplaced it with geteventlistener
        // use new class
        gamePanel.setOnKeyPressed(new KeyboardInputManager(this, gameRenderer));

        // setup overlays using method from new class
        setupOverlays();

        SceneManager.centerGameBoard(gameBoardContainer, gameBoard, gamePanel, gamePanelSceneX, gamePanelSceneY);

        final Reflection reflection = Effects.createBoardReflection();
        if (gameBoardContainer != null) {
            gameBoardContainer.setEffect(reflection);
        }
    }

    // used getter here
    public InputEventListener getEventListener() {
        return this.eventListener;
    }

    private void setupOverlays() {
        if (gameOverOverlay != null) gameOverOverlay.setVisible(false);

        if (gameOverPanel != null)
        {
            gameOverPanel.setRestartEventHandler(this::newGame);
            gameOverPanel.setExitEventHandler(e -> quitGame());
        }

        overlayManager = new OverlayManager(startOverlay, helpOverlay, groupPause, gameOverOverlay);

        if (startOverlay != null)
        {
            isPause.set(true);
            overlayManager.bindOverlayFill(startOverlay);
            overlayManager.showStart();
        }

        if (playButton != null) playButton.setOnAction(e -> startGame());

        if (helpOverlay != null)
        {
            overlayManager.bindOverlayFill(helpOverlay);
            overlayManager.hideHelp();
        }

        if (helpButton != null)
        {
            helpButton.setOnAction(e -> {
                helpFromStart = startOverlay != null && startOverlay.isVisible();
                overlayManager.showHelp();
            });
        }

        if (closeHelpButton != null)
        {
            closeHelpButton.setOnAction(e -> {
                overlayManager.hideHelp();
                if (helpFromStart && startOverlay != null) {
                    overlayManager.showStart();
                    helpFromStart = false;
                }
            });
        }

        if (pauseScreen != null && groupPause != null)
        {
            overlayManager.bindOverlayFill(groupPause);
            overlayManager.hidePause();
            pauseScreen.prefWidthProperty().bind(groupPause.widthProperty());
            pauseScreen.prefHeightProperty().bind(groupPause.heightProperty());
            pauseScreen.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            pauseScreen.setResumeHandler(e -> resumeGame());
            pauseScreen.setQuitHandler(e -> quitGame());
            pauseScreen.setNewGameHandler(this::newGame);
        }

        if (gameOverOverlay != null)
        {
            overlayManager.bindOverlayFill(gameOverOverlay);
            overlayManager.hideGameOver();
        }
    }

    public boolean isPlaying() {
        return !isPause.get() && !isGameOver.get();
    }

    public void moveDown(MoveEvent event) {
        if (isPlaying())
        {
            DownData downData = eventListener.onDownEvent(event);
            if (downData.getClearRow() != null && downData.getClearRow().getLinesRemoved() > 0)
            {
                ScorePopup notificationPanel = new ScorePopup("+" + downData.getClearRow().getScoreBonus());
                groupNotification.getChildren().add(notificationPanel);
                notificationPanel.showScore(groupNotification.getChildren());
            }
            // direct access to renderer because of sonar cube warning
            gameRenderer.refreshBrick(downData.getViewData());
        }
        gamePanel.requestFocus();
    }

    public void togglePause() {
        if (!isPause.get())
        {
            isPause.set(true);
            if (timeLine != null) timeLine.pause();
            overlayManager.showPause();
        }
        else
        {
            resumeGame();
        }
    }

    public void initGameView(int[][] boardMatrix, ViewData brick) {
        timeLine = new Timeline(new KeyFrame(
                Duration.millis(Constants.FALL_INTERVAL_MS),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));

        timeLine.setCycleCount(Animation.INDEFINITE);
        timeLine.pause();
        gameRenderer.initGameView(boardMatrix, brick);
    }

    public void refreshGameBackground(int[][] board) { gameRenderer.refreshGameBackground(board); }

    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public void bindScore(IntegerProperty integerProperty) {
        if (scoreLabel != null && integerProperty != null) {
            scoreLabel.textProperty().bind(Bindings.format("Score: %d", integerProperty));
        }
    }

    public void bindHighScore(IntegerProperty integerProperty) {
        if (highScoreLabel != null && integerProperty != null) {
            highScoreLabel.textProperty().bind(Bindings.format("High Score: %d", integerProperty));
        }
    }

    public void gameOver() {
        if (timeLine != null) timeLine.stop();
        isPause.set(true);
        isGameOver.set(true);
        overlayManager.showGameOver();
        if (gameOverPanel != null) gameOverPanel.setVisible(true);
    }

    public void newGame(ActionEvent actionEvent) {
        timeLine.stop();
        gameOverPanel.setVisible(false);
        overlayManager.hideGameOver();
        overlayManager.hidePause();
        eventListener.createNewGame();
        gamePanel.requestFocus();
        if (startOverlay != null && startOverlay.isVisible()) {
            timeLine.pause();
        } else {
            timeLine.play();
        }
        isPause.set(false);
        isGameOver.set(false);
    }

    private void startGame() {
        if (startOverlay != null) overlayManager.hideStart();
        overlayManager.hideHelp();
        if (timeLine != null) timeLine.play();
        isPause.set(false);
        gamePanel.requestFocus();
    }

    private void resumeGame() {
        isPause.set(false);
        if (timeLine != null) timeLine.play();
        overlayManager.hidePause();
        gamePanel.requestFocus();
    }

    private void quitGame() { Platform.exit(); }
}
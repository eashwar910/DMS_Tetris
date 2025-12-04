package com.comp2042.ui;

import java.net.URL;
import java.util.ResourceBundle;

import com.comp2042.core.Constants;
import com.comp2042.core.GameModeHandler;
import com.comp2042.core.GameModeHandler.GameMode;
import com.comp2042.events.EventSource;
import com.comp2042.events.EventType;
import com.comp2042.events.MoveEvent;
import com.comp2042.input.InputEventListener;
import com.comp2042.input.KeyboardInputManager;
import com.comp2042.logic.workflow.ClearRow;
import com.comp2042.logic.workflow.DownData;
import com.comp2042.logic.workflow.ViewData;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.effect.Reflection;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class GuiController implements Initializable {

    private GameRenderer gameRenderer;
    private GhostBrickHandler ghostBrickHandler;

    @FXML
    private GridPane gamePanel;

    @FXML
    private Group groupNotification;

    @FXML
    private GridPane brickPanel;

    @FXML
    private GameOverOverlay gameOverPanel;

    @FXML
    private Label scoreValueLabel;

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
    private Label highScoreValueLabel;

    @FXML
    private DynamicStartScreen dynamicStartScreen;

    @FXML
    private javafx.scene.image.ImageView titleImage;

    @FXML
    private Label linesValueLabel;

    @FXML
    private Label levelValueLabel;

    @FXML
    private javafx.scene.control.Button raceButton;

    @FXML
    private javafx.scene.control.Button mineButton;

    @FXML
    private Label timerLabel;

    private InputEventListener eventListener;

    private Timeline timeLine;

    private GameModeHandler modeHandler;

    private final BooleanProperty isPause = new SimpleBooleanProperty();

    private final BooleanProperty isGameOver = new SimpleBooleanProperty();

    // scene co ordinates for efficient brick positioning
    private final DoubleProperty gamePanelSceneX = new SimpleDoubleProperty();

    private final DoubleProperty gamePanelSceneY = new SimpleDoubleProperty();

    private OverlayManager overlayManager;

    private MusicManager musicManager;

    private int[][] lastBoardMatrix;

    private ViewData lastViewData;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // initialize renderer
        gameRenderer = new GameRenderer(gamePanel, brickPanel, nextBrickPanel1, nextBrickPanel2, nextBrickPanel3, holdBrickPanel, gamePanelSceneX, gamePanelSceneY);

        // using grid.png as the background for playing area
        gamePanel.setBackground(new javafx.scene.layout.Background(
                new javafx.scene.layout.BackgroundImage(
                        new javafx.scene.image.Image(new java.io.File("grid.png").toURI().toString(), false),
                        javafx.scene.layout.BackgroundRepeat.REPEAT,
                        javafx.scene.layout.BackgroundRepeat.REPEAT,
                        javafx.scene.layout.BackgroundPosition.DEFAULT,
                        javafx.scene.layout.BackgroundSize.DEFAULT
                )
        ));

        Font.loadFont(getClass().getClassLoader().getResource(Constants.FONT_DIGITAL).toExternalForm(), Constants.FONT_SIZE);
        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();

        // removed eventlistener and reaplaced it with geteventlistener
        // use new class
        gamePanel.setOnKeyPressed(new KeyboardInputManager(this, gameRenderer));

        musicManager = new MusicManager("theme.mp3");

        // use methods from overlay manager to set up overlays
        overlayManager = new OverlayManager(startOverlay, helpOverlay, groupPause, gameOverOverlay);
        overlayManager.setup(
                this,
                dynamicStartScreen,
                gamePanel,
                playButton,
                raceButton,
                mineButton,
                helpButton,
                closeHelpButton,
                pauseScreen,
                gameOverPanel
        );

        // setup game mode handler
        modeHandler = new GameModeHandler(
                timerLabel,
                () -> overlayManager.gameOver(false),
                mode -> {
                    // logic to toggle renderer mode based on game mode
                    boolean isUpsideDown = (mode == GameMode.BOTTOMS_UP);
                    setUpsideDownMode(isUpsideDown);

                    if (eventListener instanceof com.comp2042.core.GameController gc)
                    {
                        gc.setMode(mode);
                    }
                }
        );

        // used a title logo image found online, added it to fxml file using imageview
        // bind title image property to start overlay
        if (titleImage != null && startOverlay != null)
        {
            titleImage.fitWidthProperty().bind(startOverlay.widthProperty().multiply(0.6));
            titleImage.setSmooth(true);
            titleImage.setCache(true);
        }

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

    // helper method to switch modes in renderers
    public void setUpsideDownMode(boolean enable) {
        if (gameRenderer != null) gameRenderer.setUpsideDown(enable);
        if (ghostBrickHandler != null) ghostBrickHandler.setUpsideDown(enable);

        // force refresh if we have data, so the screen flips instantly
        if (lastBoardMatrix != null) {
            gameRenderer.refreshGameBackground(lastBoardMatrix);
        }
    }

    public boolean isPlaying() {
        return !isPause.get() && !isGameOver.get();
    }

    public void moveDown(MoveEvent event) {
        if (isPlaying())
        {
            DownData downData = eventListener.onDownEvent(event);
            showScorePopup(downData.getClearRow());
            // direct access to renderer because of sonar cube warning
            gameRenderer.refreshBrick(downData.getViewData());
            updateGhost(downData.getViewData());
        }
        gamePanel.requestFocus();
    }

    public void showScorePopup(ClearRow clearRow) {
        if (clearRow != null && clearRow.getLinesRemoved() > 0) {
            ScorePopup.showForClearRow(groupNotification.getChildren(), clearRow.getScoreBonus(), clearRow.getLinesRemoved());
        }
    }

    // used function from overlay manager
    public void togglePause() { overlayManager.togglePause(); }

    // created wrappers for methods from mode handler (fix later)
    public void startNormalMode() { modeHandler.startNormal(); }

    // wrapper for new mode (fix later)
    public void startUpsideDownMode() { modeHandler.startUpsideDown(); }

    public void startTimedMode() { modeHandler.startTimed(); }

    public void pauseModeTimer() { modeHandler.pause(); }

    public void resumeModeTimer() { modeHandler.resume(); }

    public void stopModeTimer() { modeHandler.stop(); }

    public GameMode getCurrentMode() { return modeHandler.getMode(); }

    public void restartCurrentModeTimer() { modeHandler.restartForNewGame(); }

    public void initGameView(int[][] boardMatrix, ViewData brick) {
        timeLine = new Timeline(new KeyFrame(
                Duration.millis(Constants.FALL_INTERVAL_MS),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));

        timeLine.setCycleCount(Animation.INDEFINITE);
        timeLine.pause();
        gameRenderer.initGameView(boardMatrix, brick);
        // set up ghost brick handler
        ghostBrickHandler = new GhostBrickHandler(gamePanel, brickPanel, gamePanelSceneX, gamePanelSceneY, gameRenderer);
        // ensure ghost handler respects current mode on init
        ghostBrickHandler.setUpsideDown(modeHandler.getMode() == GameMode.BOTTOMS_UP);

        lastBoardMatrix = boardMatrix;
        lastViewData = brick;
        ghostBrickHandler.init(brick);
        if (startOverlay != null)
        {
            setBrickPanelVisible(false);
        }

    }

    public void refreshGameBackground(int[][] board) {
        lastBoardMatrix = board;
        gameRenderer.refreshGameBackground(board);
        if (lastViewData != null && ghostBrickHandler != null)
        {
            ghostBrickHandler.update(lastViewData, lastBoardMatrix);
        }
    }

    public void refreshGameBackground(int[][] board, ViewData brick) { gameRenderer.refreshGameBackground(board); lastBoardMatrix = board; lastViewData = brick; }

    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public void startGameMusic() { if (musicManager != null) musicManager.playLoopFromStart(); }

    public void pauseGameMusic() { if (musicManager != null) musicManager.pause(); }

    public void resumeGameMusic() { if (musicManager != null) musicManager.resume(); }

    public void stopGameMusic() { if (musicManager != null) musicManager.stop(); }

    public void restartGameMusic() { if (musicManager != null) musicManager.restart(); }

    public void startStartScreenMusic() { if (musicManager != null) musicManager.startStartLoop("start_screen.mp3"); }

    public void stopStartScreenMusic() { if (musicManager != null) musicManager.stopStart(); }

    public void playGameOverSound() { if (musicManager != null) musicManager.playOnce("game_over.mp3"); }

    public void playHighScoreSound() { if (musicManager != null) musicManager.playOnce("high_score.mp3"); }

    public void stopOverlaySound() { if (musicManager != null) musicManager.stopFx(); }
    public void playClick() { if (musicManager != null) musicManager.playClick(); }

    // transition between game modes
    public void gameModeTransition() {
        if (timeLine != null) timeLine.stop();
        setBrickPanelVisible(false);
        if (ghostBrickHandler != null) ghostBrickHandler.clear();
        if (gameRenderer != null) gameRenderer.clearAll();
        lastViewData = null;
        lastBoardMatrix = null;
    }

    public void bindScore(IntegerProperty integerProperty) {
        if (scoreValueLabel != null && integerProperty != null) {
            scoreValueLabel.textProperty().bind(Bindings.format("%d", integerProperty));
        }
    }

    public void bindHighScore(IntegerProperty integerProperty) {
        if (highScoreValueLabel != null && integerProperty != null) {
            highScoreValueLabel.textProperty().bind(Bindings.format("%d", integerProperty));
        }
    }

    public void bindLinesCleared(IntegerProperty integerProperty) {
        if (linesValueLabel != null && integerProperty != null) {
            linesValueLabel.textProperty().bind(Bindings.format("%d", integerProperty));
        }
    }

    public void bindLevelValue(IntegerProperty integerProperty) {
        if (levelValueLabel != null && integerProperty != null) {
            levelValueLabel.textProperty().bind(Bindings.format("%d", integerProperty));
        }
    }

    // full method moved to overlay manager
    public void gameOver(boolean newHighScore) { overlayManager.gameOver(newHighScore); }

    // set up getters
    public void setOverlayManager(OverlayManager overlayManager) { this.overlayManager = overlayManager; }
    public OverlayManager getOverlayManager() { return overlayManager; }
    public Timeline getTimeLine() { return timeLine; }
    public AnchorPane getStartOverlay() { return startOverlay; }
    public BooleanProperty getIsPause() { return isPause; }
    public BooleanProperty getIsGameOver() { return isGameOver; }

    public void bindLevel(IntegerProperty integerProperty) {
        if (integerProperty == null) return;
        integerProperty.addListener((obs, oldVal, newVal) -> updateFallInterval(newVal.intValue()));
        updateFallInterval(integerProperty.get());
    }

    // updating ghost brick every brick (refactor later !)
    public void updateGhost(ViewData brick) {
        lastViewData = brick;
        boolean show = isPlaying() && !(startOverlay != null && startOverlay.isVisible());
        if (ghostBrickHandler != null && lastBoardMatrix != null && show)
        {
            ghostBrickHandler.update(brick, lastBoardMatrix);
        }
        else if (ghostBrickHandler != null)
        {
            ghostBrickHandler.clear();
        }
    }

    // pulse method
    public void pulseLandedBlocks(int[][] brickShape, int xPosition, int yPosition) {
        if (gameRenderer != null && brickShape != null) {
            gameRenderer.pulseLandedBlocks(brickShape, xPosition, yPosition);
        }
    }

    public void setBrickPanelVisible(boolean visible) {
        if (brickPanel != null) brickPanel.setVisible(visible);
        if (!visible && ghostBrickHandler != null) ghostBrickHandler.clear();
    }

    private void updateFallInterval(int level) {
        double ms;
        if (level <= 1)
        {
            ms = Constants.FALL_INTERVAL_MS;
        }
        else
        {
            double base = Constants.BASE_TIME;
            double dec = Constants.TIME_DECREMENT;
            double t = Math.pow(Math.max(0.0, base - ((level - 1) * dec)), Math.max(0, level - 1));
            ms = Math.max(Constants.MIN_FALL_INTERVAL_MS, t * 1000.0);
        }
        boolean wasRunning = timeLine != null && timeLine.getStatus() == Animation.Status.RUNNING;
        if (timeLine != null)
        {
            timeLine.stop();
            timeLine.getKeyFrames().setAll(new KeyFrame(
                    Duration.millis(ms),
                    ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
            ));
            timeLine.setCycleCount(Animation.INDEFINITE);
            if (wasRunning && !isPause.get() && !isGameOver.get())
            {
                timeLine.play();
            }
        }
    }

}

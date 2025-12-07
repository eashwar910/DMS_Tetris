package com.comp2042.ui;

import java.net.URL;
import java.util.ResourceBundle;

import com.comp2042.core.Constants;
import com.comp2042.core.GameEventListener;
import com.comp2042.core.GameLoopManager;
import com.comp2042.core.GameModeHandler.GameMode;
import com.comp2042.events.MoveEvent;
import com.comp2042.input.InputEventListener;
import com.comp2042.input.KeyboardInputManager;
import com.comp2042.logic.workflow.ClearRow;
import com.comp2042.logic.workflow.DownData;
import com.comp2042.logic.workflow.ViewData;

import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.effect.Reflection;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;

/**
 * JavaFX controller coordinating UI initialization, input handling, rendering,
 * overlays, and bindings with the core game logic.
 *
 * @author Eashwar
 * @version 1.0
 */
public class GuiController implements Initializable, GameEventListener {

    private final GameLoopManager gameLoopManager = new GameLoopManager(this);
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

    // scene co ordinates for efficient brick positioning
    private final DoubleProperty gamePanelSceneX = new SimpleDoubleProperty();

    private final DoubleProperty gamePanelSceneY = new SimpleDoubleProperty();

    private OverlayManager overlayManager;

    private MusicManager musicManager;

    private int[][] lastBoardMatrix;

    private ViewData lastViewData;

    @Override
    /**
     * Initializes the UI components, renderer, overlays, input, and bindings.
     *
     * @param location FXML resource location
     * @param resources resource bundle
     */
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

        musicManager = new MusicManager("sound/theme.mp3");

        // use methods from overlay manager to set up overlays
        overlayManager = new OverlayManager(startOverlay, helpOverlay, groupPause, gameOverOverlay);

        // pass musicManager here
        overlayManager.setup(
                this,
                musicManager,
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
        gameLoopManager.setupModeHandler(timerLabel, overlayManager);

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
    /**
     * Toggles upside-down rendering mode for renderer and ghost handler.
     *
     * @param enable whether to enable upside-down mode
     */
    public void setUpsideDownMode(boolean enable) {
        if (gameRenderer != null) gameRenderer.setUpsideDown(enable);
        if (ghostBrickHandler != null) ghostBrickHandler.setUpsideDown(enable);

        // force refresh if we have data, so the screen flips instantly
        if (lastBoardMatrix != null) {
            gameRenderer.refreshGameBackground(lastBoardMatrix);
        }
    }

    /**
     * Indicates if the game is active.
     *
     * @return true if playing; false otherwise
     */
    public boolean isPlaying() {
        return gameLoopManager.isPlaying();
    }

    /**
     * Handles a down movement event routed from input.
     *
     * @param event the move event
     */
    public void moveDown(MoveEvent event) {
        if (gameLoopManager.isPlaying())
        {
            DownData downData = eventListener.onDownEvent(event);
            showScorePopup(downData.getClearRow());
            // direct access to renderer because of sonar cube warning
            gameRenderer.refreshBrick(downData.getViewData());
            updateGhost(downData.getViewData());
        }
        gamePanel.requestFocus();
    }

    /**
     * Displays a transient score popup when rows are cleared.
     *
     * @param clearRow clear-row summary
     */
    public void showScorePopup(ClearRow clearRow) {
        if (clearRow != null && clearRow.getLinesRemoved() > 0) {
            ScorePopup.showForClearRow(groupNotification.getChildren(), clearRow.getScoreBonus(), clearRow.getLinesRemoved());
        }
    }

    // turned all wrappers into one getter
    public GameLoopManager getGameLoopManager() { return gameLoopManager; }

    @Override
    /**
     * Initializes game view and ghost brick handling.
     *
     * @param boardMatrix background matrix
     * @param brick current view data
     */
    public void initGameView(int[][] boardMatrix, ViewData brick) {
        gameLoopManager.setupTimeline();

        gameRenderer.initGameView(boardMatrix, brick);
        // set up ghost brick handler
        ghostBrickHandler = new GhostBrickHandler(gamePanel, brickPanel, gamePanelSceneX, gamePanelSceneY, gameRenderer);
        // ensure ghost handler respects current mode on init
        ghostBrickHandler.setUpsideDown(gameLoopManager.getModeHandler().getMode() == GameMode.BOTTOMS_UP);

        lastBoardMatrix = boardMatrix;
        lastViewData = brick;
        ghostBrickHandler.init(brick);
        if (startOverlay != null)
        {
            setBrickPanelVisible(false);
        }

    }

    @Override
    /**
     * Refreshes background rendering and updates ghost state.
     *
     * @param board the updated background matrix
     */
    public void refreshGameBackground(int[][] board) {
        lastBoardMatrix = board;
        gameRenderer.refreshGameBackground(board);
        if (lastViewData != null && ghostBrickHandler != null)
        {
            ghostBrickHandler.update(lastViewData, lastBoardMatrix);
        }
    }

    public void refreshGameBackground(int[][] board, ViewData brick) { gameRenderer.refreshGameBackground(board); lastBoardMatrix = board; lastViewData = brick; }

    @Override
    /**
     * Registers the input event listener.
     *
     * @param eventListener listener to register
     */
    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
    }

    /**
     * Clears and resets UI state during transitions between game modes.
     */
    public void gameModeTransition() {
        if (gameLoopManager.getTimeLine() != null) gameLoopManager.getTimeLine().stop();
        setBrickPanelVisible(false);
        if (ghostBrickHandler != null) ghostBrickHandler.clear();
        if (gameRenderer != null) gameRenderer.clearAll();
        lastViewData = null;
        lastBoardMatrix = null;
    }

    @Override
    public void bindScore(IntegerProperty integerProperty) {
        if (scoreValueLabel != null && integerProperty != null) {
            scoreValueLabel.textProperty().bind(Bindings.format("%d", integerProperty));
        }
    }

    @Override
    public void bindHighScore(IntegerProperty integerProperty) {
        if (highScoreValueLabel != null && integerProperty != null) {
            highScoreValueLabel.textProperty().bind(Bindings.format("%d", integerProperty));
        }
    }

    @Override
    public void bindLinesCleared(IntegerProperty integerProperty) {
        if (linesValueLabel != null && integerProperty != null) {
            linesValueLabel.textProperty().bind(Bindings.format("%d", integerProperty));
        }
    }

    @Override
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
    public Timeline getTimeLine() {
        return gameLoopManager.getTimeLine();
    }
    public AnchorPane getStartOverlay() { return startOverlay; }
    public BooleanProperty getIsPause() {
        return gameLoopManager.getIsPause();
    }
    @Override
    public BooleanProperty getIsGameOver() {
        return gameLoopManager.getIsGameOver();
    }

    @Override
    public void bindLevel(IntegerProperty integerProperty) {
        if (integerProperty == null) return;
        integerProperty.addListener((obs, oldVal, newVal) -> gameLoopManager.updateFallInterval(newVal.intValue()));
        gameLoopManager.updateFallInterval(integerProperty.get());
    }

    // updating ghost brick every brick (refactor later !)
    /**
     * Updates or clears the ghost brick representation based on state.
     *
     * @param brick latest view data
     */
    public void updateGhost(ViewData brick) {
        lastViewData = brick;
        boolean show = gameLoopManager.isPlaying() && !(startOverlay != null && startOverlay.isVisible());
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
    @Override
    /**
     * Triggers a pulse animation for newly landed blocks after hard drop.
     *
     * @param brickShape the brick matrix
     * @param xPosition x-position
     * @param yPosition y-position
     */
    public void pulseLandedBlocks(int[][] brickShape, int xPosition, int yPosition) {
        if (gameRenderer != null && brickShape != null) {
            gameRenderer.pulseLandedBlocks(brickShape, xPosition, yPosition);
        }
    }

    /**
     * Shows or hides the brick panel and clears ghost when hidden.
     *
     * @param visible whether the brick panel should be visible
     */
    public void setBrickPanelVisible(boolean visible) {
        if (brickPanel != null) brickPanel.setVisible(visible);
        if (!visible && ghostBrickHandler != null) ghostBrickHandler.clear();
    }
}
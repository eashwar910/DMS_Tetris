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
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class GuiController implements Initializable {

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

    private Rectangle[][] displayMatrix;

    private InputEventListener eventListener;

    private Rectangle[][] rectangles;

    private Timeline timeLine;

    private Rectangle[][] nextBrickRectangles1;

    private Rectangle[][] nextBrickRectangles2;

    private Rectangle[][] nextBrickRectangles3;

    private final BooleanProperty isPause = new SimpleBooleanProperty();

    private final BooleanProperty isGameOver = new SimpleBooleanProperty();

    // scene co ordinates for efficient brick positioning
    private final DoubleProperty gamePanelSceneX = new SimpleDoubleProperty();

    private final DoubleProperty gamePanelSceneY = new SimpleDoubleProperty();

    private boolean helpFromStart = false;

    private OverlayManager overlayManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Font.loadFont(getClass().getClassLoader().getResource(Constants.FONT_DIGITAL).toExternalForm(), Constants.FONT_SIZE);
        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();

        // modified to use only keyevent instead of two different event listeners   
        gamePanel.setOnKeyPressed(keyEvent -> {
            if (!isPause.get() && !isGameOver.get()) {
                if (keyEvent.getCode() == KeyCode.LEFT || keyEvent.getCode() == KeyCode.A) {
                    refreshBrick(eventListener.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER)));
                    keyEvent.consume();
                }
                if (keyEvent.getCode() == KeyCode.RIGHT || keyEvent.getCode() == KeyCode.D) {
                    refreshBrick(eventListener.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER)));
                    keyEvent.consume();
                }
                if (keyEvent.getCode() == KeyCode.UP || keyEvent.getCode() == KeyCode.W) {
                    refreshBrick(eventListener.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.USER)));
                    keyEvent.consume();
                }
                if (keyEvent.getCode() == KeyCode.DOWN || keyEvent.getCode() == KeyCode.S) {
                    moveDown(new MoveEvent(EventType.DOWN, EventSource.USER));
                    keyEvent.consume();
                }
            }
            if (keyEvent.getCode() == KeyCode.N) {
                newGame(null);
            }
            if (keyEvent.getCode() == KeyCode.ESCAPE) {
                if (!isGameOver.get()) {
                    togglePause();
                }
                keyEvent.consume();
            }
        });
        gameOverPanel.setVisible(false);

        // game over overlay is hidden by default
        if (gameOverOverlay != null)
        {
            gameOverOverlay.setVisible(false);
        }

        // add the restart/exit handlers to the GameOverOverlay
        if (gameOverPanel != null)
        {
            gameOverPanel.setRestartEventHandler(e -> newGame(e));
            gameOverPanel.setExitEventHandler(e -> quitGame());
        }

        // simple use the overlay manager methods
        overlayManager = new OverlayManager(startOverlay, helpOverlay, groupPause, gameOverOverlay);

        // start screen (makes sure the game is paused before entering the game)
        // did the pause thing because of old bug - mention in report
        if (startOverlay != null)
        {
            isPause.set(true);
            overlayManager.bindOverlayFill(startOverlay);
            overlayManager.showStart();
        }

        if (playButton != null)
        {
            playButton.setOnAction(e -> startGame());
        }

        // controls help menu scene setup
        if (helpOverlay != null)
        {
            overlayManager.bindOverlayFill(helpOverlay);
            overlayManager.hideHelp();
        }

        // show help overlay when help is pressed
        if (helpButton != null)
        {
            helpButton.setOnAction(e -> {
                helpFromStart = startOverlay != null && startOverlay.isVisible();
                overlayManager.showHelp();
            });
        }

        // close help overlay when close is pressed
        if (closeHelpButton != null)
        {
            closeHelpButton.setOnAction(e -> {
                overlayManager.hideHelp();
                if (helpFromStart && startOverlay != null)
                {
                    overlayManager.showStart();
                    helpFromStart = false;
                }
            });
        }

        // Centering the StackPane - Gameboard container (borderpane + game panel)
        // nfs : Gameboard container is the stack pane where the border and the game panel is combined
        // reference : https://stackoverflow.com/questions/51142808/javafx-bind-pathtransitions-element-coordinates

        Platform.runLater( () -> {
            if (gameBoardContainer != null && gameBoard != null)
            {
                javafx.scene.Scene gamescene = gameBoardContainer.getScene();
                if (gamescene != null)
                {

                    // Centre both ways ( secen width (or) height - borderpane width / 2 )
                    // calculates the leftover space in the screen after the borderpane appears in the scene
                    // divides it by 2 to get the centre space
                    gameBoardContainer.layoutXProperty().bind(
                            gamescene.widthProperty().subtract(gameBoard.widthProperty()).divide(2)
                            );
                    gameBoardContainer.layoutYProperty().bind(
                            gamescene.heightProperty().subtract(gameBoard.heightProperty()).divide(2)
                    );

                    // bind the "cache" variables to the obtained values for efficiency

                    gamePanelSceneX.bind(
                            gameBoardContainer.layoutXProperty().add(gamePanel.layoutXProperty())
                    );
                    gamePanelSceneY.bind(
                            gameBoardContainer.layoutYProperty().add(gamePanel.layoutYProperty())
                    );
                }

                // if the scene has not loaded immediately, use the pane height and width
                else if (gameBoardContainer.getParent() instanceof javafx.scene.layout.Pane)
                {

                    // obtains parent node of gameboard container and casts it to Pane
                    // this way we can obtain its height and width properties
                    javafx.scene.layout.Pane parent = (javafx.scene.layout.Pane) gameBoardContainer.getParent();

                    // use the pane height and width and do the same calculation
                    gameBoardContainer.layoutXProperty().bind(
                            parent.widthProperty().subtract(gameBoard.widthProperty()).divide(2)
                    );
                    gameBoardContainer.layoutXProperty().bind(
                            parent.heightProperty().subtract(gameBoard.heightProperty()).divide(2)
                    );

                    // bind the "cache" variables to the obtained values for efficiency
                    gamePanelSceneX.bind(
                            gameBoardContainer.layoutXProperty().add(gamePanel.layoutXProperty())
                    );
                    gamePanelSceneY.bind(
                            gameBoardContainer.layoutYProperty().add(gamePanel.layoutYProperty())
                    );
                }
            }
        });
        
        // Set up pause screen in the UI
        if (pauseScreen != null && groupPause != null)
        {
            overlayManager.bindOverlayFill(groupPause); // pause screen setup
            overlayManager.hidePause();
            pauseScreen.prefWidthProperty().bind(groupPause.widthProperty());
            pauseScreen.prefHeightProperty().bind(groupPause.heightProperty());
            pauseScreen.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            pauseScreen.setResumeHandler(e -> resumeGame());
            pauseScreen.setQuitHandler(e -> quitGame());
            pauseScreen.setNewGameHandler(e -> newGame(e));
        }

        // game over screen setup
        if (gameOverOverlay != null)
        {
            overlayManager.bindOverlayFill(gameOverOverlay);
            overlayManager.hideGameOver();
        }

        final Reflection reflection = Effects.createBoardReflection(); //function from effects.java
        if (gameBoardContainer != null)
        {
            gameBoardContainer.setEffect(reflection);
        }
    }

    public void initGameView(int[][] boardMatrix, ViewData brick) {
        displayMatrix = new Rectangle[boardMatrix.length][boardMatrix[0].length];
        for (int i = 0; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle rectangle = new Rectangle(Constants.BRICK_SIZE, Constants.BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                displayMatrix[i][j] = rectangle;
                gamePanel.add(rectangle, j, i);
            }
        }

        rectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle rectangle = new Rectangle(Constants.BRICK_SIZE, Constants.BRICK_SIZE);
                rectangle.setFill(getFillColor(brick.getBrickData()[i][j]));
                rectangles[i][j] = rectangle;
                brickPanel.add(rectangle, j, i);
            }
        }

        // gets the next three bricks data and recreates the block and adds it to the panels
        int[][][] nextBrickDataArr = brick.getNextBrickData();

        // first next brick (right next)
        nextBrickRectangles1 = new Rectangle[nextBrickDataArr[0].length][nextBrickDataArr[0][0].length];
        for (int i = 0; i < nextBrickDataArr[0].length; i++)
        {
            for (int j = 0; j < nextBrickDataArr[0][i].length; j++)
            {
                Rectangle rectangle = new Rectangle(Constants.BRICK_SIZE, Constants.BRICK_SIZE);
                rectangle.setFill(getFillColor(nextBrickDataArr[0][i][j]));
                nextBrickRectangles1[i][j] = rectangle;
                nextBrickPanel1.add(rectangle, j, i);

            }
        }

        // second next brick
        nextBrickRectangles2 = new Rectangle[nextBrickDataArr[1].length][nextBrickDataArr[1][0].length];
        for (int i = 0; i < nextBrickDataArr[1].length; i++)
        {
            for (int j = 0; j < nextBrickDataArr[1][i].length; j++)
            {
                Rectangle rectangle = new Rectangle(Constants.BRICK_SIZE, Constants.BRICK_SIZE);
                rectangle.setFill(getFillColor(nextBrickDataArr[1][i][j]));
                nextBrickRectangles2[i][j] = rectangle;
                nextBrickPanel2.add(rectangle, j, i);

            }
        }

        // third next brick
        nextBrickRectangles3 = new Rectangle[nextBrickDataArr[2].length][nextBrickDataArr[2][0].length];
        for (int i = 0; i < nextBrickDataArr[2].length; i++)
        {
            for (int j = 0; j < nextBrickDataArr[2][i].length; j++)
            {
                Rectangle rectangle = new Rectangle(Constants.BRICK_SIZE, Constants.BRICK_SIZE);
                rectangle.setFill(getFillColor(nextBrickDataArr[2][i][j]));
                nextBrickRectangles3[i][j] = rectangle;
                nextBrickPanel3.add(rectangle, j, i);

            }
        }


        // position the brick after scene is laid out
        Platform.runLater(() -> {
            positionBrickPanel(brick);
        });


        timeLine = new Timeline(new KeyFrame(
                Duration.millis(Constants.FALL_INTERVAL_MS),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.pause();
    }

    private Paint getFillColor(int i) {
        Paint returnPaint;
        switch (i) {
            case 0:
                returnPaint = Color.TRANSPARENT;
                break;
            case 1:
                returnPaint = Color.AQUA;
                break;
            case 2:
                returnPaint = Color.BLUEVIOLET;
                break;
            case 3:
                returnPaint = Color.DARKGREEN;
                break;
            case 4:
                returnPaint = Color.YELLOW;
                break;
            case 5:
                returnPaint = Color.RED;
                break;
            case 6:
                returnPaint = Color.BEIGE;
                break;
            case 7:
                returnPaint = Color.BURLYWOOD;
                break;
            default:
                returnPaint = Color.WHITE;
                break;
        }
        return returnPaint;
    }


    // method to calculate the brick position based on the cached variables
    // the game panel should be 10 x 20 (like actual tetris)
    // uses the game panel vgap and hgap and matchches the cell spacing

    private void positionBrickPanel(ViewData brick){
        double cellWidth = gamePanel.getHgap() + Constants.BRICK_SIZE;
        double cellHeight = gamePanel.getVgap() + Constants.BRICK_SIZE;

        brickPanel.setLayoutX(gamePanelSceneX.get() + brick.getxPosition() * cellWidth);
        brickPanel.setLayoutY(gamePanelSceneY.get() + brick.getyPosition() * cellHeight);
    }

    private void refreshBrick(ViewData brick) {
        if (!isPause.get())
        {
            positionBrickPanel(brick); // use the ppreviouslt defined function
            for (int i = 0; i < brick.getBrickData().length; i++)
            {
                for (int j = 0; j < brick.getBrickData()[i].length; j++)
                {
                    setRectangleData(brick.getBrickData()[i][j], rectangles[i][j]);
                }
            }

            // goes through the next brick data and updates the next brick panels
            int[][][] nextBrickDataArr = brick.getNextBrickData();

            for (int i = 0; i < nextBrickDataArr[0].length; i++)
            {
                for (int j = 0; j < nextBrickDataArr[0][i].length; j++)
                {
                    setRectangleData(nextBrickDataArr[0][i][j], nextBrickRectangles1[i][j]);
                }
            }

            for (int i = 0; i < nextBrickDataArr[1].length; i++)
            {
                for (int j = 0; j < nextBrickDataArr[1][i].length; j++)
                {
                    setRectangleData(nextBrickDataArr[1][i][j], nextBrickRectangles2[i][j]);
                }
            }

            for (int i = 0; i < nextBrickDataArr[2].length; i++)
            {
                for (int j = 0; j < nextBrickDataArr[2][i].length; j++)
                {
                    setRectangleData(nextBrickDataArr[2][i][j], nextBrickRectangles3[i][j]);
                }
            }
        }
    }

    public void refreshGameBackground(int[][] board) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                setRectangleData(board[i][j], displayMatrix[i][j]);
            }
        }
    }

    private void setRectangleData(int color, Rectangle rectangle) {
        rectangle.setFill(getFillColor(color));
        rectangle.setArcHeight(Constants.BRICK_ARC);
        rectangle.setArcWidth(Constants.BRICK_ARC);
    }

    private void moveDown(MoveEvent event) {
        if (!isPause.get() && !isGameOver.get()) {
            DownData downData = eventListener.onDownEvent(event);
            if (downData.getClearRow() != null && downData.getClearRow().getLinesRemoved() > 0) {
                ScorePopup notificationPanel = new ScorePopup("+" + downData.getClearRow().getScoreBonus());
                groupNotification.getChildren().add(notificationPanel);
                notificationPanel.showScore(groupNotification.getChildren());
            }
            refreshBrick(downData.getViewData());
        }
        gamePanel.requestFocus();
    }

    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public void bindScore(IntegerProperty integerProperty) {
        if (scoreLabel != null && integerProperty != null)
        {
            scoreLabel.textProperty().bind(Bindings.format("Score: %d", integerProperty));
        }
    }

    public void gameOver() {
        if (timeLine != null) {
            timeLine.stop();
        }
        isPause.set(true);
        isGameOver.set(true);
        overlayManager.showGameOver();
        if (gameOverPanel != null) {
            gameOverPanel.setVisible(true);
        }
    }

    public void newGame(ActionEvent actionEvent) {
        timeLine.stop();
        gameOverPanel.setVisible(false);
        overlayManager.hideGameOver();
        overlayManager.hidePause(); // added hide pause since game is paused at start - this way pause menu wont be shown when game starts
        eventListener.createNewGame();
        gamePanel.requestFocus();
        if (startOverlay != null && startOverlay.isVisible())
        {
            timeLine.pause();
        }

        else
        {
            timeLine.play();
        }
        isPause.set(false);
        isGameOver.set(false);
    }

    // new method for startgame
    private void startGame() {
        if (startOverlay != null)
        {
            overlayManager.hideStart();
        }
        overlayManager.hideHelp();
        if (timeLine != null)
        {
            timeLine.play();
        }
        isPause.set(false);
        gamePanel.requestFocus();
    }

    // method to show help once the button is pressed
    private void showHelp() {
        if (helpOverlay != null)
        {
            helpFromStart = startOverlay != null && startOverlay.isVisible();
            overlayManager.showHelp();
            if (startOverlay != null)
            {
                overlayManager.hideStart();
            }
        }
    }

    // method to close help when "close" is pressed
    private void hideHelp() {
        if (helpOverlay != null)
        {
            overlayManager.hideHelp();
        }

        if (helpFromStart && startOverlay != null)
        {
            overlayManager.showStart();
        }
        helpFromStart = false;
    }

    private void togglePause() {

        if (!isPause.get())
        {
            // Pause methodology
            isPause.set(true);
            if (timeLine != null)
            {
                timeLine.pause();
            }
            overlayManager.showPause();
        }
        else
        {
            // Resume ideology
            resumeGame();
        }
    }

    private void resumeGame() {
        isPause.set(false);
        if (timeLine != null)
        {
            timeLine.play();
        }
        overlayManager.hidePause();
        gamePanel.requestFocus();
    }

    private void quitGame() {
        Platform.exit();
    }

}

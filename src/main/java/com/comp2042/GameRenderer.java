package com.comp2042;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

public class GameRenderer {

    private final GridPane gamePanel;
    private final GridPane brickPanel;
    private final GridPane nextBrickPanel1;
    private final GridPane nextBrickPanel2;
    private final GridPane nextBrickPanel3;

    private final DoubleProperty gamePanelSceneX;
    private final DoubleProperty gamePanelSceneY;

    Rectangle[][] displayMatrix;
    Rectangle[][] rectangles;
    Rectangle[][] nextBrickRectangles1;
    Rectangle[][] nextBrickRectangles2;
    Rectangle[][] nextBrickRectangles3;

    // game renderer definition
    public GameRenderer(GridPane gamePanel, GridPane brickPanel,
                        GridPane nextBrickPanel1, GridPane nextBrickPanel2, GridPane nextBrickPanel3,
                        DoubleProperty gamePanelSceneX, DoubleProperty gamePanelSceneY) {
        this.gamePanel = gamePanel;
        this.brickPanel = brickPanel;
        this.nextBrickPanel1 = nextBrickPanel1;
        this.nextBrickPanel2 = nextBrickPanel2;
        this.nextBrickPanel3 = nextBrickPanel3;
        this.gamePanelSceneX = gamePanelSceneX;
        this.gamePanelSceneY = gamePanelSceneY;
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
                // FIXED: changed private to public
                rectangle.setFill(getFillColor(brick.getBrickData()[i][j]));
                rectangles[i][j] = rectangle;
                brickPanel.add(rectangle, j, i);
            }
        }

        // gets the next three bricks data and recreates the block and adds it to the panels
        int[][][] nextBrickDataArr = brick.getNextBrickData();

        // first next brick (right next)
        nextBrickRectangles1 = new Rectangle[nextBrickDataArr[0].length][nextBrickDataArr[0][0].length];
        for (int i = 0; i < nextBrickDataArr[0].length; i++) {
            for (int j = 0; j < nextBrickDataArr[0][i].length; j++) {
                Rectangle rectangle = new Rectangle(Constants.BRICK_SIZE, Constants.BRICK_SIZE);
                // FIXED: changed private to public
                rectangle.setFill(getFillColor(nextBrickDataArr[0][i][j]));
                nextBrickRectangles1[i][j] = rectangle;
                nextBrickPanel1.add(rectangle, j, i);
            }
        }

        // second next brick
        nextBrickRectangles2 = new Rectangle[nextBrickDataArr[1].length][nextBrickDataArr[1][0].length];
        for (int i = 0; i < nextBrickDataArr[1].length; i++) {
            for (int j = 0; j < nextBrickDataArr[1][i].length; j++) {
                Rectangle rectangle = new Rectangle(Constants.BRICK_SIZE, Constants.BRICK_SIZE);
                // FIXED: changed private to public
                rectangle.setFill(getFillColor(nextBrickDataArr[1][i][j]));
                nextBrickRectangles2[i][j] = rectangle;
                nextBrickPanel2.add(rectangle, j, i);
            }
        }

        // third next brick
        nextBrickRectangles3 = new Rectangle[nextBrickDataArr[2].length][nextBrickDataArr[2][0].length];
        for (int i = 0; i < nextBrickDataArr[2].length; i++) {
            for (int j = 0; j < nextBrickDataArr[2][i].length; j++) {
                Rectangle rectangle = new Rectangle(Constants.BRICK_SIZE, Constants.BRICK_SIZE);
                // FIXED: changed private to public
                rectangle.setFill(getFillColor(nextBrickDataArr[2][i][j]));
                nextBrickRectangles3[i][j] = rectangle;
                nextBrickPanel3.add(rectangle, j, i);
            }
        }

        // position the brick after scene is laid out
        Platform.runLater(() -> {
            positionBrickPanel(brick);
        });

        // removed timeline controller
    }

    // changed to public so gui controller can access
    public Paint getFillColor(int i) {
        Paint returnPaint;
        switch (i) {
            case 0: returnPaint = Color.TRANSPARENT; break;
            case 1: returnPaint = Color.AQUA; break;
            case 2: returnPaint = Color.BLUEVIOLET; break;
            case 3: returnPaint = Color.DARKGREEN; break;
            case 4: returnPaint = Color.YELLOW; break;
            case 5: returnPaint = Color.RED; break;
            case 6: returnPaint = Color.BEIGE; break;
            case 7: returnPaint = Color.BURLYWOOD; break;
            default: returnPaint = Color.WHITE; break;
        }
        return returnPaint;
    }

    // private to public
    public void positionBrickPanel(ViewData brick) {
        double cellWidth = gamePanel.getHgap() + Constants.BRICK_SIZE;
        double cellHeight = gamePanel.getVgap() + Constants.BRICK_SIZE;

        brickPanel.setLayoutX(gamePanelSceneX.get() + brick.getxPosition() * cellWidth);
        brickPanel.setLayoutY(gamePanelSceneY.get() + brick.getyPosition() * cellHeight);
    }

    // is pause check removed ( add it to pause manager ?? - tbd )
    public void refreshBrick(ViewData brick) {
        positionBrickPanel(brick);
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                setRectangleData(brick.getBrickData()[i][j], rectangles[i][j]);
            }
        }

        int[][][] nextBrickDataArr = brick.getNextBrickData();

        for (int i = 0; i < nextBrickDataArr[0].length; i++) {
            for (int j = 0; j < nextBrickDataArr[0][i].length; j++) {
                setRectangleData(nextBrickDataArr[0][i][j], nextBrickRectangles1[i][j]);
            }
        }
        for (int i = 0; i < nextBrickDataArr[1].length; i++) {
            for (int j = 0; j < nextBrickDataArr[1][i].length; j++) {
                setRectangleData(nextBrickDataArr[1][i][j], nextBrickRectangles2[i][j]);
            }
        }
        for (int i = 0; i < nextBrickDataArr[2].length; i++) {
            for (int j = 0; j < nextBrickDataArr[2][i].length; j++) {
                setRectangleData(nextBrickDataArr[2][i][j], nextBrickRectangles3[i][j]);
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

    // private to public
    public void setRectangleData(int color, Rectangle rectangle) {
        rectangle.setFill(getFillColor(color));
        rectangle.setArcHeight(Constants.BRICK_ARC);
        rectangle.setArcWidth(Constants.BRICK_ARC);
    }
}
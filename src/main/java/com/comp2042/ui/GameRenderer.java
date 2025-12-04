package com.comp2042.ui;

import com.comp2042.core.Constants;
import com.comp2042.logic.workflow.ViewData;
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
    private final GridPane holdBrickPanel;

    private final DoubleProperty gamePanelSceneX;
    private final DoubleProperty gamePanelSceneY;

    private boolean isUpsideDown = false;

    Rectangle[][] displayMatrix;
    Rectangle[][] rectangles;
    Rectangle[][] nextBrickRectangles1;
    Rectangle[][] nextBrickRectangles2;
    Rectangle[][] nextBrickRectangles3;
    Rectangle[][] holdBrickRectangles;

    // game renderer definition
    public GameRenderer(GridPane gamePanel, GridPane brickPanel,
                        GridPane nextBrickPanel1, GridPane nextBrickPanel2, GridPane nextBrickPanel3, GridPane holdBrickPanel,
                        DoubleProperty gamePanelSceneX, DoubleProperty gamePanelSceneY) {
        this.gamePanel = gamePanel;
        this.brickPanel = brickPanel;
        this.nextBrickPanel1 = nextBrickPanel1;
        this.nextBrickPanel2 = nextBrickPanel2;
        this.nextBrickPanel3 = nextBrickPanel3;
        this.gamePanelSceneX = gamePanelSceneX;
        this.gamePanelSceneY = gamePanelSceneY;
        this.holdBrickPanel = holdBrickPanel;
    }

    // method to toggle upside down mode
    public void setUpsideDown(boolean value) {
        this.isUpsideDown = value;
    }

    // refactored to reduce "Cognitive Complexity"
    public void initGameView(int[][] boardMatrix, ViewData brick) {
        displayMatrix = createGrid(boardMatrix.length, boardMatrix[0].length, gamePanel, true);
        rectangles = createGrid(brick.getBrickData().length, brick.getBrickData()[0].length, brickPanel, brick.getBrickData());

        int[][][] nextBrickDataArr = brick.getNextBrickData();

        // used a centralized helper function - createGrid
        // first next brick (right next)
        nextBrickRectangles1 = createGrid(nextBrickDataArr[0].length, nextBrickDataArr[0][0].length, nextBrickPanel1, nextBrickDataArr[0]);
        // second next brick
        nextBrickRectangles2 = createGrid(nextBrickDataArr[1].length, nextBrickDataArr[1][0].length, nextBrickPanel2, nextBrickDataArr[1]);
        // third next brick
        nextBrickRectangles3 = createGrid(nextBrickDataArr[2].length, nextBrickDataArr[2][0].length, nextBrickPanel3, nextBrickDataArr[2]);

        int[][] holdData = brick.getHoldBrickData();
        holdBrickRectangles = createGrid(holdData.length, holdData[0].length, holdBrickPanel, holdData);

        // position the brick after scene is laid out
        Platform.runLater(() -> {
            positionBrickPanel(brick);
        });

    // removed timeline controller
    }

    // createGrid definition for initGameView to handle grid creation
    private Rectangle[][] createGrid(int rows, int cols, GridPane panel, Object dataFn) {
        Rectangle[][] grid = new Rectangle[rows][cols];
        int[][] colorData = (dataFn instanceof int[][]) ? (int[][]) dataFn : null;
        boolean isBoard = (boolean) (dataFn instanceof Boolean && (Boolean) dataFn);

        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++)
            {
                Rectangle rectangle = new Rectangle(Constants.BRICK_SIZE, Constants.BRICK_SIZE);
                if (isBoard)
                {
                    rectangle.setFill(Color.TRANSPARENT);
                }
                else if (colorData != null)
                {
                    rectangle.setFill(getFillColor(colorData[i][j]));
                }
                grid[i][j] = rectangle;
                panel.add(rectangle, j, i);
            }
        }
        return grid;
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

    // Calculates Y position from bottom if upside down
    public void positionBrickPanel(ViewData brick) {
        double cellWidth = gamePanel.getHgap() + Constants.BRICK_SIZE;
        double cellHeight = gamePanel.getVgap() + Constants.BRICK_SIZE;

        brickPanel.setLayoutX(gamePanelSceneX.get() + brick.getxPosition() * cellWidth);

        double yPos;
        if (isUpsideDown)
        {
            // flip the Y positio and we subtract brick height to align it properly.
            yPos = Constants.BOARD_ROWS - brick.getyPosition() - brick.getBrickData().length;
        }
        else
        {
            yPos = brick.getyPosition();
        }

        brickPanel.setLayoutY(gamePanelSceneY.get() + yPos * cellHeight);
    }

    // refactored to reduce Cognitive Complexity
    // added internal row mirroring logic
    public void refreshBrick(ViewData brick) {
        positionBrickPanel(brick);

        // update main brick (contains specific mirroring logic)
        updateActiveBrick(brick);

        // update next bricks
        int[][][] nextBrickDataArr = brick.getNextBrickData();
        updateGrid(nextBrickDataArr[0], nextBrickRectangles1);
        updateGrid(nextBrickDataArr[1], nextBrickRectangles2);
        updateGrid(nextBrickDataArr[2], nextBrickRectangles3);

        // update hold brick
        updateGrid(brick.getHoldBrickData(), holdBrickRectangles);
    }

    // helper for refreshBrick to handle the active brick's specific mirroring logic
    private void updateActiveBrick(ViewData brick) {
        int brickHeight = brick.getBrickData().length;

        for (int i = 0; i < brickHeight; i++)
        {
            // if upside down, we fill the panel from bottom-to-top
            // to match the mirrored coordinate system of the main board.
            int targetRow = isUpsideDown ? (brickHeight - 1 - i) : i;

            for (int j = 0; j < brick.getBrickData()[i].length; j++)
            {
                setRectangleData(brick.getBrickData()[i][j], rectangles[targetRow][j]);
            }
        }
    }

    // helper for refreshBrick to handle standard grids
    private void updateGrid(int[][] data, Rectangle[][] gridRects) {
        for (int i = 0; i < data.length; i++)
        {
            for (int j = 0; j < data[i].length; j++)
            {
                setRectangleData(data[i][j], gridRects[i][j]);
            }
        }
    }

    // aligns logic rows to visual rows based on mode
    public void refreshGameBackground(int[][] board) {
        for (int i = 0; i < board.length; i++) {
            // calculate which visual row corresponds to logic row i
            int visualRow = isUpsideDown ? (board.length - 1 - i) : i;

            for (int j = 0; j < board[i].length; j++) {
                setRectangleData(board[i][j], displayMatrix[visualRow][j]);
            }
        }
    }

    // private to public
    public void setRectangleData(int color, Rectangle rectangle) {
        rectangle.setFill(getFillColor(color));
        rectangle.setArcHeight(Constants.BRICK_ARC);
        rectangle.setArcWidth(Constants.BRICK_ARC);
    }

    // refactored to reduce Cognitive Complexity
    // method to cleat the board while switching menus
    // did this to fix the previous board showing bug while switching game modes
    public void clearAll() {
        clearGrid(displayMatrix);
        clearGrid(rectangles);
        clearGrid(nextBrickRectangles1);
        clearGrid(nextBrickRectangles2);
        clearGrid(nextBrickRectangles3);
        clearGrid(holdBrickRectangles);
    }

    // Helper to clear a single grid
    private void clearGrid(Rectangle[][] grid) {
        if (grid != null)
        {
            for (int i = 0; i < grid.length; i++)
            {
                for (int j = 0; j < grid[i].length; j++)
                {
                    grid[i][j].setFill(Color.TRANSPARENT);
                }
            }
        }
    }

    // refactored to reduce Cognitive Complexity
    // method to apply the pulse on the bricks
    public void pulseLandedBlocks(int[][] brickData, int xPosition, int yPosition) {
        if (displayMatrix == null || brickData == null)
        {
            return;
        }

        for (int i = 0; i < brickData.length; i++)
        {
            for (int j = 0; j < brickData[i].length; j++)
            {
                if (brickData[i][j] != 0)
                {
                    triggerPulse(xPosition + j, yPosition + i);
                }
            }
        }
    }

    // Helper to trigger pulse on a specific coordinate
    private void triggerPulse(int logicCol, int logicRow) {
        int boardHeight = displayMatrix.length;
        int boardWidth = displayMatrix[0].length;

        if (logicRow >= 0 && logicRow < boardHeight &&
                logicCol >= 0 && logicCol < boardWidth)
        {

            int visualRow = isUpsideDown ? (boardHeight - 1 - logicRow) : logicRow;
            Rectangle rect = displayMatrix[visualRow][logicCol];

            if (rect != null)
            {
                javafx.animation.SequentialTransition pulse = Effects.createPulseEffect(rect);
                pulse.play();
            }
        }
    }
}
package com.comp2042.ui;

import com.comp2042.core.Constants;
import com.comp2042.core.MatrixOperations;
import com.comp2042.logic.workflow.ViewData;
import javafx.beans.property.DoubleProperty;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

public class GhostBrickHandler {

    private final GridPane gamePanel;
    private final GridPane brickPanel;
    private final DoubleProperty gamePanelSceneX;
    private final DoubleProperty gamePanelSceneY;
    private final GameRenderer renderer;
    private final GridPane ghostPanel = new GridPane();
    private Rectangle[][] ghostRectangles;
    private boolean isUpsideDown = false;

    // create a ghost brick panel with the same proportions as boreder pane
    // place it on top of the brick panel
    // add it as a child of pane
    public GhostBrickHandler(GridPane gamePanel,
                             GridPane brickPanel,
                             DoubleProperty gamePanelSceneX,
                             DoubleProperty gamePanelSceneY,
                             GameRenderer renderer) {

        this.gamePanel = gamePanel;
        this.brickPanel = brickPanel;
        this.gamePanelSceneX = gamePanelSceneX;
        this.gamePanelSceneY = gamePanelSceneY;
        this.renderer = renderer;

        ghostPanel.setHgap(brickPanel.getHgap());
        ghostPanel.setVgap(brickPanel.getVgap());
        ghostPanel.setMouseTransparent(true);
        ghostPanel.setVisible(false);

        if (brickPanel.getParent() instanceof Pane)
        {
            Pane parent = (Pane) brickPanel.getParent();
            parent.getChildren().add(ghostPanel);
            ghostPanel.toBack();
            brickPanel.toFront();
        }
    }

    // setter for mode
    public void setUpsideDown(boolean value) {
        this.isUpsideDown = value;
    }

    public void init(ViewData brick) {

        ghostRectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];

        for (int i = 0; i < ghostRectangles.length; i++)
        {
            for (int j = 0; j < ghostRectangles[i].length; j++)
            {
                Rectangle r = new Rectangle(Constants.BRICK_SIZE, Constants.BRICK_SIZE);
                r.setFill(Color.TRANSPARENT);
                r.setStroke(Color.color(1, 1, 1, 0.25));
                r.setStrokeType(javafx.scene.shape.StrokeType.INSIDE);
                r.setStrokeWidth(1.2);
                r.setArcHeight(Constants.BRICK_ARC);
                r.setArcWidth(Constants.BRICK_ARC);
                ghostRectangles[i][j] = r;
                ghostPanel.add(r, j, i);
            }
        }
    }

    // calculate landing position by simulating fall
    public void update(ViewData brick, int[][] boardMatrix) {

        if (brick == null || boardMatrix == null) return;
        ensureRectangles(brick);
        applyGhostColors(brick);
        int[][] shape = brick.getBrickData();
        int x = brick.getxPosition();
        int y = brick.getyPosition();
        int yf = y;

        while (!MatrixOperations.collidesWithBackground(boardMatrix, shape, x, yf + 1))
        {
            yf++;
        }

        positionGhostPanel(x, y, yf);
        ghostPanel.setVisible(yf >= 0);
    }

    public void clear() {
        ghostPanel.setVisible(false);
    }

    // calculates position differently if upside down
    private void positionGhostPanel(int xCurrent, int yCurrent, int yFinal) {
        double cellWidth = gamePanel.getHgap() + Constants.BRICK_SIZE;
        double cellHeight = gamePanel.getVgap() + Constants.BRICK_SIZE;
        ghostPanel.setLayoutX(brickPanel.getLayoutX());

        double yOffset;
        if (isUpsideDown) {
            // we subtract the pixel difference instead of adding it when upside down
            yOffset = -(yFinal - yCurrent) * cellHeight;
        } else {
            yOffset = (yFinal - yCurrent) * cellHeight;
        }

        ghostPanel.setLayoutY(brickPanel.getLayoutY() + yOffset);
    }

    private void ensureRectangles(ViewData brick) {
        if (ghostRectangles == null)
        {
            init(brick);
        }
    }

    // apply colours for the ghost block - same as actual block
    // added internal row mirroring logic for upside down mode
    private void applyGhostColors(ViewData brick) {
        int[][] shape = brick.getBrickData();
        int brickHeight = shape.length;

        for (int i = 0; i < brickHeight; i++)
        {
            // problem : block flipped whiel landing
            // fix : inroduced target row variable to check
            int targetRow = isUpsideDown ? (brickHeight - 1 - i) : i;

            for (int j = 0; j < shape[i].length; j++)
            {
                Rectangle r = ghostRectangles[targetRow][j];
                int cell = shape[i][j];
                if (cell == 0)
                {
                    r.setFill(Color.TRANSPARENT);
                    r.setStroke(Color.TRANSPARENT);
                }
                else
                {
                    Paint c = renderer.getFillColor(cell);
                    Color base = c instanceof Color ? (Color) c : Color.WHITE;
                    r.setFill(Color.color(base.getRed(), base.getGreen(), base.getBlue(), 0.15));
                    r.setStroke(Color.color(base.getRed(), base.getGreen(), base.getBlue(), 0.6));
                }
            }
        }
    }
}
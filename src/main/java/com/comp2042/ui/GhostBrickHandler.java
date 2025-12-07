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

/**
 * Renders and positions a translucent ghost brick indicating the landing
 * position of the active brick, supporting upside-down mode.
 *
 * @author Eashwar
 * @version 1.0
 */
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
    /**
     * Constructs the ghost handler with references to panels and renderer.
     *
     * @param gamePanel main board panel
     * @param brickPanel active brick panel
     * @param gamePanelSceneX bound scene X for positioning
     * @param gamePanelSceneY bound scene Y for positioning
     * @param renderer renderer used for colors and row mapping
     */
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
    /**
     * Enables or disables upside-down mode.
     *
     * @param value whether upside-down mode is active
     */
    public void setUpsideDown(boolean value) {
        this.isUpsideDown = value;
    }

    /**
     * Initializes ghost rectangles according to the active brick size.
     *
     * @param brick current view data
     */
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
    /**
     * Updates the ghost colors and positions the panel at the landing spot.
     *
     * @param brick current view data
     * @param boardMatrix background matrix
     */
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

    /**
     * Hides the ghost panel.
     */
    public void clear() {
        ghostPanel.setVisible(false);
    }

    // calculates position differently if upside down
    /**
     * Positions the ghost panel relative to the brick panel.
     *
     * @param xCurrent current x-position
     * @param yCurrent current y-position
     * @param yFinal landing y-position
     */
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

    /**
     * Ensures rectangles are initialized for the ghost brick.
     *
     * @param brick current view data
     */
    private void ensureRectangles(ViewData brick) {
        if (ghostRectangles == null)
        {
            init(brick);
        }
    }

    // apply colours for the ghost block - same as actual block
    // added internal row mirroring logic for upside down mode
    /**
     * Applies translucent colors to represent the ghost brick.
     *
     * @param brick current view data
     */
    private void applyGhostColors(ViewData brick) {
        int[][] shape = brick.getBrickData();
        int brickHeight = shape.length;

        for (int i = 0; i < brickHeight; i++)
        {
            // problem : block flipped whiel landing
            // fix : inroduced target row variable to check
            int targetRow = renderer.getVisualRow(i, brickHeight);

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
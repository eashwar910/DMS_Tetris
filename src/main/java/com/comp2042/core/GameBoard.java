package com.comp2042.core;

import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.RandomBrickGenerator;
import com.comp2042.logic.workflow.ClearRow;
import com.comp2042.logic.workflow.NextShapeInfo;
import com.comp2042.logic.workflow.Score;
import com.comp2042.logic.workflow.ViewData;

import java.util.List;
import java.awt.Point;

/**
 * Implements the Tetris board logic including movement, rotation, holding,
 * spawning bricks, merging into the background, row clearing, and game reset.
 * Coordinates with generators and rotators to maintain gameplay state.
 *
 * @author Eashwar
 * @version 1.0
 */
public class GameBoard implements Board {

    private final int width;
    private final int height;
    private final com.comp2042.logic.bricks.RandomBrickGenerator brickGenerator;
    private final BrickRotator brickRotator;
    private int[][] currentGameMatrix;
    private Point currentOffset;
    private final Score score;
    private com.comp2042.logic.bricks.Brick heldBrick;
    private boolean holdUsed;

    /**
     * Constructs a game board with specified dimensions and initializes state.
     *
     * @param width number of columns in the board
     * @param height number of rows in the board
     */
    public GameBoard(int width, int height) {
        this.width = width;
        this.height = height;
        currentGameMatrix = new int[width][height];
        brickGenerator = new RandomBrickGenerator();
        brickRotator = new BrickRotator();
        score = new Score();
    }

    @Override
    /**
     * Moves the active brick one row downward.
     *
     * @return true if movement succeeds, false if blocked
     */
    public boolean moveBrickDown() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(0, 1);
        boolean conflict = MatrixOperations.collidesWithBackground(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }


    @Override
    /**
     * Moves the active brick one column to the left.
     *
     * @return true if movement succeeds, false otherwise
     */
    public boolean moveBrickLeft() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(-1, 0);
        boolean conflict = MatrixOperations.collidesWithBackground(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

    @Override
    /**
     * Moves the active brick one column to the right.
     *
     * @return true if movement succeeds, false otherwise
     */
    public boolean moveBrickRight() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(1, 0);
        boolean conflict = MatrixOperations.collidesWithBackground(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

    @Override
    /**
     * Attempts to rotate the active brick counterclockwise.
     * Applies horizontal offsets if the immediate rotation collides.
     *
     * @return true if rotation succeeds, false otherwise
     */
    public boolean rotateLeftBrick() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        NextShapeInfo nextShape = brickRotator.getNextShape();
        int x = (int) currentOffset.getX();
        int y = (int) currentOffset.getY();
        if (!MatrixOperations.collidesWithBackground(currentMatrix, nextShape.getShape(), x, y)) {
            brickRotator.setCurrentShape(nextShape.getPosition());
            return true;
        }
        int[] kicks = new int[]{-1, 1, -2, 2};
        for (int dx : kicks) {
            int nx = x + dx;
            if (!MatrixOperations.collidesWithBackground(currentMatrix, nextShape.getShape(), nx, y)) {
                brickRotator.setCurrentShape(nextShape.getPosition());
                currentOffset = new Point(nx, y);
                return true;
            }
        }
        return false;
    }

    @Override
    /**
     * Holds the current brick or swaps it with the held one.
     * Validates single-use per spawn and spawn collision.
     *
     * @return true if hold or swap succeeds, false otherwise
     */
    public boolean holdBrick() {

        // hold can one hold one block and swap
        if (holdUsed)
        {
            return false;
        }
        com.comp2042.logic.bricks.Brick previousCurrent = brickRotator.getBrick();
        com.comp2042.logic.bricks.Brick previousHeld = heldBrick;
        Point previousOffset = new Point(currentOffset);

        // no brick in hold panel, so add the brick and spawn new one
        if (heldBrick == null)
        {
            heldBrick = previousCurrent;
            com.comp2042.logic.bricks.Brick newBrick = brickGenerator.getBrick();
            brickRotator.setBrick(newBrick);
        }

        // if brick already in hold panel, swap
        else
        {
            brickRotator.setBrick(heldBrick);
            heldBrick = previousCurrent;
        }

        currentOffset = new Point(Constants.BRICK_SPAWN_X, Constants.BRICK_SPAWN_Y);
        boolean conflict = MatrixOperations.collidesWithBackground(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());

        // collision error handling
        if (conflict)
        {
            brickRotator.setBrick(previousCurrent);
            heldBrick = previousHeld;
            currentOffset = previousOffset;
            return false;
        }
        holdUsed = true;
        return true;
    }

    @Override
    /**
     * Spawns a new brick at the default position and resets hold usage.
     *
     * @return true if spawn collides with background, false otherwise
     */
    public boolean createNewBrick() {
        Brick currentBrick = brickGenerator.getBrick();
        brickRotator.setBrick(currentBrick);
        currentOffset = new Point(Constants.BRICK_SPAWN_X, Constants.BRICK_SPAWN_Y);
        holdUsed = false;
        return MatrixOperations.collidesWithBackground(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
    }

    @Override
    public int[][] getBoardMatrix() {
        return currentGameMatrix;
    }

    @Override

    //obtains the view data for next 3 bricks instead of 1
    public ViewData getViewData() {
        List<com.comp2042.logic.bricks.Brick> preview = brickGenerator.getNextBricks(Constants.PREVIEW_COUNT);
        int[][][] nextBrickData = new int[preview.size()][][];
        for (int i = 0; i < preview.size(); i++)
        {
            nextBrickData[i] = preview.get(i).getShapeMatrix().get(0);
        }
        int[][] holdData;
        if (heldBrick != null) {
            holdData = heldBrick.getShapeMatrix().get(0);
        } else {
            holdData = new int[4][4];
        }
        return new ViewData(brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY(), nextBrickData, holdData);
    }

    @Override
    /**
     * Locks the active brick into the background matrix at its current offset.
     */
    public void mergeBrickToBackground() {
        currentGameMatrix = MatrixOperations.mergeBrickOntoMatrix(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
    }

    @Override
    /**
     * Clears full rows from the background and updates the board matrix.
     *
     * @return summary data of cleared rows and the updated matrix
     */
    public ClearRow clearRows() {
        ClearRow clearRow = MatrixOperations.clearFullRows(currentGameMatrix);
        currentGameMatrix = clearRow.getNewMatrix();
        return clearRow;

    }

    @Override
    public Score getScore() {
        return score;
    }


    @Override
    /**
     * Resets board, score, generators, and spawns the first brick.
     */
    public void newGame() {
        currentGameMatrix = new int[width][height];
        score.reset();
        brickGenerator.reset();
        heldBrick = null;
        holdUsed = false;
        createNewBrick();
    }

    /**
     * Empties the hold slot and resets usage flags.
     */
    public void clearHold() {
        heldBrick = null;
        holdUsed = false;
    }
}

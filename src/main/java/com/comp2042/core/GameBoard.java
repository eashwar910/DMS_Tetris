package com.comp2042.core;

import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.RandomBrickGenerator;
import com.comp2042.logic.workflow.ClearRow;
import com.comp2042.logic.workflow.NextShapeInfo;
import com.comp2042.logic.workflow.Score;
import com.comp2042.logic.workflow.ViewData;

import java.util.List;
import java.awt.Point;

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

    public GameBoard(int width, int height) {
        this.width = width;
        this.height = height;
        currentGameMatrix = new int[width][height];
        brickGenerator = new RandomBrickGenerator();
        brickRotator = new BrickRotator();
        score = new Score();
    }

    @Override
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
    public void mergeBrickToBackground() {
        currentGameMatrix = MatrixOperations.mergeBrickOntoMatrix(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
    }

    @Override
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
    public void newGame() {
        currentGameMatrix = new int[width][height];
        score.reset();
        brickGenerator.reset();
        heldBrick = null;
        holdUsed = false;
        createNewBrick();
    }

    public void clearHold() {
        heldBrick = null;
        holdUsed = false;
    }
}

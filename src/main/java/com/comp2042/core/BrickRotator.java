package com.comp2042.core;

import com.comp2042.logic.workflow.NextShapeInfo;
import com.comp2042.logic.bricks.Brick;

/**
 * Controls rotation state for a brick, tracking the current orientation
 * and providing the next rotation preview.
 *
 * @author Eashwar
 * @version 1.0
 */
public class BrickRotator {

    private Brick brick;
    private int currentShape = 0;

    /**
     * Computes the next rotation state for the current brick.
     *
     * @return information containing the next shape matrix and its index
     */
    public NextShapeInfo getNextShape() {
        int nextShape = currentShape;
        nextShape = (++nextShape) % brick.getShapeMatrix().size();
        return new NextShapeInfo(brick.getShapeMatrix().get(nextShape), nextShape);
    }

    public int[][] getCurrentShape() {
        return brick.getShapeMatrix().get(currentShape);
    }

    public void setCurrentShape(int currentShape) {
        this.currentShape = currentShape;
    }

    /**
     * Assigns the active brick and resets its rotation to the default state.
     *
     * @param brick the brick to control for rotation
     */
    public void setBrick(Brick brick) {
        this.brick = brick;
        currentShape = 0;
    }

    public Brick getBrick() {

        return brick;
    }


}

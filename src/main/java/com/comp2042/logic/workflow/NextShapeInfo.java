package com.comp2042.logic.workflow;

import com.comp2042.core.MatrixOperations;

/**
 * Provides the next rotation state for a brick, including shape matrix and
 * its index position.
 *
 * @author Eashwar
 * @version 1.0
 */
public final class NextShapeInfo {

    private final int[][] shape;
    private final int position;

    /**
     * Constructs next-shape info.
     *
     * @param shape the next shape matrix
     * @param position index of the next rotation state
     */
    public NextShapeInfo(final int[][] shape, final int position) {
        this.shape = shape;
        this.position = position;
    }

    public int[][] getShape() {
        return MatrixOperations.copy(shape);
    }

    public int getPosition() {
        return position;
    }
}

package com.comp2042.logic.bricks;

import com.comp2042.core.MatrixOperations;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines the O-shaped tetromino rotation states.
 *
 * @author Eashwar
 * @version 1.0
 */
final class OBrick implements Brick {

    private final List<int[][]> brickMatrix = new ArrayList<>();

    /**
     * Initializes the rotation matrix for the O-shaped brick.
     */
    public OBrick() {
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {0, 4, 4, 0},
                {0, 4, 4, 0},
                {0, 0, 0, 0}
        });
    }

    @Override
    public List<int[][]> getShapeMatrix() {
        return MatrixOperations.deepCopyList(brickMatrix);
    }

}

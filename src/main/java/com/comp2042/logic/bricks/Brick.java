package com.comp2042.logic.bricks;

import java.util.List;

/**
 * Represents a Tetris brick with one or more rotation states.
 *
 * @author Eashwar
 * @version 1.0
 */
public interface Brick {

    List<int[][]> getShapeMatrix();
}

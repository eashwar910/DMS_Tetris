package com.comp2042.logic.bricks;

import java.util.List;

/**
 * Contract for generating Tetris bricks, supporting single and preview
 * retrieval.
 *
 * @author Eashwar
 * @version 1.0
 */
public interface BrickGenerator {

    /**
     * Retrieves the next brick to be spawned, advancing the generator.
     *
     * @return the next brick
     */
    Brick getBrick();

    /**
     * Peeks at the upcoming brick without advancing the generator.
     *
     * @return the upcoming brick
     */
    Brick getNextBrick();

    /**
     * Provides a preview list of the next bricks.
     *
     * @param count number of bricks to preview
     * @return list of upcoming bricks
     */
    List<Brick> getNextBricks(int count);
}

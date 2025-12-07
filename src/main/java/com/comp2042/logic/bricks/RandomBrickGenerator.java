package com.comp2042.logic.bricks;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Collections;

/**
 * Generates bricks using a 7-bag randomization system to balance distribution.
 * Maintains a queue of upcoming bricks and supports preview operations.
 * All popular Tetris games use the 7-bag system for randomization.
 *
 * @author Eashwar
 * @version 1.0
 */
public class RandomBrickGenerator implements BrickGenerator {

    private final List<Brick> brickList;
    private final Deque<Brick> nextBricks = new ArrayDeque<>();

    /**
     * Constructs the generator and initializes the bag of bricks.
     */
    public RandomBrickGenerator() {
        brickList = new ArrayList<>();
        brickList.add(new IBrick());
        brickList.add(new JBrick());
        brickList.add(new LBrick());
        brickList.add(new OBrick());
        brickList.add(new SBrick());
        brickList.add(new TBrick());
        brickList.add(new ZBrick());
        refillBag();
    }

    /**
     * Resets the generator, clearing previews and refilling the bag.
     */
    public void reset() {
        nextBricks.clear();
        refillBag();
    }

    // using the famous 7 bag system
    // this way blocks dont repeat very often and one of the 7 different bricks comes
    // before each brick is repeated
    /**
     * Refills the bag with a shuffled set of the 7 bricks.
     */
    private void refillBag() {
        List<Brick> bag = new ArrayList<>(brickList);
        Collections.shuffle(bag, ThreadLocalRandom.current());
        for (Brick b : bag)
        {
            nextBricks.add(b);
        }
    }

    @Override
    /**
     * Retrieves and removes the next brick; refills the bag when low.
     *
     * @return the next brick
     */
    public Brick getBrick() {
        if (nextBricks.size() <= 1) {
            refillBag();
        }
        return nextBricks.poll();
    }

    @Override
    /**
     * Peeks at the next brick without removing it.
     *
     * @return the upcoming brick
     */
    public Brick getNextBrick() {
        return nextBricks.peek();
    }

    @Override

    // instead of giving the next one brick, it returns the next "count" bricks
    /**
     * Returns a preview list of the next `count` bricks.
     *
     * @param count number of bricks to preview
     * @return list of upcoming bricks
     */
    public java.util.List<Brick> getNextBricks(int count) {
        if (nextBricks.size() < count)
        {
            refillBag();
        }
        java.util.List<Brick> result = new java.util.ArrayList<>(count);
        int i = 0;
        for (Brick b : nextBricks)
        {
            if (i++ >= count)
            {
                break;
            }
            result.add(b);
        }
        return result;
    }
}

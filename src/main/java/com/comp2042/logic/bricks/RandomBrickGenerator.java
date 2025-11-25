package com.comp2042.logic.bricks;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Collections;

public class RandomBrickGenerator implements BrickGenerator {

    private final List<Brick> brickList;
    private final Deque<Brick> nextBricks = new ArrayDeque<>();

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

    public void reset() {
        nextBricks.clear();
        refillBag();
    }

    // using the famous 7 bag system
    // this way blocks dont repeat very often and one of the 7 different bricks comes
    // before each brick is repeated
    private void refillBag() {
        List<Brick> bag = new ArrayList<>(brickList);
        Collections.shuffle(bag, ThreadLocalRandom.current());
        for (Brick b : bag)
        {
            nextBricks.add(b);
        }
    }

    @Override
    public Brick getBrick() {
        if (nextBricks.size() <= 1) {
            refillBag();
        }
        return nextBricks.poll();
    }

    @Override
    public Brick getNextBrick() {
        return nextBricks.peek();
    }

    @Override

    // instead of giving the next one brick, it returns the next "count" bricks
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

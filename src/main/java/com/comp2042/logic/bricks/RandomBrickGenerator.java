package com.comp2042.logic.bricks;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

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
        nextBricks.add(brickList.get(ThreadLocalRandom.current().nextInt(brickList.size())));
        nextBricks.add(brickList.get(ThreadLocalRandom.current().nextInt(brickList.size())));
    }

    @Override
    public Brick getBrick() {
        if (nextBricks.size() <= 1) {
            nextBricks.add(brickList.get(ThreadLocalRandom.current().nextInt(brickList.size())));
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
        while (nextBricks.size() < count)
        {
            nextBricks.add(brickList.get(java.util.concurrent.ThreadLocalRandom.current().nextInt(brickList.size())));
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

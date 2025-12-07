package com.comp2042.logic.bricks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RandomBrickGeneratorTest {

    private RandomBrickGenerator gen;

    // set up the random brick gen before every test
    @BeforeEach
    void setup() {
        gen = new RandomBrickGenerator();
    }

    // test for reset method
    @Test
    void reset() {

        // reset should refill to a full bag of 7 unique bricks
        gen.getBrick();
        gen.getBrick();
        gen.reset();

        java.util.List<Brick> bag = gen.getNextBricks(7);
        // bag size should be 7 after reset
        assertEquals(7, bag.size());
        java.util.Set<String> names = new java.util.HashSet<>();
        for (Brick b : bag) names.add(b.getClass().getSimpleName());
        assertEquals(7, names.size());
    }

    // test get b rick method
    @Test
    void getBrick() {

        // polling 7 bricks after reset should yield all 7 types
        gen.reset();
        java.util.Set<String> names = new java.util.HashSet<>();
        for (int i = 0; i < 7; i++)
        {
            Brick b = gen.getBrick();
            assertNotNull(b);
            names.add(b.getClass().getSimpleName());
        }
        assertEquals(7, names.size());

        // next call should still return a brick (new bag started if needed)
        assertNotNull(gen.getBrick());
    }

    // test for get next brick method
    @Test
    void getNextBrick() {

        // peek should not consume
        // after poll, peek should change to next
        gen.reset();
        Brick firstPeek = gen.getNextBrick();
        assertNotNull(firstPeek);
        Brick firstPoll = gen.getBrick();
        assertEquals(firstPeek.getClass(), firstPoll.getClass());
        Brick secondPeek = gen.getNextBrick();
        assertNotNull(secondPeek);
        assertNotEquals(firstPeek, secondPeek);
    }

    // test for get next bricks method
    @Test
    void getNextBricks() {

        // next bricks should match upcoming poll order for the requested count
        gen.reset();
        java.util.List<Brick> next3 = gen.getNextBricks(3);
        assertEquals(3, next3.size());

        Brick p1 = gen.getBrick();
        Brick p2 = gen.getBrick();
        Brick p3 = gen.getBrick();

        assertEquals(next3.get(0).getClass(), p1.getClass());
        assertEquals(next3.get(1).getClass(), p2.getClass());
        assertEquals(next3.get(2).getClass(), p3.getClass());
    }
}

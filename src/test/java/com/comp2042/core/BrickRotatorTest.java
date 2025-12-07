package com.comp2042.core;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import org.junit.jupiter.api.Test;

import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.workflow.NextShapeInfo;

class BrickRotatorTest {


    // a brick implementation just for tests
    private static class TestBrick implements Brick {

        private final java.util.List<int[][]> shapes = new java.util.ArrayList<>();
        TestBrick(int[][]... s) {
            // store copies so tests don't change original arrays
            for (int[][] m : s) {
                shapes.add(MatrixOperations.copy(m));
            }
        }
        @Override
        public java.util.List<int[][]> getShapeMatrix() {
            // return deep copies so callers can't change our shapes
            return shapes.stream().map(MatrixOperations::copy).toList();
        }
    }

    // test for get next shape method
    @Test
    void getNextShape() {

        // define simple shapes
        int[][] s0 = new int[][]{{1,0},{0,1}};
        int[][] s1 = new int[][]{{2,2},{0,0}};
        int[][] s2 = new int[][]{{3,0},{3,0}};

        // create rotator and set a test brick
        BrickRotator rot = new BrickRotator();
        rot.setBrick(new TestBrick(s0, s1, s2));

        // first next shape should be position 1 and equal to s1
        NextShapeInfo n1 = rot.getNextShape();
        assertEquals(1, n1.getPosition());
        assertArrayEquals(s1, n1.getShape());

        // current shape should still be s0
        assertArrayEquals(s0, rot.getCurrentShape());

        // set current to last and ensure wrap to 0
        rot.setCurrentShape(2);
        NextShapeInfo n2 = rot.getNextShape();
        assertEquals(0, n2.getPosition());
        assertArrayEquals(s0, n2.getShape());
    }

    // test for get current shape method
    @Test
    void getCurrentShape() {

        int[][] s0 = new int[][]{{7,7},{0,0}};
        int[][] s1 = new int[][]{{0,8},{8,0}};

        BrickRotator rot = new BrickRotator();
        rot.setBrick(new TestBrick(s0, s1));
        assertArrayEquals(s0, rot.getCurrentShape());
    }

    // test for set current shape method
    @Test
    void setCurrentShape() {

        // changing current shape should return the chosen rotation
        int[][] s0 = new int[][]{{1}};
        int[][] s1 = new int[][]{{2}};

        BrickRotator rot = new BrickRotator();
        rot.setBrick(new TestBrick(s0, s1));
        rot.setCurrentShape(1);
        assertArrayEquals(s1, rot.getCurrentShape());
    }

    // test for set brick method
    @Test
    void setBrick() {

        // setting a new brick should reset current to 0
        int[][] s0 = new int[][]{{9}};
        int[][] s1 = new int[][]{{10}};

        BrickRotator rot = new BrickRotator();
        rot.setCurrentShape(1);
        rot.setBrick(new TestBrick(s0, s1));
        assertArrayEquals(s0, rot.getCurrentShape());
    }

    // test for get brick method
    @Test
    void getBrick() {

        // getBrick should return the same instance we set
        Brick b = new TestBrick(new int[][]{{1}});
        BrickRotator rot = new BrickRotator();
        rot.setBrick(b);
        assertSame(b, rot.getBrick());
    }
}

package com.comp2042.core;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.comp2042.logic.workflow.ClearRow;

class MatrixOperationsTest {

    // test for collides with background method
    @Test
    void collidesWithBackground() {

        // empty board
        int[][] board = new int[][]{
                {0,0,0,0},
                {0,0,0,0},
                {0,0,0,0},
                {0,0,0,0}
        };
        // o brick
        int[][] brick = new int[][]{
                {1,1},
                {1,1}
        };
        
        // o brick should not collide with background
        assertFalse(MatrixOperations.collidesWithBackground(board, brick, 1, 1));

        board[1][2] = 9;
        // o brick should collide with background
        assertTrue(MatrixOperations.collidesWithBackground(board, brick, 1, 1));

        int[][] emptyTop = new int[][]{
                {0,0,0,0},
                {0,0,0,0},
                {0,0,0,0},
                {0,0,0,0}
        };

        // o brick should not collide with background when placed at top
        assertFalse(MatrixOperations.collidesWithBackground(emptyTop, brick, 1, -1));

        int[][] topFilled = new int[][]{
                {0,5,0,0},
                {0,0,0,0},
                {0,0,0,0},
                {0,0,0,0}
        };

        // o brick should collide with background when placed at top
        assertTrue(MatrixOperations.collidesWithBackground(topFilled, brick, 1, -1));
    }

    // test for copy method
    @Test
    void copy() {

        // create a mtrix
        int[][] original = new int[][]{
                {1,2,3},
                {4,5,6}
        };

        // copy it
        int[][] copied = MatrixOperations.copy(original);

        // test if they are rqual
        assertArrayEquals(original[0], copied[0]);
        assertArrayEquals(original[1], copied[1]);

        original[0][0] = 99;
        assertEquals(1, copied[0][0]);

        copied[1][1] = 77;
        assertEquals(5, original[1][1]);
    }

    // test for merge brick onto matrix method
    @Test
    void mergeBrickOntoMatrix() {

        // create a matrix and a brick
        int[][] fields = new int[][]{
                {0,0,0,0},
                {0,0,0,0},
                {0,0,0,0},
                {0,0,0,0}
        };
        int[][] brick = new int[][]{
                {2,2},
                {2,2}
        };

        // merge the brick onto the matrix
        int[][] merged = MatrixOperations.mergeBrickOntoMatrix(fields, brick, 1, 1);

        // test if the brick is merged correctly
        assertEquals(2, merged[1][1]);
        assertEquals(2, merged[1][2]);
        assertEquals(2, merged[2][1]);
        assertEquals(2, merged[2][2]);

        int[][] fields2 = new int[][]{
                {0,0,0,0},
                {0,0,0,0},
                {0,0,0,0},
                {0,0,0,0}
        };

        int[][] mergedOff = MatrixOperations.mergeBrickOntoMatrix(fields2, brick, 1, -1);

        // test if the brick is merged correctly when placed off screen
        assertEquals(2, mergedOff[0][1]);
        assertEquals(2, mergedOff[0][2]);
        assertEquals(0, mergedOff[1][1]);
        assertEquals(0, mergedOff[1][2]);
    }

    // test for clear full rows method
    @Test
    void clearFullRows() {

        // crate a matrix
        int[][] matrix = new int[][]{
                {1,0,2},
                {5,6,7},
                {0,3,0},
                {8,9,1}
        };

        // test clear now
        ClearRow result = MatrixOperations.clearFullRows(matrix);

        assertEquals(2, result.getLinesRemoved());
        assertEquals(200, result.getScoreBonus());

        int[][] newM = result.getNewMatrix();

        assertArrayEquals(new int[]{0,3,0}, newM[3]);
        assertArrayEquals(new int[]{1,0,2}, newM[2]);
        assertArrayEquals(new int[]{0,0,0}, newM[1]);
        assertArrayEquals(new int[]{0,0,0}, newM[0]);
    }

    // test for deep copy list method
    @Test
    void deepCopyList() {

        // create a list of matrices
        int[][] a = new int[][]{{1,0},{0,1}};
        int[][] b = new int[][]{{2,2},{2,2}};

        java.util.List<int[][]> list = java.util.List.of(a, b);
        java.util.List<int[][]> copied = MatrixOperations.deepCopyList(list);

        // test if the list is copied correctly
        assertArrayEquals(a, copied.get(0));
        assertArrayEquals(b, copied.get(1));

        // test if the original list is not modified
        a[0][0] = 9;
        b[1][1] = 7;

        assertEquals(1, copied.get(0)[0][0]);
        assertEquals(2, copied.get(1)[1][1]);

        copied.get(0)[1][1] = 5;

        assertEquals(1, a[1][1]);
    }
}

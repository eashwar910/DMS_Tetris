package com.comp2042.logic.workflow;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClearRowTest {

    // test for get lines removed method
    @Test
    void getLinesRemoved() {

        // lines removed should match the value set in constructor
        int[][] mat = new int[][] { {1, 0}, {0, 1} };
        ClearRow cr = new ClearRow(2, mat, 300);
        assertEquals(2, cr.getLinesRemoved());
    }

    // test get new matrix method
    @Test
    void getNewMatrix() {

        // getNewMatrix should return a copy, not the same reference
        int[][] mat = new int[][] { {1, 2}, {3, 4} };
        ClearRow cr = new ClearRow(1, mat, 100);

        int[][] copy1 = cr.getNewMatrix();
        assertArrayEquals(mat, copy1);
        assertNotSame(mat, copy1);
        assertNotSame(mat[0], copy1[0]);

        // changing the returned matrix should not affect future returns
        copy1[0][0] = 99;
        int[][] copy2 = cr.getNewMatrix();
        assertEquals(1, copy2[0][0]);
    }

    // test for get score bonus method
    @Test
    void getScoreBonus() {
        // score bonus should match the value set in constructor
        int[][] mat = new int[][] { {0, 0}, {0, 0} };
        ClearRow cr = new ClearRow(0, mat, 800);
        assertEquals(800, cr.getScoreBonus());
    }
}

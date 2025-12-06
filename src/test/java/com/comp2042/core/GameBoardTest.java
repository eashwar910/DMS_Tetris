package com.comp2042.core;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.comp2042.logic.workflow.ClearRow;
import com.comp2042.logic.workflow.ViewData;

class GameBoardTest {

    // create new board
    private GameBoard newBoard() {
        return new GameBoard(10, 20);
    }

    // test for move brick down method
    @Test
    void moveBrickDown() {

        GameBoard board = newBoard();
        boolean collidedAtSpawn = board.createNewBrick();
        // on empty board spawn should not collide
        assertFalse(collidedAtSpawn);

        ViewData before = board.getViewData();
        boolean moved = board.moveBrickDown();
        // should move down successfully
        assertTrue(moved);
        ViewData after = board.getViewData();
        assertEquals(before.getxPosition(), after.getxPosition());
        assertEquals(before.getyPosition() + 1, after.getyPosition());
    }

    // tests moving brick left method
    @Test
    void moveBrickLeft() {

        GameBoard board = newBoard();
        board.createNewBrick();
        ViewData before = board.getViewData();

        boolean moved = board.moveBrickLeft();
        // should move left successfully
        assertTrue(moved);

        ViewData after = board.getViewData();
        assertEquals(before.getxPosition() - 1, after.getxPosition());
        assertEquals(before.getyPosition(), after.getyPosition());
    }

    // test for move brick right method
    @Test
    void moveBrickRight() {

        GameBoard board = newBoard();
        board.createNewBrick();

        ViewData before = board.getViewData();
        boolean moved = board.moveBrickRight();

        assertTrue(moved);

        ViewData after = board.getViewData();
        assertEquals(before.getxPosition() + 1, after.getxPosition());
        assertEquals(before.getyPosition(), after.getyPosition());
    }

    // test for rotate left brick method
    @Test
    void rotateLeftBrick() {

        GameBoard board = newBoard();
        board.createNewBrick();

        boolean rotated = board.rotateLeftBrick();

        assertTrue(rotated);
    }

    // test for hold brick method
    @Test
    void holdBrick() {
        GameBoard board = newBoard();
        board.createNewBrick();

        boolean firstHold = board.holdBrick();
        // first hold should succeed
        assertTrue(firstHold);

        boolean secondHold = board.holdBrick();
        // second hold should fail due to hold limit
        assertFalse(secondHold);

        board.clearHold();
        boolean holdAfterClear = board.holdBrick();
        assertTrue(holdAfterClear);
    }

    // test for create new brick methpd
    @Test
    void createNewBrick() {

        GameBoard board = newBoard();
        int[][] m = board.getBoardMatrix();
        m[0][Constants.BRICK_SPAWN_X] = 7; // set top row at spawn x to occupied to force collision offscreen
        boolean collided = board.createNewBrick();
        assertTrue(collided);
    }

    // test for get board matrix method
    @Test
    void getBoardMatrix() {

        GameBoard board = newBoard();
        int[][] m = board.getBoardMatrix();

        m[0][0] = 1;
        assertEquals(1, board.getBoardMatrix()[0][0]);
    }

    // test for view data method
    @Test
    void getViewData() {

        GameBoard board = newBoard();
        board.createNewBrick();

        ViewData vd = board.getViewData();
        // view must not be null
        assertNotNull(vd);
        // brick data should be 4 rows
        assertEquals(4, vd.getBrickData().length);
        // brick data should be 4 cols
        assertEquals(4, vd.getBrickData()[0].length);
        // x should match spawn x initially
        assertEquals(Constants.BRICK_SPAWN_X, vd.getxPosition());
        // y should match spawn y initially
        assertEquals(Constants.BRICK_SPAWN_Y, vd.getyPosition());
    }
    @Test
    void mergeBrickToBackground() {

        GameBoard board = newBoard();
        board.createNewBrick();
        board.moveBrickDown();
        board.moveBrickDown();

        int[][] before = board.getBoardMatrix();
        int sumBefore = java.util.Arrays.stream(before).flatMapToInt(java.util.Arrays::stream).sum();
        board.mergeBrickToBackground();
        int[][] after = board.getBoardMatrix();
        int sumAfter = java.util.Arrays.stream(after).flatMapToInt(java.util.Arrays::stream).sum();

        // after merge there should be more non zero cells
        assertTrue(sumAfter > sumBefore);
    }

    // test for clear rows method
    @Test
    void clearRows() {

        GameBoard board = newBoard();
        int[][] m = board.getBoardMatrix();

        java.util.Arrays.fill(m[0], 1);
        ClearRow cr = board.clearRows();
        // exactly one row should be removed
        assertEquals(1, cr.getLinesRemoved());
        // top row should become all zeros
        assertArrayEquals(new int[m[0].length], board.getBoardMatrix()[0]);
    }

    // test for get score method
    @Test
    void getScore() {

        GameBoard board = newBoard();
        // score should not be null
        assertNotNull(board.getScore());
    }

    // test for new game method
    @Test
    void newGame() {

        GameBoard board = newBoard();
        board.createNewBrick();
        board.moveBrickDown();
        board.mergeBrickToBackground();
        int preSum = java.util.Arrays.stream(board.getBoardMatrix()).flatMapToInt(java.util.Arrays::stream).sum();
        // we should have some blocks
        assertTrue(preSum > 0);

        board.newGame();
        int postSum = java.util.Arrays.stream(board.getBoardMatrix()).flatMapToInt(java.util.Arrays::stream).sum();
        // matrix should be cleared to all zeros after new game
        assertEquals(0, postSum);
    }

    // test for clear hold method
    @Test
    void clearHold() {
        GameBoard board = newBoard();
        board.createNewBrick();
        // first hold succeeds
        assertTrue(board.holdBrick());
        // second hold fails
        assertFalse(board.holdBrick());
        board.clearHold();
        // hold works again after clearing
        assertTrue(board.holdBrick());
    }
}

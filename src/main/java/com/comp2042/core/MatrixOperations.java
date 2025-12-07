package com.comp2042.core;

import com.comp2042.logic.workflow.ClearRow;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Utility functions for manipulating board and brick matrices including
 * collision detection, merging, row clearing, and copying.
 *
 * @author Eashwar
 * @version 1.0
 */
public class MatrixOperations {

    /**
     * Prevents instantiation of utility class.
     */
    private MatrixOperations(){

    }

    // rename function for better understanding
    /**
     * Checks whether a brick placed at the given offset collides with the background.
     *
     * @param matrix the background matrix
     * @param brick the brick matrix
     * @param x placement x-offset
     * @param y placement y-offset
     * @return true if any occupied cell collides or is out of bounds; false otherwise
     */
    public static boolean collidesWithBackground(final int[][] matrix, final int[][] brick, int x, int y) {
        for (int i = 0; i < brick.length; i++) {
            for (int j = 0; j < brick[i].length; j++) {
                int targetX = x + i;
                int targetY = y + j;
                int cell = brickCell(brick, i, j);

                // flattened the deep nested loops into a function
                if (cellCollides(matrix, cell, targetX, targetY))
                {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean checkOutOfBound(int[][] matrix, int targetX, int targetY) {

        // simplified loops using getHeight and getWidth methods
        if (targetX < 0 || targetY < 0)
        {
            return true;
        }

        if (targetY >= getHeight(matrix))
        {
            return true;
        }
        return targetX >= getWidth(matrix);
    }

    /**
     * Creates a deep copy of a 2D matrix.
     *
     * @param original the source matrix
     * @return a deep-copied matrix
     */
    public static int[][] copy(int[][] original) {
        int[][] myInt = new int[original.length][];
        for (int i = 0; i < original.length; i++) {
            int[] aMatrix = original[i];
            int aLength = aMatrix.length;
            myInt[i] = new int[aLength];
            System.arraycopy(aMatrix, 0, myInt[i], 0, aLength);
        }
        return myInt;
    }

    // rename function
    /**
     * Merges a brick into the background matrix at the specified offset.
     *
     * @param filledFields the background matrix
     * @param brick the brick matrix
     * @param x placement x-offset
     * @param y placement y-offset
     * @return a new matrix with the brick merged
     */
    public static int[][] mergeBrickOntoMatrix(int[][] filledFields, int[][] brick, int x, int y) {
        int[][] copy = copy(filledFields);
        for (int i = 0; i < brick.length; i++) {
            for (int j = 0; j < brick[i].length; j++) {
                int targetX = x + i;
                int targetY = y + j;
                int cell = brickCell(brick, i, j);  // follow through the loop simplification
                if (cell == 0)
                {
                    continue;
                }
                if (targetY >= 0 && !checkOutOfBound(copy, targetX, targetY))
                {
                    copy[targetY][targetX] = cell;
                }
            }
        }
        return copy;
    }

    // rename function
    /**
     * Clears fully occupied rows and compacts the matrix.
     *
     * @param matrix the source background matrix
     * @return details of rows removed, updated matrix, and score bonus
     */
    public static ClearRow clearFullRows(final int[][] matrix) {
        int[][] tmp = new int[getHeight(matrix)][getWidth(matrix)];
        Deque<int[]> newRows = new ArrayDeque<>();
        List<Integer> clearedRows = new ArrayList<>();

        for (int i = 0; i < getHeight(matrix); i++) {
            int[] tmpRow = new int[getWidth(matrix)];
            boolean rowToClear = true;
            for (int j = 0; j < getWidth(matrix); j++) {
                if (matrix[i][j] == 0) {
                    rowToClear = false;
                }
                tmpRow[j] = matrix[i][j];
            }
            if (rowToClear) {
                clearedRows.add(i);
            } else {
                newRows.add(tmpRow);
            }
        }
        for (int i = getHeight(matrix) - 1; i >= 0; i--) {
            int[] row = newRows.pollLast();
            if (row != null) {
                tmp[i] = row;
            } else {
                break;
            }
        }
        int scoreBonus = 50 * clearedRows.size() * clearedRows.size();
        return new ClearRow(clearedRows.size(), tmp, scoreBonus);
    }

    /**
     * Creates a deep copy of a list of matrices.
     *
     * @param list list of matrices to copy
     * @return a new list with deep-copied matrices
     */
    public static List<int[][]> deepCopyList(List<int[][]> list){
        return list.stream().map(MatrixOperations::copy).toList(); // sonarqube fix
    }

    private static int getWidth(int[][] matrix) {
        return matrix.length == 0 ? 0 : matrix[0].length;
    }

    private static int getHeight(int[][] matrix) {
        return matrix.length;
    }

    private static int brickCell(final int[][] brick, int i, int j) {
        return brick[j][i];
    }

    // methods have been migrated to this function
    // simiplifed the nested loops ever further to make code easier to read
    // used guard calsses to prevent index swapping ccomplications
    // simplifed matrix.length and matrix[0].length to getheight and getwidth
    private static boolean cellCollides(final int[][] matrix, int cell, int targetX, int targetY) {

        if (cell == 0)
        {
            return false;
        }

        if (targetY < 0)
        {
            return offscreenCollision(matrix, targetX);
        }

        if (checkOutOfBound(matrix, targetX, targetY))
        {
            return true;
        }

        return matrix[targetY][targetX] != 0;
    }

    private static boolean offscreenCollision(final int[][] matrix, int targetX) {
        return targetX < 0 || targetX >= getWidth(matrix) || matrix[0][targetX] != 0;
    }

}

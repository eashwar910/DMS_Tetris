package com.comp2042.logic.workflow;

import com.comp2042.core.MatrixOperations;

/**
 * Encapsulates the result of clearing rows: lines removed, updated matrix,
 * and the computed score bonus.
 *
 * @author Eashwar
 * @version 1.0
 */
public final class ClearRow {

    private final int linesRemoved;
    private final int[][] newMatrix;
    private final int scoreBonus;

    /**
     * Creates a clear-row summary.
     *
     * @param linesRemoved number of lines cleared
     * @param newMatrix the updated background matrix
     * @param scoreBonus bonus score awarded for the clear
     */
    public ClearRow(int linesRemoved, int[][] newMatrix, int scoreBonus) {
        this.linesRemoved = linesRemoved;
        this.newMatrix = newMatrix;
        this.scoreBonus = scoreBonus;
    }

    public int getLinesRemoved() {
        return linesRemoved;
    }

    public int[][] getNewMatrix() {
        return MatrixOperations.copy(newMatrix);
    }

    public int getScoreBonus() {
        return scoreBonus;
    }
}

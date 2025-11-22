package com.comp2042;

public final class ViewData {

    private final int[][] brickData;
    private final int xPosition;
    private final int yPosition;
    private final int[][][] nextBrickData;  //updated to hold 3 next bricks
    private final int[][] holdBrickData; // only brick can be help and swapped so 2d array

    public ViewData(int[][] brickData, int xPosition, int yPosition, int[][][] nextBrickData, int[][] holdBrickData) {
        this.brickData = brickData;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.nextBrickData = nextBrickData;
        this.holdBrickData = holdBrickData;
    }

    public int[][] getBrickData() {
        return MatrixOperations.copy(brickData);
    }

    public int getxPosition() {
        return xPosition;
    }

    public int getyPosition() {
        return yPosition;
    }

    // changed the method to get next brick data
    // updated it from holding the structure of the next brick as a 2d array
    // to holding a queue of the next 3 next bricks as 2 arrays
    public int[][][] getNextBrickData() {

        int[][][] copy = new int[nextBrickData.length][][];
        for (int i = 0; i < nextBrickData.length; i++) {
            copy[i] = MatrixOperations.copy(nextBrickData[i]);
        }
        return copy;

    }

    public int[][] getHoldBrickData() {

        return MatrixOperations.copy(holdBrickData);
    }
}

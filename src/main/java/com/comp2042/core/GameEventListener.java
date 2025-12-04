package com.comp2042.core;

import com.comp2042.input.InputEventListener;
import com.comp2042.logic.workflow.ViewData;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;

public interface GameEventListener {
    void initGameView(int[][] boardMatrix, ViewData brick);

    void refreshGameBackground(int[][] board);

    void setEventListener(InputEventListener eventListener);

    void bindScore(IntegerProperty integerProperty);

    void bindHighScore(IntegerProperty integerProperty);

    void bindLinesCleared(IntegerProperty integerProperty);

    void bindLevelValue(IntegerProperty integerProperty);

    BooleanProperty getIsGameOver();

    void bindLevel(IntegerProperty integerProperty);

    // pulse method
    void pulseLandedBlocks(int[][] brickShape, int xPosition, int yPosition);

    void gameOver(boolean newHighScore);
}

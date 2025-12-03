package com.comp2042.core;

import com.comp2042.logic.workflow.ClearRow;
import com.comp2042.logic.workflow.Score;
import com.comp2042.logic.workflow.ViewData;

public interface Board {

    boolean moveBrickDown();

    boolean moveBrickLeft();

    boolean moveBrickRight();

    boolean rotateLeftBrick();

    boolean holdBrick();

    boolean createNewBrick();

    int[][] getBoardMatrix();

    ViewData getViewData();

    void mergeBrickToBackground();

    ClearRow clearRows();

    Score getScore();

    void newGame();

    void clearHold();
}

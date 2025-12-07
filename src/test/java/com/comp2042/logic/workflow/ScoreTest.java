package com.comp2042.logic.workflow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.comp2042.core.Constants;
import com.comp2042.core.GameModeHandler.GameMode;

class ScoreTest {

    // test for score property method
    @Test
    void scoreProperty() {

        // score should start at 0 and increase with add
        Score s = new Score();
        assertEquals(0, s.scoreProperty().get());
        s.add(5);
        assertEquals(5, s.scoreProperty().get());
    }

    // test for high score property method
    @Test
    void highScoreProperty() {

        // adding enough to exceed current high should set high equal to score
        Score s = new Score();
        s.reset();
        s.setMode(GameMode.NORMAL);
        int currentHigh = s.highScoreProperty().get();
        int inc = currentHigh + 1;
        s.add(inc);
        assertEquals(inc, s.scoreProperty().get());
        assertEquals(inc, s.highScoreProperty().get());
    }

    // test for level proeprty method
    @Test
    void levelProperty() {

        // level should start at 1 and increase per lines cleared
        Score s = new Score();
        assertEquals(1, s.levelProperty().get());
        s.addLines(Constants.LINES_PER_LEVEL);
        assertEquals(2, s.levelProperty().get());
    }

    // test for set game mode method
    @Test
    void setMode() {

        // switching modes should show per-mode high scores
        Score s = new Score();

        s.setMode(GameMode.NORMAL);
        s.reset();
        int normalStart = s.highScoreProperty().get();
        s.add(normalStart + 1);
        int highNormal = s.highScoreProperty().get();
        // should be equal to normal mode high score
        assertEquals(s.scoreProperty().get(), highNormal);

        s.setMode(GameMode.TIMED);
        int timedBefore = s.highScoreProperty().get();
        s.reset();
        s.add(timedBefore + 1);
        int highTimed = s.highScoreProperty().get();
        // should be equal to timed mode high score
        assertEquals(s.scoreProperty().get(), highTimed);

        s.setMode(GameMode.BOTTOMS_UP);
        int bottomsBefore = s.highScoreProperty().get();
        s.reset();
        s.add(bottomsBefore + 1);
        int highBottoms = s.highScoreProperty().get();
        // should be equal to bottoms up mode high score
        assertEquals(s.scoreProperty().get(), highBottoms);

        s.setMode(GameMode.NORMAL);
        assertEquals(highNormal, s.highScoreProperty().get());
    }

    // test for lines cleared proeprty method
    @Test
    void linesClearedProperty() {

        // lines cleared reflects total added lines
        Score s = new Score();
        assertEquals(0, s.linesClearedProperty().get());
        s.addLines(3);
        assertEquals(3, s.linesClearedProperty().get());
        s.addLines(2);
        assertEquals(5, s.linesClearedProperty().get());
    }

    // test for add method
    @Test
    void add() {

        // add increases score
        // high score updates only if exceeding current high
        Score s = new Score();
        s.reset();
        s.setMode(GameMode.NORMAL);
        int currentHigh = s.highScoreProperty().get();
        s.add(currentHigh + 1);
        assertEquals(currentHigh + 1, s.scoreProperty().get());
        assertEquals(currentHigh + 1, s.highScoreProperty().get());

        // add increases score again
        // if still exceeding, high updates accordingly
        s.add(25);
        assertEquals(currentHigh + 1 + 25, s.scoreProperty().get());
        assertEquals(currentHigh + 1 + 25, s.highScoreProperty().get());
    }

    // test for reset method
    @Test
    void reset() {

        // reset should zero score and lines and set level to 1
        Score s = new Score();
        s.add(42);
        s.addLines(7);
        s.reset();
        assertEquals(0, s.scoreProperty().get());
        assertEquals(0, s.linesClearedProperty().get());
        assertEquals(1, s.levelProperty().get());
    }

    // test for add lines method
    @Test
    void addLines() {

        // non-positive lines do nothing; positive lines increase level appropriately
        Score s = new Score();
        s.reset();
        s.addLines(0);
        assertEquals(0, s.linesClearedProperty().get());
        assertEquals(1, s.levelProperty().get());

        s.addLines(Constants.LINES_PER_LEVEL - 1);
        assertEquals(Constants.LINES_PER_LEVEL - 1, s.linesClearedProperty().get());
        assertEquals(1, s.levelProperty().get());

        s.addLines(1);
        assertEquals(Constants.LINES_PER_LEVEL, s.linesClearedProperty().get());
        assertEquals(2, s.levelProperty().get());
    }
}

package com.comp2042.logic.workflow;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.comp2042.core.Constants;
import com.comp2042.core.GameModeHandler.GameMode;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Tracks score, high scores per mode, lines cleared, and level progression.
 * Handles loading and saving high scores from a file.
 *
 * @author Eashwar
 * @version 1.0
 */
public final class Score {

    private final IntegerProperty score = new SimpleIntegerProperty(0);
    private final IntegerProperty highScore = new SimpleIntegerProperty(0);
    private final IntegerProperty linesCleared = new SimpleIntegerProperty(0);
    private final IntegerProperty level = new SimpleIntegerProperty(1);
    private GameMode mode = GameMode.NORMAL;
    private int highNormal = 0; // seperate high scores for each game mode
    private int highTimed = 0;
    private int highBottomsUp = 0;

    /**
     * Initializes score state and loads persisted high scores.
     */
    public Score() {
        loadHighScore();
    }

    public IntegerProperty scoreProperty() {
        return score;
    }

    public IntegerProperty highScoreProperty() {
        return highScore;
    }

    public IntegerProperty levelProperty() {
        return level;
    }

    // set mode definition (wrapper in other class - fix)
    /**
     * Sets the active game mode and updates the bound high score property.
     *
     * @param mode the selected game mode
     */
    public void setMode(GameMode mode) {
        this.mode = mode;
        if (mode == GameMode.NORMAL) {
            highScore.setValue(highNormal);
        } else if (mode == GameMode.TIMED) {
            highScore.setValue(highTimed);
        } else {
            highScore.setValue(highBottomsUp);
        }
    }

    public IntegerProperty linesClearedProperty() {
        return linesCleared;
    }

    /**
     * Adds points to the score and updates high scores per mode when exceeded.
     * Persists new high score values.
     *
     * @param i points to add
     */
    public void add(int i){

        score.setValue(score.getValue() + i);
        //compare and save high score
        if (score.getValue() > highScore.getValue())
        {
            highScore.setValue(score.getValue());
            if (mode == GameMode.NORMAL) {
                highNormal = highScore.get();
            } else if (mode == GameMode.TIMED) {
                highTimed = highScore.get();
            } else {
                highBottomsUp = highScore.get();
            }
            saveHighScore();
        }
    }

    /**
     * Resets score, lines cleared, and level to initial values.
     */
    public void reset() {
        score.setValue(0);
        linesCleared.setValue(0);
        level.setValue(1);
    }

    /**
     * Adds cleared lines, updating the level based on thresholds.
     *
     * @param lines number of lines cleared
     */
    public void addLines(int lines) {
        if (lines <= 0) return;
        int total = linesCleared.get() + lines;
        linesCleared.set(total);
        int newLevel = 1 + (total / Constants.LINES_PER_LEVEL);
        if (newLevel != level.get()) level.set(newLevel);
    }

    // load high score from the .txt file
    /**
     * Loads high score values from the configured file.
     */
    private void loadHighScore() {
        Path path = Paths.get(Constants.HIGHSCORE_FILE);
        if (Files.exists(path))
        {
            try
            {
                // loop to check game mode and assign high score accoridngly
                String s = Files.readString(path).trim();
                if (!s.isEmpty())
                {
                    if (s.contains("="))
                    {
                        String[] lines = s.split("\\r?\\n");
                        for (String line : lines)
                        {
                            String[] kv = line.split("=");
                            if (kv.length == 2)
                            {
                                String key = kv[0].trim();
                                int val = Integer.parseInt(kv[1].trim());
                                if ("normal".equalsIgnoreCase(key)) highNormal = val;
                                if ("timed".equalsIgnoreCase(key)) highTimed = val;
                                if ("bottomsUp".equalsIgnoreCase(key) || "bottoms_up".equalsIgnoreCase(key)) highBottomsUp = val;
                            }
                        }
                    }
                    else
                    {
                        int val = Integer.parseInt(s);
                        highNormal = val;
                    }
                    highScore.setValue(highNormal);
                }
            }
            catch (Exception ignored) {}
        }
    }

    // write new high score into the .txt file
    /**
     * Persists high score values to the configured file.
     */
    private void saveHighScore() {
        Path path = Paths.get(Constants.HIGHSCORE_FILE);
        try
        {
            String content = "normal=" + highNormal + "\n" + "timed=" + highTimed + "\n" + "bottomsUp=" + highBottomsUp + "\n";
            Files.writeString(path, content);
        } catch (IOException ignored) {}
    }
}

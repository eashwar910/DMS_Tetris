package com.comp2042.logic.workflow;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.comp2042.core.Constants;
import com.comp2042.core.GameModeHandler.GameMode;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public final class Score {

    private final IntegerProperty score = new SimpleIntegerProperty(0);
    private final IntegerProperty highScore = new SimpleIntegerProperty(0);
    private final IntegerProperty linesCleared = new SimpleIntegerProperty(0);
    private final IntegerProperty level = new SimpleIntegerProperty(1);
    private GameMode mode = GameMode.NORMAL;
    private int highNormal = 0; // seperate high scores for each game mode
    private int highTimed = 0;

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
    public void setMode(GameMode mode) {
        this.mode = mode;
        if (mode == GameMode.NORMAL)
        {
            highScore.setValue(highNormal);
        }
        else
        {
            highScore.setValue(highTimed);
        }
    }

    public IntegerProperty linesClearedProperty() {
        return linesCleared;
    }

    public void add(int i){

        score.setValue(score.getValue() + i);
        //compare and save high score
        if (score.getValue() > highScore.getValue())
        {
            highScore.setValue(score.getValue());
            if (mode == GameMode.NORMAL)
            {
                highNormal = highScore.get();
            }
            else
            {
                highTimed = highScore.get();
            }
            saveHighScore();
        }
    }

    public void reset() {
        score.setValue(0);
        linesCleared.setValue(0);
        level.setValue(1);
    }

    public void addLines(int lines) {
        if (lines <= 0) return;
        int total = linesCleared.get() + lines;
        linesCleared.set(total);
        int newLevel = 1 + (total / Constants.LINES_PER_LEVEL);
        if (newLevel != level.get()) level.set(newLevel);
    }

    // load high score from the .txt file
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
    private void saveHighScore() {
        Path path = Paths.get(Constants.HIGHSCORE_FILE);
        try
        {
            String content = "normal=" + highNormal + "\n" + "timed=" + highTimed + "\n";
            Files.writeString(path, content);
        } catch (IOException ignored) {}
    }
}

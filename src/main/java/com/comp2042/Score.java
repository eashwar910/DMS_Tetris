package com.comp2042;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class Score {

    private final IntegerProperty score = new SimpleIntegerProperty(0);
    private final IntegerProperty highScore = new SimpleIntegerProperty(0);

    public Score() {
        loadHighScore();
    }

    public IntegerProperty scoreProperty() {
        return score;
    }

    public IntegerProperty highScoreProperty() {
        return highScore;
    }

    public void add(int i){

        score.setValue(score.getValue() + i);
        //compare and save high score
        if (score.getValue() > highScore.getValue())
        {
            highScore.setValue(score.getValue());
            saveHighScore();
        }
    }

    public void reset() {
        score.setValue(0);
    }

    // load high score from the .txt file
    private void loadHighScore() {
        Path path = Paths.get(Constants.HIGHSCORE_FILE);
        if (Files.exists(path))
        {
            try
            {
                String s = Files.readString(path).trim();
                if (!s.isEmpty())
                {
                    int val = Integer.parseInt(s);
                    highScore.setValue(val);
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
            Files.writeString(path, Integer.toString(highScore.getValue()));
        } catch (IOException ignored) {}
    }
}

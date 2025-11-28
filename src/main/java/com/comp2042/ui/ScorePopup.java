package com.comp2042.ui;

import com.comp2042.core.Constants;
import javafx.animation.ParallelTransition;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.Effect;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

public class ScorePopup extends BorderPane {

    public ScorePopup(String text) {
        setMinHeight(200);
        setMinWidth(220);
        final Label score = new Label(text);
        score.getStyleClass().add("bonusStyle");
        final Effect glow = Effects.glow(Constants.GLOW_LEVEL_SCORE);
        score.setEffect(glow);
        score.setTextFill(Color.WHITE);
        setCenter(score);

    }

    public void showScore(ObservableList<Node> list) {
        ParallelTransition transition = Effects.scorePopupTransition(this); // use the function defined in effects.java
        transition.setOnFinished(e -> list.remove(ScorePopup.this));
        transition.play();
    }

    // added method for combo popups
    public static void showForClearRow(ObservableList<Node> list, int scoreBonus, int linesRemoved) {
        ScorePopup bonusPanel = new ScorePopup("+" + scoreBonus);
        list.add(bonusPanel);
        bonusPanel.showScore(list);

        if (linesRemoved >= 2) {
            String label;
            if (linesRemoved == 2) label = "DOUBLE";
            else if (linesRemoved == 3) label = "TRIPLE";
            else if (linesRemoved == 4) label = "QUAD";
            else label = "TETRIS";
            ScorePopup comboPanel = new ScorePopup(label);
            comboPanel.setLayoutY(bonusPanel.getLayoutY() + 26);
            list.add(comboPanel);
            comboPanel.showScore(list);
        }
    }
}

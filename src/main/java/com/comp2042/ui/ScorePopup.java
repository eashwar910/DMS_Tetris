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
}

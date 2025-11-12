package com.comp2042;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

public class PauseScreen extends BorderPane {

    public PauseScreen() {
        final Label pauseLabel = new Label("PAUSED");
        pauseLabel.getStyleClass().add("gameOverStyle");
        setCenter(pauseLabel);
    }

}

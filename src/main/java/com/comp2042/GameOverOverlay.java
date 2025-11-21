package com.comp2042;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.VBox;


public class GameOverOverlay extends BorderPane {

    private Button restartButton;
    private Button exitButton;
    private EventHandler<ActionEvent> restartEventHandler;
    private EventHandler<ActionEvent> exitEventHandler;

    public GameOverOverlay() {
        final Label gameOverLabel = new Label("GAME OVER");
        gameOverLabel.getStyleClass().add("gameOverStyle");

        // setting up the restart button on the game over panel
        restartButton = new Button("RESTART");
        restartButton.getStyleClass().add("ipad-dark-grey");
        restartButton.setPrefWidth(200);
        restartButton.setPrefHeight(50);
        restartButton.setOnAction(e -> {
            if (restartEventHandler != null)
            {
                restartEventHandler.handle(e);
            }
        });

        // setting up the exit button on the game over panel
        exitButton = new Button("EXIT");
        exitButton.getStyleClass().add("ipad-dark-grey");
        exitButton.setPrefWidth(200);
        exitButton.setPrefHeight(50);
        exitButton.setOnAction(e -> {
            if (exitEventHandler != null)
            {
                exitEventHandler.handle(e);
            }
        });

        // create Vbox to align the buttons with game over text
        VBox box = new VBox(20);
        box.setStyle("-fx-alignment: center;");
        box.getChildren().addAll(gameOverLabel, restartButton, exitButton);
        setCenter(box);

    }

    // set up the event handler methods
    public void setRestartEventHandler(EventHandler<ActionEvent> handler) {
        this.restartEventHandler = handler;
    }

    public void setExitEventHandler(EventHandler<ActionEvent> handler) {
        this.exitEventHandler = handler;
    }


}

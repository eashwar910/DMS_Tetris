package com.comp2042.ui;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.VBox;

/**
 * Game over overlay with restart and exit actions, supporting a special
 * display for new high scores.
 *
 * @author Eashwar
 * @version 1.0
 */
public class GameOverOverlay extends BorderPane {

    private final Label messageLabel;
    private final Button restartButton;
    private final Button exitButton;
    private EventHandler<ActionEvent> restartEventHandler;
    private EventHandler<ActionEvent> exitEventHandler;

    /**
     * Builds the overlay UI and wires button actions.
     */
    public GameOverOverlay() {
        messageLabel = new Label("GAME OVER");
        messageLabel.getStyleClass().add("gameOverStyle");

        // setting up the restart button on the game over panel
        restartButton = new Button("RESTART");
        restartButton.getStyleClass().add("ipad-dark-grey");
        restartButton.setPrefWidth(200);
        restartButton.setPrefHeight(50);
        restartButton.setOnAction(e -> { if (restartEventHandler != null) restartEventHandler.handle(e); });

        // setting up the exit button on the game over panel
        exitButton = new Button("EXIT");
        exitButton.getStyleClass().add("ipad-dark-grey");
        exitButton.setPrefWidth(200);
        exitButton.setPrefHeight(50);
        exitButton.setOnAction(e -> { if (exitEventHandler != null) exitEventHandler.handle(e); });

        // create Vbox to align the buttons with game over text
        VBox box = new VBox(20);
        box.setStyle("-fx-alignment: center;");
        box.getChildren().addAll(messageLabel, restartButton, exitButton);
        setCenter(box);

    }

    // set up the event handler methods
    /**
     * Registers handler for the restart button.
     *
     * @param handler action handler
     */
    public void setRestartEventHandler(EventHandler<ActionEvent> handler) {
        this.restartEventHandler = handler;
    }

    /**
     * Registers handler for the exit button.
     *
     * @param handler action handler
     */
    public void setExitEventHandler(EventHandler<ActionEvent> handler) {
        this.exitEventHandler = handler;
    }

    // two modes for the game over overlay
    /**
     * Configures overlay for a new high score.
     */
    public void setHighScoreMode() {
        messageLabel.setText("NEW HIGH SCORE !!");
        messageLabel.getStyleClass().remove("gameOverStyle");
        if (!messageLabel.getStyleClass().contains("newHighScoreStyle")) {
            messageLabel.getStyleClass().add("newHighScoreStyle");
        }
        restartButton.setText("NEW GAME");
        exitButton.setText("MAIN MENU");
    }

    /**
     * Configures overlay for a standard game over.
     */
    public void setDefaultMode() {
        messageLabel.setText("GAME OVER");
        messageLabel.getStyleClass().remove("newHighScoreStyle");
        if (!messageLabel.getStyleClass().contains("gameOverStyle")) {
            messageLabel.getStyleClass().add("gameOverStyle");
        }
        restartButton.setText("RESTART");
        exitButton.setText("EXIT");
    }


}

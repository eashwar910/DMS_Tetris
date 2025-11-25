package com.comp2042.ui;

import javafx.scene.layout.BorderPane;
import javafx.scene.control.Button;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.layout.VBox;


public class PauseOverlay extends BorderPane {

    private Button resumeButton;
    private Button quitButton;
    private Button newgameButton;
    private EventHandler<ActionEvent> resumeHandler;
    private EventHandler<ActionEvent> quitHandler;
    private EventHandler<ActionEvent> newgameHandler;


    public PauseOverlay() {

        // Creating the Pause Menu Box (improvise further later)
        Rectangle background = new Rectangle();
        background.setFill(Color.rgb(0, 0, 0, 0.7));
        background.widthProperty().bind(this.widthProperty());
        background.heightProperty().bind(this.heightProperty());
        this.getChildren().add(0, background);

        // Creating Vbox for buttons to align them vertically
        VBox optionBox = new VBox(20);
        optionBox.setStyle("-fx-alignment: center;");


        // Creating the Resume button
        resumeButton = new Button("Resume Game");
        resumeButton.getStyleClass().add("ipad-dark-grey");
        resumeButton.setPrefWidth(200);
        resumeButton.setPrefHeight(50);
        resumeButton.setOnAction(e -> {
            if (resumeHandler != null)
            {
                resumeHandler.handle(e);
            }
        });

        // Creating the Quit button
        quitButton = new Button("Quit Game");
        quitButton.getStyleClass().add("ipad-dark-grey");
        quitButton.setPrefWidth(200);
        quitButton.setPrefHeight(50);
        quitButton.setOnAction(e -> {
            if (quitHandler != null)
            {
                quitHandler.handle(e);
            }
        });

        // Creating the New Game button
        newgameButton = new Button("New Game");
        newgameButton.getStyleClass().add("ipad-dark-grey");
        newgameButton.setPrefWidth(200);
        newgameButton.setPrefHeight(50);
        newgameButton.setOnAction(e -> {
            if (newgameHandler != null)
            {
                newgameHandler.handle(e);
            }
        });

        optionBox.getChildren().addAll(resumeButton, quitButton, newgameButton);
        this.setCenter(optionBox);
    }

    public void setResumeHandler(EventHandler<ActionEvent> handler) {
        this.resumeHandler = handler;
    }

    public void setQuitHandler(EventHandler<ActionEvent> handler) {
        this.quitHandler = handler;
    }

    public void setNewGameHandler(EventHandler<ActionEvent> handler) {
        this.newgameHandler = handler;
    }

}

package com.comp2042;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class SceneManager {

    private SceneManager() {
        throw new IllegalStateException("Utility class");
    }

    public static void centerGameBoard(StackPane gameBoardContainer, BorderPane gameBoard,
                                       GridPane gamePanel, DoubleProperty gamePanelSceneX,
                                       DoubleProperty gamePanelSceneY) {

        // moved the entire binding algorithm to new class
        // bind to scene width and height if scene exists
        // if not bind to pane width and height
        Platform.runLater(() -> {
            if (gameBoardContainer != null && gameBoard != null)
            {
                Scene gamescene = gameBoardContainer.getScene();

                if (gamescene != null)
                {
                    bindLayout(gamescene.widthProperty(), gamescene.heightProperty(),
                            gameBoardContainer, gameBoard, gamePanel, gamePanelSceneX, gamePanelSceneY);
                }
                else if (gameBoardContainer.getParent() instanceof Pane parent)
                {
                    bindLayout(parent.widthProperty(), parent.heightProperty(),
                            gameBoardContainer, gameBoard, gamePanel, gamePanelSceneX, gamePanelSceneY);
                }
            }
        });
    }

    private static void bindLayout(javafx.beans.value.ObservableNumberValue widthSource,
                                   javafx.beans.value.ObservableNumberValue heightSource,
                                   StackPane container, BorderPane board, GridPane panel,
                                   DoubleProperty xProp, DoubleProperty yProp) {

        // moved the centering algorithm into new class
        // calculate scene width/height - border width.height and half it
        // to find out left over space and find centre point
        container.layoutXProperty().bind(
                javafx.beans.binding.Bindings.subtract(widthSource, board.widthProperty()).divide(2)
        );
        container.layoutYProperty().bind(
                javafx.beans.binding.Bindings.subtract(heightSource, board.heightProperty()).divide(2)
        );

        xProp.bind(container.layoutXProperty().add(panel.layoutXProperty()));
        yProp.bind(container.layoutYProperty().add(panel.layoutYProperty()));
    }
}
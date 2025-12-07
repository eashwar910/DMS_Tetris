package com.comp2042.app;

import java.net.URL;

import com.comp2042.core.Constants;
import com.comp2042.core.GameController;
import com.comp2042.ui.GuiController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Entry point for the application.
 * Initializes the JavaFX UI by loading the FXML layout, configures the
 * primary stage, and connects the GUI to game controllers.
 *
 * @author Eashwar
 * @version 1.0
 */
public class Main extends Application {

    /**
     * Initializes and displays the primary application window.
     * Loads the FXML layout, obtains the GUI controller, sets up the scene,
     * and starts the game controller.
     *
     * @param primaryStage the primary JavaFX stage provided by the runtime
     * @throws Exception if the FXML resource cannot be located or loaded
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        URL location = getClass().getClassLoader().getResource("gameLayout.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(location);
        Parent root = fxmlLoader.load();
        GuiController c = fxmlLoader.getController();

        primaryStage.setTitle("TetrisJFX");
        Scene scene = new Scene(root, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
        new GameController(c);
    }


    /**
     * Launches the JavaFX application.
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
        launch(args);
    }
}

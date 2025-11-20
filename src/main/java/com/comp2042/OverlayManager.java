package com.comp2042;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.layout.Region;

public final class OverlayManager {

    private final Region startOverlay;
    private final Region helpOverlay;
    private final Region pauseOverlay;
    private final Region gameOverOverlay;

    public OverlayManager(Region startOverlay, Region helpOverlay, Region pauseOverlay, Region gameOverOverlay) {

        this.startOverlay = startOverlay;
        this.helpOverlay = helpOverlay;
        this.pauseOverlay = pauseOverlay;
        this.gameOverOverlay = gameOverOverlay;

    }

    public void bindOverlayFill(Region overlay) {

        // transfer the scene high and width calculation methods to overlay manager
        // if scene exists, bind overlay to scene width and height
        // if secne doesnt exist, bind overlay to pane width and hight
        if (overlay == null) return;
        Platform.runLater(() -> {
            if (overlay.getScene() != null)
            {
                overlay.prefWidthProperty().bind(overlay.getScene().widthProperty());
                overlay.prefHeightProperty().bind(overlay.getScene().heightProperty());
            }

            else if (overlay.getParent() instanceof javafx.scene.layout.Pane)
            {
                javafx.scene.layout.Pane parent = (javafx.scene.layout.Pane) overlay.getParent();
                overlay.prefWidthProperty().bind(parent.widthProperty());
                overlay.prefHeightProperty().bind(parent.heightProperty());
            }

        });
    }

    // create shoe and hide methods for all overlays
    public void showStart() { show(startOverlay); }
    public void hideStart() { hide(startOverlay); }

    public void showHelp() { show(helpOverlay); }
    public void hideHelp() { hide(helpOverlay); }

    public void showPause() { show(pauseOverlay); }
    public void hidePause() { hide(pauseOverlay); }

    public void showGameOver() { show(gameOverOverlay); }
    public void hideGameOver() { hide(gameOverOverlay); }

    // create a method to bring overlays to the front of the screen
    public void toFront(Node overlay) {
        if (overlay != null) overlay.toFront();
    }

    // show and hide overlay method definiton
    private void show(Region overlay) {

        if (overlay != null)
        {
            overlay.setVisible(true);
            overlay.toFront();
        }

    }

    private void hide(Region overlay) {

        if (overlay != null)
        {
            overlay.setVisible(false);
        }

    }
}


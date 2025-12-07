package com.comp2042.input;

import com.comp2042.logic.workflow.DownData;
import com.comp2042.ui.GameRenderer;
import com.comp2042.ui.GuiController;
import com.comp2042.events.EventSource;
import com.comp2042.events.EventType;
import com.comp2042.events.MoveEvent;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Translates keyboard input into game events, delegating to the controller
 * and renderer for movement, rotation, holding, dropping, and overlay actions.
 *
 * @author Eashwar
 * @version 1.0
 */
public class KeyboardInputManager implements EventHandler<KeyEvent> {

    private final GuiController controller;
    private final GameRenderer renderer;

    /**
     * Creates a keyboard input manager bound to a controller and renderer.
     *
     * @param controller the GUI controller to dispatch actions to
     * @param renderer the renderer to update visual state
     */
    public KeyboardInputManager(GuiController controller, GameRenderer renderer) {
        this.controller = controller;
        this.renderer = renderer;
    }

    @Override
    /**
     * Handles key events, routing them to movement handlers and overlay actions.
     *
     * @param keyEvent the JavaFX key event
     */
    public void handle(KeyEvent keyEvent) {
        // used getter (keyboard wasnt working with normal event listener )
        InputEventListener eventListener = controller.getEventListener();

        // Only proceed if we have a listener and the game is active
        if (eventListener != null && controller.isPlaying()) {
            handleMovement(keyEvent, eventListener);
        }

        if (keyEvent.getCode() == KeyCode.N) {
            controller.getOverlayManager().newGame(null);
        }
        if (keyEvent.getCode() == KeyCode.ESCAPE) {
            controller.getOverlayManager().togglePause();
            keyEvent.consume();
        }
    }

    /**
     * Processes movement-related key inputs when the game is active.
     *
     * @param keyEvent the JavaFX key event
     * @param eventListener the listener to receive movement commands
     */
    private void handleMovement(KeyEvent keyEvent, InputEventListener eventListener) {
        if (keyEvent.getCode() == KeyCode.LEFT || keyEvent.getCode() == KeyCode.A) {
            com.comp2042.logic.workflow.ViewData vd = eventListener.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER));
            renderer.refreshBrick(vd);
            controller.updateGhost(vd);
            keyEvent.consume();
        }
        if (keyEvent.getCode() == KeyCode.RIGHT || keyEvent.getCode() == KeyCode.D) {
            com.comp2042.logic.workflow.ViewData vd = eventListener.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER));
            renderer.refreshBrick(vd);
            controller.updateGhost(vd);
            keyEvent.consume();
        }
        if (keyEvent.getCode() == KeyCode.UP || keyEvent.getCode() == KeyCode.W) {
            com.comp2042.logic.workflow.ViewData vd = eventListener.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.USER));
            renderer.refreshBrick(vd);
            controller.updateGhost(vd);
            keyEvent.consume();
        }
        if (keyEvent.getCode() == KeyCode.DOWN || keyEvent.getCode() == KeyCode.S) {
            controller.moveDown(new MoveEvent(EventType.DOWN, EventSource.USER));
            keyEvent.consume();
        }
        if (keyEvent.getCode() == KeyCode.C) {
            com.comp2042.logic.workflow.ViewData vd = eventListener.onHoldEvent(new MoveEvent(EventType.HOLD, EventSource.USER));
            renderer.refreshBrick(vd);
            controller.updateGhost(vd);
            keyEvent.consume();
        }
        if (keyEvent.getCode() == KeyCode.SPACE) {
            DownData downData = eventListener.onHardDropEvent(new MoveEvent(EventType.HARD_DROP, EventSource.USER));
            renderer.refreshBrick(downData.getViewData());
            controller.showScorePopup(downData.getClearRow());
            controller.updateGhost(downData.getViewData());
            keyEvent.consume();
        }
    }
}

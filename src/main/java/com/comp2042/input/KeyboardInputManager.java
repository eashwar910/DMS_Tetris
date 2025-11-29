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

public class KeyboardInputManager implements EventHandler<KeyEvent> {

    private final GuiController controller;
    private final GameRenderer renderer;

    public KeyboardInputManager(GuiController controller, GameRenderer renderer) {
        this.controller = controller;
        this.renderer = renderer;
    }

    @Override
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
            controller.togglePause();
            keyEvent.consume();
        }
    }

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

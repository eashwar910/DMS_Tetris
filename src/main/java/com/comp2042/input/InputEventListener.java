package com.comp2042.input;

import com.comp2042.logic.workflow.DownData;
import com.comp2042.logic.workflow.ViewData;
import com.comp2042.events.MoveEvent;

public interface InputEventListener {

    DownData onDownEvent(MoveEvent event);

    ViewData onLeftEvent(MoveEvent event);

    ViewData onRightEvent(MoveEvent event);

    ViewData onRotateEvent(MoveEvent event);

    ViewData onHoldEvent(MoveEvent event);

    DownData onHardDropEvent(MoveEvent event);

    void createNewGame();
}

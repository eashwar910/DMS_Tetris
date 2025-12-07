package com.comp2042.events;

/**
 * Immutable move event carrying the type and source of an action.
 *
 * @author Eashwar
 * @version 1.0
 */
public final class MoveEvent {
    private final EventType eventType;
    private final EventSource eventSource;

    /**
     * Creates a move event with type and source metadata.
     *
     * @param eventType the event type
     * @param eventSource the origin of the event
     */
    public MoveEvent(EventType eventType, EventSource eventSource) {
        this.eventType = eventType;
        this.eventSource = eventSource;
    }

    public EventType getEventType() {
        return eventType;
    }

    public EventSource getEventSource() {
        return eventSource;
    }
}

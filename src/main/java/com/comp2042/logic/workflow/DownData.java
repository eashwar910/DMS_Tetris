package com.comp2042.logic.workflow;

/**
 * Aggregates data resulting from a down movement, including any cleared rows
 * and the current view state.
 *
 * @author Eashwar
 * @version 1.0
 */
public final class DownData {
    private final ClearRow clearRow;
    private final ViewData viewData;

    /**
     * Constructs a down-data payload.
     *
     * @param clearRow summary of rows cleared (nullable)
     * @param viewData current view data
     */
    public DownData(ClearRow clearRow, ViewData viewData) {
        this.clearRow = clearRow;
        this.viewData = viewData;
    }

    public ClearRow getClearRow() {
        return clearRow;
    }

    public ViewData getViewData() {
        return viewData;
    }
}

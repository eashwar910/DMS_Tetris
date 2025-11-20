// src/main/java/com/comp2042/Constants.java
package com.comp2042;

public final class Constants {
    
    //GuiController.java
    public static final int BRICK_SIZE = 20;
    public static final double FALL_INTERVAL_MS = 400.0;
    public static final int BRICK_ARC = 9;
    public static final double REFLECTION_FRACTION = 0.28;
    public static final double REFLECTION_TOP_OPACITY = 0.35;
    public static final double REFLECTION_TOP_OFFSET = 10.0;
    public static final String FONT_DIGITAL = "digital.ttf";
    public static final double FONT_SIZE = 38.0;

    //GameController.java
    public static final int BOARD_ROWS = 20;
    public static final int BOARD_COLS = 10;

    //SimpleBoard.java
    public static final int BRICK_SPAWN_X = 3;
    public static final int BRICK_SPAWN_Y = -1;
    public static final int PREVIEW_COUNT = 3;

    // Used in Main.java
    public static final int WINDOW_WIDTH = 670;
    public static final int WINDOW_HEIGHT = 550;

    private Constants() {}
}
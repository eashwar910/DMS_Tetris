// src/main/java/com/comp2042/Constants.java
package com.comp2042.core;

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
    public static final int LINES_PER_LEVEL = 10;
    public static final double BASE_TIME = 0.9;
    public static final double TIME_DECREMENT = 0.007;
    public static final double MIN_FALL_INTERVAL_MS = 50.0;

    //GameController.java
    public static final int BOARD_ROWS = 20;
    public static final int BOARD_COLS = 10;

    //GameBoard.java
    public static final int BRICK_SPAWN_X = 3;
    public static final int BRICK_SPAWN_Y = -3;
    public static final int PREVIEW_COUNT = 3;
    public static final String HIGHSCORE_FILE = "highscore.txt";

    //in Main.java
    public static final int WINDOW_WIDTH = 670;
    public static final int WINDOW_HEIGHT = 550;

    // Effects.java
    public static final double SCORE_FADE_MS = 2000.0;
    public static final double SCORE_TRANSLATE_MS = 2500.0;
    public static final double SCORE_TRANSLATE_DELTA_Y = -40.0;
    public static final double GLOW_LEVEL_SCORE = 0.6;
    public static final double PULSE_DURATION_MS = 300.0;
    public static final double PULSE_SCALE = 0.8;

    private Constants() {}
}
// src/main/java/com/comp2042/Effects.java
package com.comp2042.ui;

import com.comp2042.core.Constants;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Glow;
import javafx.scene.effect.Reflection;
import javafx.util.Duration;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;

public final class Effects {
    public static Reflection createBoardReflection() {
        Reflection r = new Reflection();
        r.setFraction(Constants.REFLECTION_FRACTION);
        r.setTopOpacity(Constants.REFLECTION_TOP_OPACITY);
        r.setTopOffset(Constants.REFLECTION_TOP_OFFSET);
        return r;
    }

    public static Effect glow(double level) {
        return new Glow(level);
    }

    public static ParallelTransition scorePopupTransition(Node node) {
        FadeTransition ft = new FadeTransition(Duration.millis(Constants.SCORE_FADE_MS), node);
        ft.setFromValue(1);
        ft.setToValue(0);
        TranslateTransition tt = new TranslateTransition(Duration.millis(Constants.SCORE_TRANSLATE_MS), node);
        tt.setToY(node.getLayoutY() + Constants.SCORE_TRANSLATE_DELTA_Y);
        return new ParallelTransition(tt, ft);
    }

    // pulsing effect for hard drop
    public static SequentialTransition createPulseEffect(Node node) {
        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(Constants.PULSE_DURATION_MS / 2), node);
        scaleOut.setFromX(1.0);
        scaleOut.setFromY(1.0);
        scaleOut.setToX(Constants.PULSE_SCALE);
        scaleOut.setToY(Constants.PULSE_SCALE);

        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(Constants.PULSE_DURATION_MS / 2), node);
        scaleIn.setFromX(Constants.PULSE_SCALE);
        scaleIn.setFromY(Constants.PULSE_SCALE);
        scaleIn.setToX(1.0);
        scaleIn.setToY(1.0);

        return new SequentialTransition(scaleOut, scaleIn);
    }
}
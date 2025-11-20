// src/main/java/com/comp2042/Effects.java
package com.comp2042;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Glow;
import javafx.scene.effect.Reflection;
import javafx.util.Duration;

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
}
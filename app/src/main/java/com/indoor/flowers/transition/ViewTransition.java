package com.indoor.flowers.transition;

import android.transition.Transition;
import android.transition.TransitionValues;
import android.view.View;

import org.jetbrains.annotations.NotNull;

public class ViewTransition extends Transition {

    private static final int DEFAULT_DURATION = 1000;
    private static final String VIEW_KEY = "viewTransition:view";

    public ViewTransition() {
        setDuration(DEFAULT_DURATION);
    }

    @Override
    public void captureStartValues(@NotNull TransitionValues transitionValues) {
        transitionValues.values.put(VIEW_KEY, transitionValues.view);
    }

    @Override
    public void captureEndValues(@NotNull TransitionValues transitionValues) {
        transitionValues.values.put(VIEW_KEY, transitionValues.view);
    }

    protected View getView(TransitionValues values) {
        return values != null ? (View) values.values.get(VIEW_KEY) : null;
    }
}
package com.indoor.flowers.util.behavior;

import android.animation.FloatEvaluator;
import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.evgeniysharafan.utils.Utils;

public class BackgroundLayersCollapseBehavior extends CoordinatorLayout.Behavior<ViewGroup> {

    private static final float CHILD_FRACTION_DELAY = 3f;

    private FloatEvaluator floatEvaluator = new FloatEvaluator();

    public BackgroundLayersCollapseBehavior() {
        super();
    }

    public BackgroundLayersCollapseBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, ViewGroup child, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, ViewGroup child, View dependency) {
        if (Utils.getApp() == null || !(dependency instanceof AppBarLayout)) {
            return false;
        }

        AppBarLayout appBarLayout = (AppBarLayout) dependency;
        int maxScrollHeight = appBarLayout.getTotalScrollRange();
        float fraction = Math.abs(appBarLayout.getY()) / maxScrollHeight;

        for (int i = 0; i < child.getChildCount(); i++) {
            View view = child.getChildAt(i);
            float childFraction = i != 0 ? fraction / (i * CHILD_FRACTION_DELAY) : fraction;
            view.setTranslationX(floatEvaluator.evaluate(childFraction, 0,
                    -view.getMeasuredWidth() - view.getMeasuredWidth() * i));
            view.setTranslationY(floatEvaluator.evaluate(childFraction, 0,
                    -view.getMeasuredHeight() - view.getMeasuredHeight() * i));
        }

        return true;
    }
}

package com.indoor.flowers.util.behavior;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

import com.evgeniysharafan.utils.Utils;

public class HideOnScrollBehavior extends CoordinatorLayout.Behavior<View> {

    public HideOnScrollBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        if (Utils.getApp() == null || !(dependency instanceof AppBarLayout)) {
            return false;
        }

        AppBarLayout appBarLayout = (AppBarLayout) dependency;
        int maxScrollHeight = appBarLayout.getTotalScrollRange();
        float fraction = Math.abs(appBarLayout.getY()) / maxScrollHeight;
        child.setAlpha(1 - fraction);
        return true;
    }
}

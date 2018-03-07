package com.indoor.flowers.util.behavior;

import android.animation.RectEvaluator;
import android.content.Context;
import android.graphics.Rect;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.evgeniysharafan.utils.Res;
import com.evgeniysharafan.utils.Utils;
import com.indoor.flowers.R;

public class AvatarToolbarBehavior extends CoordinatorLayout.Behavior<ImageView> {

    private Toolbar toolbarView;

    private boolean isOriginalDataSet = false;

    private Rect startAvatarRect = new Rect();
    private Rect resultAvatarRect = new Rect();

    private RectEvaluator rectEvaluator = new RectEvaluator(new Rect());

    public AvatarToolbarBehavior() {
        super();
    }

    public AvatarToolbarBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, ImageView child, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, ImageView child, View dependency) {
        if (Utils.getApp() == null || !(dependency instanceof AppBarLayout)) {
            return false;
        }

        AppBarLayout appBarLayout = (AppBarLayout) dependency;
        if (!isOriginalDataSet) {
            setOriginalData(child, appBarLayout);
        }


        int maxScrollHeight = appBarLayout.getTotalScrollRange();
        float fraction = Math.abs(appBarLayout.getY()) / maxScrollHeight;
        Rect currentRect = rectEvaluator.evaluate(fraction, startAvatarRect, resultAvatarRect);
        refreshAvatarState(child, currentRect);
        return true;
    }

    private void refreshAvatarState(ImageView child, Rect currentRect) {
        child.setScaleX(currentRect.width() / (float) startAvatarRect.width());
        child.setScaleY(currentRect.height() / (float) startAvatarRect.height());
        child.setX(currentRect.left);
        child.setY(currentRect.top);
    }

    private void setOriginalData(ImageView child, AppBarLayout appBarLayout) {
        toolbarView = appBarLayout.findViewWithTag("Toolbar");
        setupAvatarData(child);
        isOriginalDataSet = true;
    }

    private void setupAvatarData(ImageView child) {
        startAvatarRect = new Rect(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
        startAvatarRect.offsetTo((int) child.getX(), (int) child.getY());

        resultAvatarRect = new Rect(startAvatarRect);
        if (toolbarView != null) {
            int margin = Res.getDimensionPixelSize(R.dimen.margin_small);
            float resultHeight = toolbarView.getMeasuredHeight() - margin * 2;
            float resultWidth = (resultHeight * startAvatarRect.width()) / startAvatarRect.height();

            resultAvatarRect.set(0, 0, (int) resultWidth, (int) resultHeight);
            resultAvatarRect.offset((int) ((resultAvatarRect.width() - startAvatarRect.width()) / 2f),
                    (int) ((resultAvatarRect.height() - startAvatarRect.height()) / 2f));
            resultAvatarRect.offset(margin, margin);

            resultAvatarRect.offset(Res.getDimensionPixelSize(R.dimen.toolbar_padding), 0);
        }
    }
}

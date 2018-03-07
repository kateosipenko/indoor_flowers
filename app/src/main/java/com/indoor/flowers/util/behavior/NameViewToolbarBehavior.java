package com.indoor.flowers.util.behavior;

import android.animation.FloatEvaluator;
import android.animation.RectEvaluator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.evgeniysharafan.utils.Res;
import com.evgeniysharafan.utils.Utils;
import com.indoor.flowers.R;
import com.indoor.flowers.view.NameView;

public class NameViewToolbarBehavior extends CoordinatorLayout.Behavior<NameView> {

    private static final int[] ATTRIBUTES = new int[]{
            R.attr.collapsedMarginStart
    };

    private boolean isOriginalDataSet = false;

    private Rect startNameRect = new Rect();
    private Rect resultNameRect = new Rect();

    private float originalTextSize = 0f;
    private float resultTextSize = 0f;

    private int collapsedMarginStart;

    private FloatEvaluator floatEvaluator = new FloatEvaluator();
    private RectEvaluator rectEvaluator = new RectEvaluator(new Rect());

    public NameViewToolbarBehavior() {
        super();
        init(null, null);
    }

    public NameViewToolbarBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, NameView child, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, NameView child, View dependency) {
        if (Utils.getApp() == null || !(dependency instanceof AppBarLayout)) {
            return false;
        }

        AppBarLayout appBarLayout = (AppBarLayout) dependency;
        if (!isOriginalDataSet) {
            setOriginalData(child);
        }

        int maxScrollHeight = appBarLayout.getTotalScrollRange();
        float fraction = Math.abs(appBarLayout.getY()) / maxScrollHeight;
        Rect currentRect = rectEvaluator.evaluate(fraction, startNameRect, resultNameRect);
        child.setX(currentRect.left);
        child.setTranslationY(currentRect.top);
        child.setTextSize(TypedValue.COMPLEX_UNIT_FRACTION, floatEvaluator.evaluate(fraction, originalTextSize, resultTextSize));

        if (fraction == 1f) {
            child.setTextColor(Color.WHITE);
        } else {
            child.setTextColor(Color.BLACK);
        }

        int alpha = (int) ((1 - fraction) * 255);
        child.getBackground().setAlpha(alpha);
        Drawable[] drawables = child.getCompoundDrawables();
        for (Drawable drawable : drawables) {
            if (drawable != null) {
                drawable.setAlpha(alpha);
            }
        }

        return true;
    }

    private void setOriginalData(final NameView child) {
        int margin = Res.getDimensionPixelSize(R.dimen.margin_small);
        startNameRect.set((int) child.getX(), (int) child.getY(), child.getMeasuredWidth(), child.getMeasuredHeight());
        resultNameRect.set(startNameRect);
        resultNameRect.offsetTo(collapsedMarginStart + margin * 2, margin);

        originalTextSize = child.getTextSize();
        resultTextSize = Res.getDimensionPixelSize(R.dimen.text_body_big);

        ViewCompat.setElevation(child, 15f);
        child.setOutlineProvider(null);

        clearNameViewParams(child);
        child.setAlpha(0f);
        child.post(new Runnable() {
            @Override
            public void run() {
                child.setX(startNameRect.left);
                child.setY(startNameRect.top);
                child.setAlpha(1f);
            }
        });
        isOriginalDataSet = true;
    }

    private void clearNameViewParams(NameView nameView) {
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) nameView.getLayoutParams();
        params.setAnchorId(View.NO_ID);
        params.anchorGravity = 0;
        params.gravity = 0;
        params.topMargin = 0;
        nameView.setLayoutParams(params);
    }

    private void init(Context context, AttributeSet attributeSet) {
        if (attributeSet != null && context != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attributeSet, ATTRIBUTES);
            if (typedArray.hasValue(0)) {
                collapsedMarginStart = typedArray.getDimensionPixelSize(0, 0);
            }

            typedArray.recycle();
        }
    }
}

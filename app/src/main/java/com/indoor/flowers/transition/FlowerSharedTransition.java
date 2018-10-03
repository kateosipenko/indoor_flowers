package com.indoor.flowers.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Rect;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.transition.TransitionValues;
import android.view.View;
import android.view.ViewGroup;

import com.indoor.flowers.R;

public class FlowerSharedTransition extends ViewTransition {

    private boolean isReverse = false;

    public FlowerSharedTransition(boolean isReverse) {
        this.isReverse = isReverse;
    }

    @Nullable
    @Override
    public Animator createAnimator(@NonNull ViewGroup sceneRoot,
                                   @Nullable TransitionValues startValues,
                                   @Nullable TransitionValues endValues) {
        if (startValues == null && endValues == null) {
            return null;
        }

        View startRootView = isReverse ? getView(endValues) : getView(startValues);
        View endRootView = isReverse ? getView(startValues) : getView(endValues);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(createIconAnimator(sceneRoot, startRootView, endRootView),
                createHeaderAnimator(sceneRoot, endRootView));
        return animatorSet;
    }

    private Animator createHeaderAnimator(ViewGroup sceneRoot, View endRootView) {
        AnimatorSet headerAnimator = new AnimatorSet();
        headerAnimator.playTogether(createHeaderLayerAnimator(endRootView, R.id.ffp_back_layer_one, 0),
                createHeaderLayerAnimator(endRootView, R.id.ffp_back_layer_two, (int) (getDuration() * 0.2f)),
                createHeaderLayerAnimator(endRootView, R.id.ffp_back_layer_three, (int) (getDuration() * 0.4f)));
        return headerAnimator;
    }

    private Animator createHeaderLayerAnimator(View rootView, @IdRes int id, int delay) {
        View layerView = rootView.findViewById(id);
        Animator result = null;
        if (layerView != null) {
            layerView.setTranslationY(-layerView.getMeasuredHeight());
            result = ObjectAnimator.ofFloat(layerView, View.TRANSLATION_Y,
                    layerView.getTranslationY(), 0);
            result.setDuration(getDuration() - delay);
            result.setStartDelay(delay);
        }

        return result;
    }


    private Animator createIconAnimator(final ViewGroup sceneRoot, View startRootView, View endRootView) {
        final View startIconView = startRootView.findViewById(R.id.rf_icon);
        final View endIconView = endRootView.findViewById(R.id.ffp_icon);

        AnimatorSet result = new AnimatorSet();
        if (startIconView != null && endIconView != null) {
            endIconView.setVisibility(View.GONE);
            Rect startLocation = getViewRect(startIconView);
            Rect endLocation = getViewRect(endIconView);

            startIconView.setX(startLocation.left);
            startIconView.setY(startLocation.top);
            sceneRoot.getOverlay().add(startIconView);

            ValueAnimator xAnimator = ObjectAnimator.ofFloat(startLocation.left, endLocation.left);
            xAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float) animation.getAnimatedValue();
                    startIconView.setX(value);
                }
            });

            ValueAnimator yAnimator = ObjectAnimator.ofFloat(startLocation.top, endLocation.top);
            yAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float) animation.getAnimatedValue();
                    startIconView.setY(value);
                }
            });

            float scale = endIconView.getMeasuredWidth() / (float) startIconView.getMeasuredWidth();
            result.playTogether(xAnimator, yAnimator,
                    ObjectAnimator.ofFloat(startIconView, View.SCALE_X, 1, scale),
                    ObjectAnimator.ofFloat(startIconView, View.SCALE_Y, 1, scale));
            result.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    sceneRoot.getOverlay().clear();
                    endIconView.setVisibility(View.VISIBLE);
                }
            });
        }

        return result;
    }

    private Rect getViewRect(View view) {
        Rect outRect = new Rect();
        view.getGlobalVisibleRect(outRect);
        return outRect;
    }
}
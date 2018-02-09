package com.indoor.flowers.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;

import com.evgeniysharafan.utils.Res;
import com.evgeniysharafan.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public final class AnimationUtils {

    public static ValueAnimator getBottomAppearAnimator(final View view, final int visibility) {
        ValueAnimator result = null;
        if (view != null && visibility != view.getVisibility()) {
            float from = visibility == View.VISIBLE ? view.getMeasuredHeight() : 0;
            float to = visibility == View.VISIBLE ? 0 : view.getMeasuredHeight();
            result = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, from, to)
                    .setDuration(300);
            result.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    if (view != null && visibility == View.VISIBLE) {
                        view.setVisibility(visibility);
                    }
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (view != null && visibility != View.VISIBLE) {
                        view.setVisibility(visibility);
                    }
                }
            });
        }

        return result;
    }

    /**
     * Create list of {@link ObjectAnimator} for translationX animation.
     *
     * @param gravity   Gravity for translation. Can be {@link Gravity#START} or {@link Gravity#END}
     * @param container View that contains target views as children.
     * @param targetIds Ids of views which need to animate
     */
    public static List<Animator> createHorizontalSlideAnimators(int gravity, View container, @IdRes int... targetIds) {
        return createHorizontalSlideAnimators(false, gravity, container, targetIds);
    }

    /**
     * Create list of {@link ObjectAnimator} for translationX animation.
     *
     * @param reverse   Need reverse animation
     * @param gravity   Gravity for translation. Can be {@link Gravity#START} or {@link Gravity#END}
     * @param container View that contains target views as children.
     * @param targetIds Ids of views which need to animate
     */
    public static List<Animator> createHorizontalSlideAnimators(boolean reverse, int gravity, View container, @IdRes int... targetIds) {
        List<Animator> result = new ArrayList<>();
        DisplayMetrics metrics = Res.getDisplayMetrics();
        int[] location = new int[2];
        for (int id : targetIds) {
            View view = container.findViewById(id);
            view.getLocationOnScreen(location);
            float start = gravity == Gravity.START ? location[0] - metrics.widthPixels : metrics.widthPixels - location[0];
            view.setTranslationX(start);
            ObjectAnimator animator = reverse ? ObjectAnimator.ofFloat(view, "translationX", 0, start) : ObjectAnimator.ofFloat(view, "translationX", start, 0);
            result.add(animator);
        }

        return result;
    }

    /**
     * Create list of {@link ObjectAnimator} for translationY animation.
     *
     * @param gravity   Gravity for translation. Can be {@link Gravity#TOP} or {@link Gravity#BOTTOM}
     * @param container View that contains target views as children.
     * @param targetIds Ids of views which need to animate
     */
    public static List<Animator> createVerticalSlideAnimators(int gravity, View container, @IdRes int... targetIds) {
        List<Animator> result = new ArrayList<>();
        DisplayMetrics metrics = Res.getDisplayMetrics();
        int[] location = new int[2];
        for (int id : targetIds) {
            View view = container.findViewById(id);
            view.getLocationOnScreen(location);
            float start = gravity == Gravity.TOP ? location[1] - metrics.heightPixels : metrics.heightPixels - location[1];
            view.setTranslationY(start);
            result.add(ObjectAnimator.ofFloat(view, "translationY", start, 0));
        }

        return result;
    }

    @SuppressLint("RtlHardcoded")
    public static int getGravityDirection(int gravity) {
        if (Utils.getApiVersion() <= Build.VERSION_CODES.LOLLIPOP && (gravity == Gravity.START || gravity == Gravity.END)) {
            gravity = gravity == Gravity.START ? Gravity.LEFT : Gravity.RIGHT;
        }

        return gravity;
    }

    /**
     * It doesn't support nested TransitionSets
     *
     * @param transition Transition or TransitionSet
     */
    public static void changeVisibilityToStartTransition(@NonNull Transition transition, View rootLayout) {
        List<View> views = new ArrayList<>();
        if (transition instanceof TransitionSet) {
            TransitionSet transitionSet = (TransitionSet) transition;

            for (int i = 0; i < transitionSet.getTransitionCount(); i++) {
                Transition t = transitionSet.getTransitionAt(i);

                List<Integer> targetIds = t.getTargetIds();
                if (targetIds != null && !targetIds.isEmpty()) {
                    for (Integer id : targetIds) {
                        views.add(rootLayout.findViewById(id));
                    }
                }

                if (t.getTargets() != null) {
                    views.addAll(t.getTargets());
                }
            }
        } else {
            views.addAll(transition.getTargets());
        }

        for (View view : views) {
            if (view != null && view.getVisibility() == View.VISIBLE) {
                view.setVisibility(View.INVISIBLE);
            }
        }
    }
}

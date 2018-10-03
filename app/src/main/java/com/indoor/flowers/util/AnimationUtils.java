package com.indoor.flowers.util;

import android.annotation.SuppressLint;
import android.os.Build;
import android.view.Gravity;

import com.evgeniysharafan.utils.Utils;

public class AnimationUtils {

    public static final int TRANSITION_DURATION = 500;
    public static final int TRANSITION_DELAY = 500;

    @SuppressLint("RtlHardcoded")
    public static int getGravityDirection(int gravity) {
        if (Utils.getApiVersion() <= Build.VERSION_CODES.LOLLIPOP && (gravity == Gravity.START || gravity == Gravity.END)) {
            gravity = gravity == Gravity.START ? Gravity.LEFT : Gravity.RIGHT;
        }

        return gravity;
    }
}

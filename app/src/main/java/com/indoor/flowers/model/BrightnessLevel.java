package com.indoor.flowers.model;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({BrightnessLevel.SMALL, BrightnessLevel.MEDIUM, BrightnessLevel.MAX})
@Retention(RetentionPolicy.SOURCE)
public @interface BrightnessLevel {
    int SMALL = 0;
    int MEDIUM = 1;
    int MAX = 2;
}

package com.indoor.flowers.model;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({HumidityLevel.NOT_SET, HumidityLevel.SMALL, HumidityLevel.MEDIUM, HumidityLevel.MAX})
@Retention(RetentionPolicy.SOURCE)
public @interface HumidityLevel {
    int NOT_SET = -1;
    int SMALL = 0;
    int MEDIUM = 1;
    int MAX = 2;
}

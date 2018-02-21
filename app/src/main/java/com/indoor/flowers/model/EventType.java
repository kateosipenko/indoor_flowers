package com.indoor.flowers.model;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({EventType.CREATED, EventType.WATERING, EventType.FERTILIZER,
        EventType.TRANSPLANTING})
@Retention(RetentionPolicy.SOURCE)
public @interface EventType {
    int CREATED = 0;
    int WATERING = 1;
    int FERTILIZER = 2;
    int TRANSPLANTING = 3;
}
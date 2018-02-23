package com.indoor.flowers.database;

import android.arch.persistence.room.TypeConverter;

import java.util.Calendar;

public class DbTypesConverter {
    @TypeConverter
    public static Long dateToLong(Calendar calendar) {
        return calendar != null ? calendar.getTimeInMillis() : null;
    }

    @TypeConverter
    public static Calendar longToDate(Long value) {
        Calendar result = null;
        if (value != null && value != 0) {
            result = Calendar.getInstance();
            result.setTimeInMillis(value);
        }

        return result;
    }
}

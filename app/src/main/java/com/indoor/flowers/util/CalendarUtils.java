package com.indoor.flowers.util;

import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class CalendarUtils {

    public static String getNameForMonth(int month) {
        String result = null;
        Calendar temp = Calendar.getInstance();
        temp.clear();
        if (month >= temp.getActualMinimum(Calendar.MONTH)
                && month <= temp.getActualMaximum(Calendar.MONTH)) {
            temp.set(Calendar.MONTH, month);
            result = String.format(Locale.getDefault(), "%1$tb", temp);
        }

        return result;
    }

    public static int getDaysDiff(Calendar start, Calendar end) {
        long startDays = TimeUnit.MILLISECONDS.toDays(start.getTimeInMillis());
        long endDays = TimeUnit.MILLISECONDS.toDays(end.getTimeInMillis());
        long diff = endDays - startDays;
        return (int) diff;
    }

    public static boolean isToday(Calendar calendar) {
        if (calendar == null)
            return false;

        Calendar current = Calendar.getInstance();
        return current.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
                && current.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR);
    }
}

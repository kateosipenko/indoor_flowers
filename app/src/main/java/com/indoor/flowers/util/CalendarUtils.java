package com.indoor.flowers.util;

import java.util.Calendar;
import java.util.Locale;

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
}

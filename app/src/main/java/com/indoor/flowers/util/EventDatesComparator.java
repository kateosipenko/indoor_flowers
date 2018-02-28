package com.indoor.flowers.util;

import com.indoor.flowers.model.NotificationWithTarget;

import java.util.Comparator;

public class EventDatesComparator implements Comparator<NotificationWithTarget> {
    @Override
    public int compare(NotificationWithTarget first, NotificationWithTarget second) {
        if (first == null || first.getEventDate() == null) {
            return -1;
        }

        if (second == null || second.getEventDate() == null) {
            return 1;
        }

        return first.getEventDate().compareTo(second.getEventDate());
    }
}

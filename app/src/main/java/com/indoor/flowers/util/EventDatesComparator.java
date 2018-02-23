package com.indoor.flowers.util;

import com.indoor.flowers.model.Notification;

import java.util.Comparator;

public class EventDatesComparator implements Comparator<Notification> {
    @Override
    public int compare(Notification first, Notification second) {
        if (first == null) {
            return -1;
        }

        if (second == null) {
            return 1;
        }

        return first.getDate().compareTo(second.getDate());
    }
}

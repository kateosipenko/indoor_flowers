package com.indoor.flowers.util;

import com.indoor.flowers.model.Event;

import java.util.Comparator;

public class EventDatesComparator implements Comparator<Event> {
    @Override
    public int compare(Event first, Event second) {
        if (first == null) {
            return -1;
        }

        if (second == null) {
            return 1;
        }

        return first.getEventDate().compareTo(second.getEventDate());
    }
}

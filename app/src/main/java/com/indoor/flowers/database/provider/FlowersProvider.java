package com.indoor.flowers.database.provider;

import android.content.Context;
import android.text.TextUtils;

import com.indoor.flowers.database.Columns;
import com.indoor.flowers.database.dao.EventDao;
import com.indoor.flowers.model.CalendarFilter;
import com.indoor.flowers.model.CalendarFilter.FilterElements;
import com.indoor.flowers.model.Event;
import com.indoor.flowers.model.EventType;
import com.indoor.flowers.model.EventWithTarget;
import com.indoor.flowers.model.Flower;
import com.indoor.flowers.model.Group;
import com.indoor.flowers.util.CalendarUtils;
import com.indoor.flowers.util.EventsUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class FlowersProvider extends DatabaseProvider {

    public FlowersProvider(Context context) {
        super(context);
    }

    // region GROUP

    public void deleteGroup(Group group) {
        database.getEventDao().deleteForTarget(group.getId(), Group.TABLE_NAME);
        database.getGroupDao().deleteFlowersForGroup(group.getId());
        database.getGroupDao().deleteGroup(group);
    }

    public void createOrUpdateGroup(Group group, List<Flower> flowers) {
        if (database.getGroupDao().hasGroup(group.getId())) {
            database.getGroupDao().update(group);
        } else {
            group.setId(invalidateIdForInsert(group.getId()));
            long id = database.getGroupDao().insert(group);
            group.setId(id);
            createEventForCreation(group.getId(), Group.TABLE_NAME, group.getName());
        }

        database.getGroupDao().updateGroupFlowers(group.getId(), flowers);
    }

    public List<Group> getAllGroups() {
        return database.getGroupDao().getAllGroups();
    }

    public Group getGroupById(long groupId) {
        return database.getGroupDao().getGroupById(groupId);
    }

    // endregion GROUP

    // region FLOWER

    public void createOrUpdateFlower(Flower flower) {
        if (database.getFlowersDao().hasFlower(flower.getId())) {
            database.getFlowersDao().update(flower);
        } else {
            flower.setId(invalidateIdForInsert(flower.getId()));
            long id = database.getFlowersDao().insert(flower);
            flower.setId(id);

            createEventForCreation(id, Flower.TABLE_NAME, flower.getName());
        }
    }

    public List<Flower> getAllFlowers() {
        return database.getFlowersDao().getAllFlowers();
    }

    public Flower getFlowerById(long flowerId) {
        return database.getFlowersDao().getFlowerById(flowerId);
    }

    public List<Flower> getFlowersForGroup(long groupId) {
        return database.getFlowersDao().getFlowersForGroup(groupId);
    }

    public List<Flower> getFlowersWithoutGroup() {
        return database.getFlowersDao().getFlowersWithoutGroup();
    }

    public void deleteFlower(Flower flower, boolean deleteTargetEvents) {
        database.getFlowersDao().delete(flower);
        if (deleteTargetEvents) {
            database.getEventDao().deleteForTarget(flower.getId(), Flower.TABLE_NAME);
        }
    }

    // endregion FLOWER

    // region EVENTS

    public void markEventDone(Event event, Calendar eventDoneDate) {
        Calendar nextEventDate = event.getEventDate();
        nextEventDate.add(Calendar.DAY_OF_YEAR, CalendarUtils.getDaysDiff(nextEventDate, eventDoneDate));
        if (event.getFrequency() != null) {
            nextEventDate.add(Calendar.DAY_OF_YEAR, event.getFrequency());
        } else {
            event.setEndDate(eventDoneDate);
        }

        database.getEventDao().update(event);
    }

    public List<Event> getNearbyEvents(Calendar start, int daysCount,
                                       int minItemsCount, boolean includeOldEvents) {
        Calendar startDate = (Calendar) start.clone();
        Calendar endDate = (Calendar) startDate.clone();
        endDate.add(Calendar.DAY_OF_YEAR, daysCount);
        List<Event> events = database.getEventDao().getEventForSelection(
                String.format(EventDao.QUERY_EVENTS_NEARBY, startDate.getTimeInMillis(),
                        endDate.getTimeInMillis()));
        if (events != null) {
            events = EventsUtils.createOrderedEventsWithPeriodically(events, startDate, endDate, includeOldEvents);
            if (events.size() < minItemsCount) {
                events = getNearbyEvents(start, daysCount * 2, minItemsCount, includeOldEvents);
            }
        }

        return events;
    }

    public List<Event> getEventsForTarget(long targetId, String targetTable) {
        return database.getEventDao().getEventsForTarget(targetId, targetTable);
    }

    public HashMap<Integer, List<Event>> getEventsForPeriod(Calendar startDate, Calendar endDate,
                                                            CalendarFilter filter) {
        String selection = "";
        switch (filter.getElementsFilterType()) {
            case FilterElements.FLOWERS:
                selection += Columns.TARGET_TABLE + "='" + Flower.TABLE_NAME + "'";
                break;
            case FilterElements.GROUPS:
                selection += Columns.TARGET_TABLE + "='" + Group.TABLE_NAME + "'";
                break;
            case FilterElements.SELECTED:
                if (filter.getSelectedElements() != null && filter.getSelectedElements().size() > 0) {
                    selection += Columns.TARGET_ID + " in ("
                            + TextUtils.join(",", filter.getSelectedElements())
                            + ") ";
                }

                break;
        }

        if (filter.getSelectedEventTypes() != null && filter.getSelectedEventTypes().size() > 0) {
            if (!TextUtils.isEmpty(selection)) {
                selection += " and ";
            }

            selection += Columns.EVENT_TYPE + " in ("
                    + TextUtils.join(",", filter.getSelectedEventTypes())
                    + ") ";
        }

        String query = String.format(EventDao.QUERY_EVENTS_FILTER, startDate.getTimeInMillis(),
                endDate.getTimeInMillis());
        if (!TextUtils.isEmpty(selection)) {
            query += " and " + selection;
        }

        List<Event> events = database.getEventDao().getEventForSelection(query);
        return EventsUtils.groupEventsByDays(events, startDate, endDate);
    }

    private void createEventForCreation(long targetId, String tableName, String title) {
        Event event = new Event();
        event.setTargetId(targetId);
        event.setTargetTable(tableName);
        Calendar now = Calendar.getInstance();
        event.setCreationDate(now);
        event.setEndDate(now);
        event.setEventDate(now);
        event.setEventType(EventType.CREATED);
        event.setTitle(title);
        database.getEventDao().insert(event);
    }

    public List<EventWithTarget> getEventsTarget(List<Event> eventsPerDay) {
        List<EventWithTarget> result = new ArrayList<>();
        if (eventsPerDay != null) {
            for (Event event : eventsPerDay) {
                EventWithTarget eventWithTarget = new EventWithTarget();
                eventWithTarget.setEvent(event);
                if (Flower.TABLE_NAME.equalsIgnoreCase(event.getTargetTable())) {
                    Flower flower = database.getFlowersDao().getFlowerById(event.getTargetId());
                    eventWithTarget.setTarget(flower);
                } else if (Group.TABLE_NAME.equalsIgnoreCase(event.getTargetTable())) {
                    Group group = database.getGroupDao().getGroupById(event.getTargetId());
                    eventWithTarget.setTarget(group);
                }

                result.add(eventWithTarget);
            }
        }

        return result;
    }

    public Event getEventById(long eventId) {
        return database.getEventDao().getEventById(eventId);
    }

    public void createOrUpdateEvent(Event event) {
        if (event.getId() == DEFAULT_ID) {
            event.setId(invalidateIdForInsert(event.getId()));
            long id = database.getEventDao().insert(event);
            event.setId(id);
        } else {
            database.getEventDao().update(event);
        }
    }

    public void deleteEvent(Event event) {
        database.getEventDao().delete(event);
    }

    // endregion EVENTS
}

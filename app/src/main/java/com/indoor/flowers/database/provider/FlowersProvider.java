package com.indoor.flowers.database.provider;

import android.content.Context;
import android.text.TextUtils;

import com.evgeniysharafan.utils.Res;
import com.indoor.flowers.R;
import com.indoor.flowers.database.Columns;
import com.indoor.flowers.database.dao.EventDao;
import com.indoor.flowers.model.CalendarFilter;
import com.indoor.flowers.model.CalendarFilter.FilterElements;
import com.indoor.flowers.model.Event;
import com.indoor.flowers.model.EventType;
import com.indoor.flowers.model.EventWithTarget;
import com.indoor.flowers.model.Flower;
import com.indoor.flowers.model.FlowerWithSetting;
import com.indoor.flowers.model.Group;
import com.indoor.flowers.model.SettingData;
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

    public void createGroup(Group group, SettingData data) {
        createSettingData(data);
        group.setSettingDataId(data.getId());
        group.setId(invalidateIdForInsert(group.getId()));
        long id = database.getGroupDao().insert(group);
        group.setId(id);

        createEventForCreation(group.getId(), Group.TABLE_NAME, group.getName());
        createEventsForSetting(data, group.getName(), group.getId(), Group.TABLE_NAME);
    }

    public List<Group> getAllGroups() {
        return database.getGroupDao().getAllGroups();
    }

    public Group getGroupById(long groupId) {
        return database.getGroupDao().getGroupById(groupId);
    }

    public void setGroupLastTimeWatering(long groupId, long timeInMillis) {
        database.getGroupDao().setGroupLastTimeWatering(groupId, timeInMillis);
    }

    // endregion GROUP

    // region FLOWER

    public void createFlower(Flower flower, SettingData data) {
        createSettingData(data);
        flower.setSettingDataId(data.getId());

        flower.setId(invalidateIdForInsert(flower.getId()));
        long id = database.getFlowersDao().insert(flower);
        flower.setId(id);

        createEventForCreation(id, Flower.TABLE_NAME, flower.getName());
        createEventsForSetting(data, flower.getName(), id, Flower.TABLE_NAME);
    }

    public List<FlowerWithSetting> getAllFlowersWithSetting() {
        return database.getFlowersDao().getAllFlowersWithSetting();
    }

    public List<Flower> getAllFlowers() {
        return database.getFlowersDao().getAllFlowers();
    }

    public Flower getFlowerById(long flowerId) {
        return database.getFlowersDao().getFlowerById(flowerId);
    }

    public List<FlowerWithSetting> getFlowersForGroup(long groupId) {
        return database.getFlowersDao().getFlowersForGroup(groupId);
    }

    public List<FlowerWithSetting> getFlowersWithoutGroup() {
        return database.getFlowersDao().getFlowersWithoutGroup();
    }

    public void deleteFlower(Flower flower, boolean deleteTargetEvents) {
        database.getFlowersDao().delete(flower);
        if (deleteTargetEvents) {
            database.getEventDao().deleteForTarget(flower.getId(), Flower.TABLE_NAME);
        }
    }

    public void setFlowerLastTimeWatering(long flowerId, long timeInMillis) {
        database.getFlowersDao().setFlowerLastTimeWatering(flowerId, timeInMillis);
    }

    // endregion FLOWER

    // region SETTING_DATA

    private void createSettingData(SettingData data) {
        data.setId(invalidateIdForInsert(data.getId()));
        long id = database.getSettingDao().insert(data);
        data.setId(id);
    }

    // endregion SETTING_DATA

    // region EVENTS

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

    private void createEventsForSetting(SettingData setting, String targetTitle,
                                        long targetId, String targetTable) {
        refreshEvent(targetId, targetTable, EventType.WATERING, setting.getLastWateringDate(),
                setting.getWateringFrequency(), Res.getString(R.string.event_watering_format, targetTitle));
        refreshEvent(targetId, targetTable, EventType.NUTRITION, setting.getLastNutritionDate(),
                setting.getNutritionFreq(), Res.getString(R.string.event_nutrition_format, targetTitle));
        refreshEvent(targetId, targetTable, EventType.TRANSPLANTING, setting.getLastTransplanting(),
                null, Res.getString(R.string.event_transplanting_format, targetTitle));
        refreshEvent(targetId, targetTable, EventType.TRANSPLANTING, setting.getNextTransplanting(),
                null, Res.getString(R.string.event_transplanting_format, targetTitle));
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

    private void refreshEvent(long targetId, String targetTable, @EventType int eventType,
                              Calendar eventDate, Integer frequency, String title) {
        Event event = database.getEventDao().getForTarget(targetId, targetTable, eventType);
        if (eventDate != null) {
            if (event == null) {
                event = new Event();
                event.setTargetId(targetId);
                event.setTargetTable(targetTable);
                event.setEventType(eventType);
                event.setCreationDate(Calendar.getInstance());
                event.setId(invalidateIdForInsert(event.getId()));
                long id = database.getEventDao().insert(event);
                event.setId(id);
            }

            event.setTitle(title);
            event.setEventDate(eventDate);
            event.setFrequency(frequency);
            event.setEndDate(null);
            database.getEventDao().update(event);
        } else if (event != null && event.getEndDate() == null) {
            event.setEndDate(Calendar.getInstance());
            database.getEventDao().update(event);
        }
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

    // endregion EVENTS
}

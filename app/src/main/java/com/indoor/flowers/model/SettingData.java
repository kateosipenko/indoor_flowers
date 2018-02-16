package com.indoor.flowers.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.TypeConverters;

import com.indoor.flowers.database.Columns;
import com.indoor.flowers.database.DbTypesConverter;

import java.util.Calendar;

@TypeConverters({DbTypesConverter.class})
public class SettingData {

    @HumidityLevel
    @ColumnInfo(name = Columns.HUMIDITY)
    private int humidityLevel = HumidityLevel.NOT_SET;
    @BrightnessLevel
    @ColumnInfo(name = Columns.BRIGHTNESS)
    private int brightnessLevel = BrightnessLevel.NOT_SET;
    @ColumnInfo(name = Columns.WATERING_PERIOD)
    private int wateringFrequency;
    @ColumnInfo(name = Columns.ACTIVE_FROM)
    private int activeFrom = -1;
    @ColumnInfo(name = Columns.ACTIVE_TO)
    private int activeTo = -1;
    @ColumnInfo(name = Columns.PASSIVE_FROM)
    private int passiveFrom = -1;
    @ColumnInfo(name = Columns.PASSIVE_TO)
    private int passiveTo = -1;
    @ColumnInfo(name = Columns.LAST_WATERING_DATE)
    private Calendar lastWateringDate;
    @ColumnInfo(name = Columns.PREFERRED_TIME)
    private Calendar preferredTime;

    public boolean isEmpty() {
        return wateringFrequency <= 0 && lastWateringDate == null;
    }

    @HumidityLevel
    public int getHumidityLevel() {
        return humidityLevel;
    }

    public void setHumidityLevel(@HumidityLevel int humidityLevel) {
        this.humidityLevel = humidityLevel;
    }

    @BrightnessLevel
    public int getBrightnessLevel() {
        return brightnessLevel;
    }

    public void setBrightnessLevel(@BrightnessLevel int brightnessLevel) {
        this.brightnessLevel = brightnessLevel;
    }

    public int getWateringFrequency() {
        return wateringFrequency;
    }

    public void setWateringFrequency(int wateringFrequency) {
        this.wateringFrequency = wateringFrequency;
    }

    public int getActiveFrom() {
        return activeFrom;
    }

    public void setActiveFrom(int activeFrom) {
        this.activeFrom = activeFrom;
    }

    public int getActiveTo() {
        return activeTo;
    }

    public void setActiveTo(int activeTo) {
        this.activeTo = activeTo;
    }

    public int getPassiveFrom() {
        return passiveFrom;
    }

    public void setPassiveFrom(int passiveFrom) {
        this.passiveFrom = passiveFrom;
    }

    public int getPassiveTo() {
        return passiveTo;
    }

    public void setPassiveTo(int passiveTo) {
        this.passiveTo = passiveTo;
    }

    public Calendar getLastWateringDate() {
        return lastWateringDate;
    }

    public void setLastWateringDate(Calendar lastWateringDate) {
        this.lastWateringDate = lastWateringDate;
    }

    public Calendar getPreferredTime() {
        return preferredTime;
    }

    public void setPreferredTime(Calendar preferredTime) {
        this.preferredTime = preferredTime;
    }

    public Calendar getNextWateringTime() {
        Calendar result = Calendar.getInstance();
        result.setTimeInMillis(lastWateringDate.getTimeInMillis());
        result.add(Calendar.DAY_OF_MONTH, wateringFrequency);
        result.set(Calendar.HOUR_OF_DAY, preferredTime.get(Calendar.HOUR_OF_DAY));
        result.set(Calendar.MINUTE, preferredTime.get(Calendar.MINUTE));
        return result;
    }
}

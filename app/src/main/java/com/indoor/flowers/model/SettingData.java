package com.indoor.flowers.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.indoor.flowers.database.Columns;
import com.indoor.flowers.database.DbTypesConverter;

import org.jetbrains.annotations.Nullable;

import java.util.Calendar;

@Entity(tableName = SettingData.TABLE_NAME)
@TypeConverters({DbTypesConverter.class})
public class SettingData {

    public static final String TABLE_NAME = "SettingDataTable";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = Columns.SETTING_ID)
    private long id;
    @ColumnInfo(name = Columns.LAST_WATERING_DATE)
    private Calendar lastWateringDate;
    @ColumnInfo(name = Columns.WATERING_PERIOD)
    private int wateringFrequency;
    @ColumnInfo(name = Columns.LAST_NUTRITION_DATE)
    private Calendar lastNutritionDate;
    @ColumnInfo(name = Columns.NUTRITION_FREQ)
    private int nutritionFreq;
    @ColumnInfo(name = Columns.LAST_TRANSPLANTING_DATE)
    private Calendar lastTransplanting;
    @ColumnInfo(name = Columns.NEXT_TRANSPLANTING_DATE)
    private Calendar nextTransplanting;
    @ColumnInfo(name = Columns.PREFERRED_TIME)
    private Calendar preferredTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Calendar getLastWateringDate() {
        return lastWateringDate;
    }

    public void setLastWateringDate(Calendar lastWateringDate) {
        this.lastWateringDate = lastWateringDate;
    }

    public int getWateringFrequency() {
        return wateringFrequency;
    }

    public void setWateringFrequency(int wateringFrequency) {
        this.wateringFrequency = wateringFrequency;
    }

    public Calendar getLastNutritionDate() {
        return lastNutritionDate;
    }

    public void setLastNutritionDate(Calendar lastNutritionDate) {
        this.lastNutritionDate = lastNutritionDate;
    }

    public int getNutritionFreq() {
        return nutritionFreq;
    }

    public void setNutritionFreq(int nutritionFreq) {
        this.nutritionFreq = nutritionFreq;
    }

    public Calendar getLastTransplanting() {
        return lastTransplanting;
    }

    public void setLastTransplanting(Calendar lastTransplanting) {
        this.lastTransplanting = lastTransplanting;
    }

    public Calendar getNextTransplanting() {
        return nextTransplanting;
    }

    public void setNextTransplanting(Calendar nextTransplanting) {
        this.nextTransplanting = nextTransplanting;
    }

    public Calendar getPreferredTime() {
        return preferredTime;
    }

    public void setPreferredTime(Calendar preferredTime) {
        this.preferredTime = preferredTime;
    }

    @Nullable
    public Calendar getNextWateringDate() {
        return getNextDate(lastWateringDate, wateringFrequency);
    }

    @Nullable
    public Calendar getNextNutritionDate() {
        return getNextDate(lastNutritionDate, nutritionFreq);
    }

    @Nullable
    public Calendar getNextTransplantingDate() {
        return getNextDate(nextTransplanting, null);
    }

    @Nullable
    private Calendar getNextDate(Calendar from, Integer frequency) {
        if (from == null) {
            return null;
        }

        Calendar result = (Calendar) from.clone();
        if (frequency != null) {
            result.add(Calendar.DAY_OF_YEAR, frequency);
        }

        if (preferredTime != null) {
            result.set(Calendar.HOUR_OF_DAY, preferredTime.get(Calendar.HOUR_OF_DAY));
            result.set(Calendar.MINUTE, preferredTime.get(Calendar.MINUTE));
        }

        return result;
    }
}

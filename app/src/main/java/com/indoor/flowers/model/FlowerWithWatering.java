package com.indoor.flowers.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.TypeConverters;

import com.indoor.flowers.database.Columns;
import com.indoor.flowers.database.DbTypesConverter;

import java.util.Calendar;

@TypeConverters({DbTypesConverter.class})
public class FlowerWithWatering {

    @Embedded
    private Flower flower;
    @ColumnInfo(name = Columns.FREQUENCY)
    private int frequency;
    @ColumnInfo(name = Columns.DATE)
    private Calendar date;

    public Flower getFlower() {
        return flower;
    }

    public void setFlower(Flower flower) {
        this.flower = flower;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }
}

package com.indoor.flowers.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.indoor.flowers.database.Columns;

@Entity(tableName = Room.TABLE_NAME)
public class Room {

    public static final String TABLE_NAME = "RoomTable";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = Columns.ID)
    private long id;
    @ColumnInfo(name = Columns.NAME)
    private String name;
    @ColumnInfo(name = Columns.IMAGE_PATH)
    private String imagePath;
    @ColumnInfo(name = Columns.TEMPERATURE)
    private int temperature;
    @ColumnInfo(name = Columns.HUMIDITY)
    @HumidityLevel
    private int humidity;
    @ColumnInfo(name = Columns.BRIGHTNESS)
    @BrightnessLevel
    private int brightness;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    @HumidityLevel
    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(@HumidityLevel int humidity) {
        this.humidity = humidity;
    }

    @BrightnessLevel
    public int getBrightness() {
        return brightness;
    }

    public void setBrightness(@BrightnessLevel int brightness) {
        this.brightness = brightness;
    }

    public String getImagePath() {
        return imagePath;
    }

    public int getIconRes() {
        int result = -1;
        try {
            result = Integer.valueOf(imagePath);
        } catch (NumberFormatException ignore) {
        }

        return result;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}

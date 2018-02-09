package com.indoor.flowers.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.os.Parcel;
import android.os.Parcelable;

import com.indoor.flowers.database.Columns;
import com.indoor.flowers.database.DbTypesConverter;

import java.util.Calendar;

@Entity(tableName = Flower.TABLE_NAME)
@TypeConverters({DbTypesConverter.class})
public class Flower implements Parcelable {

    public static final String TABLE_NAME = "FlowerTable";

    @ColumnInfo(name = Columns.ID)
    @PrimaryKey(autoGenerate = true)
    private long id;
    @ColumnInfo(name = Columns.NAME)
    private String name;
    @ColumnInfo(name = Columns.IMAGE_PATH)
    private String imagePath;
    @ColumnInfo(name = Columns.ROOM_ID)
    private long roomId;
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
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

    public Flower() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeString(this.imagePath);
        dest.writeLong(this.roomId);
    }

    protected Flower(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.imagePath = in.readString();
        this.roomId = in.readLong();
    }

    public static final Creator<Flower> CREATOR = new Creator<Flower>() {
        @Override
        public Flower createFromParcel(Parcel source) {
            return new Flower(source);
        }

        @Override
        public Flower[] newArray(int size) {
            return new Flower[size];
        }
    };
}

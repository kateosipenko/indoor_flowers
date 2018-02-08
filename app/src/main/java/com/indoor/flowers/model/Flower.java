package com.indoor.flowers.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import com.indoor.flowers.database.Columns;

@Entity(tableName = Flower.TABLE_NAME)
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
    @ColumnInfo(name = Columns.PERIOD)
    private int period;

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

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
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
        dest.writeInt(this.period);
    }

    protected Flower(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.imagePath = in.readString();
        this.roomId = in.readLong();
        this.period = in.readInt();
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

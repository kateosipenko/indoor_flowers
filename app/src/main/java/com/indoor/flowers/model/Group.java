package com.indoor.flowers.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.indoor.flowers.database.Columns;

import java.util.Objects;

@Entity(tableName = Group.TABLE_NAME)
public class Group {

    public static final String TABLE_NAME = "GroupTable";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = Columns.ID)
    private long id;
    @ColumnInfo(name = Columns.NAME)
    private String name;
    @ColumnInfo(name = Columns.IMAGE_PATH)
    private String imagePath;

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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Group)) {
            return false;
        }

        return Objects.equals(this.id, ((Group) obj).getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.name);
    }
}

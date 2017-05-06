package com.indoor.flowers.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Flower implements Parcelable {

    private String name;
    private String imagePath;

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
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.imagePath);
    }

    public Flower() {
    }

    protected Flower(Parcel in) {
        this.name = in.readString();
        this.imagePath = in.readString();
    }

    public static final Parcelable.Creator<Flower> CREATOR = new Parcelable.Creator<Flower>() {
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

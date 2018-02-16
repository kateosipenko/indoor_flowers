package com.indoor.flowers.model;

import android.arch.persistence.room.Embedded;

public class FlowerWithSetting {
    @Embedded
    private Flower flower;
    @Embedded
    private SettingData settingData;

    public Flower getFlower() {
        return flower;
    }

    public void setFlower(Flower flower) {
        this.flower = flower;
    }

    public SettingData getSettingData() {
        return settingData;
    }

    public void setSettingData(SettingData settingData) {
        this.settingData = settingData;
    }
}

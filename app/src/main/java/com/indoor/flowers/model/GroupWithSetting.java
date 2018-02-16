package com.indoor.flowers.model;

import android.arch.persistence.room.Embedded;

public class GroupWithSetting {
    @Embedded
    private Group group;
    @Embedded
    private SettingData settingData;

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public SettingData getSettingData() {
        return settingData;
    }

    public void setSettingData(SettingData settingData) {
        this.settingData = settingData;
    }
}

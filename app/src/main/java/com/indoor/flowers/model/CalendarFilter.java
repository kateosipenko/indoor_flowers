package com.indoor.flowers.model;

import android.support.annotation.IntDef;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

public class CalendarFilter implements Serializable {

    @FilterElements
    private int elementsFilterType = FilterElements.NONE;
    private ArrayList<Integer> selectedEventTypes = new ArrayList<>();
    private ArrayList<Long> selectedFlowers = new ArrayList<>();
    private ArrayList<Long> selectedGroups = new ArrayList<>();

    public CalendarFilter() {
    }

    @FilterElements
    public int getElementsFilterType() {
        return elementsFilterType;
    }

    public void setElementsFilterType(@FilterElements int elementsFilterType) {
        this.elementsFilterType = elementsFilterType;
    }

    public ArrayList<Integer> getSelectedEventTypes() {
        return selectedEventTypes;
    }

    public void setSelectedEventTypes(ArrayList<Integer> selectedEventTypes) {
        this.selectedEventTypes = selectedEventTypes;
    }

    public ArrayList<Long> getSelectedFlowers() {
        return selectedFlowers;
    }

    public void setSelectedFlowers(ArrayList<Long> selectedFlowers) {
        this.selectedFlowers = selectedFlowers;
    }

    public ArrayList<Long> getSelectedGroups() {
        return selectedGroups;
    }

    public void setSelectedGroups(ArrayList<Long> selectedGroups) {
        this.selectedGroups = selectedGroups;
    }

    public void clearSelectedItems() {
        if (selectedFlowers != null) {
            selectedFlowers.clear();
        }
        if (selectedGroups != null) {
            selectedGroups.clear();
        }
    }

    @IntDef({FilterElements.NONE, FilterElements.FLOWERS, FilterElements.GROUPS, FilterElements.SELECTED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface FilterElements {
        int NONE = 0;
        int FLOWERS = 1;
        int GROUPS = 2;
        int SELECTED = 3;
    }
}

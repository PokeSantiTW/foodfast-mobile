package com.santisj.foodfast.objects;

import com.santisj.foodfast.enums.FoodIcons;

public class FoodSection {

    private String sectionName;
    private String sectionIcon;

    public FoodSection(String sectionName, FoodIcons icon) {
        this.sectionName = sectionName;
        this.sectionIcon = icon.getDrawable();
    }

    public FoodSection() {
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getSectionIcon() {
        return sectionIcon;
    }

    public void setSectionIcon(String sectionIcon) {
        this.sectionIcon = sectionIcon;
    }

    @Override
    public String toString() {
        return "FoodSection{" +
                "sectionName='" + sectionName + '\'' +
                ", sectionIcon=" + sectionIcon +
                '}';
    }
}

package com.example.market.ljw.core.utils;

//

import android.graphics.drawable.Drawable;

/**
 * Created by GYH on 2014/10/16.
 */
public class AppsItemInfo {

    private Drawable icon;
    private String labelName;
    private String packageName;

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getlabelName() {
        return labelName;
    }

    public void setlabelName(String labelName) {
        this.labelName = labelName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}

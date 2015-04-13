package com.example.market.ljw.entity.bean.output;

import java.io.Serializable;

/**
 * Created by GYH on 2014/11/9.
 */
public class Advertisement implements Serializable{

    private String ID;
    private String ImageUrl;
    private String AdUrl;
    private String UpdateTime;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getAdUrl() {
        return AdUrl;
    }

    public void setAdUrl(String adUrl) {
        AdUrl = adUrl;
    }

    public String getUpdateTime() {
        return UpdateTime;
    }

    public void setUpdateTime(String updateTime) {
        UpdateTime = updateTime;
    }
}

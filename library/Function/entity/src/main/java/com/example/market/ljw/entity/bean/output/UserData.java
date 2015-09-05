package com.example.market.ljw.entity.bean.output;


import java.io.Serializable;

/**
 * Created by GYH on 2014/10/22.
 */
public class UserData extends Member implements Serializable{
    private String todayTime;//今日已经挂的时间

    public String getTodayTime() {
        return todayTime;
    }

    public void setTodayTime(String todayTime) {
        this.todayTime = todayTime;
    }
}

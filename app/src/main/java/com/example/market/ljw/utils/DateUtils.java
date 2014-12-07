package com.example.market.ljw.utils;

import android.widget.TextView;

import java.util.Calendar;

/**
 * Created by GYH on 2014/10/24.
 *
 * 日期、时间显示工具类
 *
 */
public class DateUtils {

    /**
     * 将时间显示在视图上
     * */
    public static void setTimeStringToView(TextView view){
        Calendar ca = Calendar.getInstance();
        int minute=ca.get(Calendar.MINUTE);//分
        int hour=ca.get(Calendar.HOUR);//小时
        view.setText(hour+":"+minute);
    }

    /**
     * 将日期显示到视图上
     * */
    public static void setDateStringtoView(TextView view){
        Calendar ca = Calendar.getInstance();
        int year = ca.get(Calendar.YEAR);//获取年份
        int month=ca.get(Calendar.MONTH);//获取月份
        int day=ca.get(Calendar.DATE);//获取日
        view.setText(year +"年"+ month +"月"+ day + "日");
    }

    public static void setCurTimeToView(TextView view,long delayMillis){
        long N = delayMillis/3600;
        delayMillis = delayMillis%3600;
        long K = delayMillis/60;
        delayMillis = delayMillis%60;
        long M = delayMillis;
        String houre = "";
        if(Constant.DEBUG){
            System.out.println("时间是："+N+"小时 "+K+"分钟 "+M+"秒");
        }
        if(N<=9){
            houre = "0"+N;
        }else {
            houre = N+"";
        }
        String min = "";
        if(K<=9){
            min = "0"+K;
        }else {
            min = K + "";
        }
        String sin = "";
        if(M<=9){
            sin = "0"+M;
        }else {
            sin = M + "";
        }
        view.setText("在线时间："+houre + ":"+min + ":"+sin);
    }

}

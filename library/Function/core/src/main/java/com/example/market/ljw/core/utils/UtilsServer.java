package com.example.market.ljw.core.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by GYH on 2015/3/13.
 */
public class UtilsServer {

    /**
     * 按照秒来加时间
     * */
    public static String modifyServerTime(String strtime,int limtime){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date timeb = null;
        try {
            timeb = df.parse(strtime);
            Calendar c12 = Calendar.getInstance();
            c12.setTime(timeb);
            c12.add(c12.SECOND,+limtime);
            Date temp_date = c12.getTime();
            strtime = df.format(temp_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
       return strtime;
    }

}

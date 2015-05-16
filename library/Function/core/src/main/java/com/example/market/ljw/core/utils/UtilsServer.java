package com.example.market.ljw.core.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by GYH on 2015/3/13.
 */
public class UtilsServer {

    public static void main(String arg[]){
       String servicetimestr = "2015-04-23 18:10:09";
       for(int i=0;i<5000;i++){
           servicetimestr = modifyServerTime(servicetimestr,10);
           Utils.showSystem("gyh--servertime = ",servicetimestr);
//           Utils.showSystem("gyh-- isCanAddscore= ",Utils.isCanAddScore(servicetimestr)+"");
//           Utils.setFileLog(servicetimestr);
       }
    }

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

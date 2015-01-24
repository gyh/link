package com.example.market.ljw.function.floatwindow;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

/**
 * Created by GYH on 2014/11/30.
 */
@Deprecated
public class TimeService extends Service implements Runnable {
    protected static final String ACTIONServiceTOActivity = "Service-Activity";//TimeService把时间变化告诉Mychronometer的广播
    protected static final String ACTIONActivityToService = "Activity-Service";//Mychronometer告知TimeService停止、开始、清零的广播
    private ServiceReceiver serviceReceiver;
    private IntentFilter filter;
    private MyBinder binder = new MyBinder();
    private Handler handler;
    private Thread timecountThread;    //告诉handler时间递增的线程
    private boolean flagIfContinnute;   //接收timecountThread线程的标志
    private boolean timeAdd;     //handler执行时间递增的信息标志

    //时间数据
    private int hour;
    private int min;
    private int sec;

    private Intent intentSendTime;    //传递时间数据的Intent

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public class MyBinder extends Binder {
        public TimeService getService() {
            return TimeService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        serviceReceiver = new ServiceReceiver();   //初始化广播
        filter = new IntentFilter();
        filter.addAction(ACTIONActivityToService);
        registerReceiver(serviceReceiver, filter);   //注册广播

        //初始化变量
        flagIfContinnute = false;
        hour = 0;
        min = 0;
        sec = 0;
        intentSendTime = new Intent();


        //接收线程的信息实现时间递增
        handler = new Handler() {
            public void handleMessage(Message msg) {
                timeAdd = msg.getData().getBoolean("timeadd");
                //模拟时间变化规律
                if (timeAdd) {
                    if (sec + 1 == 60) {
                        if (min + 1 == 60) {
                            hour++;
                            min = 0;
                        } else {
                            min++;
                        }
                        sec = 0;
                    } else {
                        sec++;
                    }
                }

                intentSendTime.putExtra("hour", hour);
                intentSendTime.putExtra("min", min);
                intentSendTime.putExtra("sec", sec);

                intentSendTime.setAction(ACTIONServiceTOActivity);
                sendBroadcast(intentSendTime);
            }
        };
        timecountThread = new Thread(this);
        timecountThread.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(serviceReceiver);
    }

    @Override
    public void run() {
        flagIfContinnute = true;
        while (flagIfContinnute) {
            Message msg = handler.obtainMessage();//每一次都必须新建msg！
            Bundle data = new Bundle();
            data.putBoolean("timeadd", true);
            msg.setData(data);
            handler.sendMessage(msg);
            try {
                Thread.sleep(1000);//每隔一秒钟告诉handler时间递增1
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    class ServiceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle1 = intent.getExtras();
            boolean temp = bundle1.getBoolean("StartOrNot", true);
            if (!flagIfContinnute && temp)//本来没有启动线程而Activity告知启动线程，那么就启动
            {
                timecountThread = new Thread(TimeService.this);
                timecountThread.start();
            }
            if (flagIfContinnute && !temp)//本来已经启动线程而Activity告知停止线程，那么就停止
                flagIfContinnute = false;
        }
    }
}

package com.example.market.ljw.function.lockscreen;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.example.market.ljw.function.glowpadview.LockActivity;
import com.example.market.ljw.utils.Log;

/**
 * Created by GYH on 2014/10/17.
 */
public class MyLockScreenService extends Service {
    private final String ACT_SCREEN_OFF = "android.intent.action.SCREEN_OFF";
    private final String ACT_SCREEN_ON = "android.intent.action.SCREEN_ON";

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        Log.e("", "***********onBind MyLockScreenService");
        return null;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        Log.e("", "***********onCreate registerReceiver");
        IntentFilter intentFilter = new IntentFilter(ACT_SCREEN_OFF);
        registerReceiver(mScreenBCR, intentFilter);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        unregisterReceiver(mScreenBCR);
    }

    private BroadcastReceiver mScreenBCR = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            try {
                Intent i = new Intent();
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setClass(context, LockActivity.class);
                context.startActivity(i);
            } catch (Exception e) {
                // TODO: handle exception
                Log.e("", "***********onReceive Error=" + e);
            }
        }
    };
}

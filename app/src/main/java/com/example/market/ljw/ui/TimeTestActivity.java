package com.example.market.ljw.ui;

import com.example.market.ljw.R;
import com.example.market.ljw.common.frame.BaseActivity;
import com.example.market.ljw.function.service.FxService;
import com.example.market.ljw.function.service.TimeService;
import com.example.market.ljw.utils.Utils;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by GYH on 2014/11/30.
 */
public class TimeTestActivity extends BaseActivity {
    protected static final String ACTIONServiceTOActivity = "Service-Activity";//TimeService把时间变化告诉Mychronometer的广播
    protected static final String ACTIONActivityToService = "Activity-Service";//Mychronometer告知TimeService停止、开始、清零的广播

    //定义控件
    private TextView hourTxt;
    private TextView minTxt;
    private TextView secTxt;
    private Button start;
    private Button stop;
    private Button setZero;
    //时间数据
    private int hour;
    private int min;
    private int sec;
    //广播
    private IntentFilter filter;
    private FromServiceReceiver receiver;//接收TimeService传来的时间变化数据

    private boolean ifBind;    //判断是否绑定了服务
    private Intent intentfxService;//悬浮窗口配置

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetest);

        ifBind = false;        //一开始没有绑定服务

        receiver = new FromServiceReceiver();   //初始化广播
        filter = new IntentFilter();
        filter.addAction(ACTIONServiceTOActivity);

        findview();         //初始化控件
        setListener();        //给控件设置监听
        hourTxt.setText("00");
        minTxt.setText("00");
        secTxt.setText("00");
        initService();
        setBroadcastOfNo();
    }

    /**
     * 初始化服务
     */
    private void initService() {
        intentfxService = new Intent(TimeTestActivity.this, FxService.class);
        if (!Utils.isServiceRunning(this, FxService.class.getName())) {
            startService(intentfxService);//开启浮动窗口服务
        }
    }

    /**
     * 设置锁屏后及时继续
     * */
    private void setBroadcastOfNo(){
        Intent intent =new Intent(TimeTestActivity.this, alarmreceiver.class);
        intent.setAction("repeating");
        PendingIntent sender=PendingIntent.getBroadcast(TimeTestActivity.this, 0, intent, 0);
        //开始时间
        long firstime= SystemClock.elapsedRealtime();
        AlarmManager am=(AlarmManager)getSystemService(ALARM_SERVICE);
        //5秒一个周期，不停的发送广播
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstime, 1000, sender);
    }

    /**
     * 设置不锁广播
     * */
    public static class alarmreceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, filter);   //注册广播
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);    //注销广播
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unBind();
    }

    private void setListener() {
        start.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                bindService();      //启动TimeService服务，开始计时
            }
        });
        stop.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                stopService();      //暂停服务，暂停计时
            }
        });
        setZero.setOnClickListener(new Button.OnClickListener()//清零
        {

            @Override
            public void onClick(View v) {
                unBind();       //解除绑定，销毁Service
                hourTxt.setText("00");
                minTxt.setText("00");
                secTxt.setText("00");
            }
        });
    }

    protected void stopService()     //告诉TimeService.java停止线程
    {
        Intent intentStart = new Intent();
        intentStart.putExtra("StartOrNot", false);
        intentStart.setAction(ACTIONActivityToService);
        sendBroadcast(intentStart);
    }

    protected void unBind()       //解除绑定
    {
        if (ifBind) {
            Intent intentStart = new Intent();
            intentStart.putExtra("StartOrNot", false);
            intentStart.setAction(ACTIONActivityToService);
            sendBroadcast(intentStart);
            unbindService(conn);
            Toast.makeText(this, "unbind", Toast.LENGTH_LONG).show();
            ifBind = false;
        }
    }

    protected void bindService()     //启动TimeService服务，开始计时
    {
        if (!ifBind){//没有绑定则绑定
            Intent intent = new Intent(TimeTestActivity.this, TimeService.class);
            bindService(intent, conn, Context.BIND_AUTO_CREATE);//绑定服务
            ifBind = true;
        } else{ //已经绑定则广播告诉TimeService.java开启线程
            Intent intentStart = new Intent();
            intentStart.putExtra("StartOrNot", true);
            intentStart.setAction(ACTIONActivityToService);
            sendBroadcast(intentStart);
        }
    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
        }
    };

    private void findview() {
        hourTxt = (TextView) findViewById(R.id.hour);
        minTxt = (TextView) findViewById(R.id.min);
        secTxt = (TextView) findViewById(R.id.sec);
        start = (Button) findViewById(R.id.ButtonStart);
        stop = (Button) findViewById(R.id.ButtonStop);
        setZero = (Button) findViewById(R.id.ButtonSet0);
    }

    class FromServiceReceiver extends BroadcastReceiver //接收TimeService.java返回的时间信息
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle1 = intent.getExtras();
            hour = bundle1.getInt("hour", 0);
            min = bundle1.getInt("min", 0);
            sec = bundle1.getInt("sec", 0);


            //把时间数据setText
            if (hour < 10)
                hourTxt.setText("0" + hour);
            else
                hourTxt.setText(hour + "");

            if (min < 10)
                minTxt.setText("0" + min);
            else
                minTxt.setText(min + "");

            if (sec < 10)
                secTxt.setText("0" + sec);
            else
                secTxt.setText(sec + "");
        }
    }
}

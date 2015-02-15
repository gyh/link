package com.example.market.ljw.function.floatwindow;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;

import com.example.market.ljw.ui.MainActivity;
import com.example.market.ljw.R;
import com.example.market.ljw.core.common.frame.AppContext;
import com.example.market.ljw.glowpadview.LockActivity;
import com.example.market.ljw.core.utils.Constant;
import com.example.market.ljw.core.utils.Utils;

import static java.lang.Thread.sleep;

public class LjwService extends Service implements View.OnClickListener {
    private final String ACT_SCREEN_OFF = "android.intent.action.SCREEN_OFF";
    private final String ACT_SCREEN_ON = "android.intent.action.SCREEN_ON";
    private KeyguardManager keyguardManager = null;
    private KeyguardManager.KeyguardLock keyguardLock = null;
    private Intent lockIntent;
    private boolean isShowView = false;
    private boolean isrunning = false;
    private boolean mainActivityIsRun = true;
    //服务线程
    private Thread ljwThread = null;

    Handler clockhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //比较当前应用的包名是否是应用包名或者打开的包名
            if (msg.what == 4) {
                Constant.makeAppName = Constant.PACKAGENAME;
                Intent it = new Intent(LjwService.this, MainActivity.class);
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                it.putExtra(Constant.FromWhere.KEY, Constant.FromWhere.FXSERVICE);
                startActivity(it);
            } else if (msg.what == 6) {
                MyWindowManager.hiddenSmallWindow();
            } else if (msg.what == 5) {
                Constant.theNextLen = Constant.theNextLen - 1;
                MyWindowManager.showSmallWindow();
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        isrunning = true;
        createFloatView();
        initLjwServiceThread();
        lockIntent = new Intent(LjwService.this, LockActivity.class);
        lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //注册广播
        IntentFilter mScreenOffFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mScreenBCR, mScreenOffFilter);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return mybinder;
    }

    /**
     * 创建浮动窗口
     */
    private void createFloatView() {
        MyWindowManager.createSmallWindow(getApplicationContext());
    }
    /**
     * 初始化服务现场
     */
    private void initLjwServiceThread() {
        mainActivityIsRun = true;
        ljwThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isrunning) {
                    try {
                        sleep(1000);
                        //获取当前应用的包名
                        isShowView = Utils.isAppOnForeground(LjwService.this);
                        //判断等待时间是否结束
                        if (isShowView && mainActivityIsRun) {
                            if (Constant.makeAppName.equals(Constant.MMSPACKAGENAME) ||
                                    Constant.makeAppName.equals(Constant.CONTACTSPACKAGENAME)) {
                                clockhandler.sendEmptyMessage(4);
                            } else {
                                if (Constant.theNextLen <= 0) {
                                    clockhandler.sendEmptyMessage(4);
                                } else {
                                    clockhandler.sendEmptyMessage(5);
                                }
                            }
                        } else {
                            clockhandler.sendEmptyMessage(6);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        ljwThread.start();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        isrunning = false;
        MyWindowManager.hiddenSmallWindow();
        unregisterReceiver(mScreenBCR);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        //设置事件监听跳转到页面
        if (i == R.id.ljw_float_img) {
            if (AppContext.getInstance().getBaseActivity() != null) {
                clockhandler.sendEmptyMessage(4);
            } else {
                isrunning = false;
                LjwService.this.stopSelf();
            }
        }
    }

    /**
     * 锁频广播监听
     */
    private BroadcastReceiver mScreenBCR = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Constant.isShowLock &&
                    !Utils.getFirstTask(LjwService.this).equals(Constant.PHONEPACKAGENAME) &&
                    (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)
                            || intent.getAction().equals(Intent.ACTION_SCREEN_ON))) {
                Constant.isShowLock = false;
                Constant.TimeSystemCal.TEMPTIMESYSTEM = Utils.getCurrentDate();
                keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
                keyguardLock = keyguardManager.newKeyguardLock("");
                keyguardLock.disableKeyguard(); //这里就是取消系统默认的锁屏
                startActivity(lockIntent); //注意这里跳转的意图
            }
        }
    };

    MyBinder mybinder = new MyBinder();

    public class MyBinder extends Binder implements ServiceInterface {
        /*show只是测试方法，可以不要*/
        public void show() {
            mainActivityIsRun = true;
        }

        @Override
        public void hidden() {
            mainActivityIsRun = false;
        }

        /*返回service服务，方便activity中得到*/
        public LjwService getService() {
            return LjwService.this;
        }
    }
}


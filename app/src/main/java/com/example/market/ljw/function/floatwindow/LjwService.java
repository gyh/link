package com.example.market.ljw.function.floatwindow;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;

import com.example.market.ljw.MainActivity;
import com.example.market.ljw.R;
import com.example.market.ljw.common.frame.AppContext;
import com.example.market.ljw.function.glowpadview.LockActivity;
import com.example.market.ljw.utils.Constant;
import com.example.market.ljw.utils.Utils;

import static java.lang.Thread.sleep;

public class LjwService extends Service implements View.OnClickListener {
    private final String ACT_SCREEN_OFF = "android.intent.action.SCREEN_OFF";
    private final String ACT_SCREEN_ON = "android.intent.action.SCREEN_ON";
    //定义浮动窗口布局
    LinearLayout mFloatLayout;
    LayoutParams wmParams;
    //创建浮动窗口设置布局参数的对象
    WindowManager mWindowManager;
    private KeyguardManager keyguardManager = null;
    private KeyguardManager.KeyguardLock keyguardLock = null;
    private Intent lockIntent;
    private boolean isShowView = false;
    private boolean isrunning = false;
    private View floatview;
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
//                mFloatLayout.setVisibility(View.GONE);
                MyWindowManager.hiddenSmallWindow();
            } else if (msg.what == 5) {
                Constant.theNextLen = Constant.theNextLen - 1;
//                mFloatLayout.setVisibility(View.VISIBLE);
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
        return null;
    }

    /**
     * 创建浮动窗口
     */
    private void createFloatView() {
        MyWindowManager.createSmallWindow(getApplicationContext());
//        initViewParams();
//        initView();
    }

    /**
     * 初始化浮动界面样式
     */
    private void initViewParams() {
        wmParams = new LayoutParams();
        Point windowSize = new Point();
        //获取WindowManagerImpl.CompatModeWrapper
        mWindowManager = (WindowManager) getApplication().getSystemService(getApplication().WINDOW_SERVICE);
        mWindowManager.getDefaultDisplay().getSize(windowSize);
        //设置window type
        wmParams.type = LayoutParams.TYPE_PHONE;
        //设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
        //调整悬浮窗显示的停靠位置为左侧置顶
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;

        // 以屏幕左上角为原点，设置x、y初始值
        wmParams.x = windowSize.x;
        wmParams.y = windowSize.y/2;
        //设置悬浮窗口长宽数据
        wmParams.width = LayoutParams.WRAP_CONTENT;
        wmParams.height = LayoutParams.WRAP_CONTENT;
    }

    /**
     * 初始化浮动布局
     */
    private void initView() {
        LayoutInflater inflater = LayoutInflater.from(getApplication());
        //获取浮动窗口视图所在布局

        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.layout_ljw_float, null);
        mWindowManager.addView(mFloatLayout, wmParams);
        //浮动窗口按钮
        floatview = mFloatLayout.findViewById(R.id.ljw_float_img);
//        floatview.setOnClickListener(this);
        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        mFloatLayout.setVisibility(View.GONE);
        floatview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
                wmParams.x = (int) event.getRawX() - floatview.getMeasuredWidth() / 2;
                Utils.showSystem("service", "RawX" + event.getRawX());
                Utils.showSystem("service", "RawX" + event.getX());
                //25为状态栏的高度
                wmParams.y = (int) event.getRawY() - floatview.getMeasuredHeight() / 2 - Utils.getStatusBarHeight(LjwService.this);
                Utils.showSystem("service", "RawY" + event.getRawY());
                Utils.showSystem("service", "Y" + event.getY());
                //刷新
                mWindowManager.updateViewLayout(mFloatLayout, wmParams);
                return false;
            }
        });
    }

    /**
     * 初始化服务现场
     */
    private void initLjwServiceThread() {
        ljwThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isrunning) {
                    try {
                        sleep(1000);
                        //获取当前应用的包名
                        isShowView = Utils.isAppOnForeground(LjwService.this);
                        //判断等待时间是否结束
                        if (isShowView) {
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
        if (mFloatLayout != null) {
            mWindowManager.removeView(mFloatLayout);
        }
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


}


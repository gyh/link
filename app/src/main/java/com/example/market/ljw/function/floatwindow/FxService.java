package com.example.market.ljw.function.floatwindow;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.market.ljw.MainActivity;
import com.example.market.ljw.R;
import com.example.market.ljw.common.frame.AppContext;
import com.example.market.ljw.function.glowpadview.LockActivity;
import com.example.market.ljw.utils.Constant;
import com.example.market.ljw.utils.Utils;

import static java.lang.Thread.sleep;

@Deprecated
public class FxService extends Service {
    private final String ACT_SCREEN_OFF = "android.intent.action.SCREEN_OFF";
    private final String ACT_SCREEN_ON = "android.intent.action.SCREEN_ON";
    //定义浮动窗口布局
    LinearLayout mFloatLayout;
    WindowManager.LayoutParams wmParams;
    //创建浮动窗口设置布局参数的对象
    WindowManager mWindowManager;
    private KeyguardManager keyguardManager = null;
    private KeyguardManager.KeyguardLock keyguardLock = null;
    private Intent lockIntent;
    private Button openBtn;
    private TextView tvmsg;
    private boolean isShowView = false;
    private boolean isrunning = false;
    Handler clockhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //比较当前应用的包名是否是应用包名或者打开的包名
            if(msg.what == 4){
                Constant.makeAppName = Constant.PACKAGENAME;
                Intent it = new Intent(FxService.this, MainActivity.class);
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                it.putExtra(Constant.FromWhere.KEY, Constant.FromWhere.FXSERVICE);
                startActivity(it);
            }else {
                Constant.theNextLen = Constant.theNextLen-1;
            }
            if(isShowView){
                if(Constant.makeAppName.equals(Constant.MMSPACKAGENAME)||Constant.makeAppName.equals(Constant.CONTACTSPACKAGENAME)){
                    Constant.makeAppName = Constant.PACKAGENAME;
                    Intent it = new Intent(FxService.this, MainActivity.class);
                    it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    it.putExtra(Constant.FromWhere.KEY, Constant.FromWhere.FXSERVICE);
                    startActivity(it);
                }else {
//                    mFloatLayout.setVisibility(View.VISIBLE);//如果不是则显示并且提交显示
                    Constant.theNextLen = Constant.theNextLen-1;
//                    tvmsg.setText("积分系统将在（"+Constant.theNextLen+"）秒后关闭，请重新打开积分系统");
//                    Utils.showSystem("showpackname",Utils.getFirstTask(FxService.this));
//                    if(AppContext.getInstance().getBaseActivity()!=null){
//                        AppContext.getInstance().getBaseActivity().finish();
//                    }
                }
            }else {
                mFloatLayout.setVisibility(View.GONE);
            }
        }
    };
    @Override
    public void onCreate() {
        super.onCreate();
        isrunning = true;
        createFloatView();
        lockIntent = new Intent(FxService.this, LockActivity.class);
        lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //注册广播
        IntentFilter mScreenOffFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mScreenBCR, mScreenOffFilter);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createFloatView() {
        wmParams = new WindowManager.LayoutParams();
        //获取WindowManagerImpl.CompatModeWrapper
        mWindowManager = (WindowManager) getApplication().getSystemService(getApplication().WINDOW_SERVICE);
        //设置window type
        wmParams.type = LayoutParams.TYPE_PHONE;
        //设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;

        //调整悬浮窗显示的停靠位置为左侧置顶
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;

        // 以屏幕左上角为原点，设置x、y初始值
        wmParams.x = 0;
        wmParams.y = 0;
        //设置悬浮窗口长宽数据
        wmParams.width = LayoutParams.MATCH_PARENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        LayoutInflater inflater = LayoutInflater.from(getApplication());
        //获取浮动窗口视图所在布局
        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.float_layout, null);
        mWindowManager.addView(mFloatLayout, wmParams);
        //浮动窗口按钮
        openBtn = (Button) mFloatLayout.findViewById(R.id.openbtn);
        tvmsg = (TextView)mFloatLayout.findViewById(R.id.tvmsg);
        openBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AppContext.getInstance().getBaseActivity() != null) {
                    Intent it = new Intent(FxService.this, MainActivity.class);
                    it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    it.putExtra(Constant.FromWhere.KEY, Constant.FromWhere.FXSERVICE);
                    startActivity(it);
                } else {
                    isrunning = false;
                    FxService.this.stopSelf();
                }
            }
        });
        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        mFloatLayout.setVisibility(View.GONE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isrunning){
                    try {
                        sleep(1000);
                        //获取当前应用的包名
                        isShowView = Utils.isAppOnForeground(FxService.this);
                        //判断等待时间是否结束
                        if(isShowView){
                            if(Constant.makeAppName.equals(Constant.MMSPACKAGENAME)||
                                    Constant.makeAppName.equals(Constant.CONTACTSPACKAGENAME)){
                                clockhandler.sendEmptyMessage(4);
                            }else {
                                if(Constant.theNextLen<=0){//如果结束则结束整个应用
                                    clockhandler.sendEmptyMessage(4);
                                }else {
                                    clockhandler.sendEmptyMessage(5);
                                }

                            }

                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
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

    private BroadcastReceiver mScreenBCR = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(Constant.isShowLock&&
                    !Utils.getFirstTask(FxService.this).equals(Constant.PHONEPACKAGENAME)&&
                    (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)
                    || intent.getAction().equals(Intent.ACTION_SCREEN_ON))){
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


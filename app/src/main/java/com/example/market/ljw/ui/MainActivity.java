package com.example.market.ljw.ui;


import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.*;
import android.os.Process;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.market.ljw.R;
import com.example.market.ljw.core.common.frame.AppContext;
import com.example.market.ljw.core.common.frame.BaseActivity;
import com.example.market.ljw.core.common.frame.MyAppContext;
import com.example.market.ljw.core.common.frame.MyBaseActivity;
import com.example.market.ljw.core.common.frame.taskstack.ApplicationManager;
import com.example.market.ljw.core.common.frame.taskstack.BackStackManager;
import com.example.market.ljw.core.common.frame.taskstack.MyApplicationManager;
import com.example.market.ljw.core.common.http.HttpGroup;
import com.example.market.ljw.core.common.http.HttpResponse;
import com.example.market.ljw.core.utils.Constant;
import com.example.market.ljw.core.utils.DateUtils;
import com.example.market.ljw.core.utils.MiaoshaUtil;
import com.example.market.ljw.core.utils.MyCountdownTimer;
import com.example.market.ljw.core.utils.PopUtils;
import com.example.market.ljw.core.utils.PromptUtil;
import com.example.market.ljw.core.utils.Util;
import com.example.market.ljw.core.utils.Utils;
import com.example.market.ljw.entity.bean.Entity;
import com.example.market.ljw.entity.bean.output.Member;
import com.example.market.ljw.entity.bean.output.MemberOutput;
import com.example.market.ljw.entity.bean.output.UserData;
import com.example.market.ljw.fragment.AppListFragment;
import com.example.market.ljw.fragment.CarouselFragment;
import com.example.market.ljw.fragment.MarketListFragment;
import com.example.market.ljw.fragment.WebViewFragment;
import com.example.market.ljw.function.floatwindow.LjwService;
import com.example.market.ljw.service.InputDataUtils;
import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.lang.Thread.sleep;


public class MainActivity extends MyBaseActivity {

    public static String MEMBERDATAKEY = "com.example.market.ljw.ui.MainActivity.memberkey";

    public static int currentStats = 0;

    //用户名显示view
    private TextView tvusername;
    //积分显示视图
    private TextView tvnumber;
    //时间显示视图
    private TextView tvtime;
    //背景视图
    private FrameLayout ljwview;
    //左上角Log图标
    private View logimg;

    //登录后接受广播
    private PreventReceiveBroadCast preventReceiveBroadCast;

    FragmentManager fragmentManager ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ljw);
        currentStats = Constant.MainActivityState.CREATE;
        MyApplicationManager.clearFragment();
        //定义广播
        preventReceiveBroadCast = new PreventReceiveBroadCast();
        IntentFilter filter = new IntentFilter(); //只有持有相同的action的接受者才能接收此广播
        filter.addAction(Constant.ReceiveBroadCastKey.PREVENTBROAD_FLAG);
        registerReceiver(preventReceiveBroadCast, filter);
        //初始化视图
        initView();
        if (!checkDicServiceWorked()) {
            Intent dirUpdataService = new Intent(MainActivity.this, LjwService.class);
            startService(dirUpdataService);
        }

        fragmentManager = getSupportFragmentManager();
        FragmentTransaction carouselFragmenttFT = fragmentManager.beginTransaction();
        CarouselFragment carouselFragment = new CarouselFragment();
        carouselFragmenttFT.add(R.id.carouselfragment, carouselFragment, CarouselFragment.tag);
        carouselFragmenttFT.commit();

        AppListFragment appListFragment = new AppListFragment();
        FragmentTransaction appListFragmentFT = fragmentManager.beginTransaction();
        appListFragmentFT.add(R.id.contain, appListFragment, AppListFragment.tag);
        appListFragmentFT.commit();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Constant.makeAppName = Constant.PACKAGENAME;
        post(new Runnable() {
            @Override
            public void run() {
                currentStats = Constant.MainActivityState.RUN;
            }
        },2000);
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (preventReceiveBroadCast != null) {
            unregisterReceiver(preventReceiveBroadCast); //注销此广播
        }
        currentStats = Constant.MainActivityState.OVER;
    }

    /**
     * 初始化视图
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void initView() {
        logimg = findViewById(R.id.logimg);
        ljwview = (FrameLayout) findViewById(R.id.layoutljw);
        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            ljwview.setBackgroundDrawable(new BitmapDrawable(getResources(), Utils.getAndroidSystmeBtmp(this)));
        } else {
            ljwview.setBackground(new BitmapDrawable(getResources(), Utils.getAndroidSystmeBtmp(this)));
        }
        tvusername = (TextView) findViewById(R.id.tvusername);
        tvnumber = (TextView) findViewById(R.id.tvnumber);
        tvtime = (TextView) findViewById(R.id.tvtime);
        logimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MyApplicationManager.getCurrentFragemnt() instanceof WebViewFragment){
                    fragmentManager.popBackStack();
                    MyApplicationManager.removeFragment();
                }
            }
        });
    }


    /**
     * 初始化视图数据
     */
    private void initViewData(final String username, final String userIntegral, final String todayTime) {
        post(new Runnable() {
            @Override
            public void run() {
                tvusername.setText("账号：" + username);
                tvnumber.setText("积分：" + userIntegral);
                tvtime.setText("时间：" + todayTime);
            }
        });
    }


    /**
     * 检查service是否运行
     *
     * @return
     */
    private static boolean checkDicServiceWorked() {
        ActivityManager myManager = (ActivityManager) MyAppContext.getInstance().getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> services = (ArrayList<ActivityManager.RunningServiceInfo>) myManager.getRunningServices(30);
        for (int i = 0; i < services.size(); i++) {
            if (LjwService.class.getName(). //如果运行的service有字典数据检查service的话
                    equals(services.get(i).service.getClassName().toString())) {
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(fragmentManager.getBackStackEntryCount()>0){
                if(MyApplicationManager.getCurrentFragemnt() instanceof WebViewFragment&&WebViewFragment.mWebView.canGoBack()){
                    WebViewFragment.mWebView.goBack();
                    return true;
                }else {
                    fragmentManager.popBackStack();
                    MyApplicationManager.removeFragment();
                }
                return false;
            }else {
                final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(this);
                PromptUtil.showExitAlert(dialogBuilder, MainActivity.this, Effectstype.Fall, ljwview,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogBuilder.dismiss();
                                currentStats = Constant.MainActivityState.OVER;
                                MainActivity.this.finish();
                            }
                        });
            }
        }
        return false;
    }

    /**
     * 注册广播，防止销毁
     */
    class PreventReceiveBroadCast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Member member = (Member) intent.getSerializableExtra(MEMBERDATAKEY);
            if (member != null) {
                initViewData(member.getLoginName(), member.getTodayScore() + "", member.getDuration()+"");
            }
        }
    }
}

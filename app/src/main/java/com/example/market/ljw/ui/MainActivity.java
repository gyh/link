package com.example.market.ljw.ui;


import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.*;
import android.os.Process;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.market.ljw.R;
import com.example.market.ljw.core.common.frame.AppContext;
import com.example.market.ljw.core.common.frame.BaseActivity;
import com.example.market.ljw.core.common.frame.taskstack.ApplicationManager;
import com.example.market.ljw.core.common.frame.taskstack.BackStackManager;
import com.example.market.ljw.core.common.http.HttpGroup;
import com.example.market.ljw.core.common.http.HttpResponse;
import com.example.market.ljw.core.utils.Constant;
import com.example.market.ljw.core.utils.DateUtils;
import com.example.market.ljw.core.utils.MiaoshaUtil;
import com.example.market.ljw.core.utils.MyCountdownTimer;
import com.example.market.ljw.core.utils.PopUtils;
import com.example.market.ljw.core.utils.PromptUtil;
import com.example.market.ljw.core.utils.Utils;
import com.example.market.ljw.entity.bean.Entity;
import com.example.market.ljw.entity.bean.output.Member;
import com.example.market.ljw.entity.bean.output.MemberOutput;
import com.example.market.ljw.fragment.AppListFragment;
import com.example.market.ljw.fragment.CarouselFragment;
import com.example.market.ljw.fragment.MarketListFragment;
import com.example.market.ljw.fragment.WebViewFragment;
import com.example.market.ljw.function.floatwindow.LjwService;
import com.example.market.ljw.service.InputDataUtils;
import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.lang.Thread.sleep;


public class MainActivity extends BaseActivity {
    public static String tagName = "";
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
    //倒计时工具类
    private MiaoshaUtil localMiaoShaUtil;
    //判断是否停止
    private boolean isHasStop = false;
    //判断是否是第一次开始
    private boolean isHasFirstStart = true;
    //主线程
    private boolean isMainRun = true;
    //能够挂积分
    private static final int START_RHANGING_POINTS = 0;
    private static final int STOP_RHANGING_POINTS = 1;
    private static final int RESTART_RHANGING_POINTS = 2;
    private static final int NO_RHANGING_POINTS = 3;
    //微信登录后接受广播
    private PreventReceiveBroadCast preventReceiveBroadCast;
    //积分获取后改变的ui线程
    Handler refhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case START_RHANGING_POINTS:
                    startTimeNum();
                    break;
                case STOP_RHANGING_POINTS:
                    Constant.member.setDuration(0);
                    Constant.member.setTodayScore(0);
                    localMiaoShaUtil.countdownCancel();
                    break;
                case RESTART_RHANGING_POINTS:
                    startTimeNum();
                    break;
            }
            tvnumber.setText("积分：" + Constant.member.getTodayScore());
            DateUtils.setCurTimeToView(tvtime, Constant.member.getDuration());
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tagName = this.getClass().getName();
        setContentView(R.layout.activity_ljw);
        //清除碎片栈
        ApplicationManager.clearBackStack();
        //定义广播
        preventReceiveBroadCast = new PreventReceiveBroadCast();
        IntentFilter filter = new IntentFilter(); //只有持有相同的action的接受者才能接收此广播
        filter.addAction(Constant.ReceiveBroadCastKey.PREVENTBROAD_FLAG);
        registerReceiver(preventReceiveBroadCast, filter);

        //设置main
        AppContext.getInstance().setMainActivity(this.getClass());
        //初始化视图
        initView();
        getMemberInfo();//开始
        //添加广告图片
        CarouselFragment carouselFragment = new CarouselFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.carouselfragment, carouselFragment).commit();
        //列表
        AppListFragment.AppListFragmentTM appListFragmentTM = new AppListFragment.AppListFragmentTM(R.id.contain);
        ApplicationManager.go(appListFragmentTM);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Constant.theNextLen = Constant.theWaitTime;//重置倒计时时间
        Constant.makeAppName = Constant.PACKAGENAME;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (localMiaoShaUtil != null) {
            localMiaoShaUtil.countdownCancel();
        }
        if(preventReceiveBroadCast !=null){
            unregisterReceiver(preventReceiveBroadCast); //注销此广播
        }
        isMainRun = false;
        AppContext.getInstance().setMainActivity(null);
    }

    /**
     * 初始化视图
     */
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
    }

    /**
     * 获取用户信息
     */
    private void getMemberInfo() {
        if (Constant.FromWhere.LOGINACTIVITY.equals(getIntent().getStringExtra(Constant.FromWhere.KEY))) {
            Constant.member = (Member) getIntent().getSerializableExtra(Constant.ExtraKey.MEMBER);
            Map<String, Object> param = new LinkedHashMap<String, Object>();
            param.put(Constant.RequestKeys.SERVICENAME, "get_member_info");
            param.put(Constant.RequestKeys.DATA, gson.toJson(InputDataUtils.getUserInfo(Constant.member.getID() + "")));
            execute(Constant.SERVER_URL, true, param, null, new HttpGroup.OnEndListener() {
                @Override
                public void onEnd(HttpResponse httpresponse) {
                    MemberOutput memberOuput = (MemberOutput) httpresponse.getResultObject();
                    if (memberOuput.isSuccess()) {
                        Constant.member = memberOuput.getMember();
                        initLJWPrcoss();
                    } else {
                        PopUtils.showToast(memberOuput.getErrmsg());
                    }

                }
            }, new HttpGroup.OnParseListener() {
                @Override
                public Entity onParse(String result) {
                    MemberOutput memberOuput = new MemberOutput();
                    memberOuput.setContent(result);
                    return memberOuput;
                }
            });
        } else if (Constant.FromWhere.FXSERVICE.equals(getIntent().getStringExtra(Constant.FromWhere.KEY))) {
            initLJWPrcoss();
        }
    }

    /**
     * 获取数据成功后初始化流程
     */
    private void initLJWPrcoss() {
        isMainRun = true;
        isHasFirstStart = true;
        try {
            Constant.intervalTime = Long.valueOf(Constant.member.getClientSubmitInterval());//心跳时间
        }catch (Exception e){
            startActivity(new Intent(this,WelcomeActivity.class));
            this.finish();
        }
        localMiaoShaUtil = new MiaoshaUtil();//倒计时
        initViewData();
        mainThread();
        if (!checkDicServiceWorked()) {
            Intent dirUpdataService = new Intent(MainActivity.this, LjwService.class);
            startService(dirUpdataService);
        }
    }

    /**
     * 初始化视图数据
     */
    private void initViewData() {
        tvusername.setText("账号：" + Constant.member.getLoginName());
        tvnumber.setText("积分：" + Constant.member.getTodayScore());
        logimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getCurrentMyActivity() instanceof WebViewFragment) {
                    ApplicationManager.back();
                }
            }
        });

    }

    private void mainThread() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
                while (isMainRun) {
                    //判断是否能够挂积分了
                    if (Utils.isCanAddScore(Constant.member.getServerTime(), MainActivity.this)&&isHasFirstStart) {
                        refhandler.sendEmptyMessage(START_RHANGING_POINTS);
                        isHasFirstStart = false;
                        isHasStop = false;
                    } else if(Utils.isCanAddScore(Constant.member.getServerTime(), MainActivity.this)&&isHasStop){
                        refhandler.sendEmptyMessage(RESTART_RHANGING_POINTS);
                        isHasStop = false;
                    }else if(!Utils.isCanAddScore(Constant.member.getServerTime(), MainActivity.this)&&!isHasStop){
                        isHasStop = true;
                        refhandler.sendEmptyMessage(STOP_RHANGING_POINTS);
                    }
                }
            }
        }).start();

    }


    /**
     * 检查字典service是否运行
     *
     * @return
     */
    private static boolean checkDicServiceWorked() {
        ActivityManager myManager = (ActivityManager) AppContext.getInstance().getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> services = (ArrayList<ActivityManager.RunningServiceInfo>) myManager.getRunningServices(30);
        for (int i = 0; i < services.size(); i++) {
            if (LjwService.class.getName(). //如果运行的service有字典数据检查service的话
                    equals(services.get(i).service.getClassName().toString())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 开始计数
     */
    private void startTimeNum() {
        localMiaoShaUtil.setCountdown(Constant.member.getDuration() * 1000, System.currentTimeMillis() + Constant.ENDTIME, new MiaoshaUtil.CountDownListener() {
            @Override
            public void changed(MyCountdownTimer paramMyCountdownTimer, long residueTime, long[] threeTimePoint, int what) {
                //获取现在的挂机时间
                //threeTimePoint[0] * 60 * 60 * 1000 - threeTimePoint[1] * 60 * 1000 - threeTimePoint[2] * 1000 == 剩余时间
                //需要解决的问题是怎么怎么计算剩余时间
                long duration = (Constant.ENDTIME - threeTimePoint[0] * 60 * 60 * 1000 - threeTimePoint[1] * 60 * 1000 - threeTimePoint[2] * 1000) / 1000;
                Constant.member.setDuration(duration);
//                Utils.showSystem("changed", "" + (Constant.ENDTIME - threeTimePoint[0] * 60 * 60 * 1000 - threeTimePoint[1] * 60 * 1000 - threeTimePoint[2] * 1000) / 1000);
                refhandler.sendEmptyMessage(NO_RHANGING_POINTS);
            }

            @Override
            public boolean finish(MyCountdownTimer paramMyCountdownTimer, long endRemainTime, int what) {
                Constant.member.setDuration(0);
                Constant.member.setTodayScore(0);
                refhandler.sendEmptyMessage(NO_RHANGING_POINTS);
                return false;
            }
        });
    }


    /**
     * 初始化商城列表
     */
    public void showMarkList() {
        MarketListFragment.MarketListFragmentTM marketListFragmentTM = new MarketListFragment.MarketListFragmentTM(R.id.contain);
        ApplicationManager.go(marketListFragmentTM);
    }

    /**
     * 显示Webview
     */
    public void showWebView(int type, String url) {
        switch (type) {
            case 0:
                WebViewFragment.WebViewFragmentTM webViewFragmentTM = new WebViewFragment.WebViewFragmentTM(R.id.contain);
                Bundle bundle = new Bundle();
                bundle.putString(Constant.ValueKey.URLKEY, url);
                webViewFragmentTM.setBundle(bundle);
                ApplicationManager.go(webViewFragmentTM);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (BackStackManager.getInstance().isLast()) {
                // 创建退出对话框
                final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(this);
                PromptUtil.showExitAlert(dialogBuilder, MainActivity.this, Effectstype.Fall, ljwview,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogBuilder.dismiss();
                                isMainRun = false;
                                AppContext.clearActivitys();
                            }
                        });
            }
            if (getCurrentMyActivity() instanceof WebViewFragment && WebViewFragment.mWebView.canGoBack()) {
                WebViewFragment.mWebView.goBack();
                return true;
            } else {
                ApplicationManager.back();
            }
        }
        return false;
    }

    /**
     * 注册广播，防止销毁
     */
    class PreventReceiveBroadCast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent){

        }
    }
}

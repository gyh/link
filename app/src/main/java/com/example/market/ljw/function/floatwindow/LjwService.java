package com.example.market.ljw.function.floatwindow;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.widget.Toast;

import com.example.market.ljw.core.common.frame.MyAppContext;
import com.example.market.ljw.core.utils.MiaoshaUtil;
import com.example.market.ljw.core.utils.MyCountdownTimer;
import com.example.market.ljw.core.utils.PromptUtil;
import com.example.market.ljw.entity.bean.output.Member;
import com.example.market.ljw.entity.bean.output.MemberOutput;
import com.example.market.ljw.receiver.ScreenBCReceiver;
import com.example.market.ljw.service.InputDataUtils;
import com.example.market.ljw.core.utils.Constant;
import com.example.market.ljw.core.utils.Utils;
import com.example.market.ljw.ui.MainActivity;
import com.example.market.ljw.ui.WelcomeActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class LjwService extends Service {
    //锁屏管理
    private KeyguardManager keyguardManager = null;
    private KeyguardManager.KeyguardLock keyguardLock = null;
    //跳转锁屏界面intent
//    private Intent lockIntent;
    //防止页面锁屏
    private Intent intentNoset;
    private boolean isShowView = false;
    private MiaoshaUtil serviceMiaoshaUtil;
    //链接网服务数据请求
    private LjwServiceData ljwServiceData;
    //跳转到主页
    private static final int GO_MAIN = 0;
    //隐藏小图标
    private static final int HIDDEN_SMALLWINDOW = 1;
    //显示小图标
    private static final int SHOW_SMALLWINDOW = 2;
    //广播
    private ScreenBCReceiver mScreenBCR;
    //用户数据
    private Member member;

    Handler ljwServicehandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //比较当前应用的包名是否是应用包名或者打开的包名
            switch (msg.what) {
                case GO_MAIN:
                    goToMainActivity();
                    break;
                case HIDDEN_SMALLWINDOW:
                    MyWindowManager.hiddenSmallWindow();
                    break;
                case SHOW_SMALLWINDOW:
                    MyWindowManager.showSmallWindow();
                    break;
            }
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();
        //创建浮动窗口
        MyWindowManager.createSmallWindow(getApplicationContext());
        //防止锁屏
        intentNoset = new Intent();  //Itent就是我们要发送的内容
        //设置你这个广播的action，只有和这个action一样的接受者才能接受者才能接收广播
        intentNoset.setAction(Constant.ReceiveBroadCastKey.PREVENTBROAD_FLAG);
        //链接网积分请求
        ljwServiceData = new LjwServiceData();
        //创建锁屏对象
        keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        keyguardLock = keyguardManager.newKeyguardLock("");
        //注册锁屏广播
        IntentFilter mScreenOffFilter = new IntentFilter();
        mScreenOffFilter.addAction(Intent.ACTION_SCREEN_OFF);
        mScreenOffFilter.addAction(Intent.ACTION_SCREEN_ON);
        mScreenBCR = new ScreenBCReceiver();
        registerReceiver(mScreenBCR, mScreenOffFilter);
        member = new Member();
        //初始化服务线程
        initUserData();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        //log
        Utils.showSystem("service ", "destroy");
        //结束服务线程
        serviceMiaoshaUtil.countdownCancel();
        //隐藏浮动窗口
        MyWindowManager.hiddenSmallWindow();
        //注销锁屏广播
        unregisterReceiver(mScreenBCR);
        super.onDestroy();
    }

    /**
     * 跳转到主页面
     */
    private void goToMainActivity() {
        Constant.makeAppName = Constant.PACKAGENAME;
        Intent intent2 = new Intent(this,MainActivity.class);
        intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent2);
    }


    private void initUserData() {
        Ion.with(MyAppContext.getInstance())
                .load(Constant.SERVER_URL)
                .setBodyParameter(Constant.RequestKeys.SERVICENAME, "get_member_info")
                .setBodyParameter(Constant.RequestKeys.DATA, new Gson().toJson(InputDataUtils.getUserInfo(
                        MyAppContext.getInstance().getDataForShPre("userId", "")
                )))
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null) {
                            return;
                        }
                        MemberOutput memberOuput = new MemberOutput();
                        try {
                            memberOuput.setContent(result.toString());
                        } catch (Exception e1) {
                            e1.printStackTrace();
                            PromptUtil.showMessage(LjwService.this, e1.toString());
                        }
                        if (memberOuput.isSuccess()) {
                            member.setTodayScore(memberOuput.getMember().getTodayScore());
                            member.setLoginName(memberOuput.getMember().getLoginName());
                            member.setDuration(memberOuput.getMember().getDuration());
                            member.setID(Long.valueOf(MyAppContext.getInstance().getDataForShPre("userId", "")));
                            member.setClientSubmitInterval(memberOuput.getMember().getClientSubmitInterval());
                            intentNoset.putExtra(MainActivity.MEMBERDATAKEY, member);
                            sendBroadcast(intentNoset);//发送广播
                            initLjwServiceThread();
                        } else {
                            PromptUtil.showMessage(LjwService.this, memberOuput.getErrmsg());
                        }
                    }
                });

    }

    /**
     * 初始化服务现场
     */
    private void initLjwServiceThread() {
        serviceMiaoshaUtil = new MiaoshaUtil();
        serviceMiaoshaUtil.setCountdown(0, System.currentTimeMillis() + Constant.ENDTIME, new MiaoshaUtil.CountDownListener() {
            @Override
            public void changed(MyCountdownTimer paramMyCountdownTimer, long residueTime, long[] threeTimePoint, int what) {
                //设置桌面图标展示数据
                setClockhandler();
                Utils.showSystem("MainActivity.currentStats",MainActivity.currentStats+"");
                if (MainActivity.currentStats == Constant.MainActivityState.OVER) {
                    return;
                }
                if (MainActivity.currentStats == Constant.MainActivityState.CREATE) {
                    intentNoset.putExtra(MainActivity.MEMBERDATAKEY, member);
                    sendBroadcast(intentNoset);//发送广播
                }
                //设置服务器请求
                ljwServiceData.setServiceDataRequest();
            }

            @Override
            public boolean finish(MyCountdownTimer paramMyCountdownTimer, long endRemainTime, int what) {
                return false;
            }
        });
    }

    /**
     * 设置桌面图标展示数据
     */
    private void setClockhandler() {
        if (Utils.isAppOnForeground(LjwService.this)) {
            if(MainActivity.currentStats == Constant.MainActivityState.OVER){
                ljwServicehandler.sendEmptyMessage(HIDDEN_SMALLWINDOW);
                return;
            }
            if (Constant.makeAppName.equals(Constant.MMSPACKAGENAME) ||
                    Constant.makeAppName.equals(Constant.CONTACTSPACKAGENAME)) {
                ljwServicehandler.sendEmptyMessage(GO_MAIN);
            } else {
                ljwServicehandler.sendEmptyMessage(SHOW_SMALLWINDOW);
            }
        }else {
            ljwServicehandler.sendEmptyMessage(HIDDEN_SMALLWINDOW);
        }
    }

    /**
     * 挂积分
     */
    class LjwServiceData {

        //记录上一次提交时间
        String mRequetTimeInFuture = Utils.getCurrentDate();

        /**
         * 设置服务器请求
         * 记录上次请求时间
         * 判断这次循环是否需要请求
         */
        public void setServiceDataRequest() {
            //判断是么时候能够请求 （判断请求间隔）
            if (Utils.CalculationInterval(mRequetTimeInFuture, Utils.getCurrentDate()) >= Long.valueOf(member.getClientSubmitInterval())) {
                setDataToService();
            }
        }

        /**
         * 向服务器提交积分
         */
        private void setDataToService() {
            if (member == null) {
                return;
            }
            //设置请求数据时间
            mRequetTimeInFuture = Utils.getCurrentDate();

            member.setDuration(member.getDuration() + Long.valueOf(member.getClientSubmitInterval()));

            //没有网络的情况不需要请求
            if (!Utils.isNetworkConnected(LjwService.this)) {
                intentNoset.putExtra(MainActivity.MEMBERDATAKEY, member);
                sendBroadcast(intentNoset);//发送广播
                return;
            }

            Ion.with(MyAppContext.getInstance())
                    .load(Constant.SERVER_URL)
                    .setBodyParameter(Constant.RequestKeys.SERVICENAME, "submit_score")
                    .setBodyParameter(Constant.RequestKeys.DATA, new Gson().toJson(InputDataUtils.submitScore
                            (MyAppContext.getInstance().getDataForShPre(Constant.SaveKeys.TOKENKEY, ""),
                                    member.getDuration(), member.getID() + "")))
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (e != null) {
                                return;
                            }
                            MemberOutput memberOuput = new MemberOutput();
                            try {
                                memberOuput.setContent(result.toString());
                            } catch (Exception e1) {
                                e1.printStackTrace();
                                PromptUtil.showMessage(LjwService.this, e1.toString());
                            }
                            if (memberOuput.isSuccess()) {
                                member.setTodayScore(memberOuput.getMember().getTodayScore());
                                member.setDuration(memberOuput.getMember().getDuration());
                                intentNoset.putExtra(MainActivity.MEMBERDATAKEY, member);
                                sendBroadcast(intentNoset);//发送广播
                            } else {
                                PromptUtil.showMessage(LjwService.this, memberOuput.getErrmsg());
                            }
                        }
                    });

        }
    }
}


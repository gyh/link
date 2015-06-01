package com.example.market.ljw.function.floatwindow;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;

import com.example.market.ljw.core.common.http.HttpGroup;
import com.example.market.ljw.core.common.http.HttpGroupSetting;
import com.example.market.ljw.core.common.http.HttpResponse;
import com.example.market.ljw.core.common.http.HttpSetting;
import com.example.market.ljw.core.utils.MiaoshaUtil;
import com.example.market.ljw.core.utils.MyCountdownTimer;
import com.example.market.ljw.core.utils.Util;
import com.example.market.ljw.entity.bean.Entity;
import com.example.market.ljw.entity.bean.output.MemberOutput;
import com.example.market.ljw.service.InputDataUtils;
import com.example.market.ljw.R;
import com.example.market.ljw.core.common.frame.AppContext;
import com.example.market.ljw.glowpadview.LockActivity;
import com.example.market.ljw.core.utils.Constant;
import com.example.market.ljw.core.utils.Utils;
import com.example.market.ljw.ui.MainActivity;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import java.util.Map;

public class LjwService extends Service implements View.OnClickListener {
    //锁屏管理
    private KeyguardManager keyguardManager = null;
    private KeyguardManager.KeyguardLock keyguardLock = null;
    //跳转锁屏界面intent
    private Intent lockIntent;
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
    //
    Handler ljwServicehandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //比较当前应用的包名是否是应用包名或者打开的包名
            switch (msg.what){
                case GO_MAIN:
                    goToMainActivity();
                    break;
                case HIDDEN_SMALLWINDOW:
                    MyWindowManager.hiddenSmallWindow();
                    break;
                case SHOW_SMALLWINDOW:
                    Constant.theNextLen = Constant.theNextLen - 1;
                    MyWindowManager.showSmallWindow();
                    break;
            }
        }
    };

    /**
     * 锁频广播监听
     */
    private BroadcastReceiver mScreenBCR = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //判断如果列表为空
            if(AppContext.getTempActivitySize()==0 || AppContext.getInstance().getMainActivity() == null){
                ljwServicehandler.sendEmptyMessage(HIDDEN_SMALLWINDOW);
                return;
            }
            if (Constant.isShowLock &&
                    !Utils.getFirstTask(LjwService.this).equals(Constant.PHONEPACKAGENAME) &&
                    (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)
                            || intent.getAction().equals(Intent.ACTION_SCREEN_ON))) {
                Constant.isShowLock = false;
                Constant.TimeSystemCal.TEMPTIMESYSTEM = Utils.getCurrentDate();
                keyguardLock.disableKeyguard(); //这里就是取消系统默认的锁屏
                startActivity(lockIntent); //注意这里跳转的意图
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        //创建浮动窗口
        MyWindowManager.createSmallWindow(getApplicationContext());
        //初始化锁屏界面跳转intent
        lockIntent = new Intent(LjwService.this, LockActivity.class);
        lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //链接网积分请求
        ljwServiceData = new LjwServiceData();
        //创建锁屏对象
        keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        keyguardLock = keyguardManager.newKeyguardLock("");
        //注册锁屏广播
        IntentFilter mScreenOffFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mScreenBCR, mScreenOffFilter);
        //初始化服务线程
        initLjwServiceThread();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        //log
        Utils.showSystem("service ","destroy");
        //结束服务线程
        serviceMiaoshaUtil.countdownCancel();
        //隐藏浮动窗口
        MyWindowManager.hiddenSmallWindow();
        //注销锁屏广播
        unregisterReceiver(mScreenBCR);
        super.onDestroy();
    }


    @Override
    public void onClick(View view) {
        int i = view.getId();
        //设置事件监听跳转到页面
        if (i == R.id.ljw_float_img) {
            if (AppContext.getInstance().getBaseActivity() != null) {
                ljwServicehandler.sendEmptyMessage(GO_MAIN);
            } else {
                serviceMiaoshaUtil.countdownCancel();
                LjwService.this.stopSelf();
            }
        }
    }

    /**
     * 跳转到主页面
     * */
    private void goToMainActivity(){
        Constant.makeAppName = Constant.PACKAGENAME;
        Intent intent = Utils.getLJWAppIntent2(LjwService.this);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constant.FromWhere.KEY, Constant.FromWhere.FXSERVICE);
        startActivity(intent);
    }

    /**
     * 初始化服务现场
     */
    private void initLjwServiceThread() {
        serviceMiaoshaUtil = new MiaoshaUtil();
        serviceMiaoshaUtil.setCountdown(0, System.currentTimeMillis() + Constant.ENDTIME, new MiaoshaUtil.CountDownListener() {
            @Override
            public void changed(MyCountdownTimer paramMyCountdownTimer, long residueTime, long[] threeTimePoint, int what) {
                //获取现在的挂机时间
                //threeTimePoint[0] * 60 * 60 * 1000 - threeTimePoint[1] * 60 * 1000 - threeTimePoint[2] * 1000 == 剩余时间
                //需要解决的问题是怎么怎么计算剩余时间
                long duration = (Constant.ENDTIME - threeTimePoint[0] * 60 * 60 * 1000 - threeTimePoint[1] * 60 * 1000 - threeTimePoint[2] * 1000) / 1000;
                if(AppContext.getTempActivitySize()==0 || AppContext.getInstance().getMainActivity() == null){
                    ljwServicehandler.sendEmptyMessage(HIDDEN_SMALLWINDOW);
                    return;
                }
                setClockhandler();
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
     * */
    private void setClockhandler(){
        //获取当前应用的包名
        isShowView = Utils.isAppOnForeground(LjwService.this);
        //判断等待时间是否结束
        if (isShowView) {
            if (Constant.makeAppName.equals(Constant.MMSPACKAGENAME) ||
                    Constant.makeAppName.equals(Constant.CONTACTSPACKAGENAME)) {
                ljwServicehandler.sendEmptyMessage(GO_MAIN);
            } else {
                if (Constant.theNextLen <= 0) {
                    ljwServicehandler.sendEmptyMessage(GO_MAIN);
                } else {
                    ljwServicehandler.sendEmptyMessage(SHOW_SMALLWINDOW);
                }
            }
        } else {
            ljwServicehandler.sendEmptyMessage(HIDDEN_SMALLWINDOW);
        }
    }

    /**
     * 挂积分
     * */
    class LjwServiceData{

        //记录上一次提交时间
        String  mRequetTimeInFuture = Utils.getCurrentDate();
        /**
         * 设置服务器请求
         * 记录上次请求时间
         * 判断这次循环是否需要请求
         * */
        public void setServiceDataRequest(){
            //没有网络的情况不需要请求
            if(!Utils.isNetworkConnected(LjwService.this)){
                return;
            }
            //判断是么时候能够请求 （判断请求间隔）
            if (Utils.CalculationInterval(mRequetTimeInFuture,Utils.getCurrentDate())>= Constant.intervalTime) {
                setDataToService();
            }
        }
        /**
         * 向服务器提交积分
         */
        private void setDataToService() {
            if(Constant.member==null){
                return;
            }
            //设置请求数据时间
            mRequetTimeInFuture = Utils.getCurrentDate();
            //准备提交数据
            Map<String, Object> param = new LinkedHashMap<String, Object>();
            param.put(Constant.RequestKeys.SERVICENAME, "submit_score");
            param.put(Constant.RequestKeys.DATA, new Gson().toJson(InputDataUtils.submitScore
                    (AppContext.getInstance().getBaseActivity().getDataForShPre(Constant.SaveKeys.TOKENKEY, ""),
                            Constant.member.getDuration(), Constant.member.getMemberID() + "")));
            execute(Constant.SERVER_URL, false, param, null, new HttpGroup.OnEndListener() {
                @Override
                public void onEnd(HttpResponse httpresponse) {
                    //判断是否数据
                    MemberOutput memberOuput = (MemberOutput) httpresponse.getResultObject();
                    if (memberOuput.isSuccess()) {
                        //如果返回为空的话
                        Constant.member.setTodayScore(memberOuput.getMember().getTodayScore());//重新设置当日积分
                        Constant.member.setServerTime(memberOuput.getMember().getServerTime());//重新设置服务器时间
                        Utils.showSystem("duration",Constant.member.getDuration()+"");
                    } else {
                        //返回错误的话
                        Utils.showSystem("duration", memberOuput.getErrmsg());
                    }
                }
            }, new HttpGroup.OnParseListener() {
                @Override
                public Entity onParse(String result) {
                    //解析数据
                    MemberOutput memberOuput = new MemberOutput();
                    memberOuput.setContent(result);
                    return memberOuput;
                }
            },false);
        }


        /**
         * 加载json数据核心方法
         * @param finalUrl 接口基础地址
         * @param paramMap 请求参数
         * @param dataClass 返回数据类型
         * @param onEndListener ui回调函数
         * @param isPost 是否是post请求
         * @param onParseListener 解析监听
         * @param isSafe 是否是安全链接
         */
        public void execute(String finalUrl,boolean isPost, Map<String, Object> paramMap,
                            Class dataClass, HttpGroup.OnEndListener onEndListener,
                            HttpGroup.OnParseListener onParseListener,boolean isSafe) {
            HttpGroup httpGroup = getHttpGroupaAsynPool();
            HttpSetting httpSetting = new HttpSetting();
            httpSetting.setParamMaps(paramMap);
            httpSetting.setSafe(isSafe);
            httpSetting.setFinalUrl(finalUrl);
            httpSetting.setCurrentEntity(dataClass);
            httpSetting.setPost(isPost);
            httpSetting.setType(HttpGroupSetting.TYPE_JSON);
            httpSetting.setPriority(HttpGroupSetting.PRIORITY_JSON);
            httpSetting.setCacheMode(HttpSetting.CACHE_MODE_ONLY_NET);
            httpSetting.setListener(onEndListener);
            httpSetting.setListener(onParseListener);
            httpGroup.add(httpSetting);
        }

        /**
         * 获取图片,并附加到视图上
         * @param imageUrl
         * @param onEndListener
         */
        public void executeImage(String imageUrl, HttpGroup.OnEndListener onEndListener) {
            HttpGroup httpGroup = getHttpGroupaAsynPool();
            HttpSetting httpSetting = new HttpSetting();
            httpSetting.setFinalUrl(imageUrl);
            httpSetting.setType(HttpGroupSetting.TYPE_IMAGE);
            httpSetting.setPriority(HttpGroupSetting.PRIORITY_IMAGE);
            httpSetting.setCacheMode(HttpSetting.CACHE_MODE_AUTO);
            httpSetting.setListener(onEndListener);
            httpGroup.add(httpSetting);
        }


        /**
         * 获取数据的请求池子
         * @return
         */
        public HttpGroup getHttpGroupaAsynPool() {
            return getHttpGroupaAsynPool(HttpGroupSetting.TYPE_JSON);
        }

        /**
         * 获取不同类型请求数据的池子
         * @param type
         * @return
         */
        public HttpGroup getHttpGroupaAsynPool(int type) {
            HttpGroupSetting localHttpGroupSetting = new HttpGroupSetting();
            localHttpGroupSetting.setType(type);
            return getHttpGroupaAsynPool(localHttpGroupSetting);
        }

        /**
         * 获取请求的数据池子
         * @param paramHttpGroupSetting
         * @return
         */
        public HttpGroup getHttpGroupaAsynPool(
                final HttpGroupSetting paramHttpGroupSetting) {
            HttpGroup.HttpGroupaAsynPool localHttpGroupaAsynPool = new HttpGroup.HttpGroupaAsynPool(
                    paramHttpGroupSetting);
            return localHttpGroupaAsynPool;
        }
    }
}


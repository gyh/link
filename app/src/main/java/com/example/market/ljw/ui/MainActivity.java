package com.example.market.ljw.ui;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
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
import com.example.market.ljw.core.utils.UtilsServer;
import com.example.market.ljw.entity.bean.Entity;
import com.example.market.ljw.entity.bean.output.Member;
import com.example.market.ljw.entity.bean.output.MemberOutput;
import com.example.market.ljw.fragment.AppListFragment;
import com.example.market.ljw.fragment.CarouselFragment;
import com.example.market.ljw.fragment.MarketListFragment;
import com.example.market.ljw.fragment.WebViewFragment;
import com.example.market.ljw.function.floatwindow.LjwService;
import com.example.market.ljw.function.floatwindow.ServiceInterface;
import com.example.market.ljw.service.InputDataUtils;
import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;

import java.util.LinkedHashMap;
import java.util.Map;

import static java.lang.Thread.sleep;


public class MainActivity extends BaseActivity {

    private Member member;//本地的用户数据信息
    private TextView tvusername;//用户名显示view
    private TextView tvnumber;//积分显示视图
    private TextView tvtime;//时间显示视图
    private FrameLayout ljwview;//背景视图
    private View logimg;
    private final long delayMillis = 1000;//定义的一秒
    private long intervalTime = 10; //定义的心跳
    private boolean isRunning = false;//定义是否开启线程
    private Intent intentfxService = null;//悬浮窗口配置
    private long mRequetTimeInFuture = 0;//记录上一次提交时间
    private boolean isCanAddscore = true;
    //倒计时工具类
    private MiaoshaUtil localMiaoShaUtil;
    Handler refhandler = new Handler() {//积分获取后改变的ui线程
        @Override
        public void handleMessage(Message msg) {
            Constant.member = member;
            tvnumber.setText("积分：" + member.getTodayScore());
            DateUtils.setCurTimeToView(tvtime, member.getDuration());
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ljw);
        ApplicationManager.clearBackStack();
        AppContext.getInstance().setMainActivity(this.getClass());
        initView();
        //添加广告图片
        CarouselFragment carouselFragment = new CarouselFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.carouselfragment, carouselFragment).commit();

        AppListFragment.AppListFragmentTM appListFragmentTM = new AppListFragment.AppListFragmentTM(R.id.contain);
        ApplicationManager.go(appListFragmentTM);


        getMemberInfo();//开始
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
        unbindService(sconnection);
        super.onDestroy();
        isRunning = false;//结束积分线程
        if (localMiaoShaUtil != null)
            localMiaoShaUtil.countdownCancel();
        Utils.showSystem("onDestroy", isRunning + "");
    }

    /**
     * 获取用户信息
     */
    private void getMemberInfo() {
        Utils.showSystem("getMemberInfo", "-----重新开始初始化");
        Intent ljwintent = getIntent();
        if (Constant.FromWhere.LOGINACTIVITY.equals(ljwintent.getStringExtra(Constant.FromWhere.KEY))) {
            member = (Member) getIntent().getSerializableExtra(Constant.ExtraKey.MEMBER);
            Constant.member = member;
            Map<String, Object> param = new LinkedHashMap<String, Object>();
            param.put(Constant.RequestKeys.SERVICENAME, "get_member_info");
            param.put(Constant.RequestKeys.DATA, gson.toJson(InputDataUtils.getUserInfo(member.getID() + "")));
            execute(Constant.SERVER_URL, true, param, null, new HttpGroup.OnEndListener() {
                @Override
                public void onEnd(HttpResponse httpresponse) {
                    MemberOutput memberOuput = (MemberOutput) httpresponse.getResultObject();
                    if (memberOuput.isSuccess()) {
                        initLJWPrcoss(memberOuput.getMember());
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
        } else if (Constant.FromWhere.FXSERVICE.equals(ljwintent.getStringExtra(Constant.FromWhere.KEY))) {
            initLJWPrcoss(Constant.member);
        }
    }

    /**
     * 获取数据成功后初始化流程
     */
    private void initLJWPrcoss(Member member) {
        isRunning = true;//提交积分线程
        this.member = member;//获取用户数据
        intervalTime = Long.valueOf(member.getClientSubmitInterval());//心跳时间
        isCanAddscore = Utils.isCanAddScore(member.getServerTime());//判断服务器时间能够增长积分
        localMiaoShaUtil = new MiaoshaUtil();//倒计时
        initViewData();
        if (isCanAddscore) {
            startTimeNum();
        }
        initData();
        initService();
    }

    /**
     * 开始计数
     */
    private void startTimeNum() {
        localMiaoShaUtil.setCountdown(member.getDuration()*1000, System.currentTimeMillis() + Constant.ENDTIME, new MiaoshaUtil.CountDownListener() {
            @Override
            public void changed(MyCountdownTimer paramMyCountdownTimer, long residueTime, long[] threeTimePoint, int what) {
                member.setDuration((Constant.ENDTIME - threeTimePoint[0] * 60 * 60 * 1000 - threeTimePoint[1] * 60 * 1000 - threeTimePoint[2] * 1000) / 1000);
                Utils.showSystem("changed",""+(Constant.ENDTIME - threeTimePoint[0] * 60 * 60 * 1000 - threeTimePoint[1] * 60 * 1000 - threeTimePoint[2] * 1000) / 1000);
                refhandler.sendEmptyMessage(1);
            }
            @Override
            public boolean finish(MyCountdownTimer paramMyCountdownTimer, long endRemainTime, int what) {
                member.setDuration(0);
                member.setTodayScore(0);
                refhandler.sendEmptyMessage(1);
                return false;
            }
        });
    }

    /**
     * 初始化视图
     */
    private void initView() {
        logimg = findViewById(R.id.logimg);
        ljwview = (FrameLayout)findViewById(R.id.layoutljw);
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
     * 初始化视图数据
     * */
    private void initViewData(){
        tvusername.setText("账号：" + member.getLoginName());
        tvnumber.setText("积分：" + member.getTodayScore());
        logimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getCurrentMyActivity() instanceof WebViewFragment) {
                    ApplicationManager.back();
                }
            }
        });

    }

    /**
     * 初始化数据，开启提交积分线程
     */
    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //设置该线程的优先等级为最高
                Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
                while (isRunning) {//提交数据循环
                    try {
                        sleep(delayMillis*10);//心跳时间
                        Utils.showSystem("gyh server",member.getServerTime());
                        if (!Utils.isNetworkConnected(MainActivity.this)) {//判断是否有网络
                            //没有网络的时候,修改服务器时间
                            member.setServerTime(UtilsServer.modifyServerTime(member.getServerTime(),10));
                            isCanAddscore = Utils.isCanAddScore(member.getServerTime());
                            if (!isCanAddscore && localMiaoShaUtil != null) {
                                localMiaoShaUtil.countdownCancel();
                                member.setDuration(0);
                                member.setTodayScore(0);
                                refhandler.sendEmptyMessage(1);
                            } else if (isCanAddscore && member.getDuration() == 0) {
                                startTimeNum();
                            }
                        } else {
                            //有网络的时候
                            if (mRequetTimeInFuture == 0) {
                                setDataToService();
                            } else if ((member.getDuration() - mRequetTimeInFuture) >= intervalTime/2 || member.getDuration() - mRequetTimeInFuture<=0) {
                                setDataToService();
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /**
     * 向服务器提交积分
     */
    private void setDataToService() {
        mRequetTimeInFuture = member.getDuration();
        //准备提交数据
        Map<String, Object> param = new LinkedHashMap<String, Object>();
        param.put(Constant.RequestKeys.SERVICENAME, "submit_score");
        param.put(Constant.RequestKeys.DATA, gson.toJson(InputDataUtils.submitScore
                (getDataForShPre(Constant.SaveKeys.TOKENKEY, ""), member.getDuration(), member.getMemberID() + "")));
        execute(Constant.SERVER_URL, false, param, null, new HttpGroup.OnEndListener() {
            @Override
            public void onEnd(HttpResponse httpresponse) {
                //判断是否数据
                MemberOutput memberOuput = (MemberOutput) httpresponse.getResultObject();
                if (memberOuput.isSuccess()) {
                    //如果返回为空的话
                    member.setTodayScore(memberOuput.getMember().getTodayScore());//重新设置当日积分
                    member.setServerTime(memberOuput.getMember().getServerTime());//重新设置服务器时间
//                    member.setDuration(memberOuput.getMember().getDuration());
                    Utils.showSystem("duration",member.getDuration()+"");
                    isCanAddscore = Utils.isCanAddScore(member.getServerTime());//判断服务器时间能够增长积分
                    if (!isCanAddscore && localMiaoShaUtil != null) {
                        localMiaoShaUtil.countdownCancel();
                        member.setDuration(0);
                        member.setTodayScore(0);
                        refhandler.sendEmptyMessage(1);
                    } else if (isCanAddscore && member.getDuration() == 0) {
                        startTimeNum();
                    }
                } else {
                    //返回错误的话
                    PopUtils.showToast(memberOuput.getErrmsg());
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
        });
    }

    /**
     * 初始化服务
     */
    private void initService() {
        intentfxService = new Intent(MainActivity.this, LjwService.class);
        if (!Utils.isServiceRunning(this, LjwService.class.getName())) {
            try {
                bindService(intentfxService, sconnection, Context.BIND_AUTO_CREATE);
            }catch (Exception e){
                finish();
            }

        }
    }

    private LjwService serviceBinder;
    /* 注册接口方法*/
    ServiceInterface mService = null;
    /* 绑定service监听*/
    ServiceConnection sconnection = new ServiceConnection() {
        /*当绑定时执行*/
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = (ServiceInterface) service;
            if (mService != null) {
                mService.show();//测试方法
            }
//            Intent intent = new Intent();//这里只是为了下面传intent对象而构建的，没有实际意义
//			/*绑定后就可以使用Service的相关方法和属性来开始你对Service的操作*/
            serviceBinder = ((LjwService.MyBinder) service).getService();
//			/*比如：你可以掉用Service的onStartCommand()方法*/
//            serviceBinder.onStartCommand(intent, 0, 0);//0,0是我随意的参数
        }
        /*当断开绑定时执行，但调用unbindService()时不会触发改方法*/
        public void onServiceDisconnected(ComponentName name) {
            mService.hidden();
        }
    };

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
                                finish();
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
}

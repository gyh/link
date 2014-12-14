package com.example.market.ljw;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.*;
import android.os.Process;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.market.ljw.bean.Entity;
import com.example.market.ljw.bean.output.Member;
import com.example.market.ljw.bean.output.MemberOutput;
import com.example.market.ljw.common.frame.AppContext;
import com.example.market.ljw.common.frame.BaseActivity;
import com.example.market.ljw.common.frame.taskstack.ApplicationManager;
import com.example.market.ljw.common.frame.taskstack.BackStackManager;
import com.example.market.ljw.common.http.HttpGroup;
import com.example.market.ljw.common.http.HttpResponse;
import com.example.market.ljw.fragment.AppListFragment;
import com.example.market.ljw.fragment.CarouselFragment;
import com.example.market.ljw.fragment.MarketListFragment;
import com.example.market.ljw.fragment.WebViewFragment;
import com.example.market.ljw.function.service.FxService;
import com.example.market.ljw.service.InputDataUtils;
import com.example.market.ljw.utils.Constant;
import com.example.market.ljw.utils.DateUtils;
import com.example.market.ljw.utils.PopUtils;
import com.example.market.ljw.utils.PromptUtil;
import com.example.market.ljw.utils.Util;
import com.example.market.ljw.utils.Utils;
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
    private View ljwview;//背景视图
    private View logimg;
    private final long delayMillis = 1000;//定义的一秒
    private long curDuration = 0;   //定义在线时间
    private long intervalTime = 10; //定义的心跳
    private boolean isRunning = false;//定义是否开启线程
    private Intent intentfxService;//悬浮窗口配置
    private String setTimeSystem = "0";//初始化提交时间
    private boolean isCanAddscore = true;

    Handler durhandler = new Handler() {//积分获取后改变的ui线程
        @Override
        public void handleMessage(Message msg) {
            if(!isCanAddscore){
                curDuration = 0;
                tvnumber.setText("积分：0");
            }
            DateUtils.setCurTimeToView(tvtime, curDuration);
        }
    };
    Handler refhandler = new Handler() {//积分获取后改变的ui线程
        @Override
        public void handleMessage(Message msg) {
            tvnumber.setText("积分：" + member.getTodayScore());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ljw);
        ApplicationManager.clearBackStack();
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
        Constant.theNextLen = 10;//重置倒计时时间
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRunning = false;//结束积分线程
        durhandler.removeMessages(1);
    }

    /**
     * 获取用户信息
     */
    private void getMemberInfo() {
        Intent ljwintent = getIntent();
        if (Constant.FromWhere.LOGINACTIVITY.equals(ljwintent.getStringExtra(Constant.FromWhere.KEY))) {
            member = (Member) getIntent().getSerializableExtra(Constant.ExtraKey.MEMBER);
            Constant.member = member;
        } else if (Constant.FromWhere.FXSERVICE.equals(ljwintent.getStringExtra(Constant.FromWhere.KEY))) {
            member = Constant.member;
        }
        Map<String, Object> param = new LinkedHashMap<String, Object>();
        param.put(Constant.RequestKeys.SERVICENAME, "get_member_info");
        param.put(Constant.RequestKeys.DATA, gson.toJson(InputDataUtils.getUserInfo(member.getID() + "")));
        execute(Constant.SERVER_URL, true, param, null, new HttpGroup.OnEndListener() {
            @Override
            public void onEnd(HttpResponse httpresponse) {
                MemberOutput memberOuput = (MemberOutput) httpresponse.getResultObject();
                if (memberOuput.isSuccess()) {
                    initLJWPrcoss(memberOuput);
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

    }

    /**
     * 获取数据成功后初始化流程
     */
    private void initLJWPrcoss(MemberOutput memberOuput) {
        isRunning = true;
        member = memberOuput.getMember();
        curDuration = member.getDuration();
        intervalTime = Long.valueOf(member.getClientSubmitInterval());
        isCanAddscore = Utils.isCanAddScore(member.getServerTime());//判断服务器时间能够增长积分
        initView();
        initData();
        initService();
    }

    /**
     * 初始化视图
     */
    private void initView() {
        logimg = findViewById(R.id.logimg);
        ljwview = findViewById(R.id.layoutljw);
        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            ljwview.setBackgroundDrawable(new BitmapDrawable(getResources(), Utils.getAndroidSystmeBtmp(this)));
        } else {
            ljwview.setBackground(new BitmapDrawable(getResources(), Utils.getAndroidSystmeBtmp(this)));
        }
        tvusername = (TextView) findViewById(R.id.tvusername);
        tvnumber = (TextView) findViewById(R.id.tvnumber);
        tvtime = (TextView) findViewById(R.id.tvtime);
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
                        sleep(delayMillis);//心跳时间
                        curDuration++;//时间增加
                        isCanAddscore = Utils.isNetworkConnected(MainActivity.this);
                        if (!Utils.isNetworkConnected(MainActivity.this)) {//判断是否有网络
                            //没有网络的时候
                            if (!isCanAddscore) {
                                curDuration = 0;
                            }
                        } else {
                            //有网络的时候
                            if (setTimeSystem.equals("0")) {
                                setTimeSystem = Utils.getCurrentDate();
                                setDataToService();
                            } else if (Utils.calculationDurFrom(setTimeSystem, Utils.getCurrentDate())
                                    >= intervalTime * delayMillis) {
                                setTimeSystem = Utils.getCurrentDate();
                                setDataToService();
                            }
                        }
                        durhandler.sendEmptyMessage(1);
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
        //准备提交数据
        Map<String, Object> param = new LinkedHashMap<String, Object>();
        param.put(Constant.RequestKeys.SERVICENAME, "submit_score");
        param.put(Constant.RequestKeys.DATA, gson.toJson(InputDataUtils.submitScore
                (getDataForShPre(Constant.SaveKeys.TOKENKEY, ""), curDuration, member.getMemberID() + "")));
        execute(Constant.SERVER_URL, false, param, null, new HttpGroup.OnEndListener() {
            @Override
            public void onEnd(HttpResponse httpresponse) {
                //判断是否数据
                MemberOutput memberOuput = (MemberOutput) httpresponse.getResultObject();
                if (memberOuput.isSuccess()) {
                    //如果返回为空的话
                    member.setTodayScore(memberOuput.getMember().getTodayScore());//重新设置当日积分
                    member.setServerTime(memberOuput.getMember().getServerTime());//重新设置服务器时间
                    isCanAddscore = Utils.isCanAddScore(member.getServerTime());//判断服务器时间能够增长积分
                    refhandler.sendEmptyMessage(1);//更新积分数据
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
        intentfxService = new Intent(MainActivity.this, FxService.class);
        if (!Utils.isServiceRunning(this, FxService.class.getName())) {
            startService(intentfxService);//开启浮动窗口服务
        }
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
                                stopService(intentfxService);//去除悬浮窗口
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

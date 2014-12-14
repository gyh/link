package com.example.market.ljw.ui;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;

import com.baidu.kirin.CheckUpdateListener;
import com.baidu.kirin.PostChoiceListener;
import com.baidu.kirin.StatUpdateAgent;
import com.baidu.kirin.objects.KirinCheckState;
import com.baidu.mobstat.SendStrategyEnum;
import com.baidu.mobstat.StatService;
import com.example.market.ljw.R;
import com.example.market.ljw.common.frame.AppContext;
import com.example.market.ljw.common.frame.BaseActivity;
import com.example.market.ljw.receiver.DownloadCompleteReceiver;
import com.example.market.ljw.utils.Constant;
import com.example.market.ljw.utils.PromptUtil;
import com.example.market.ljw.utils.Utils;
import com.example.market.ljw.utils.view.UpdateDialog;
import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by GYH on 2014/10/21.
 */
public class WelcomeActivity extends BaseActivity implements CheckUpdateListener,PostChoiceListener {

    private UpdateDialog utestUpdate;
    private CheckUpdateListener mCheckUpdateResponse;
    private PostChoiceListener mPostUpdateChoiceListener;
    private DownloadCompleteReceiver downloadCompleteReceiver;//接受下载完成的广播

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        if(Constant.DEBUG){
            Constant.SERVERBASE_URL = Constant.SERVERBASE_DEBUG_URL;
            Constant.LJWBASE_URL = "http://marketmobile.strosoft.com/";
        }
        //初始化接口地址
        Constant.SERVER_URL = Constant.SERVERBASE_URL+"API/Service.ashx?";//初始化地址
        Constant.REGISTERURL = Constant.LJWBASE_URL+"MemberShop/Register.aspx";
        downloadCompleteReceiver = new DownloadCompleteReceiver();//创建下载完毕接收器
        Constant.ShowPackName.ANDROID_HOME=Utils.getLauncherPackageName(WelcomeActivity.this);//设置当前home页面包名
        setBaiduTongji();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(downloadCompleteReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(downloadCompleteReceiver != null) {
            unregisterReceiver(downloadCompleteReceiver);
        }
    }

    /**
     * 检查手机是否是小米手机
     */
    private void checkPhone() {
        if (Constant.TheOtherSystem.XIAOMI.equals(Utils.getPhoneSystemInfo())) {
            final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(this);
            PromptUtil.showXiaomiInfo(this, Effectstype.Fliph, dialogBuilder, findViewById(R.id.layoutwelcome), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogBuilder.dismiss();
                    Utils.showInstalledAppDetails(WelcomeActivity.this, Constant.PACKAGENAME);
                    finish();
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogBuilder.dismiss();
                    checkLogin();
                }
            });
        } else {
            checkLogin();
        }
    }

    /**
     * 验证登录
     */
    public void checkLogin() {
        //停顿2秒
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent();
        intent.setClass(WelcomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 设置百度移动统计
     * */
    private void setBaiduTongji(){
        StatService.setSessionTimeOut(30);
        StatService.setLogSenderDelayed(30);
        StatService.setSendLogStrategy(this, SendStrategyEnum.APP_START, 1, false);
        StatService.setAppChannel(this,	"Baidu Market",	true);
        // 小流量发布相关---------------------start------------------------------------------------------
        // 这些设置以及检查更新的代码需要在StatService的系列设置调用之后才行（如果使用了setAppChannel来设置渠道
        // ，起码必须在setAppChannel之后）
        mCheckUpdateResponse = this;
        mPostUpdateChoiceListener = this;
        utestUpdate = new UpdateDialog(this, getResources().getString(R.string.app_name),mPostUpdateChoiceListener);
        StatUpdateAgent.setTestMode(); // 打开小流量调试模式，在该模式下，不受更新频率设置的影响。如果不设置测试模式，那么请求间隔默认每天会请求一次

        // 小流量检查是否有更新，该调用必须在setAppChannel之后调用才可以。启动调用的时候，第二个参数设置true，此时每天启动只提示一次
        StatUpdateAgent.checkUpdate(AppContext.getInstance(), true,mCheckUpdateResponse);


    }

    /**
     * 获取升级数据监听
     * */
    @Override
    public void checkUpdateResponse(KirinCheckState state,HashMap<String, String> dataContainer) {
        if (state == KirinCheckState.ALREADY_UP_TO_DATE) {
            Log.d("ljw", "stat == KirinCheckState.ALREADY_UP_TO_DATE");
            // KirinAgent.postUserChoice(getApplicationContext(),
            // choice);//choice 几种升级类型：0-未更新，1-不更新，2-稍后更新，3-手动更新，4-强制更新
            checkPhone();
        } else if (state == KirinCheckState.ERROR_CHECK_VERSION) {
            checkPhone();
            Log.d("ljw", "KirinCheckState.ERROR_CHECK_VERSION");
        } else if (state == KirinCheckState.NEWER_VERSION_FOUND) {
            Log.d("ljw", "KirinCheckState.NEWER_VERSION_FOUND" + dataContainer.toString());
            String isForce = dataContainer.get("updatetype");
            String noteInfo = dataContainer.get("note");
            String publicTime = dataContainer.get("time");
            String appUrl = dataContainer.get("appurl");
            String appName = dataContainer.get("appname");
            String newVersionName = dataContainer.get("version");
            String newVersionCode = dataContainer.get("buildid");
            String attachInfo = dataContainer.get("attach");
            // 这些信息都是在mtj.baidu.com上您选择的小流量定制信息
            utestUpdate.doUpdate(appUrl, noteInfo,AppContext.getInstance().getBaseActivity(),findViewById(R.id.layoutwelcome));
        }
    }

    /**
     * 判断是否升级，开启下载队列
     * */
    @Override
    public void PostUpdateChoiceResponse(JSONObject jsonObject) {
        try {
            if("1".equals(jsonObject.getString("isdown"))){
                DownloadManager manager =(DownloadManager)this.getSystemService(Context.DOWNLOAD_SERVICE); //初始化下载管理器
                String url = jsonObject.getString("downloadUrl");
                //开始下载
                Uri resource = Uri.parse(url);
                DownloadManager.Request request = new DownloadManager.Request(resource);
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
                request.setAllowedOverRoaming(false);
                //设置文件类型
                MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
                String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url));
                request.setMimeType(mimeString);
                //在通知栏中显示
                request.setShowRunningNotification(true);
                request.setVisibleInDownloadsUi(true);
                //sdcard的目录下的download文件夹
                request.setDestinationInExternalPublicDir("/download/", "lianjiewang.apk");
                request.setTitle("lianjiewang.apk");
                manager.enqueue(request);
            }else {
                checkPhone();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

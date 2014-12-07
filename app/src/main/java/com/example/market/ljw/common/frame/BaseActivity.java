package com.example.market.ljw.common.frame;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;

import com.baidu.mobstat.StatService;
import com.example.market.ljw.cache.GlobalImageCache;
import com.example.market.ljw.cache.bitmap.HandlerRecycleBitmapDrawable;
import com.example.market.ljw.common.http.HttpGroup;
import com.example.market.ljw.common.http.HttpGroupSetting;
import com.example.market.ljw.common.http.HttpSetting;
import com.example.market.ljw.utils.Constant;
import com.example.market.ljw.utils.InflateUtil;
import com.example.market.ljw.utils.view.MyProgressDialog;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by GYH on 2014/10/16.
 */
public class BaseActivity extends FragmentActivity {

    public Gson gson = new Gson();
    //当前碎片
    private MyActivity currentMyActivity;
    //针对activity的生命周期的回调
    private ArrayList<IDestroyListener> destroyListenerList = new ArrayList<IDestroyListener>();
    private ArrayList<MyActivity.PauseListener> pauseListenerList = new ArrayList<MyActivity.PauseListener>();
    private ArrayList<MyActivity.ResumeListener> resumeListenerList = new ArrayList<MyActivity.ResumeListener>();
    private ArrayList<MyActivity.StopListener> stopListenerList = new ArrayList<MyActivity.StopListener>();
    //是否销毁
    protected boolean isDestroy;
    //转圈
    private MyProgressDialog mProgressDialog;
    //本地存储
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppContext.addTempActiviyts(this);
        AppContext.getInstance().setBaseActivity(this);
        initSaveData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppContext.getInstance().setBaseActivity(this);
        StatService.onResume(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AppContext.getInstance().setBaseActivity(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPause(this);
    }

    @Override
    protected void onDestroy() {
        isDestroy = true;
        super.onDestroy();
        //销毁所有分支
        if (this.destroyListenerList != null) {
            Iterator<IDestroyListener> destroyIterator = this.destroyListenerList
                    .iterator();
            while (destroyIterator.hasNext())
                destroyIterator.next().onDestroy();
        }
        destroyListenerList = null;
        pauseListenerList = null;
        resumeListenerList = null;
        stopListenerList = null;
        AppContext.removeActivity(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * 初始化本地存储
     */
    private void initSaveData() {
        //实例化SharedPreferences对象
        sharedPreferences = getSharedPreferences(Constant.SharedPreferencesKey, Activity.MODE_APPEND);
        //实例化SharedPreferences.Editor对象
        editor = sharedPreferences.edit();
    }

    /**
     * 向本地存储数据
     * */
    public void setDataForShPre(String key,String val){
        editor.putString(key,val);
        editor.commit();
    }

    /**
     * 从本地获取数据
     * */
    public String getDataForShPre(String key,String deval){
        String val = null;
        val = sharedPreferences.getString(key,deval);
        return val;
    }

    /**
     * 获取当前碎片
     */
    public MyActivity getCurrentMyActivity() {
        return currentMyActivity;
    }

    /**
     * 设置当前碎片
     */
    public void setCurrentMyActivity(MyActivity paramMyActivity) {
        currentMyActivity = paramMyActivity;
    }

    /**
     * 获取当前碎片视图id
     */
    public int getCurrentFragmentViewId() {
        return getCurrentMyActivity().getViewId();
    }

    /**
     * 获取数据的请求池子
     *
     * @return 请求池
     */
    public HttpGroup getHttpGroupaAsynPool() {
        return getHttpGroupaAsynPool(HttpGroupSetting.TYPE_JSON);
    }

    /**
     * 获取不同类型请求数据的池子
     *
     * @param type 请求类型
     * @return 请求池
     */
    public HttpGroup getHttpGroupaAsynPool(int type) {
        //请求设置初始化
        HttpGroupSetting localHttpGroupSetting = new HttpGroupSetting();
        localHttpGroupSetting.setType(type);
        return getHttpGroupaAsynPool(localHttpGroupSetting);
    }

    /**
     * 获取请求的数据池子
     *
     * @param paramHttpGroupSetting
     * @return 请求池
     */
    public HttpGroup getHttpGroupaAsynPool(final HttpGroupSetting paramHttpGroupSetting) {
        //初始化请求池
        HttpGroup.HttpGroupaAsynPool localHttpGroupaAsynPool = new HttpGroup.HttpGroupaAsynPool(paramHttpGroupSetting);
        addDestroyListener(localHttpGroupaAsynPool);
        addPauseListener(localHttpGroupaAsynPool);
        return localHttpGroupaAsynPool;
    }

    /**
     * 获取图片,并附加到视图上
     *
     * @param imageUrl
     * @param onEndListener
     */
    public void executeImage(String imageUrl, HttpGroup.OnEndListener onEndListener) {
        HttpGroup httpGroup = getHttpGroupaAsynPool();//获取请求池
        HttpSetting httpSetting = new HttpSetting();//实例化请求设置
        httpSetting.setFinalUrl(imageUrl);//设置请求图片路径
        httpSetting.setType(HttpGroupSetting.TYPE_IMAGE);//请求类型是图片
        httpSetting.setPriority(HttpGroupSetting.PRIORITY_IMAGE);//请求等级
        httpSetting.setCacheMode(HttpSetting.CACHE_MODE_AUTO);//请求缓存
        httpSetting.setListener(onEndListener);//请求监听
        httpGroup.add(httpSetting);//添加请求
    }

    /**
     * 加载json数据核心方法
     *
     * @param finalUrl      接口地址
     * @param paramMap      接口参数
     *                      请求参数
     * @param dataClass     返回数据类型
     * @param onEndListener 回调函数
     */
    public void execute(String finalUrl, boolean isPost, Map<String, Object> paramMap,
                        Class dataClass, HttpGroup.OnEndListener onEndListener,
                        HttpGroup.OnParseListener onParseListener) {
        HttpGroup httpGroup = getHttpGroupaAsynPool();//获取请求池
        HttpSetting httpSetting = new HttpSetting();//
        httpSetting.setParamMaps(paramMap);
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
     * 为请求增加销毁事件
     *
     * @param paramIDestroyListener
     */
    public void addDestroyListener(IDestroyListener paramIDestroyListener) {
        if (this.destroyListenerList != null)
            this.destroyListenerList.add(paramIDestroyListener);
    }

    /**
     * 为请求增加暂停事件
     *
     * @param paramPauseListener
     */
    public void addPauseListener(MyActivity.PauseListener paramPauseListener) {
        if (this.pauseListenerList != null)
            this.pauseListenerList.add(paramPauseListener);
    }

    /**
     * 弹出转圈
     *
     * @param isCancel    是否允许取消
     * @param dialogMsgId 提示内容
     */
    public void showProgressDialog(int dialogMsgId, boolean isCancel) {
        String dialogMsg = getResources().getString(dialogMsgId);
        try {
            if (this.mProgressDialog == null) {
                this.mProgressDialog = new MyProgressDialog(this);
            }
            mProgressDialog.setMessage(dialogMsg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        showProgressDialog(isCancel);
    }

    /**
     * 弹出转圈
     *
     * @param isCancel 是否允许取消
     */
    public void showProgressDialog(boolean isCancel) {
        try {
            if (this.mProgressDialog == null) {
                this.mProgressDialog = new MyProgressDialog(this);
            }
            this.mProgressDialog.setCancelable(isCancel);
            this.mProgressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 隐藏转圈
     */
    public void dismissProgressDialog() {
        try {
            if (this.mProgressDialog != null)
                this.mProgressDialog.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 锁屏设置
     *
     * @param bEnable
     */
    public void EnableSystemKeyguard(boolean bEnable) {
        KeyguardManager mKeyguardManager = null;
        KeyguardManager.KeyguardLock mKeyguardLock = null;
        mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        mKeyguardLock = mKeyguardManager.newKeyguardLock("");
        if (bEnable)
            mKeyguardLock.reenableKeyguard();
        else
            mKeyguardLock.disableKeyguard();
    }

    /**
     * 设置图片
     */
    public void setBitmapToImageView(String picPath, ImageView imageView) {
        //定义图片
        GlobalImageCache.BitmapDigest localBitmapDigest = new GlobalImageCache.BitmapDigest(picPath);
        localBitmapDigest.setWidth(imageView.getWidth());
        localBitmapDigest.setHeight(imageView.getHeight());
        localBitmapDigest.setInUsing(true);
        //缓存中获取图片
        Bitmap localBitmap = InflateUtil.loadImageWithCache(localBitmapDigest);
        if (localBitmap != null) {
            //定义画布
            HandlerRecycleBitmapDrawable localHandlerRecycleBitmapDrawable = new HandlerRecycleBitmapDrawable(localBitmap, this);
            imageView.setImageDrawable(localHandlerRecycleBitmapDrawable);
        }
    }
}

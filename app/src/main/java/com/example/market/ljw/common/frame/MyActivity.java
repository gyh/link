package com.example.market.ljw.common.frame;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.baidu.mobstat.StatService;
import com.example.market.ljw.cache.GlobalImageCache;
import com.example.market.ljw.cache.bitmap.HandlerRecycleBitmapDrawable;
import com.example.market.ljw.common.http.HttpGroup;
import com.example.market.ljw.common.http.HttpGroup.OnEndListener;
import com.example.market.ljw.common.http.HttpGroupSetting;
import com.example.market.ljw.common.http.HttpSetting;
import com.example.market.ljw.utils.InflateUtil;
import com.example.market.ljw.utils.Log;
import com.example.market.ljw.utils.view.MyProgressDialog;
import com.google.inject.Key;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by GYH on 2014/10/16.
 */
public class MyActivity extends Fragment implements IMyActivity{

    private ArrayList<IDestroyListener> destroyListenerList = new ArrayList<IDestroyListener>();
    private ArrayList<PauseListener> pauseListenerList = new ArrayList<PauseListener>();
    private ArrayList<ResumeListener> resumeListenerList = new ArrayList<ResumeListener>();
    private ArrayList<StopListener> stopListenerList = new ArrayList<StopListener>();
    private Handler handler;
    protected boolean isDestroy;
    //当前视图的ID
    private int viewId;
    //当前碎片的视图
    private View frameView;
    //当前碎片所属activity
    private FragmentActivity mActivity;
    //视图扩展
    private LayoutInflater inflater;
    // 控制视图的添加与移除
    private ViewGroup mViewGroup;
    //判断是否停止
    public boolean isStoped;
    protected HashMap<Key<?>,Object> scopedObjects = new HashMap<Key<?>, Object>();
    //转圈
    private MyProgressDialog mProgressDialog;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSoftInput();
        inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (this.frameView == null) {
            this.frameView = realCreateViewMethod(this.inflater, null);
            setViewId(this.frameView.getId());
        }
        ((BaseActivity)mActivity).setCurrentMyActivity(this);
    }

    @Override
    public void onAttach(Activity activity) {
        mActivity = (FragmentActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return frameView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isDestroy = true;
        if (this.destroyListenerList != null) {
            Iterator<IDestroyListener> destroyIterator = this.destroyListenerList
                    .iterator();
            while (destroyIterator.hasNext())
                destroyIterator.next().onDestroy();
        }
        handler = null;
        inflater = null;
        destroyListenerList = null;
        pauseListenerList = null;
        resumeListenerList = null;
        stopListenerList = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (this.pauseListenerList != null) {
            Iterator<PauseListener> pauseIterator = this.pauseListenerList
                    .iterator();
            while (pauseIterator.hasNext())
                pauseIterator.next().onPause();
        }
        StatService.onPause(this);
    }

    /**
     * 当碎片唤醒时，会添加view并调用此岁片中所有继承ResumeListener的变量的回调方法
     * 通知父级activity当前碎片为this
     */
    @Override
    public void onResume() {
        super.onResume();
        //片段唤醒时，将片段VIEW恢复过去
        if (this.isStoped && this.frameView != null) {
            ViewGroup viewGroup = (ViewGroup)(this.frameView.getParent());
            if(viewGroup == null && mViewGroup != null){
                mViewGroup.removeView(this.frameView);
                mViewGroup.addView(this.frameView);
            }else if(viewGroup != null){
                viewGroup.removeView(this.frameView);
                viewGroup.addView(this.frameView);
            }
            isStoped = false;
        }
        getBaseActivity().setCurrentMyActivity(this);
        if (this.resumeListenerList != null) {
            Iterator<ResumeListener> resumeIterator = this.resumeListenerList
                    .iterator();
            while (resumeIterator.hasNext())
                resumeIterator.next().onResume();
        }
        StatService.onResume(this);
    }

    /**
     * 当碎片停止时，会调用此碎片中所有继承toplistener的变量的回调方法，
     * 并会通知父级activity清空此当前碎片
     */
    @Override
    public void onStop() {
        super.onStop();
        ((BaseActivity)mActivity).setCurrentMyActivity(null);
        //片段停止时 将片段VIEW移除
        if ((this.frameView != null)) {
            this.mViewGroup = ((ViewGroup) this.frameView.getParent());
            if (this.mViewGroup != null) {
                this.isStoped = true;
                this.mViewGroup.removeView(this.frameView);
            }
        }
        if (this.stopListenerList != null) {
            Iterator<StopListener> stopIterator = this.stopListenerList
                    .iterator();
            while (stopIterator.hasNext())
                stopIterator.next().onStop();
        }
    }

    public HashMap<Key<?>, Object> getScopedObjects() {
        return scopedObjects;
    }

    @Override
    public Handler getHandler() {
        return handler;
    }

    @Override
    public void post(Runnable paramRunnable) {
        if (!isDestroy && handler != null)
            handler.post(paramRunnable);
    }

    @Override
    public void post(Runnable paramRunnable, int paramInt) {
        if (!isDestroy && handler != null)
            handler.post(paramRunnable);
    }

    @Override
    public View inflate(int layoutResourceId) {
        return inflater.inflate(layoutResourceId, null);
    }


    /**
     * 加载图片核心方法
     * @param imageUrl 图片地址
     * @param onEndListener 回调函数
     */
    @Override
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
     * 获取当前碎片所属activity
     * */
    @Override
    public FragmentActivity getThisActivity() {
        return mActivity;
    }


    /**
     * 加载json数据核心方法
     * @param finalUrl 接口地址
     * @param paramMap 接口参数
     *            请求参数
     * @param dataClass
     *            返回数据类型
     * @param onEndListener 回调函数
     */
    public void execute(String finalUrl, Map<String, Object> paramMap,
                        Class dataClass, OnEndListener onEndListener,HttpGroup.OnParseListener onParseListener) {
        HttpGroup httpGroup = getHttpGroupaAsynPool();
        HttpSetting httpSetting = new HttpSetting();
        httpSetting.setFinalUrl(finalUrl);
        httpSetting.setParamMaps(paramMap);
        httpSetting.setCurrentEntity(dataClass);
        httpSetting.setPost(true);
        httpSetting.setType(HttpGroupSetting.TYPE_JSON);
        httpSetting.setPriority(HttpGroupSetting.PRIORITY_JSON);
        httpSetting.setCacheMode(HttpSetting.CACHE_MODE_ONLY_NET);
        httpSetting.setListener(onEndListener);
        httpSetting.setListener(onParseListener);
        httpGroup.add(httpSetting);
    }

    /**
     * 加载json数据核心方法
     * @param finalUrl 接口地址
     * @param paramMap 接口参数
     *            请求参数
     * @param dataClass
     *            返回数据类型
     * @param onEndListener 回调函数
     * @param CACHE_MODE 缓存策略 0.CACHE_MODE_AUTO  1.CACHE_MODE_ONLY_CACHE  2.CACHE_MODE_ONLY_NET
     */
    public void execute(String finalUrl, Map<String, Object> paramMap,
                        Class dataClass, OnEndListener onEndListener,HttpGroup.OnParseListener onParseListener,int CACHE_MODE) {
        HttpGroup httpGroup = getHttpGroupaAsynPool();
        HttpSetting httpSetting = new HttpSetting();
        httpSetting.setFinalUrl(finalUrl);
        httpSetting.setParamMaps(paramMap);
        httpSetting.setCurrentEntity(dataClass);
        httpSetting.setPost(true);
        httpSetting.setType(HttpGroupSetting.TYPE_JSON);
        httpSetting.setPriority(HttpGroupSetting.PRIORITY_JSON);
        httpSetting.setCacheMode(CACHE_MODE);
        httpSetting.setListener(onEndListener);
        httpSetting.setListener(onParseListener);
        httpGroup.add(httpSetting);
    }




    /**
     * 隐藏键盘
     */
    public void hideSoftInput() {
        ((InputMethodManager) this.mActivity
                .getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(this.mActivity.getWindow()
                        .getDecorView().getWindowToken(), 0);
    }

    /**
     * 子类实现
     * */
    protected View realCreateViewMethod(LayoutInflater paramLayoutInflater,ViewGroup paramViewGroup) {
        if (Log.D)
            Log.d("MyActivity", "子类必须重写该方法 -->> ");
        return null;
    }

    /**
     * 暂停监听
     * */
    public static abstract interface PauseListener {
        public abstract void onPause();
    }

    /**
     * 显示监听
     * */
    public static abstract interface ResumeListener {
        public abstract void onResume();
    }

    /**
     * 停止监听
     * */
    public static abstract interface StopListener {
        public abstract void onStop();
    }

    /**
     * 设置碎片视图id
     * */
    public void setViewId(int paramInt) {
        this.viewId = paramInt;
    }

    /**
     * 获取视图ID
     * */
    public int getViewId() {
        return this.viewId;
    }


    /**
     * 显示轮播图每个图片
     * @param facePath 图片路径
     * @param targetView 图片ui元素
     * @param isrefresh 是否刷新
     * @param isfirst 是否为第一次加载，如果为true的话，则清一遍缓存
     * @return
     */
    public GlobalImageCache.BitmapDigest showPageImage(String facePath,ImageView targetView, boolean isrefresh, boolean isfirst,boolean isKeepInMem){
        final WeakReference<ImageView> weakImageView = new WeakReference(targetView);
        HandlerRecycleBitmapDrawable localHandlerRecycleBitmapDrawable = new HandlerRecycleBitmapDrawable(null, getThisActivity());
        targetView.setImageDrawable(localHandlerRecycleBitmapDrawable);
        GlobalImageCache.BitmapDigest localBitmapDigest = new GlobalImageCache.BitmapDigest(facePath);
        localBitmapDigest.setWidth(targetView.getWidth());
        localBitmapDigest.setHeight(targetView.getHeight());
        //是否回收
        localBitmapDigest.setInUsing(isKeepInMem);

        if(isfirst)
            localBitmapDigest.setLarge(true);
        Bitmap localBitmap = InflateUtil.loadImageWithCache(localBitmapDigest);
        if (localBitmap == null) {
            InflateUtil.loadImageWithUrl(getHttpGroupaAsynPool(),
                    localBitmapDigest, isrefresh, new InflateUtil.ImageLoadListener() {
                        @Override
                        public void onError(GlobalImageCache.BitmapDigest paramBitmapDigest) {

                        }

                        @Override
                        public void onProgress(GlobalImageCache.BitmapDigest paramBitmapDigest, int paramInt1, int paramInt2) {

                        }

                        @Override
                        public void onStart(GlobalImageCache.BitmapDigest paramBitmapDigest) {

                        }

                        @Override
                        public void onSuccess(GlobalImageCache.BitmapDigest paramBitmapDigest, Bitmap paramAnonymousBitmap) {
                            if (weakImageView != null && weakImageView.get() != null) {
                                HandlerRecycleBitmapDrawable localHandlerRecycleBitmapDrawable =
                                        (HandlerRecycleBitmapDrawable) weakImageView.get().getDrawable();
                                localHandlerRecycleBitmapDrawable.setBitmap(paramAnonymousBitmap);
                                localHandlerRecycleBitmapDrawable.invalidateSelf();

                            }
                        }
                    });
        }else {
            localHandlerRecycleBitmapDrawable.setBitmap(localBitmap);
            localHandlerRecycleBitmapDrawable.invalidateSelf();
        }
        return localBitmapDigest;
    }

    /**
     * 显示详细页面的图片
     * @param pciPath 图片地址
     * @param targetView 图片view
     * @return 内存中此图片的key
     */
    public GlobalImageCache.BitmapDigest showDetailImage(String pciPath,ImageView targetView){
        return showPageImage(pciPath,targetView,false,false,false);
    }


    /**
     * 获取baseactivity
     * */
    public static BaseActivity getBaseActivity() {
        return AppContext.getInstance().getBaseActivity();
    }

    /**
     * 移除片段
     * @param paramMyActivity 移除目标
     */
    public static void remove(MyActivity paramMyActivity) {
        FragmentTransaction localFragmentTransaction = getBaseActivity().getSupportFragmentManager().beginTransaction();
        localFragmentTransaction.remove(paramMyActivity);
        localFragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * 弹出转圈
     * @param isCancel 是否允许取消
     */
    public void showProgressDialog(boolean isCancel){
        try{
            if (this.mProgressDialog == null) {
                this.mProgressDialog = new MyProgressDialog(getThisActivity());
                this.mProgressDialog.setCancelable(isCancel);
            }
            if(!isDetached())
                this.mProgressDialog.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * 隐藏当前的转圈
     * */
    public void dismissProgressDialog() {
        try{
            if (this.mProgressDialog != null && !isDetached())
                this.mProgressDialog.cancel();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 添加销毁监听
     * */
    public void addDestroyListener(IDestroyListener paramIDestroyListener) {
        if (this.destroyListenerList != null)
            this.destroyListenerList.add(paramIDestroyListener);
    }
    /**
     * 添加暂停监听
     * */
    public void addPauseListener(PauseListener paramPauseListener) {
        if (this.pauseListenerList != null)
            this.pauseListenerList.add(paramPauseListener);
    }
    /**
     * 添加显示监听
     * */
    public void addResumeListener(ResumeListener paramResumeListener) {
        if (this.resumeListenerList != null)
            this.resumeListenerList.add(paramResumeListener);
    }
    /**
     * 添加停止监听
     * */
    public void addStopListener(StopListener paramStopListener) {
        if (this.stopListenerList != null)
            this.stopListenerList.add(paramStopListener);
    }

    /**
     * 获取请求池
     * @return 请求池
     * */
    public HttpGroup getHttpGroupaAsynPool() {
        return getHttpGroupaAsynPool(HttpGroupSetting.TYPE_JSON);
    }

    /**
     * 获取请求池
     * @param type 请求类型
     * @return 请求池
     * */
    public HttpGroup getHttpGroupaAsynPool(int type) {
        HttpGroupSetting localHttpGroupSetting = new HttpGroupSetting();
        localHttpGroupSetting.setType(type);
        return getHttpGroupaAsynPool(localHttpGroupSetting);
    }
    /**
     * 获取请求池
     * @param paramHttpGroupSetting 请求
     * @return 请求池
     * */
    public HttpGroup getHttpGroupaAsynPool(final HttpGroupSetting paramHttpGroupSetting) {
        HttpGroup.HttpGroupaAsynPool localHttpGroupaAsynPool = new HttpGroup.HttpGroupaAsynPool(paramHttpGroupSetting);
        addDestroyListener(localHttpGroupaAsynPool);
        addPauseListener(localHttpGroupaAsynPool);
        return localHttpGroupaAsynPool;
    }


}

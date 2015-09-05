package com.example.market.ljw.core.common.frame;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.StrictMode;

import com.example.market.ljw.core.utils.Constant;
import com.example.market.ljw.core.utils.DPIUtil;

/**
 * Created by GYH on 2015/8/8.
 */
public class MyAppContext extends Application{

    private static MyAppContext app;
    private MyBaseActivity baseActivity;
    //本地存储
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public static MyAppContext getInstance() {
        return app;
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

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        initSaveData();
        //获取屏幕密度
        DPIUtil.setDensity(getResources().getDisplayMetrics().density);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyDeath()
                .build());
    }

    /**
     * 获取App安装包信息
     *
     * @return
     */
    public PackageInfo getPackageInfo() {
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace(System.err);
        }
        if (info == null)
            info = new PackageInfo();
        return info;
    }

    public void runOnUIThread(Runnable uiTask){
        if(baseActivity != null)
            baseActivity.runOnUiThread(uiTask);
    }

    public void setBaseActivity(MyBaseActivity baseActivity) {
        this.baseActivity = baseActivity;
    }

    public MyBaseActivity getBaseActivity() {
        return baseActivity;
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

}

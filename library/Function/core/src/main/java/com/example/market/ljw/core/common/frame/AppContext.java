package com.example.market.ljw.core.common.frame;

import android.app.Activity;

import com.example.market.ljw.core.utils.DPIUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GYH on 2014/10/16.
 */
public class AppContext extends android.app.Application{

    private static AppContext app;
    private BaseActivity baseActivity;
    //存储的所有页面
    private static List<Activity> tempActiviyts = new ArrayList<Activity>();
    //主页面的class
    public Class mainActivity = null;


    @Override
    public void onCreate() {
        super.onCreate();
        super.onCreate();
        app = this;
        android.os.Process.setThreadPriority(-20);
        //获取屏幕密度
        DPIUtil.setDensity(getResources().getDisplayMetrics().density);
    }

    /**
     * 添加页面
     * */
    public static void addTempActiviyts(Activity activity) {
        tempActiviyts.add(activity);
    }

    /**
     * 移除页面
     * */
    public static void removeActivity(Activity activity) {
        tempActiviyts.remove(activity);
    }

    public static int getTempActivitySize(){
        return tempActiviyts.size();
    }

    /**
     * 清除所有的页面
     * */
    public static void clearActivitys() {
        for (int i = tempActiviyts.size() - 1; i >= 0; i--) {
            tempActiviyts.get(i).finish();
        }
    }
    /**
     * 获取全文
     * @return 全文
     */
    public static AppContext getInstance() {
        return app;
    }

    /**
     * 设置当前的Activity
     * @param baseActivity
     * */
    public void setBaseActivity(BaseActivity baseActivity) {
        this.baseActivity = baseActivity;
    }
    /**
     * 获取当前的activity
     * @return baseactivity
     * */
    public BaseActivity getBaseActivity() {
        return baseActivity;
    }

    /**
     * 执行ui线程
     * */
    public void runOnUIThread(Runnable uiTask) {
        if (baseActivity != null)
            baseActivity.runOnUiThread(uiTask);
    }

    public void setMainActivity(Class mainActivity) {
        this.mainActivity = mainActivity;
    }

    public Class getMainActivity() {
        return mainActivity;
    }
}

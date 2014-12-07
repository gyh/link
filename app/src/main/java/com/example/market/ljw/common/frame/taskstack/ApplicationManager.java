package com.example.market.ljw.common.frame.taskstack;

/**
 * Created by GYH on 2014/10/20.
 */

import android.support.v4.app.FragmentManager;

import com.example.market.ljw.common.frame.AppContext;
import com.example.market.ljw.common.frame.BaseActivity;
import com.example.market.ljw.common.frame.MyActivity;
import com.example.market.ljw.utils.Constant;
import com.example.market.ljw.utils.Log;
import com.example.market.ljw.utils.PopUtils;

/**
 * fragment跳转工具类
 *
 * @author yepeng
 */
public class ApplicationManager {

    private static final String TAG = "ApplicationManager";
    private static final BackStackManager backStackManager = BackStackManager.getInstance();

    /**
     * 点击返回按钮，调用此方法，会移出当前屏幕的碎片
     */
    public static void back(){
        if (Log.D)
            Log.d(TAG, "back() -->>");
        //如果是最后一个碎片也消失的话
        if (backStackManager.isLast()) {
//            PopUtils.showToast("是否退出");
            return;
        }
        //获取当前的活动
        BaseActivity mainActivity = AppContext.getInstance().getBaseActivity();
        FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
        //如果不是主页面的话则弹出系统碎片栈，如果当栈底，则结束当前页面
        MyActivity.remove(mainActivity.getCurrentMyActivity());
        //弹出系统级别的栈
        fragmentManager.popBackStack();
        //弹出自定义栈
        backStackManager.pop();
        if (Log.D)
            Log.d(TAG, "back() -->> backStackManager.size()" + backStackManager.size());
    }

    /**
     * 返回栈
     * */
    public static void simpleBack(){
        //获取当前的活动
        BaseActivity mainActivity = AppContext.getInstance().getBaseActivity();
        FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
        //如果不是主页面的话则弹出系统碎片栈，如果当栈底，则结束当前页面
//        MyActivity.remove(mainActivity.getCurrentMyActivity());
        //弹出系统级别的栈
        fragmentManager.popBackStack();
        //弹出自定义栈
        backStackManager.pop();
    }

    /**
     * @param paramTaskModule 添加碎片的相关操作
     */
    public static void go(TaskModule paramTaskModule) {
        if (Log.D)
            Log.d(TAG, "go() -->> taskModule:" + paramTaskModule);
        paramTaskModule.setPrev(backStackManager.getCurrent());
        if (!paramTaskModule.premise())
            return;
        backStackManager.setCurrent(paramTaskModule);
        if (paramTaskModule.isNeedClearBackStack()) {
            paramTaskModule.setPrev(null);
            clearBackStack();
        }
        simpleGo(paramTaskModule);
    }

    /**
     * 内部碎片跳转
     * @param paramTaskModule
     */
    public static void simpleGo(TaskModule paramTaskModule) {
        if (!paramTaskModule.isInit())
            paramTaskModule.init();
        paramTaskModule.show();
    }

    /**
     * 清除历史碎片及任务
     */
    public static void clearBackStack() {
        backStackManager.clearHistory();
        if (Log.D)
            Log.d(TAG, "clearBackStack backStackManager.size() -->> " + backStackManager.size());
        //获取当前的活动
        BaseActivity mainActivity = AppContext.getInstance().getBaseActivity();
        if (mainActivity != null)
            mainActivity.getSupportFragmentManager()
                    .popBackStack(Constant.BACK_STACK_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        System.gc();
    }

}

package com.example.market.ljw.common.frame.taskstack;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.example.market.ljw.common.frame.AppContext;
import com.example.market.ljw.common.frame.MyActivity;
import com.example.market.ljw.utils.Constant;
import com.example.market.ljw.utils.Log;

/**
 * 控制片段的显示流程
 * @author yepeng
 *
 */
public abstract class TaskModule {

    private static final String TAG = "TaskModule";
    private Bundle bundle;
    private boolean init;
    private boolean isNeedClearBackStack;
    private TaskModule prev;

    /**
     * 是否增加标志位，第一个不加
     *
     * @return
     */
    private boolean checkBackStack() {
        TaskModule preTaskModle = getPrev();
        if (preTaskModle instanceof NeedShowAgainModule) {
            return true;
        }
        return false;
    }

    /**
     * 增加碎片
     *
     * @param paramInt        父级容器id
     * @param paramMyActivity 碎片
     */
    public void addAndCommit(int paramInt, MyActivity paramMyActivity) {
        addAndCommit(paramInt, paramMyActivity, null);
    }


    /**
     * 增加碎片
     *
     * @param paramInt        父级容器id
     * @param paramMyActivity 碎片
     * @param paramString     碎片标志
     */
    public void addAndCommit(int paramInt, MyActivity paramMyActivity, String paramString) {
        if (Log.D)
            Log.d(TAG, "addAndCommit -->> "+AppContext.getInstance().getBaseActivity());

        FragmentTransaction localFragmentTransaction = AppContext
                .getInstance().getBaseActivity().getSupportFragmentManager()
                .beginTransaction();
        localFragmentTransaction.add(paramInt, paramMyActivity);
        if (checkBackStack()) {
            localFragmentTransaction.addToBackStack(Constant.BACK_STACK_TAG);
        }
        localFragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * 增加碎片
     *
     * @param paramMyActivity 碎片
     */
    public void addAndCommit(MyActivity paramMyActivity) {
        addAndCommit(
                AppContext.getInstance().getBaseActivity().getCurrentFragmentViewId(),
                paramMyActivity);
    }

    /**
     * 增加碎片
     *
     * @param paramMyActivity 碎片
     * @param paramString     碎片标志
     */
    public void addAndCommit(MyActivity paramMyActivity, String paramString) {
        addAndCommit(
                AppContext.getInstance().getBaseActivity()
                        .getCurrentFragmentViewId(),
                paramMyActivity, paramString
        );
    }

    /**
     * 初始化
     */
    protected void doInit() {
    }

    /**
     * 显示
     */
    protected void doShow() {
    }

    /**
     * 获取原有的buidle
     *
     * @return
     */
    public Bundle getBundle() {
        if (this.bundle == null)
            bundle = new Bundle();
        return bundle;
    }

    /**
     * 获取前一个碎片任务
     *
     * @return
     */
    public TaskModule getPrev() {
        return this.prev;
    }

    /**
     * 初始化
     */
    public void init() {
        doInit();
        this.init = true;
    }

    /**
     * 是否已经初始化
     *
     * @return
     */
    public boolean isInit() {
        return this.init;
    }

    /**
     * 是否需要清除历史碎片操作
     *
     * @return
     */
    public boolean isNeedClearBackStack() {
        return this.isNeedClearBackStack;
    }

    /**
     * 此操作是否需要登陆
     *
     * @return
     */
    public boolean premise() {
        return true;
    }

    /**
     * 替换碎片操作
     *
     * @param paramInt        父级容器id
     * @param paramMyActivity 碎片
     */
    public void replaceAndCommit(int paramInt, MyActivity paramMyActivity) {
        replaceAndCommit(paramInt, paramMyActivity, null);
    }

    /**
     * 替换碎片操作
     *
     * @param paramInt        父级容器id
     * @param paramMyActivity 碎片
     * @param paramString     返回标志
     */
    public void replaceAndCommit(int paramInt, MyActivity paramMyActivity,
                                 String paramString) {
        FragmentTransaction localFragmentTransaction = AppContext.getInstance()
                .getBaseActivity().getSupportFragmentManager()
                .beginTransaction();
        localFragmentTransaction.replace(paramInt, paramMyActivity);
        if (checkBackStack()) {
            localFragmentTransaction.addToBackStack(Constant.BACK_STACK_TAG);
        }
        localFragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * 替换碎片操作
     *
     * @param paramMyActivity 碎片
     */
    public void replaceAndCommit(MyActivity paramMyActivity) {
        replaceAndCommit(AppContext.getInstance().getBaseActivity()
                .getCurrentFragmentViewId(), paramMyActivity);
    }

    /**
     * 替换碎片
     *
     * @param paramMyActivity 碎片
     * @param paramString     清除标志
     */
    public void replaceAndCommit(MyActivity paramMyActivity, String paramString) {
        replaceAndCommit(AppContext.getInstance().getBaseActivity()
                .getCurrentFragmentViewId(), paramMyActivity, paramString);
    }

    /**
     * 设置传递参数
     *
     * @param paramBundle
     */
    public void setBundle(Bundle paramBundle) {
        this.bundle = paramBundle;
    }


    /**
     * 设置是否清除带有tag标志的碎片
     *
     * @param paramBoolean
     */
    public void setNeedClearBackStack(boolean paramBoolean) {
        this.isNeedClearBackStack = paramBoolean;
    }


    /**
     * 设置前一个碎片任务
     *
     * @param paramTaskModule
     */
    public void setPrev(TaskModule paramTaskModule) {
        this.prev = paramTaskModule;
    }

    /**
     * 显示当前碎片
     */
    public void show() {
        doShow();
    }
}

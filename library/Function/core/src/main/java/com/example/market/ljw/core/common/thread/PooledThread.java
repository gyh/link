package com.example.market.ljw.core.common.thread;

import android.os.*;
import android.os.Process;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by GYH on 2014/10/17.
 * @author 郭跃华
 * 线程池内的线程，接受线程池分配的任务，并且执行任务完成后通知线程池。
 */
public class PooledThread extends Thread {

    //所属当前线程池
    private ThreadPool pool;
    //判断线程是否正在运行
    private boolean running;
    //判断线程是否清除
    private boolean killed = false;
    //任务列表
    protected List<Runnable> tasks = new ArrayList<Runnable>();

    public PooledThread(ThreadPool pool) {
        this.pool = pool;
    }

    /**
     * 返回是否在运行标志位
     * @return
     */
    public boolean isRunning() {
        return this.running;
    }
    /**
     * 清除掉当前线程
     */
    public void kill() {
        if (!this.running)//判断线程是否执行
            interrupt();//打断
        else
            this.killed = true;//不在运行就清除
    }

    /**
     * 物理上清除当前线程
     */
    public void killSync(){
        kill();//清除线程
        while (true) {
            if (!isAlive())//判断当前线程为不可用状态
                return;
            try {
                sleep(5L);//睡眠5秒
            } catch (InterruptedException localInterruptedException) {
            }
        }
    }

    /**
     * 弹出第一个任务
     * @return 任务
     */
    protected Runnable popTask() {
        if (this.tasks.size() > 0) //判断线程任务大于0
            return this.tasks.remove(0);//返回第一个任务
        else
            return null;
    }

    /**
     * 加入任务集合
     * @param tasks
     */
    public void putTasks(Collection tasks) {
        this.tasks.addAll(tasks);
    }

    /**
     * 开始唤醒线程，执行任务
     */
    public void startTasks() {
        synchronized (this) {//互斥
            this.running = true;//线程可以
            notify();//唤醒线程，执行任务
        }
    }

    /**
     * 执行任务集合
     */
    public void run() {
        //设置线程中等优先级
        android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_DISPLAY);
        this.pool.addThreadId(Process.myTid());//向线程池添加id
        while (true) {
            if (killed)//判断此线程是否终止
                break;//终止执行
            if (running && tasks.size() != 0) {//判断线程正在运行，并且认为列表不为空
                Runnable runnable = popTask();//获取任务
                if (runnable != null) {//如果任务不为空
                    runnable.run();//则执行线程
                }
            } else {
                try {
                    synchronized (this) {//当线程暂停或认为执行完
                        running = false;//则停止运行
                        pool.notifyForIdleThread();//通知线程池有空余线程了
                        wait();//睡眠线程
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

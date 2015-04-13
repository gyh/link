package com.example.market.ljw.core.common.thread;

import android.os.Process;

import com.example.market.ljw.core.utils.IPriority;
import com.example.market.ljw.core.utils.PriorityCollection;

import java.util.Collection;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Vector;

/**
 * Created by GYH on 2014/10/17.
 */
public class ThreadPool {
    //判断线程池是否初始化
    protected boolean initialized;
    //判断当前线程是否有空闲的
    private boolean hasIdleThread;
    // ui线程是否繁忙的标志为
    private static boolean isUiThreadBusy = false;
    //初始化开启线程的个数
    protected int initPoolSize;
    //最大开启的线程的个数
    protected int maxPoolSize;
    //第一个线程池,加载数据
    private static ThreadPool firstThreadPool = new ThreadPool(2,1);
    //第二个线程池，加载图片
    private static ThreadPool secondThreadPool = new ThreadPool(4,1);
    //第三个线程池，加载图片
    private static ThreadPool thridThreadPool = new ThreadPool(1,1);
    //当前线程池的线程容器
    protected Vector<PooledThread> effectiveThread = new Vector<PooledThread>();
    //当前运行的线程的id集合
    protected Vector<Integer> effectiveThreadIds = new Vector<Integer>();
    // 优先级列表
    protected PriorityQueue<IPriority> queue = new PriorityQueue<IPriority>();

    public ThreadPool(int maxPoolSize, int initPoolSize) {
        this.maxPoolSize = maxPoolSize;
        this.initPoolSize = initPoolSize;
    }

    static {
        firstThreadPool.init();
        secondThreadPool.init();
        thridThreadPool.init();
    }

    /**
     * 初始化线程池内的线程，并且监听队列，有任务时交给空闲的线程
     * */
    private void init(){
        this.initialized = true;
        //初始化线程池内的最小开启线程数目的线程
        for(int i=0;i<initPoolSize;i++){
            PooledThread pooledThread = new PooledThread(this);
            pooledThread.start();
            this.effectiveThread.add(pooledThread);
        }
        //开启监听线程，监听任务并交给空闲线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                //设置该线程的优先等级为最高
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
                while (true){
                    Collection collection = (Collection)pollTasks();//获取任务
                    if(collection!=null){//判断任务不为空
                        PooledThread pooledThread = getIdleThread();//获取空余线程
                        pooledThread.putTasks(collection);//添加任务
                        pooledThread.startTasks();//开始任务
                    }else {
                        synchronized (queue){//添加互斥
                            try {
                                queue.wait();//队列等待
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }).start();
    }

    public static ThreadPool getFirstThreadPool() {
        return firstThreadPool;
    }

    public static ThreadPool getSecondThreadPool() {
        return secondThreadPool;
    }

    public static ThreadPool getThirdThreadPool() {
        return thridThreadPool;
    }

    /**
     * 弹出第一个优先级任务
     * @return 优先级任务列表
     */
    private IPriority pollTasks() {
        synchronized (queue) {
            IPriority localIPriority = this.queue.poll();
            return localIPriority;
        }
    }

    /**
     * 获取空闲的线程
     * @return 空余的线程
     * */
    private PooledThread getIdleThread(){
        while (true){
            Iterator<PooledThread> threadIterator = effectiveThread.iterator();//获取当前在线程池中的线程
            while (threadIterator.hasNext()){//遍历线程池中的线程
                PooledThread pooledThread = threadIterator.next();
                if(!pooledThread.isRunning()){//判断是否正在运行，返回不在运行的线程
                    return pooledThread;//返回空余线程
                }
            }
            if(getPoolSize()<maxPoolSize){//判断当前线程池大小是否是最大值
                PooledThread pooledThread = new PooledThread(this);//新建线程
                pooledThread.start();//开启线程
                effectiveThread.add(pooledThread);//添加线程池
                return pooledThread;//返回空余线程
            }
            //等待空闲的线程
            if(waitForIdleThread()){
                continue;
            }
        }
    }

    /**
     * 获取线程池的大小
     * @return 当前线程池的所有线程的个数
     */
    public int getPoolSize() {
        return this.effectiveThread.size();
    }

    /**
     * 等待获取空闲的线程
     * @return 等待线程
     */
    protected boolean waitForIdleThread() {
        this.hasIdleThread = false; //没有空余线程
        while (true) {
            if (!hasIdleThread) {//判断是否有空余线程
                if (this.getPoolSize() >= maxPoolSize) {//判断线程数是否大于最大线程数
                    try {
                        synchronized (this) {
                            this.wait();//对象等待
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }else {//返回有空余线程
                    return true;
                }
            } else {//返回有空余线程
                return true;
            }
        }
    }

    /**
     * 添加运行的线程的id
     * @param threadId 线程的id
     * */
    public void addThreadId(int threadId){
        this.effectiveThreadIds.add(threadId);//添加线程id
    }

    /**
     * 通知线程池有空余线程
     */
    protected void notifyForIdleThread() {
        synchronized (this) {//互斥
            this.hasIdleThread = true;//有空余线程
            this.notify();//唤醒等待对象，已经有空余线程了
        }
    }

    /**
     * 设置池子内线程的优先级
     * 1.当ui线程繁忙的时候，要设置线程池内的所有线程低优先级
     * @param priority
     */
    public void setAllThreadPriority(int priority) {
        synchronized (this.effectiveThreadIds) {    //互斥线程池集合
            Iterator<Integer> localIterator = this.effectiveThreadIds.iterator();//获取正在执行的线程池的集合
            while (localIterator.hasNext()) {//遍历线程池集合
                Integer localInteger = localIterator.next();//获取线程id
                android.os.Process.setThreadPriority(localInteger,priority);//设置线程优先级
            }
        }
    }

    /**
     * 向队列内增加任务
     * @param paramRunnable
     * @param priority
     */
    public void offerTask(Runnable paramRunnable, int priority) {
        PriorityCollection localPriorityCollection = new PriorityCollection(priority);
        localPriorityCollection.add(paramRunnable);
        offerTasks(localPriorityCollection);
    }
    /**
     * 提供任务优先队列
     *
     * @param paramIPriority
     */
    public void offerTasks(IPriority paramIPriority) {
        synchronized (queue) {
            queue.offer(paramIPriority);
            queueNotify();
        }
    }

    /**
     * 唤醒任务队列上等待的线程池子
     */
    public void queueNotify() {
        synchronized (this.queue) {
            this.queue.notify();
        }
    }

    /**
     * 把线程池扩展到最大
     *
     * @param paramInt
     */
    public void setMaxPoolSize(int paramInt) {
        this.maxPoolSize = paramInt;
        if (paramInt >= getPoolSize())
            return;
        setPoolSize(paramInt);
    }

    /**
     * 设置线程池的大小
     *
     * @param paramInt
     */
    public void setPoolSize(int paramInt) {
        if (!this.initialized) {
            this.initPoolSize = paramInt;
        } else {
            if (paramInt > getPoolSize()) {
                for (int i = getPoolSize(); (i < paramInt)
                        && (i < this.maxPoolSize); ++i) {
                    PooledThread localPooledThread = new PooledThread(this);
                    localPooledThread.start();
                    this.effectiveThread.add(localPooledThread);
                }
            }
            while (paramInt != getPoolSize()) {
                ((PooledThread) this.effectiveThread.remove(0)).killSync();
                this.effectiveThreadIds.remove(0);
            }
        }
    }
    /**
     * 如果ui线程繁忙的话则等待->每次请求数据的时候都调用此方法
     */
    public static void sleepForUiThreadBusy() {
        boolean sleeped = false;
        while (isUiThreadBusy()) {
            sleeped = true;
            try {
                Thread.sleep(Math.round(500.0D * Math.random()));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(sleeped)
            notifyUIThreadNotBusy();
    }

    /**
     * 线程池醒来后，通知线程池子线程队列启动
     */
    public static void notifyUIThreadNotBusy() {
        firstThreadPool.queueNotify();
        secondThreadPool.queueNotify();
        thridThreadPool.queueNotify();
    }


    /**
     * 设置当前ui线程是否繁忙--》页面滚动时设置为繁忙；页面停止滚动是设置为不繁忙
     *
     * @param isBusy
     */
    public static void setUiThreadBusy(boolean isBusy) {
        isUiThreadBusy = isBusy;
        if (!isUiThreadBusy)
            setAllThreadPoolPriority(Process.THREAD_PRIORITY_DISPLAY);
        else
            setAllThreadPoolPriority(Process.THREAD_PRIORITY_BACKGROUND);
    }

    /**
     * 设置线程池内线程的优先级
     *
     * @param priority
     *            线程优先级
     */
    public static void setAllThreadPoolPriority(int priority) {
        firstThreadPool.setAllThreadPriority(priority);
        secondThreadPool.setAllThreadPriority(priority);
        thridThreadPool.setAllThreadPriority(priority);
    }

    /**
     * 获取当前ui线程是否繁忙
     * @return
     */
    public static boolean isUiThreadBusy() {
        return isUiThreadBusy;
    }
}

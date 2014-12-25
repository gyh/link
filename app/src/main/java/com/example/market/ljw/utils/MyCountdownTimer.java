package com.example.market.ljw.utils;


import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

/**
 * Created by yepeng on 14-3-10.
 * 依据服务器的结束时间与距离的毫秒数，回调秒杀结束
 */
public abstract class MyCountdownTimer
{
    private static final int MSG = 1;
    private long mCountdownInterval;
    private long mMillisInFuture;
    private long mStopTimeInFuture;
    private int what;
    private Handler mHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            int what = msg.what;

            //计算剩余时间，通知是否完成
            long residueTime;
            synchronized (MyCountdownTimer.this)
            {
                residueTime = MyCountdownTimer.this.mStopTimeInFuture - SystemClock.elapsedRealtime();
                if (residueTime <= 0L)
                {
                    MyCountdownTimer.this.onFinish(what);
                    return;
                }
                if (residueTime < MyCountdownTimer.this.mCountdownInterval) {
                    sendMessageDelayed(obtainMessage(what), residueTime);
                    return;
                }
            }

            //计算下一秒时间，并添加消息队列
            long lastTickStart = SystemClock.elapsedRealtime();
            MyCountdownTimer.this.onTick(residueTime, what);
            long l4;
            for (long delay = lastTickStart + MyCountdownTimer.this.mCountdownInterval - SystemClock.elapsedRealtime();; delay += l4)
            {
                if (delay >= 0L)
                {
                    sendMessageDelayed(obtainMessage(what), delay);
                    break;
                }
                l4 = MyCountdownTimer.this.mCountdownInterval;
            }
        }
    };


    /**
     * 构建一个时间观察者
     * @param mMillisInFuture  倒计时时长
     * @param mCountdownInterval 查数频率
     * @param what
     */
    public MyCountdownTimer(long mMillisInFuture, long mCountdownInterval, int what)
    {
        this.mMillisInFuture = mMillisInFuture;
        this.mCountdownInterval = mCountdownInterval;
        this.what = what;
    }

    /**
     * 页面退出时，结束计数
     * @param what
     */
    public final void cancel(int what)
    {
        this.mHandler.removeMessages(what);
    }

    public abstract void onFinish(int what);

    public abstract void onTick(long paramLong, int paramInt);

    /**
     * 重置timer
     * @param mMillisInFuture 倒计时时长
     * @param mCountdownInterval 查数频率
     * @param what
     */
    public final void reset(long mMillisInFuture, long mCountdownInterval, int what)
    {
        try
        {
            this.mMillisInFuture = mMillisInFuture;
            this.mCountdownInterval = mCountdownInterval;
            this.what = what;
            start();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 开始记时
     * @return
     */
    public final MyCountdownTimer start()
    {
        synchronized (MyCountdownTimer.this){
            try{
                if(mMillisInFuture <= 0){
                        onFinish(what);
                }else{
                    mStopTimeInFuture = mMillisInFuture + SystemClock.elapsedRealtime();
                    mHandler.sendMessage(mHandler.obtainMessage(what));

                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return this;
    }
}

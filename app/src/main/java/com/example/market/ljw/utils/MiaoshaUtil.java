package com.example.market.ljw.utils;

import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by yepeng on 14-3-10.
 */
public class MiaoshaUtil {

    public static final int MIAOSHA_BEGINING = 2;
    public static final int MIAOSHA_FINISH = 3;
    public static final int MIAOSHA_WILLBEGIN = 1;
    //查数间隔
    private long mCountdownInterval = 1000L;

    public static final String TAG = "MiaoSha";
    private Map<Integer, CountDownListener> countdownMap = new HashMap();
    private MyCountdownTimer myCountdownTimer;
    private int what;

    //设置查数
    public void setmCountdownInterval(long mCountdownInterval) {
        this.mCountdownInterval = mCountdownInterval;
    }

    public static abstract interface CountDownListener {
        public abstract void changed(MyCountdownTimer paramMyCountdownTimer, long residueTime, long[] threeTimePoint, int what);

        public abstract boolean finish(MyCountdownTimer paramMyCountdownTimer, long endRemainTime, int what);
    }

    /**
     * 设置倒记时秒数
     *
     * @param mMillisInFuture
     */
    public void setHMS(long mMillisInFuture) {
        if (this.myCountdownTimer != null) {
            this.myCountdownTimer.reset(mMillisInFuture, mCountdownInterval, this.what);
        }
    }

    /**
     * 毫秒转时间数组
     *
     * @param time 毫秒数
     * @return
     */
    public long[] toHMS(long time) {
        long hour = time / 1000L / 60L / 60L;
        long minute = (time - 1000L * (60L * (60L * hour))) / 1000L / 60L;
        long second = time / 1000L - 60L * (60L * hour) - 60L * minute;
        if (hour < 0L) {
            hour = 0L;
        }
        if (minute < 0L) {
            minute = 0L;
        }
        if (second < 0L) {
            second = 0L;
        }
        return new long[]{hour, minute, second};
    }

    /**
     * 格式化时间为一位时间补0
     *
     * @param timeunit 时间单位数
     * @return
     */
    public String format(long timeunit) {
        String str = timeunit + "";
        if (str.length() == 1) {
            str = "0" + str;
        }
        return str;
    }

    /**
     * 增加监听者
     *
     * @param paramInteger
     * @param paramCountDownListener
     */
    public void addListener(Integer paramInteger, CountDownListener paramCountDownListener) {
        this.countdownMap.put(paramInteger, paramCountDownListener);
    }

    /**
     * 放弃当前计数
     */
    public void countdownCancel() {
        if (this.myCountdownTimer != null) {
            this.myCountdownTimer.cancel(what);
        }
    }

    /**
     * 转换显示的字符串
     *
     * @param paramArrayOfLong
     * @return
     */
    public SpannableStringBuilder hmsToString(long[] paramArrayOfLong) {
        String str1 = format(paramArrayOfLong[0]);
        String str2 = format(paramArrayOfLong[1]);
        String str3 = format(paramArrayOfLong[2]);
        SpannableStringBuilder localSpannableStringBuilder = new SpannableStringBuilder("还剩" + str1 + "时" + str2 + "分" + str3 + "秒");
        ForegroundColorSpan localForegroundColorSpan1 = new ForegroundColorSpan(-65536);
        ForegroundColorSpan localForegroundColorSpan2 = new ForegroundColorSpan(-65536);
        ForegroundColorSpan localForegroundColorSpan3 = new ForegroundColorSpan(-65536);
        localSpannableStringBuilder.setSpan(localForegroundColorSpan1, "还剩".length(), "还剩".length() + str1.length(), 33);
        localSpannableStringBuilder.setSpan(localForegroundColorSpan2, "还剩".length() + str1.length() + "时".length(), "还剩".length() + str1.length() + "时".length() + str2.length(), 33);
        localSpannableStringBuilder.setSpan(localForegroundColorSpan3, "还剩".length() + str1.length() + "时".length() + str2.length() + "分".length(), "还剩".length() + str1.length() + "时".length() + str2.length() + "分".length() + str3.length(), 33);
        return localSpannableStringBuilder;
    }

    /**
     * 设置倒记时的开始和结束时间
     *
     * @param startRemainTime
     * @param endRemainTime
     */
    public void setCountdown(long startRemainTime, long endRemainTime) {
        setCountdown(startRemainTime, endRemainTime, this.countdownMap);
    }

    /**
     * 设置倒记时的开始和结束时间和监听者
     *
     * @param startRemainTime
     * @param endRemainTime
     * @param paramCountDownListener
     */
    public void setCountdown(long startRemainTime, long endRemainTime, CountDownListener paramCountDownListener) {
        this.countdownMap.put(Integer.valueOf(0), paramCountDownListener);
        setCountdown(startRemainTime, endRemainTime, this.countdownMap);
    }

    /**
     * 复用监听，只更新时间
     *
     * @param startRemainTime
     * @param endRemainTime
     * @param paramMap
     */
    public void setCountdown(long startRemainTime, final long endRemainTime, Map<Integer, CountDownListener> paramMap) {
        if ((paramMap == null) || (paramMap.size() == 0)) {
            return;
        }
        long mMillisInFuture = getCountdownTime(startRemainTime, endRemainTime);
        //如果时间小于等于0的话，就设置成0,防止显示负数
        if (mMillisInFuture <= 0)
            mMillisInFuture = 0;
        if (this.myCountdownTimer == null) {
            this.myCountdownTimer = new MyCountdownTimer(mMillisInFuture, mCountdownInterval, this.what) {
                public void onFinish(int what) {
                    Iterator localIterator = countdownMap.keySet().iterator();
                    while (localIterator.hasNext()) {
                        Integer localInteger = (Integer) localIterator.next();
                        MiaoshaUtil.CountDownListener localCountDownListener = countdownMap.get(localInteger);
                        if (localCountDownListener != null) {
                            localCountDownListener.finish(this, endRemainTime, what);
                        }
                    }
                }

                public void onTick(long residueTime, int what) {
                    long[] threeTimePoint = MiaoshaUtil.this.toHMS(residueTime);
                    Iterator localIterator = countdownMap.keySet().iterator();
                    while (localIterator.hasNext()) {
                        Integer localInteger = (Integer) localIterator.next();
                        MiaoshaUtil.CountDownListener localCountDownListener = countdownMap.get(localInteger);
                        if (localCountDownListener != null) {
                            localCountDownListener.changed(this, residueTime, threeTimePoint, what);
                        }
                    }
                }
            }.start();
        } else {
            this.myCountdownTimer.reset(mMillisInFuture, mCountdownInterval, this.what);
        }

    }

    /**
     * 计算倒记时秒数
     *
     * @param startRemainTime 开始时间
     * @param endRemainTime   结束时间
     * @return TODO 商品开始秒杀开始结束 类似于夺宝岛
     */
    public long getCountdownTime(long startRemainTime, long endRemainTime) {
        if (startRemainTime > 0) {
            what = MIAOSHA_WILLBEGIN;
        } else if (startRemainTime <= 0 && endRemainTime > 0) {
            what = MIAOSHA_BEGINING;
        } else if (startRemainTime <= 0 && endRemainTime <= 0) {
            what = MIAOSHA_FINISH;
        }
        return endRemainTime - System.currentTimeMillis();
        //return endRemainTime-startRemainTime;
    }

}

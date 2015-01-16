package com.example.market.ljw.utils.view;

/**
 * Created by GYH on 14-2-19.
 */
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.example.market.ljw.utils.Utils;

public class HackyViewPager extends ViewPager {

    private boolean isTouched;
    public HackyViewPager(Context context) {
        super(context);
    }

    public HackyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(ev.getAction() == MotionEvent.ACTION_DOWN)
            isTouched = true;
        else if(ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL)
            isTouched = false;
        Utils.showSystem("onTouchEvent","x = "+ev.getX());
        getParent().requestDisallowInterceptTouchEvent(false);
        return super.onTouchEvent(ev);
    }

    public boolean isTouched() {
        return isTouched;
    }



    @Override
    public PagerAdapter getAdapter() {
        return super.getAdapter();
    }
}

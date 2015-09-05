package com.example.market.ljw.core.utils;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.market.ljw.core.R;
import com.example.market.ljw.core.cache.bitmap.HandlerRecycleBitmapDrawable;
import com.example.market.ljw.core.common.frame.MyActivity;
import com.koushikdutta.ion.Ion;

import java.util.List;

/**
 * Created by GYH on 2014/10/16.
 */
public class ImageAdapter extends PagerAdapter {
    private List<ImageView> imageViews;
    private List<String> imageurls;
    private Fragment myActivity;
    private boolean isfirst=true;
    //设置条目点击事件
    private View.OnClickListener clickListener;

    //判断当前适配器内的图片是否在使用
    private boolean isInUsing;
    //是否接触
    private boolean isTouched;

    /**
     * 判断是否手指在屏幕上
     * @return
     */
    public boolean isTouched() {
        return isTouched;
    }

    public void setInUsing(boolean isInUsing) {
        this.isInUsing = isInUsing;
    }

    public ImageAdapter(List<ImageView> imageViews,List<String> imageurls,Fragment myActivity,View.OnClickListener clickListener){
        this(imageViews,imageurls,myActivity);
        this.clickListener = clickListener;
    }

    public ImageAdapter(List<ImageView> imageViews,List<String> imageurls,Fragment myActivity){
        this(imageurls,myActivity);
        this.imageViews=imageViews;
    }

    public ImageAdapter(List<String> imageurls,Fragment myActivity){
        this.imageurls=imageurls;
        this.myActivity = myActivity;
    }


    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);

    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        imageViews.get(position).setOnClickListener(clickListener);
        imageViews.get(position).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    isTouched = true;
                else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)
                    isTouched = false;
                return false;
            }
        });
        Ion.with(myActivity)
                .load(imageurls.get(position))
                .withBitmap()
                .placeholder(R.drawable.ic_launcher)
                .error(R.drawable.ic_item_google)
                .intoImageView(imageViews.get(position));
        isfirst=false;
        container.addView(imageViews.get(position), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return imageViews.get(position);
    }

    @Override
    public int getCount() {
        return imageViews.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int arg1, Object arg2) {
        container.removeView((View) arg2);
//        HandlerRecycleBitmapDrawable handlerRecycleBitmapDrawable = (HandlerRecycleBitmapDrawable)imageViews.get(arg1).getDrawable();
//        handlerRecycleBitmapDrawable.setBitmap(null);
//        handlerRecycleBitmapDrawable.invalidateSelf();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void restoreState(Parcelable arg0, ClassLoader arg1) {

    }

    @Override
    public Parcelable saveState() {
        return null;
    }
}

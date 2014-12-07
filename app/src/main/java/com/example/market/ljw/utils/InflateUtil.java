package com.example.market.ljw.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.market.ljw.cache.GlobalImageCache;
import com.example.market.ljw.cache.GlobalImageCache.BitmapDigest;
import com.example.market.ljw.common.frame.AppContext;
import com.example.market.ljw.common.frame.BaseActivity;
import com.example.market.ljw.common.http.HttpError;
import com.example.market.ljw.common.http.HttpGroup;
import com.example.market.ljw.common.http.HttpGroupSetting;
import com.example.market.ljw.common.http.HttpResponse;
import com.example.market.ljw.common.http.HttpSetting;

public class InflateUtil {


    private static LayoutInflater getLayoutInflater() {
        BaseActivity localMainActivity = AppContext.getInstance().getBaseActivity();
        if (localMainActivity != null)
            return LayoutInflater.from(localMainActivity);
        return (LayoutInflater) AppContext.getInstance().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public static View inflate(int paramInt, ViewGroup paramViewGroup) {
        try {
            View localView = getLayoutInflater().inflate(paramInt, paramViewGroup);
            return localView;
        } catch (Throwable localThrowable) {
            GlobalImageCache.getLruBitmapCache().clean();
        }
        return getLayoutInflater().inflate(paramInt, paramViewGroup);
    }

    public static View inflate(int paramInt, ViewGroup paramViewGroup, boolean paramBoolean) {
        try {
            View localView = getLayoutInflater().inflate(paramInt, paramViewGroup, paramBoolean);
            return localView;
        } catch (Throwable localThrowable) {
            GlobalImageCache.getLruBitmapCache().clean();
        }
        return getLayoutInflater().inflate(paramInt, paramViewGroup, paramBoolean);
    }

    public static Bitmap loadImageWithCache(BitmapDigest paramBitmapDigest) {
        Bitmap localBitmap = GlobalImageCache.getLruBitmapCache().get(paramBitmapDigest);
        if ((localBitmap != null) && (!localBitmap.isRecycled()))
            return localBitmap;
        return null;
    }

    public static void loadImageWithUrl(HttpGroup paramHttpGroup, BitmapDigest paramBitmapDigest, ImageLoadListener paramImageLoadListener) {
        loadImageWithUrl(paramHttpGroup, paramBitmapDigest, true, paramImageLoadListener);
    }

    public static void loadImageWithUrl(HttpGroup paramHttpGroup, final BitmapDigest paramBitmapDigest, boolean isRefresh, final ImageLoadListener paramImageLoadListener) {
        if (paramBitmapDigest != null) {
            Bitmap localBitmap = loadImageWithCache(paramBitmapDigest);
            if ((localBitmap != null) && !(localBitmap.isRecycled())) {
                paramImageLoadListener.onSuccess(paramBitmapDigest, localBitmap);
            } else {
                HttpSetting httpSetting = new HttpSetting();
                httpSetting.setFinalUrl(paramBitmapDigest.getUrl());
                httpSetting.setType(HttpGroupSetting.TYPE_IMAGE);
                httpSetting.setPriority(HttpGroupSetting.PRIORITY_IMAGE);
                if (!isRefresh)
                    httpSetting.setCacheMode(HttpSetting.CACHE_MODE_AUTO);
                else
                    httpSetting.setCacheMode(HttpSetting.CACHE_MODE_ONLY_NET);
                httpSetting.setListener(new HttpGroup.OnAllListener() {
                    public void onEnd(HttpResponse paramAnonymousHttpResponse) {
                        if (paramImageLoadListener != null) {
                            final Bitmap localBitmap = ImageUtil.createBitmap(ImageUtil.InputWay.createInputWay(paramAnonymousHttpResponse), paramBitmapDigest);
                            AppContext.getInstance().runOnUIThread(new Runnable() {
                                @Override
                                public void run() {
                                    if ((localBitmap != null) && (!localBitmap.isRecycled())) {
                                        GlobalImageCache.getLruBitmapCache().put(paramBitmapDigest, localBitmap);
                                        paramImageLoadListener.onSuccess(paramBitmapDigest, localBitmap);
                                    }
                                }
                            });

                        } else {
                            paramImageLoadListener.onError(paramBitmapDigest);
                        }

                    }

                    public void onError(HttpError paramAnonymousHttpError) {
                        if (paramImageLoadListener != null)
                            paramImageLoadListener.onError(paramBitmapDigest);
                    }

                    public void onProgress(int paramAnonymousInt1, int paramAnonymousInt2) {
                        if (paramImageLoadListener != null)
                            paramImageLoadListener.onProgress(paramBitmapDigest, paramAnonymousInt1, paramAnonymousInt2);
                    }

                    public void onStart() {
                        if (paramImageLoadListener != null)
                            paramImageLoadListener.onStart(paramBitmapDigest);
                    }
                });
                paramHttpGroup.add(httpSetting);
            }
        }

    }

    public static abstract interface ImageLoadListener {
        public abstract void onError(BitmapDigest paramBitmapDigest);

        public abstract void onProgress(BitmapDigest paramBitmapDigest, int paramInt1, int paramInt2);

        public abstract void onStart(BitmapDigest paramBitmapDigest);

        public abstract void onSuccess(BitmapDigest paramBitmapDigest, Bitmap paramBitmap);
    }
}
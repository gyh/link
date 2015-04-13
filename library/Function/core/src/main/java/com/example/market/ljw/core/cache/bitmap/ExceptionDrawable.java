package com.example.market.ljw.core.cache.bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.example.market.ljw.core.R;
import com.example.market.ljw.core.cache.GlobalImageCache;
import com.example.market.ljw.core.common.frame.AppContext;
import com.example.market.ljw.core.utils.DPIUtil;
import com.example.market.ljw.core.utils.Log;


/**
 * 自定义异常图片类
 * @author yepeng
 *
 */
public class ExceptionDrawable extends Drawable {

	private Bitmap bitmap = null;
	protected Paint paint = new Paint();
	private final String text;

	public ExceptionDrawable(Context paramContext, String paramString) {
		this.paint.setColor(Color.RED);
		this.paint.setStyle(Paint.Style.FILL);
		int textSize;
		if (DPIUtil.isBigScreen())
			textSize = 12;
		else
			textSize = DPIUtil.dip2px(12.0F);
		this.paint.setTextSize(textSize);
		this.paint.setTextAlign(Paint.Align.CENTER);
		this.paint.setAntiAlias(true);
		this.text = paramString;

        //如果缓存内没有默认图片的话，则去加载然后放到缓存中，解决异常问题
        Bitmap cacheBitmap = getDefaultBt();
		if (bitmap ==null) {
			bitmap = cacheBitmap;
		}
	}

    /**
     * 从缓存中获取图片，如果没有则从资源中加载
     */
    private Bitmap getDefaultBt(){
        Bitmap cacheBitmap = getBitmapFromCache(R.drawable.ic_launcher+"");
        //如果bitmap没被回收企且不为空的话，则重新载入内存
        if((cacheBitmap == null || cacheBitmap.isRecycled())
                && AppContext.getInstance().getBaseActivity() != null){
            cacheBitmap =  ((BitmapDrawable)AppContext.getInstance().getBaseActivity().
                    getResources().getDrawable(R.drawable.default_empty)).getBitmap();
            //放入缓存
            GlobalImageCache.getLruBitmapCache().put(new GlobalImageCache.BitmapDigest(R.drawable.ic_launcher+""), cacheBitmap);
        }
        return cacheBitmap;
    }

    /**
     * 从缓存中获取图片
     * @param url
     */
    private Bitmap getBitmapFromCache(String url) {
        return GlobalImageCache.getLruBitmapCache()
                .get(new GlobalImageCache.BitmapDigest(url));
    }

    @Override
	public void draw(Canvas paramCanvas) {
		Rect localRect = getBounds();
        int centerX = localRect.centerX();
        int centerY = localRect.centerY();
		if (Log.D) {
			Log.d(ExceptionDrawable.class.getSimpleName(),
					"draw bounds.width()-->> " + localRect.width());
			Log.d(ExceptionDrawable.class.getSimpleName(),
					"draw bounds.height()-->> " + localRect.height());
			Log.d(ExceptionDrawable.class.getSimpleName(),
					"draw bounds.left-->> " + localRect.left);
			Log.d(ExceptionDrawable.class.getSimpleName(),
					"draw bounds.top-->> " + localRect.top);
		}
		float x = localRect.right - localRect.width() / 2;
		float y = localRect.bottom - localRect.height() / 2;
		paramCanvas.drawText(this.text, x, y, this.paint);
        //如果图片回收的话，则获取默认图片
        if(bitmap == null || bitmap.isRecycled()){
            bitmap = getDefaultBt();
        }
        //如果没回收的话，则画上相应的图片
		if (bitmap != null && !bitmap.isRecycled()) {
            RectF rectF = new RectF(centerX - centerY * 3 / 4, centerY / 4, centerX + centerY * 3 / 4, centerY * 7 / 4);
            paramCanvas.drawBitmap(bitmap, null,
                    rectF,
                    this.paint);
        }
	}

	public int getOpacity() {
		return 0;
	}

	public void setAlpha(int paramInt) {
	}

	public void setColorFilter(ColorFilter paramColorFilter) {
	}
}
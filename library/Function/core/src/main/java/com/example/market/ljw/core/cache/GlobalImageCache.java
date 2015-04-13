package com.example.market.ljw.core.cache;

import android.graphics.Bitmap;

import com.example.market.ljw.core.common.frame.AppContext;
import com.example.market.ljw.core.utils.Log;

import java.util.HashMap;
import java.util.Map;


/**
 * 全局的图片缓存控制
 * */
public class GlobalImageCache {
	
	public static final int STATE_FAILURE = 2;
	public static final int STATE_LOADING = 1;
	public static final int STATE_NONE = 0;
	public static final int STATE_SUCCESS = 3;
	private static final Map<ImageState, BitmapDigest> digestMap = new HashMap<ImageState, BitmapDigest>();
	private static final Map<BitmapDigest, ImageState> imageMap = new HashMap<BitmapDigest, ImageState>();
	private static LruBitmapCache lruBitmapCache;

	/**
	 * 请求图片请求时，获取ImageState对象，图片加载完毕调用该对象的success方法--》加入到缓存中
	 * @param paramBitmapDigest
	 * @return
	 */
	public static ImageState getImageState(BitmapDigest paramBitmapDigest) {
		ImageState localImageState = (ImageState) imageMap
				.get(paramBitmapDigest);
		if (localImageState == null) {
            localImageState = new ImageState();
			imageMap.put(paramBitmapDigest, localImageState);
			digestMap.put(localImageState, paramBitmapDigest);
		}
		return localImageState;
	}

	public static BitmapDigest getBitmapDigest(ImageState paramImageState) {
		return (BitmapDigest) digestMap.get(paramImageState);
	}

	public static void remove(BitmapDigest paramBitmapDigest) {
		if (Log.D)
			Log.d(GlobalImageCache.class.getName(),
					"remove() bitmapDigest -->> " + paramBitmapDigest);
		ImageState localImageState = (ImageState) imageMap
				.remove(paramBitmapDigest);
		digestMap.remove(localImageState);
	}

	public static class BitmapDigest {

		private boolean allowRecycle = true;
		private int height;
		private boolean inUsing;
		private boolean keepVisibleBitmap;
		private boolean large;
		private Map<String, Object> moreParameter;
		private int position;
		private int round;
		private String url;
		private int width;
        private boolean isCutPath;

		public BitmapDigest(String paramString) {
			this.url = paramString;
		}


		public int getHeight() {
			return this.height;
		}

		public Object getMoreParameter(String paramString) {
			return this.moreParameter.get(paramString);
		}

		public int getPosition() {
			return this.position;
		}

		public int getRound() {
			return this.round;
		}

		public String getUrl() {
			return this.url;
		}

		public int getWidth() {
			return this.width;
		}

		public boolean isAllowRecycle() {
			return this.allowRecycle;
		}

		public boolean isInUsing() {
			return this.inUsing;
		}

		public boolean isKeepVisibleBitmap() {
			return this.keepVisibleBitmap;
		}

		public boolean isLarge() {
			return this.large;
		}

		public void putMoreParameter(String paramString, Object paramObject) {
			if (this.moreParameter == null)
				this.moreParameter = new HashMap<String, Object>();
			this.moreParameter.put(paramString, paramObject);
		}

		public void setAllowRecycle(boolean paramBoolean) {
			this.allowRecycle = paramBoolean;
		}

		public void setHeight(int paramInt) {
			this.height = paramInt;
		}

		public void setInUsing(boolean paramBoolean) {
			this.inUsing = paramBoolean;
		}

		public void setKeepVisibleBitmap(boolean paramBoolean) {
			this.keepVisibleBitmap = paramBoolean;
		}

		public void setLarge(boolean paramBoolean) {
			this.large = paramBoolean;
		}

		public void setPosition(int paramInt) {
			this.position = paramInt;
		}

		public void setRound(int paramInt) {
			this.round = paramInt;
		}

		public void setUrl(String paramString) {
			this.url = paramString;
		}

		public void setWidth(int paramInt) {
			this.width = paramInt;
		}

        public boolean isCutPath() {
            return isCutPath;
        }

        public void setCutPath(boolean isCutPath) {
            this.isCutPath = isCutPath;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            BitmapDigest that = (BitmapDigest) o;

            if (allowRecycle != that.allowRecycle) return false;
            if (height != that.height) return false;
            if (inUsing != that.inUsing) return false;
            if (isCutPath != that.isCutPath) return false;
            if (keepVisibleBitmap != that.keepVisibleBitmap) return false;
            if (large != that.large) return false;
            if (position != that.position) return false;
            if (round != that.round) return false;
            if (width != that.width) return false;
            if (moreParameter != null ? !moreParameter.equals(that.moreParameter) : that.moreParameter != null)
                return false;
            if (!url.equals(that.url)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = (allowRecycle ? 1 : 0);
            result = 31 * result + height;
            result = 31 * result + (inUsing ? 1 : 0);
            result = 31 * result + (keepVisibleBitmap ? 1 : 0);
            result = 31 * result + (large ? 1 : 0);
            result = 31 * result + (moreParameter != null ? moreParameter.hashCode() : 0);
            result = 31 * result + position;
            result = 31 * result + round;
            result = 31 * result + url.hashCode();
            result = 31 * result + width;
            result = 31 * result + (isCutPath ? 1 : 0);
            return result;
        }
    }

	public static class ImageState {

		private boolean available = true;
		private boolean canRecycle = false;
		private int mState = STATE_NONE;

		public void failure() {
			this.mState = STATE_FAILURE;
		}

		/**
		 * 获取缓存中的图片--》目的为附加图片到imageView上
		 * @return
		 */
		public Bitmap getBitmap() {
			Bitmap localBitmap = null;
			GlobalImageCache.BitmapDigest localBitmapDigest = GlobalImageCache
					.getBitmapDigest(this);
			if (localBitmapDigest != null) {
				localBitmap = GlobalImageCache.getLruBitmapCache().get(localBitmapDigest);
			}
			return localBitmap;
		}

		/**
		 * 获取缓存中的图片时，先获取此状态
		 * @return
		 */
		public int getState() {
			if ((this.mState == STATE_SUCCESS) && (getBitmap() == null))
				this.mState = STATE_NONE;
			return this.mState;
		}

		public boolean isAvailable() {
			return this.available;
		}

		public boolean isCanRecycle() {
			return this.canRecycle;
		}

		public void loading() {
			this.mState = STATE_LOADING;
		}

		public void none() {
			this.mState = STATE_NONE;
		}

		public void setAvailable(boolean paramBoolean) {
			this.available = paramBoolean;
			none();
		}

		public void setCanRecycle(boolean paramBoolean) {
			this.canRecycle = paramBoolean;
		}

		/**
		 * 加载图片完成后调用此方法，完成图片的缓存
		 * @param paramBitmap
		 */
		public void success(Bitmap paramBitmap) {
			if (Log.D)
				Log.d(GlobalImageCache.class.getName(), "success() b -->> "
						+ paramBitmap);

			GlobalImageCache.getLruBitmapCache().put(
					GlobalImageCache.getBitmapDigest(this), paramBitmap);
			this.mState = STATE_SUCCESS;
		}

		public String toString() {
			return "ImageState [bitmap=" + getBitmap() + ", mState="
					+ this.mState + "]";
		}
	}

	/**
	 * 获取全局缓存图片对象
	 * @return
	 */
	public static synchronized LruBitmapCache getLruBitmapCache() {
		if (lruBitmapCache == null)
			lruBitmapCache = new LruBitmapCache(AppContext.getInstance(), LruBitmapCache.DEFAULT_MEMORY_CACHE_PERCENTAGE);
		return lruBitmapCache;
	}
}

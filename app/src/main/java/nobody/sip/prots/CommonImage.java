package nobody.sip.prots;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.graphics.Bitmap;
import nobody.sip.core.PlayerService;
import nobody.sip.helpers.CacheHelper;
import nobody.sip.prots.ImageTemplate.ImageConfig;
import nobody.sip.prots.ImageTemplate.Image;

public abstract class CommonImage implements Image {
	private int mWidth = PlayerService.INVALID_ID_OR_POSITION;
	private int mHeight = PlayerService.INVALID_ID_OR_POSITION;
	private WeakReference<Bitmap> mBitmapBufferReference;
	public CommonConfigObject configObject;

	public CommonImage(CommonConfigObject config) {
		configObject = config;
	}

	public static class CommonConfigObject implements ImageConfig {
		private boolean mGetFromCache = false;
		private boolean mPutOnCache = false;

		public CommonConfigObject(Boolean getFromCache, Boolean putOnCache) {
			mGetFromCache = getFromCache;
			mPutOnCache = putOnCache;
		}

		@Override
		public ImageConfig cacheGet(Boolean get) {
			mGetFromCache = get;
			return this;
		}

		@Override
		public ImageConfig cachePut(Boolean put) {
			mPutOnCache = put;
			return this;
		}

		@Override
		public Boolean isCacheGetEnabled() {
			return mGetFromCache;
		}

		@Override
		public Boolean isCachePutEnabled() {
			return mPutOnCache;
		}

	}

	@Override
	public int getImageWidth() {
		return mWidth;
	}

	@Override
	public int getImageHeight() {
		return mHeight;
	}

	@Override
	public void setImageWidth(int w) {
		mWidth = w;
	}

	@Override
	public void setImageHeight(int h) {
		mHeight = h;
	}

	@Override
	public Bitmap getBitmap(Context context) {
		if (mBitmapBufferReference == null) {
			Context c = context;
			mBitmapBufferReference = createBitmapBuffer(c, configObject);
		}

		return mBitmapBufferReference.get();
	}

	@Override
	public Bitmap getFromCache(Context context) {
		return CacheHelper.getInstance(context).get(getImageID());
	}

	@Override
	public void cache(Context context, Bitmap b) {
		CacheHelper.getInstance(context).put(getImageID(), b);
	}

	@Override
	public abstract Object getImageTag();

	@Override
	public abstract String getImageID();
	
	public abstract WeakReference<Bitmap> createBitmapBuffer(Context context, CommonConfigObject config);

}

package nobody.sip.helpers;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

public class CacheHelper extends LruCache<String, Bitmap> {
	@Override
	protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
		super.entryRemoved(evicted, key, oldValue, newValue);

		Log.d("CACHE", "Entry removed");
	}

	private static int mBitmapSize = 0;
	private static final int mFactor = 10;
	private static int mUsed = 0;

	private static CacheHelper mInstance;
	private final String TAG = "sip.helpers.CACHE_HELPER";

	public static CacheHelper getInstance(Context context) {
		if (mInstance == null) {
			int memClass = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
			int size = memClass * 1024 * 1024 / mFactor;

			mInstance = new CacheHelper(size);
		}

		return mInstance;
	}

	private CacheHelper(int maxSize) {
		super(maxSize);
	}

	@Override
	protected int sizeOf(String key, Bitmap value) {
		mBitmapSize = value.getByteCount();
		mUsed = mUsed + mBitmapSize / 1024;

		return mBitmapSize;
	}

	@Override
	protected Bitmap create(String key) {
		return super.create(key);
	}

}

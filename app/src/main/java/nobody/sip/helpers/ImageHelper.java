package nobody.sip.helpers;

import java.io.File;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AlbumColumns;
import android.util.Log;
import android.view.View;
import android.support.v8.renderscript.*;

public class ImageHelper {
	private static final String TAG = "sip.helpers.IMAGE_HELPER";
	private final Uri mAlbumArtPath = Uri.parse("content://media/external/audio/albumart");

	private Context mContext;

	private static ImageHelper mInstance;

	public static ImageHelper getInstance(Context context) {
		if (mInstance == null) {
			Context appContext = context.getApplicationContext();
			mInstance = new ImageHelper(appContext);
		}

		return mInstance;
	}

	public ImageHelper(Context context) {
		mContext = context;
	}

	private int getSampleRatio(BitmapFactory.Options options, int width, int height) {
		final int w = options.outWidth;
		final int h = options.outHeight;
		int ratio = 1;

		if (w > width || h > height) {
			int sWidth = Math.round((float) w / (float) width);
			int sHeigth = Math.round((float) h / (float) height);

			ratio = sWidth > sHeigth ? sHeigth : sWidth;
		}

		return ratio;
	}

	public String getArtURIFromDirectory(long albumID) {
		String uri = ContentUris.withAppendedId(mAlbumArtPath, albumID).toString();

		if (new File(uri).exists())
			return uri;
		else
			return null;
	}

	private String getArtUriFromProvider(long albumID) {
		Log.d(TAG, "Get from provider check it");

		String uri = null;
		Cursor c = mContext.getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
				new String[] { AlbumColumns.ALBUM_ART }, BaseColumns._ID + "=" + albumID, null, null);

		while (c.moveToNext()) {
			uri = c.getString(0);
			c.close();
			break;
		}

		return uri;
	}

	public Bitmap getNonProcessedFileBitmap(String uri, int width, int height) {
		try {
			Bitmap bitmap = null;

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;

			bitmap = BitmapFactory.decodeFile(uri, options);

			options.inSampleSize = getSampleRatio(options, width, height);
			options.inJustDecodeBounds = false;
			options.inMutable = true;

			bitmap = BitmapFactory.decodeFile(uri, options);
			
			return bitmap;
		} catch (Exception ex) {
			Log.d(TAG, "Im broken");
			return null;
		}

	}

	public Bitmap scaleImage(Bitmap bitmap, int width, int height) {
		Bitmap b = null;
		if (bitmap != null) {
			b = Bitmap.createBitmap(bitmap);

			int h = b.getHeight();
			int w = b.getWidth();
			double dummy;

			if (h > w) {
				dummy = (double) height / (double) h;
				w = (int) (w * dummy);
				h = height;
			} else {
				dummy = (double) width / (double) w;
				h = (int) (h * dummy);
				w = width;
			}

			b = Bitmap.createScaledBitmap(bitmap, w, h, false);
		}

		return b;
	}

	public Bitmap fillSizingScale(Bitmap bitmap, int width, int height) {
		Bitmap b = null;

		if (bitmap != null) {
			int h = bitmap.getHeight();
			int w = bitmap.getWidth();

			double dummy;

			if (h > w) {
				dummy = (double) height / (double) h;
				w = (int) (w * dummy);
				h = height;
			} else {
				dummy = (double) width / (double) w;
				h = (int) (h * dummy);
				w = width;
			}

			b = scaleImage(bitmap, w, h);
		}

		return b;
	}

	public Bitmap cropSizingScale(Bitmap image, int size) {
		Bitmap b = null;

		if (image != null) {
			int h = image.getHeight();
			int w = image.getWidth();
			double dummy;

			if (h < w) {

				dummy = (double) size / (double) h;
				b = Bitmap.createScaledBitmap(image, (int) (h * dummy), (int) (h * dummy), false);

			} else {

				dummy = (double) size / (double) w;
				b = Bitmap.createScaledBitmap(image, (int) (w * dummy), (int) (w * dummy), false);
			}
		}

		return b;
	}

	public Bitmap getAlbumImage(long albumID, String albumUri, int width, int height) {
		String uri = albumUri;
		Bitmap b = null;

		if (width != 0 && height != 0) {
			if (uri != null)
				b = getNonProcessedFileBitmap(uri, width, height);

			if (b != null)
				return b;

			uri = getArtURIFromDirectory(albumID);
			if (uri != null)
				b = getNonProcessedFileBitmap(uri, width, height);

			uri = getArtUriFromProvider(albumID);

			if (uri != null)
				b = getNonProcessedFileBitmap(uri, width, height);
		}
		return b;
	}

	public Bitmap blurImage(Bitmap b, int blurRatio) {
		final RenderScript renderScript = RenderScript.create(mContext);
		final Allocation sourceAlloc = Allocation.createFromBitmap(renderScript, b);

		final ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(renderScript, sourceAlloc.getElement());

		blurScript.setInput(sourceAlloc);
		blurScript.setRadius(1);

		final Bitmap destBitmap = Bitmap.createBitmap(b.getWidth(), b.getHeight(), b.getConfig());
		final Allocation destAlloc = Allocation.createFromBitmap(renderScript, destBitmap);
		
		blurScript.forEach(destAlloc);
		destAlloc.copyTo(destBitmap);

		destAlloc.destroy();
		sourceAlloc.destroy();
		blurScript.destroy();
		renderScript.destroy();

		return destBitmap;
	}

	// private Canvas mCanvas;
	// private Paint mPaint;
	// private LinearGradient mLinearGradient;
	// private RadialGradient mRadialGradient;
	// private Bitmap mEffectBitmap, mCopyBitmap;
	//

	//

	//

	// //
	// protected Bitmap getNonPorcessedFileImage(String uri, int width, int
	// height) {

	// }
	//
	// public void applyRadialGradient(Image image, float x, float y, float
	// radius, int startColor, int endColor) {
	// if (image.getBitmap() != null) {
	// mPaint = new Paint();
	// mCanvas = new Canvas(image.getBitmap());
	//
	// mRadialGradient = new RadialGradient(x, y, radius, startColor, endColor,
	// TileMode.MIRROR);
	// mPaint.setShader(mRadialGradient);
	// mPaint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	//
	// mCanvas.drawRect(0, 0, image.getBitmap().getWidth(),
	// image.getBitmap().getHeight(), mPaint);
	//
	// mRadialGradient = null;
	// mCanvas = null;
	// mPaint = null;
	// }
	// }
	//
	// public void applyGradient(Image image, float x0, float y0, float x1,
	// float y1, int startColor, int endColor) {
	// if (image.getBitmap() != null) {
	// mPaint = new Paint();
	// mCanvas = new Canvas(image.getBitmap());
	//
	// mLinearGradient = new LinearGradient(x0, y0, x1, y1, startColor,
	// endColor, TileMode.CLAMP);
	// mPaint.setShader(mLinearGradient);
	// mPaint.setXfermode(new PorterDuffXfermode(Mode.DST_OVER));
	//
	// mCanvas.drawRect(0, 0, image.getBitmap().getWidth(),
	// image.getBitmap().getHeight(), mPaint);
	//
	// mLinearGradient = null;
	// mCanvas = null;
	// mPaint = null;
	// }
	// }
	//
	// public void applyMask(Image image, int width, int height, int idMask) {
	// if (image.getBitmap() != null) {
	// mEffectBitmap = getNonProcessedResourceImage(idMask, width, height);
	// mEffectBitmap = Bitmap.createScaledBitmap(mEffectBitmap, width, height,
	// false);
	//
	// mCanvas = new Canvas(image.getBitmap());
	//
	// mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	// mPaint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
	//
	// mCanvas.drawBitmap(mEffectBitmap, 0, 0, mPaint);
	// mPaint.setXfermode(null);
	//
	// mEffectBitmap.recycle();
	// mCanvas = null;
	// mPaint = null;
	// }
	// }
	//
	// public void applyReflection(Image image, int width, int height, int
	// ratio) {
	// if (image.getBitmap() != null) {
	//
	// mPaint = new Paint();
	//
	// Matrix matrix = new Matrix();
	// matrix.preScale(1, -1);
	//
	// mEffectBitmap = Bitmap.createBitmap(image.getBitmap(), 0, height / ratio,
	// width, height / ratio, matrix, false);
	// mCopyBitmap = Bitmap.createBitmap(width, height + (height / ratio),
	// Config.ARGB_8888);
	//
	// mCanvas = new Canvas(mCopyBitmap);
	// mCanvas.drawBitmap(image.getBitmap(), 0, 0, null);
	// mCanvas.drawBitmap(mEffectBitmap, 0, height, null);
	//
	// mLinearGradient = new LinearGradient(0, height, 0, height + (height /
	// ratio), 0x70ffffff, 0x00ffffff, TileMode.CLAMP);
	// mPaint.setShader(mLinearGradient);
	// mPaint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
	//
	// mCanvas.drawRect(0, height, width, height + (height + ratio), mPaint);
	//
	// image.setBitmap(mCopyBitmap);
	//
	// mPaint.setXfermode(null);
	//
	// mLinearGradient = null;
	//
	// mEffectBitmap.recycle();
	// mCopyBitmap.recycle();
	// mCanvas = null;
	// mPaint = null;
	// }
	// }
}

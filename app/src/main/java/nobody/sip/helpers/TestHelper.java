package nobody.sip.helpers;

import java.lang.ref.WeakReference;

import nobody.sip.prots.Album;
import nobody.sip.prots.ImageTemplate.Image;
import nobody.sip.prots.views.MeasuredImageView;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

public class TestHelper {
	private static TestHelper mInstance = null;

	public static TestHelper getInstance() {
		if (mInstance == null) {
			mInstance = new TestHelper();
		}

		return mInstance;
	}

	public TestHelper() {

	}

	public void setProtGridImage(MeasuredImageView view, Image image, Context context) {
		Bitmap b = null;

		final ProttestImageWorker imageWorker = new ProttestImageWorker(context, view);
		final AsyncProtImage im = new AsyncProtImage(context.getResources(), b, imageWorker);
		view.setImageDrawable(im);
		imageWorker.execute(image);
	}

	public class ProttestImageWorker extends AsyncTask<Image, Void, Bitmap> {
		private WeakReference<MeasuredImageView> mViewRef;
		private Context mInnerContext;
		private BitmapDrawable mDrawable;

		public ProttestImageWorker(Context context, MeasuredImageView imageView) {
			mViewRef = new WeakReference<MeasuredImageView>(imageView);
			mInnerContext = context;
		}

		@Override
		protected Bitmap doInBackground(Image... params) {
			Image currentImage = params[0];
			
			// AlbumImageConfigurator config = new
			// AlbumImageConfigurator((Album) currentImage.getImageTag(), false,
			// currentImage.getImageWidth(), currentImage.getImageHeight());
			//
			// Bitmap b = currentImage.getBitmap(mInnerContext, config);
			// return b;

			return null;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			if (isCancelled())
				result = null;

			final ImageView imageView = getAttachedView();

			if (imageView != null && result != null) {
				mDrawable = new BitmapDrawable(mInnerContext.getResources(), result);
				imageView.setImageDrawable(mDrawable);

			}
		}

		private ImageView getAttachedView() {
			final ImageView imageView = mViewRef.get();
			final ProttestImageWorker imageWorker = getProtWorker(imageView);

			if (this == imageWorker) {
				return imageView;
			}

			return null;
		}
	}

	private static ProttestImageWorker getProtWorker(ImageView imageView) {
		if (imageView != null) {
			final Drawable drawable = imageView.getDrawable();
			if (drawable instanceof AsyncProtImage) {
				final AsyncProtImage asyncImage = (AsyncProtImage) drawable;
				return asyncImage.getWorker();
			}
		}
		return null;
	}

	private static class AsyncProtImage extends BitmapDrawable {
		private final WeakReference<ProttestImageWorker> mWorkerReference;

		public AsyncProtImage(Resources resources, Bitmap bitmap, ProttestImageWorker worker) {
			super(resources, bitmap);
			mWorkerReference = new WeakReference<ProttestImageWorker>(worker);
		}

		public ProttestImageWorker getWorker() {
			return mWorkerReference.get();
		}
	}
	//
	// public class TestImageWorker extends AsyncTask<Album, Void, Bitmap> {
	// private WeakReference<MeasuredImageView> mViewReference;
	// private Context mContext;
	// private BitmapDrawable mDrawable;
	//
	// public TestImageWorker(MeasuredImageView image, Context context) {
	// mViewReference = new WeakReference<MeasuredImageView>(image);
	// mContext = context;
	// }
	//
	// @Override
	// protected Bitmap doInBackground(Album... params) {
	// int height = 200;
	// int width = 200;
	// Album album = params[0];
	//
	// Bitmap bitmap = mViewReference.get().getSource(mContext, false);
	// return bitmap;
	// }
	//
	// @Override
	// protected void onPostExecute(Bitmap result) {
	// if (isCancelled())
	// result = null;
	//
	// final ImageView imageView = getAttachedView();
	//
	// if (imageView != null && result != null) {
	// mDrawable = new BitmapDrawable(mContext.getResources(), result);
	// imageView.setImageDrawable(mDrawable);
	// }
	// }
	//
	// private ImageView getAttachedView() {
	// final ImageView imageView = mViewReference.get();
	// final TestImageWorker imageWorker = getWorker(imageView);
	//
	// if (this == imageWorker)
	// return imageView;
	//
	// return null;
	// }
	// }
	//
	// private static TestImageWorker getWorker(ImageView imageView) {
	// if (imageView != null) {
	// final Drawable drawable = imageView.getDrawable();
	// if (drawable instanceof AsyncImage) {
	// final AsyncImage asyncImage = (AsyncImage) drawable;
	// return asyncImage.getWorker();
	// }
	// }
	// return null;
	// }
	//
	// private static class AsyncImage extends BitmapDrawable {
	// private final WeakReference<TestImageWorker> mWorkerReference;
	//
	// public AsyncImage(Resources resources, Bitmap bitmap, TestImageWorker
	// worker) {
	// super(resources, bitmap);
	// mWorkerReference = new WeakReference<TestImageWorker>(worker);
	// }
	//
	// public TestImageWorker getWorker() {
	// return mWorkerReference.get();
	// }
	// }
}

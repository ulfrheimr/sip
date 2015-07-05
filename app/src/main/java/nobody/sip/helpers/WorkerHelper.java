package nobody.sip.helpers;

import java.lang.ref.WeakReference;

import nobody.sip.helpers.TestHelper.ProttestImageWorker;
import nobody.sip.prots.CommonImage;
import nobody.sip.prots.views.MeasuredImageView;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

public class WorkerHelper {
	private static WorkerHelper mInstance;

	public static WorkerHelper getInstance() {
		if (mInstance == null)
			mInstance = new WorkerHelper();

		return mInstance;
	}

	public <T extends CommonImage> void display(MeasuredImageView imageView, T image, Context context) {
		Bitmap b = null;
		final ImageWorker<T> imageWorker = new ImageWorker<T>(context, imageView);
		final AsyncImage asyncImage = new AsyncImage(context.getResources(), b, imageWorker);

		imageView.setImageDrawable(asyncImage);
		imageWorker.execute(image);
	}

	public class ImageWorker<T extends CommonImage> extends AsyncTask<T, Void, Bitmap> {
		private WeakReference<MeasuredImageView> mViewReference;
		private Context mInnerContext;

		public ImageWorker(Context context, MeasuredImageView imageView) {
			mInnerContext = context;
			mViewReference = new WeakReference<MeasuredImageView>(imageView);
		}

		@Override
		protected Bitmap doInBackground(T... commonImage) {
			final T currentImage = commonImage[0];

			Bitmap b = currentImage.getBitmap(mInnerContext);
			return b;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			if (isCancelled())
				result = null;

			final MeasuredImageView imageView = getAttachedView();

			if (imageView != null && result != null) {
				final BitmapDrawable drawable = new BitmapDrawable(mInnerContext.getResources(), result);
				imageView.displayDrawable(drawable);
			}
		}

		private MeasuredImageView getAttachedView() {
			final MeasuredImageView imageView = mViewReference.get();
			final ImageWorker imageWorker = getImageWorker(imageView);

			if (this == imageWorker) {
				return imageView;
			}

			return null;
		}
	}

	private static ImageWorker getImageWorker(ImageView imageView) {
		if (imageView != null) {
			final Drawable drawable = imageView.getDrawable();
			if (drawable instanceof AsyncImage) {
				final AsyncImage asyncImage = (AsyncImage) drawable;
				return asyncImage.getWorker();
			}
		}

		return null;
	}

	private static class AsyncImage extends BitmapDrawable {
		private final WeakReference<ImageWorker> mWorkerReference;

		public AsyncImage(Resources resources, Bitmap bitmap, ImageWorker imageWorker) {
			super(resources, bitmap);
			mWorkerReference = new WeakReference<WorkerHelper.ImageWorker>(imageWorker);
		}

		public ImageWorker getWorker() {
			return mWorkerReference.get();
		}
	}

	// public interface WorkerConfig {
	//
	// }
	//
	// public static class ImageWorker<T extends MeasuredImageView> extends
	// AsyncTask<WorkerConfig, Void, Bitmap> {
	// private WeakReference<T> mViewReference;
	// private Bitmap mBuffer;
	// private Context mContext;
	// private BitmapDrawable mDrawable;
	//
	// public ImageWorker(T imageView, Context context) {
	// mContext = context;
	// mViewReference = new WeakReference<T>(imageView);
	// }
	//
	// @Override
	// protected Bitmap doInBackground(WorkerConfig... params) {
	// mBuffer = mViewReference.get().getSource(mContext, true);
	//
	// // b.recycle();
	// return mBuffer;
	// }
	//
	// @Override
	// protected void onPostExecute(Bitmap result) {
	// if (isCancelled())
	// result = null;
	//
	// final T imageView = getAttachedView();
	//
	// if (imageView != null && result != null) {
	//
	// mDrawable = new BitmapDrawable(mContext.getResources(), result);
	// imageView.setDrawable(mDrawable);
	// imageView.invalidate();
	// }
	// }
	//
	// private T getAttachedView() {
	// final T imageView = mViewReference.get();
	// final ImageWorker imageWorker = getWorker(imageView);
	//
	// if (this == imageWorker)
	// return imageView;
	//
	// return null;
	// }
	// }
	//
	// private static <T extends MeasuredImageView> ImageWorker getWorker(T
	// imageView) {
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
	// public static class AsyncImage<T extends MeasuredImageView> extends
	// BitmapDrawable {
	// private final WeakReference<ImageWorker> mWorkerReference;
	//
	// public AsyncImage(Resources resources, T source, ImageWorker worker) {
	// super(resources, "");
	// mWorkerReference = new WeakReference<ImageWorker>(worker);
	// }
	//
	// public ImageWorker getWorker() {
	// return mWorkerReference.get();
	// }
	// }

}

package nobody.sip.prots.views;

import nobody.sip.helpers.WorkerHelper;
import nobody.sip.prots.CommonImage;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;

public class AlbumImageView extends MeasuredImageView {

	private final String TAG = "sip.adapters.ALBUM_IMAGE_VIEW";
	private CommonImage mImage;
	private boolean mLayoutAllowed = false;

	public AlbumImageView(Context context) {
		super(context);
	}

	public AlbumImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AlbumImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setImageWidth(int w) {
		super.mImageWidth = w;

	}

	public void setImageHeight(int h) {
		super.mImageHeight = h;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		super.mImageWidth = getMeasuredWidth();
		super.mImageHeight = getMeasuredWidth();

		setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
	}

	@Override
	public void requestLayout() {
		if (!mLayoutAllowed)
			return;

		super.requestLayout();

	}

	@Override
	public MeasuredImageView setImgDrawable(CommonImage image) {
		mImage = image;
		return this;
	}

	@Override
	public CommonImage getImageDrawable() {
		return mImage;
	}

	@Override
	public int getImageWidth() {
		return super.mImageWidth;
	}

	@Override
	public int getImageHeight() {
		return super.mImageHeight;
	}

	@Override
	public void startImageDisplaying() {
		if (mImageHeight == 0 || mImageWidth == 0)
			calculateOnZero();
		else {
			if (mImage.configObject.isCacheGetEnabled()) {
				final Bitmap b = mImage.getFromCache(getContext());

				if (b != null) {
					final BitmapDrawable drawable = new BitmapDrawable(getResources(), b);
					displayDrawable(drawable);
					return;
				}
			}

			mImage.setImageHeight(mImageHeight);
			mImage.setImageWidth(mImageWidth);

			WorkerHelper.getInstance().display(this, mImage, getContext());
		}
	}

	@Override
	public void displayDrawable(BitmapDrawable drawable) {
		setImageDrawable(drawable);
	}

}

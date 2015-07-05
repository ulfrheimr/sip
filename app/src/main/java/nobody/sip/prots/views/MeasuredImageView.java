package nobody.sip.prots.views;

import nobody.sip.prots.CommonImage;
import nobody.sip.prots.ImageTemplate.Image;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;

public abstract class MeasuredImageView extends ImageView implements OnGlobalLayoutListener {
	protected int mImageWidth;
	protected int mImageHeight;

	public MeasuredImageView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MeasuredImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public MeasuredImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	protected void calculateOnZero() {
		getViewTreeObserver().addOnGlobalLayoutListener(this);
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	@Override
	public void onGlobalLayout() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
			getViewTreeObserver().removeGlobalOnLayoutListener(this);
		else
			getViewTreeObserver().removeOnGlobalLayoutListener(this);

		mImageHeight = getHeight() <= 0 ? getLayoutParams().height : getHeight();
		mImageWidth = getWidth() <= 0 ? getLayoutParams().width : getWidth();

		
		startImageDisplaying();
	}

	public abstract int getImageWidth();

	public abstract int getImageHeight();

	public abstract CommonImage getImageDrawable();

	public abstract MeasuredImageView setImgDrawable(CommonImage image);

	public abstract void startImageDisplaying();

	public abstract void displayDrawable(BitmapDrawable drawable);

}

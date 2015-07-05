package nobody.sip.ui;

import nobody.sip.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

public class LayoutWithBanner extends RelativeLayout {
	private final String TAG = "sip.ui.LAYOUT_WITH_BANNER";
	private final int SLOPE = ViewConfiguration.get(getContext()).getScaledTouchSlop();

	private int CONTRACTED_SIZE = 50;
	private int NORMAL_SIZE = 100;
	private boolean mHandlingTouch = true;
	private int mStartX;
	private int mStartY;
	private int dX;
	private int dY;

	private int mSpan = 0;
	private boolean mControlRet = false;

	private View mTopContainer;
	private SeekBar mTopSeekbar;

	private RelativeLayout mBannerLayout;
	private OnLayoutWithBannerListener mListener;

	public View userContainer;

	public LayoutWithBanner(Context context) {
		super(context);
	}

	public LayoutWithBanner(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public LayoutWithBanner(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public interface OnLayoutWithBannerListener {
		public void onLayoutWithBannerBuilt();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mStartX = (int) event.getX();
			mStartY = (int) event.getY();
			mHandlingTouch = true;
			break;

		case MotionEvent.ACTION_MOVE:
			if (mHandlingTouch) {
				dX = (int) Math.abs(mStartX - event.getX());
				dY = (int) Math.abs(mStartY - event.getY());

				if (dX > SLOPE || dY > SLOPE) {
					if (dX < 3 * dY)
						mHandlingTouch = true;

					mSpan = (int) (mStartY - event.getY());
					mControlRet = handleTopExpand(mTopContainer.getTop() + mTopContainer.getHeight() - mSpan);

					mStartX = (int) event.getX();
					mStartY = (int) event.getY();

					if (!mControlRet)
						return super.dispatchTouchEvent(event);
					else
						requestLayout();
				}
			}
			return true;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			mStartX = 0;
			mStartY = 0;
			mSpan = 0;
			dX = 0;
			dY = 0;
			break;
		}

		return super.dispatchTouchEvent(event);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (mControlRet) {
			mTopContainer.layout(0, mTopContainer.getTop() - mSpan, getWidth(),
					mTopContainer.getTop() - mSpan + mTopContainer.getHeight());
			userContainer.layout(0, userContainer.getTop() - mSpan, getWidth(), getHeight());
		} else
			super.onLayout(changed, l, t, r, b);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();

		mTopContainer = getChildAt(0);
		userContainer = getChildAt(1);

		mBannerLayout = (RelativeLayout) mTopContainer.findViewById(R.id.lay_banner_inner_top_container);
		mTopSeekbar = (SeekBar) mTopContainer.findViewById(R.id.lay_banner_seekbar);

		mTopSeekbar.setEnabled(false);

		CONTRACTED_SIZE = (int) getContext().getResources().getDimension(R.dimen.small_player_size);
		NORMAL_SIZE = (int) getContext().getResources().getDimension(R.dimen.details_full_size);

		if (mListener != null)
			mListener.onLayoutWithBannerBuilt();
	}

	private boolean handleTopExpand(int height) {
		boolean ret = true;

		if (height <= CONTRACTED_SIZE) {
			height = CONTRACTED_SIZE;
			ret = false;
		} else if (height >= NORMAL_SIZE) {
			height = NORMAL_SIZE;
			ret = false;
		}

		return ret;
	}

	public boolean reportMoving() {
		return mControlRet;
	}

	public void setSeekBarSpecs(int max) {
		mTopSeekbar.setMax(max);
	}

	public void setBannerView(View bannerView) {
		if (bannerView != null) {
			this.mBannerLayout.removeAllViews();
			this.mBannerLayout.addView(bannerView);
		}
	}

	public void setOnLayoutWithBannerListener(OnLayoutWithBannerListener listener) {
		mListener = listener;
	}

	public void updateSeekBar(int progress) {
		mTopSeekbar.setProgress(progress);
	}

}

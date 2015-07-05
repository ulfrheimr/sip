package nobody.sip.ui;

import nobody.sip.R;
import nobody.sip.core.SongSelector.AddMode;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.View.OnClickListener;

public class MultiSelect extends ToggleView implements OnClickListener, OnGlobalLayoutListener {
	private final String TAG = "sip.ui.MULTISELECT";
	private final double PIXEL_RATE = 3.5;
	private final int REFRESH_TIME = 8;
	private int mSize;

	private View mTop;
	private View mModeToggler;
	private View mToggler;
	private View mOK;
	private TextView mItemCount;
	private TextView mFirstText;
	private TextView mLastText;
	public ViewGroup multiContainer;

	private int mAnimOffset = 0;

	public MultiSelectBannerState bannerState = MultiSelectBannerState.ADDING;
	public AddMode addMode = AddMode.FIRST;

	private OnMultiSelectListener mListener;
	private RelativeLayout.LayoutParams mLayoutParams;

	public MultiSelect(Context context) {
		super(context);

	}

	public MultiSelect(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	public MultiSelect(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

	}

	public enum MultiSelectBannerState {
		PLAYLIST, ADDING
	}

	public interface OnMultiSelectListener {
		public void onOKPressed();
	}

	public void setItemCount(int itemCount) {
		if (itemCount > 0) {
			if (toggleVisibility != ToggleViewVisibility.SHOWING || toggleVisibility != ToggleViewVisibility.SHOWN)
				show();

			mItemCount.setText(" " + itemCount + " ");
		} else {
			mModeToggler.setVisibility(View.INVISIBLE);
			bannerState = MultiSelectBannerState.ADDING;
			hide();
		}

	}

	public void setOnMultiSelectListener(OnMultiSelectListener listener) {
		mListener = listener;
	}

	@Override
	public void onClick(View v) {
		if (v == mOK) {
			hide();

			if (mListener != null)
				mListener.onOKPressed();
		} else if (v == mToggler) {
			if (bannerState == MultiSelectBannerState.ADDING) {
				mModeToggler.setVisibility(View.INVISIBLE);
				bannerState = MultiSelectBannerState.PLAYLIST;

				mItemCount.setVisibility(View.GONE);
				mLastText.setVisibility(View.GONE);
				mFirstText.setText(R.string.multi_playlist);
			} else {
				addMode = AddMode.CLEAR;
				bannerState = MultiSelectBannerState.ADDING;
				mModeToggler.setVisibility(View.VISIBLE);
				mModeToggler.setBackgroundResource(R.drawable.clear);
				mItemCount.setVisibility(View.VISIBLE);
				mLastText.setVisibility(View.VISIBLE);

				mFirstText.setText(R.string.multi_adding);
			}
		} else if (v == mModeToggler) {
			switch (addMode) {
			case CLEAR:
				addMode = AddMode.FIRST;
				mModeToggler.setBackgroundResource(R.drawable.firs);
				break;
			case FIRST:
				addMode = AddMode.NEXT_TO;
				mModeToggler.setBackgroundResource(R.drawable.next_to);
				break;
			case NEXT_TO:
				addMode = AddMode.AT_LAST;
				mModeToggler.setBackgroundResource(R.drawable.at_last);
				break;
			case AT_LAST:
				addMode = AddMode.CLEAR;
				mModeToggler.setBackgroundResource(R.drawable.clear);
				break;
			}
		}

	}

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	@Override
	public void onGlobalLayout() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
			getViewTreeObserver().removeGlobalOnLayoutListener(this);
		else
			getViewTreeObserver().removeOnGlobalLayoutListener(this);

		mSize = mTop.getHeight();
		mTop.getLayoutParams().height = 0;
	}

	@Override
	public void handleHiding() {
		if (mTop.getHeight() < mAnimOffset)
			mAnimOffset = mTop.getHeight();

		mLayoutParams.height = mTop.getHeight() - mAnimOffset;
		mTop.setAlpha((float) mTop.getLayoutParams().height / (float) mSize);

		mTop.setLayoutParams(mLayoutParams);
	}

	@Override
	public void handleShowing() {
		if (mTop.getHeight() == 0) {
			mTop.setVisibility(View.VISIBLE);
			mLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 0);
		}

		if (mTop.getHeight() + mAnimOffset >= mSize)
			mAnimOffset = mSize - mTop.getHeight();

		mLayoutParams.height = mTop.getHeight() + mAnimOffset;
		mTop.setAlpha((float) mTop.getLayoutParams().height / (float) mSize);

		mTop.setLayoutParams(mLayoutParams);
	}

	@Override
	public boolean isContinueNeeded() {
		if (mTop.getLayoutParams().height >= mSize) {
			toggleVisibility = ToggleViewVisibility.SHOWN;
			return false;
		}

		if (mTop.getLayoutParams().height == 0) {
			mLayoutParams = null;
			restartMulti();

			mTop.setVisibility(View.INVISIBLE);
			toggleVisibility = ToggleViewVisibility.HIDDEN;
			return false;
		}

		return true;
	}

	@Override
	public void handleStopping() {
		// TODO Auto-generated method stub

	}

	@Override
	public void forceHide() {
		toggleVisibility = ToggleViewVisibility.SHOWN;
		toggleMulti();

	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		init();
	}

	private void init() {
		super.setUpdateRate(PIXEL_RATE);
		super.setUpdateTime(REFRESH_TIME);
		toggleVisibility = ToggleViewVisibility.HIDDEN;

		mTop = findViewById(R.id.multi_top_banner);
		mModeToggler = findViewById(R.id.multi_mode_toggler);
		mToggler = findViewById(R.id.multi_toggler);
		mOK = findViewById(R.id.multi_ok);

		mItemCount = (TextView) findViewById(R.id.multi_item_count);
		mFirstText = (TextView) findViewById(R.id.multi_item_text);
		mLastText = (TextView) findViewById(R.id.multi_item_desc);

		mModeToggler.setOnClickListener(this);
		mToggler.setOnClickListener(this);
		mOK.setOnClickListener(this);

		multiContainer = (ViewGroup) findViewById(R.id.multi_container);

		getViewTreeObserver().addOnGlobalLayoutListener(this);
	}

	private void restartMulti() {
		mFirstText.setText(R.string.multi_adding);
		mModeToggler.setBackgroundResource(R.drawable.clear);
		mItemCount.setVisibility(View.VISIBLE);
		mLastText.setVisibility(View.VISIBLE);

		bannerState = MultiSelectBannerState.ADDING;
		addMode = AddMode.CLEAR;
	}

	public void show() {
		toggleVisibility = ToggleViewVisibility.HIDDEN;
		toggleMulti();
	}

	public void hide() {
		toggleVisibility = ToggleViewVisibility.SHOWN;
		toggleMulti();
	}

	public void toggleMulti() {
		mAnimOffset = (int) ((mSize * PIXEL_RATE) / REFRESH_TIME);
		super.toggle();
	}

}

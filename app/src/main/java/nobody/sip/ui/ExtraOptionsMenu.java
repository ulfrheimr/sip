package nobody.sip.ui;

import nobody.sip.R;
import nobody.sip.core.PlayerService;
import nobody.sip.core.PlayerService.ItemType;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ExtraOptionsMenu extends ToggleView implements OnClickListener, OnGlobalLayoutListener {
	private final String TAG = "sip.ui.EXTRA_OPTIONS_MENU";
	private final float ALPHA_SPAN = 0.09f;

	private final int REFRESH_TIME = 5;
	private double mAnimSpan = 0.35;

	private ViewGroup mContainer;
	private int[] mContainerLocation = new int[2];

	private View mAnchorView;
	private int[] mAnchorLocation = new int[2];

	private Point mWindowSize = new Point();

	private View mNextTo;
	private View mAtLast;
	private View mEdit;

	private Object mIDPressed = null;

	private OnExtraOptionsMenuListener mListener;

	private ItemType mOptionType = ItemType.SONG;

	private OpenDirection mOpenDirection = OpenDirection.BOTTOM_RIGHT;

	private int[] mNormalSize;
	private LayoutParams mLayoutParams;

	public ExtraOptionsMenu(Context context) {
		super(context);
	}

	public ExtraOptionsMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ExtraOptionsMenu(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	private enum OpenDirection {
		TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
	}

	public enum ExtraOptionsMenuState {
		SHOWN, HIDDEN, SHOWING, HIDING
	}

	public interface OnExtraOptionsMenuListener {
		public ItemType onRequestFragmentItem();

		public void onNextTopSelected(Object item, ItemType type);

		public void onAtLastSelected(Object item, ItemType type);

		public void onEditSelected(Object item, ItemType type);
	}

	private OpenDirection getOpenDirection(View anchorView) {
		boolean right = true;
		boolean bottom = true;

		mLayoutParams = new LayoutParams(0, 0);

		mContainer.getLocationOnScreen(mContainerLocation);
		anchorView.getLocationOnScreen(mAnchorLocation);

		if (mWindowSize.x < (mAnchorLocation[0] + mNormalSize[0]))
			right = false;

		if (mWindowSize.y < (mAnchorLocation[1] + mNormalSize[1] + anchorView.getHeight()))
			bottom = false;

		if (!right && !bottom)
			return OpenDirection.TOP_LEFT;
		else if (!right && bottom)
			return OpenDirection.BOTTOM_LEFT;
		else if (right && !bottom)
			return OpenDirection.TOP_RIGHT;
		else
			return OpenDirection.BOTTOM_RIGHT;
	}

	@Override
	protected void onAttachedToWindow() {
		String editText = "";

		super.onAttachedToWindow();

		setUpdateTime(REFRESH_TIME);

		LayoutInflater.from(getContext()).inflate(R.layout.extra_options_menu, this, true);

		if (mListener != null)
			mOptionType = mListener.onRequestFragmentItem();

		mNextTo = findViewById(R.id.extra_next);
		mAtLast = findViewById(R.id.extra_at_last);
		mEdit = findViewById(R.id.extra_edit);

		editText = ((TextView) findViewById(R.id.extra_edit_text)).getText().toString();

		switch (mOptionType) {
		case ARTIST:
			((TextView) findViewById(R.id.extra_edit_text)).setText(editText + " "
					+ getContext().getResources().getString(R.string.artist));
			break;
		case GENRE:
			((TextView) findViewById(R.id.extra_edit_text)).setText(editText + " "
					+ getContext().getResources().getString(R.string.genre));
			break;
		default:
			mEdit.setVisibility(View.GONE);
			break;
		}

		mNextTo.setOnClickListener(this);
		mAtLast.setOnClickListener(this);
		mEdit.setOnClickListener(this);

		this.setVisibility(View.INVISIBLE);
		getViewTreeObserver().addOnGlobalLayoutListener(this);
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
	public void onGlobalLayout() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
			getViewTreeObserver().removeGlobalOnLayoutListener(this);
		else
			getViewTreeObserver().removeOnGlobalLayoutListener(this);

		((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getSize(mWindowSize);

		mNormalSize = new int[] { getWidth(), getHeight() };

		mLayoutParams = new LayoutParams(0, 0);
		this.setLayoutParams(mLayoutParams);

	}

	@Override
	public void onClick(View v) {
		if (mListener != null) {
			if (v == mEdit)
				mListener.onEditSelected(mIDPressed, mOptionType);
			else if (v == mAtLast)
				mListener.onAtLastSelected(mIDPressed, mOptionType);
			else if (v == mNextTo)
				mListener.onNextTopSelected(mIDPressed, mOptionType);
		}

		forceHide();
	}

	/* ToggleView implementation */
	@Override
	public void handleHiding() {

		if (this.getAlpha() - ALPHA_SPAN <= 0) {
			toggleVisibility = ToggleViewVisibility.HIDDEN;
			this.setAlpha(0f);

			mLayoutParams.width = 0;
			mLayoutParams.height = 0;

			this.setLayoutParams(mLayoutParams);

			this.setVisibility(View.INVISIBLE);
		} else
			this.setAlpha(this.getAlpha() - ALPHA_SPAN);
	}

	@Override
	public void handleShowing() {
		Log.d(TAG, "Start");
		if (this.getWidth() == 0)
			this.setVisibility(View.VISIBLE);

		if ((mLayoutParams.height + (mAnimSpan * mNormalSize[1])) >= mNormalSize[1]) {
			mLayoutParams.width = mNormalSize[0];
			mLayoutParams.height = mNormalSize[1];

			toggleVisibility = ToggleViewVisibility.SHOWN;
		} else {
			mLayoutParams.width = (int) (mLayoutParams.width + (mAnimSpan * mNormalSize[0]));
			mLayoutParams.height = (int) (mLayoutParams.height + (mAnimSpan * mNormalSize[1]));
		}

		setWindowPosition();

		this.setLayoutParams(mLayoutParams);
		this.setAlpha(this.getLayoutParams().width / (float) mNormalSize[0]);
		Log.d(TAG, "Finish");
	}

	@Override
	public boolean isContinueNeeded() {
		if (toggleVisibility == ToggleViewVisibility.HIDDEN || toggleVisibility == ToggleViewVisibility.SHOWN)
			return false;

		return true;
	}

	@Override
	public void handleStopping() {
		// TODO Auto-generated method stub

	}

	@Override
	public void forceHide() {
		if (toggleVisibility != ToggleViewVisibility.HIDDEN && toggleVisibility != ToggleViewVisibility.HIDING) {
			toggleVisibility = ToggleViewVisibility.SHOWN;
			toggle();
		}

	}

	@Override
	public void stopCommonView() {
		super.stopCommonView();
	}

	private void setWindowPosition() {
		mAnchorView.getLocationOnScreen(mAnchorLocation);
		mContainer.getLocationOnScreen(mContainerLocation);

		switch (mOpenDirection) {
		case BOTTOM_LEFT:

			mLayoutParams.topMargin = mAnchorLocation[1] - mContainerLocation[1] + mAnchorView.getHeight();
			mLayoutParams.leftMargin = mAnchorLocation[0] - mLayoutParams.width;

			break;
		case TOP_LEFT:
			mLayoutParams.topMargin = mAnchorLocation[1] - mContainerLocation[1] - mLayoutParams.height;
			mLayoutParams.leftMargin = mAnchorLocation[0] - mLayoutParams.width;
			break;

		case TOP_RIGHT:
			mLayoutParams.topMargin = mAnchorLocation[1] - mContainerLocation[1] - mLayoutParams.height;
			mLayoutParams.leftMargin = mAnchorLocation[0] + mAnchorView.getWidth();
			break;
		case BOTTOM_RIGHT:
			mLayoutParams.topMargin = mAnchorLocation[1] - mContainerLocation[1] + mAnchorView.getHeight();
			mLayoutParams.leftMargin = mAnchorLocation[0] + mAnchorView.getWidth();
			break;
		}

	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		Log.d(TAG, " onLayout");
	}

	public void setRoot(ViewGroup container) throws Exception {
		if (container == null)
			throw new Exception("Null container");

		mContainer = container;
		mContainer.addView(this);

	}

	public void setOnExtraOptionsMenuListener(OnExtraOptionsMenuListener listener) {
		mListener = listener;
	}

	public void toggle(View view, Object item) {
		mAnchorView = view;
		mIDPressed = item;

		if (toggleVisibility == ToggleViewVisibility.HIDDEN)
			mOpenDirection = getOpenDirection(view);

		super.toggle();
	}

}

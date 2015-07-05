package nobody.sip.ui;

import nobody.sip.ui.commons.OnStopCommonView;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;

public abstract class ToggleView extends RelativeLayout implements Runnable, OnStopCommonView {
	private final String TAG = "sip.ui.TOGGLE_VIEW";

	private int REFRESH_TIME = 10;
	private double REFRESH_RATE = 8;

	public ToggleViewVisibility toggleVisibility = ToggleViewVisibility.HIDDEN;
	private Handler mHandler = new Handler();

	public enum ToggleViewVisibility {
		SHOWN, HIDDEN, SHOWING, HIDING
	}

	public ToggleView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public ToggleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public ToggleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public void setUpdateTime(int time) {
		REFRESH_TIME = time;
	}

	public float getUpdateTime() {
		return REFRESH_TIME;
	}

	public void setUpdateRate(double rate) {
		REFRESH_RATE = rate;
	}

	public double getUpdateRate() {
		return REFRESH_RATE;
	}
	
	@Override
	public void run() {
		if (toggleVisibility == ToggleViewVisibility.HIDING)
			handleHiding();
		else if (toggleVisibility == ToggleViewVisibility.SHOWING)
			handleShowing();

		if (isContinueNeeded()) {
			if (REFRESH_TIME > 0)
				mHandler.postDelayed(this, REFRESH_TIME);
			else
				mHandler.post(this);
		} else
			mHandler.removeCallbacks(this);
	}

	protected void toggle() {
		if (toggleVisibility == ToggleViewVisibility.HIDDEN) {
			toggleVisibility = ToggleViewVisibility.SHOWING;
			mHandler.post(this);
		} else if (toggleVisibility == ToggleViewVisibility.SHOWN) {
			toggleVisibility = ToggleViewVisibility.HIDING;
			mHandler.post(this);
		}

	}

	public void stopRunning() {
		mHandler.removeCallbacks(this);
		handleStopping();
	}

	@Override
	public void stopCommonView() {
		mHandler.removeCallbacksAndMessages(null);
	}

	public abstract void handleHiding();

	public abstract void handleShowing();

	public abstract boolean isContinueNeeded();

	public abstract void handleStopping();

	public abstract void forceHide();
}

package nobody.sip.ui;

import nobody.sip.R;
import nobody.sip.core.PlayerService;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class Player extends RelativeLayout implements OnClickListener, OnGlobalLayoutListener {
	private final String TAG = "sip.ui.PLAYER";
	private final float PRIMARY_EXPAND_RATIO = 1;
	private final float SECONDARY_EXPAND_RATIO = 0.5f;
	private final float TERTIARY_EXPAND_RATIO = 0.25f;

	private int BUTTON_NORMAL_SIZE;
	private int GO_TO_CURRENT_NORMAL_SIZE;

	// private View mGoToCurrent;
	private View mToggle;
	private View mRewind;
	private View mForward;
	private View mRepeat;
	private View mRandom;

	// private ImageView mArt;

	private TextView mSongText;

	private OnPlayerListener mListener;

	public Player(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public Player(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public Player(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public interface OnPlayerListener {

		public void onPlayerBuilt();
	}

	public void setOnPlayerListener(OnPlayerListener listener) {
		mListener = listener;
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		
		mRewind = findViewById(R.id.player_rewind);
		mToggle = findViewById(R.id.player_toggle);
		mForward = findViewById(R.id.player_forward);
		mRandom = findViewById(R.id.player_random);
		mRepeat = findViewById(R.id.player_repeat);

		// mGoToCurrent = (ImageView) findViewById(R.id.player_go_plist);

		mSongText = (TextView) findViewById(R.id.player_song_text);

		mRewind.setOnClickListener(this);
		mToggle.setOnClickListener(this);
		mForward.setOnClickListener(this);
		// mGoToCurrent.setOnClickListener(this);

		getViewTreeObserver().addOnGlobalLayoutListener(this);

		if (mListener != null)
			mListener.onPlayerBuilt();
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	@Override
	public void onGlobalLayout() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
			getViewTreeObserver().removeGlobalOnLayoutListener(this);
		else
			getViewTreeObserver().removeOnGlobalLayoutListener(this);

		BUTTON_NORMAL_SIZE = mForward.getWidth();
		// GO_TO_CURRENT_NORMAL_SIZE = mGoToCurrent.getWidth();
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
	}

	@Override
	public void onClick(View v) {
		if (v == mToggle)
			getContext().startService(new Intent(PlayerService.TOGGLE));
		else if (v == mRewind)
			getContext().startService(new Intent(PlayerService.REWIND));
		else if (v == mForward)
			getContext().startService(new Intent(PlayerService.FORWARD));
		// else if (v == mGoToCurrent)
		// getContext().startActivity(new Intent(getContext(),
		// OldCurrentPlaylist.class));
		// getContext().stopService(new Intent(getContext(),
		// PlayerService.class));

	}

	public void updateSongText(String text) {
		mSongText.setText(text);
	}

	public void hidePlaylistIcon() {
		// mGoToCurrent.setVisibility(View.GONE);
	}

	public void handleExpand(float ratio) {
		mToggle.getLayoutParams().width = (int) ((1 + (ratio * PRIMARY_EXPAND_RATIO)) * BUTTON_NORMAL_SIZE);
		mToggle.getLayoutParams().height = (int) ((1 + (ratio * PRIMARY_EXPAND_RATIO)) * BUTTON_NORMAL_SIZE);

		mForward.getLayoutParams().width = (int) ((1 + (ratio * SECONDARY_EXPAND_RATIO)) * BUTTON_NORMAL_SIZE);
		mForward.getLayoutParams().height = (int) ((1 + (ratio * SECONDARY_EXPAND_RATIO)) * BUTTON_NORMAL_SIZE);

		mRewind.getLayoutParams().width = (int) ((1 + (ratio * SECONDARY_EXPAND_RATIO)) * BUTTON_NORMAL_SIZE);
		mRewind.getLayoutParams().height = (int) ((1 + (ratio * SECONDARY_EXPAND_RATIO)) * BUTTON_NORMAL_SIZE);

		// mGoToCurrent.getLayoutParams().width = (int) ((1 - (ratio *
		// TERTIARY_EXPAND_RATIO)) * GO_TO_CURRENT_NORMAL_SIZE);
		// mGoToCurrent.getLayoutParams().height = (int) ((1 - (ratio *
		// TERTIARY_EXPAND_RATIO)) * GO_TO_CURRENT_NORMAL_SIZE);

		mSongText.setAlpha(1 - ratio);
		mRepeat.setAlpha(ratio);
		mRandom.setAlpha(ratio);

		if (ratio < 0.1f) {
			if (mRepeat.getVisibility() != INVISIBLE) {
				mRepeat.setVisibility(View.INVISIBLE);
				mRandom.setVisibility(View.INVISIBLE);
			}
		} else {
			if (mRepeat.getVisibility() != VISIBLE) {
				mRepeat.setVisibility(View.VISIBLE);
				mRandom.setVisibility(View.VISIBLE);
			}
		}
	}
}

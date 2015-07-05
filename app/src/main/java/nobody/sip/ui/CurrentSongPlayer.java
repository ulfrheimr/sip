package nobody.sip.ui;

import nobody.sip.R;
import nobody.sip.core.PlayerService;
import nobody.sip.core.PlayerService.PlayerState;
import nobody.sip.helpers.ArtworkHelper;
import nobody.sip.prots.Album;
import nobody.sip.prots.CommonImage.CommonConfigObject;
import nobody.sip.prots.views.AlbumImage;
import nobody.sip.prots.views.AlbumImageView;
import nobody.sip.prots.Song;
import nobody.sip.ui.Player.OnPlayerListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.view.MotionEvent;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

public class CurrentSongPlayer extends ToggleView implements OnPlayerListener, OnTouchListener, OnGlobalLayoutListener {
	private final String TAG = "sip.ui.CURRENT_SONG_PLAYER";
	private final int SLOP = ViewConfiguration.get(getContext()).getScaledTouchSlop();

	private final int REFRESH_TIME = 5;
	private final double PIXEL_RATE = 2;
	private final float TOP_EXPAND_RATIO = 0.6f;

	private int TOP_PLAYER_NORMAL_SIZE;
	private int TOP_PLAYER_EXPANDED_SIZE;
	private int SMALL_ART_NORMAL_SIZE[];

	private View mSongContainer;

	private View mPrincipalContainer;
	private View mTopContainer;
	private Player mPlayer;
	private AlbumImageView mSmallArt;
	private SeekBar mSmallSeekbar;

	private OnSongPlayerListener mListener;
	private ArtworkHelper.AlbumArtworkLoader mSmallArtUpdater;

	private int mLastMovement = 0;
	private int mAnimOffset = 0;

	private int mTopMarginSpan;

	private long mBackIDSong = PlayerService.INVALID_ID_OR_POSITION;

	private Handler mHandler = new Handler();

	/*
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
	// private ArtworkUpdater mLargeArtworkUpdater;
	// private ArtworkGesture mArtworkGesture;

	// private int mOriginalArtTouch;
	// private int mBackArtTouch;
	//
	// private float mTravelSpanWidth;
	// private BitmapDrawable mMobileCurrent;
	// private Rect mOriginalBounds;
	// private Rect mCurrentBounds;

	public CurrentSongPlayer(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CurrentSongPlayer(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CurrentSongPlayer(Context context) {
		super(context);
	}

	public interface OnSongPlayerListener {

		public void onPlayerToggling(ToggleViewVisibility visibility);

		public void onSongPlayerBuilt();
	}

	public void setOnSongPlayerListener(OnSongPlayerListener l) {
		mListener = l;
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();

		super.setUpdateRate(PIXEL_RATE);
		super.setUpdateTime(REFRESH_TIME);

		mSongContainer = getChildAt(0);
		mPrincipalContainer = getChildAt(1);

		mTopContainer = mPrincipalContainer.findViewById(R.id.cspl_top_container);
		mPlayer = (Player) mTopContainer.findViewById(R.id.cspl_player);
		mSmallArt = (AlbumImageView) mTopContainer.findViewById(R.id.cspl_small_art);
		mSmallSeekbar = (SeekBar) mTopContainer.findViewById(R.id.cspl_small_seekbar);

		mSmallSeekbar.setEnabled(false);
		mSmallSeekbar.setClickable(false);

		mSmallArt.setOnTouchListener(this);

		mPlayer.setOnPlayerListener(this);
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

		TOP_PLAYER_NORMAL_SIZE = mTopContainer.getHeight();
		TOP_PLAYER_EXPANDED_SIZE = (int) ((1 + TOP_EXPAND_RATIO) * mTopContainer.getHeight());
		SMALL_ART_NORMAL_SIZE = new int[] { mSmallArt.getWidth(), mSmallArt.getHeight() };

		mSongContainer.getLayoutParams().height = (int) (this.getHeight() - (TOP_PLAYER_NORMAL_SIZE * (1 + TOP_EXPAND_RATIO) - 5));

		if (mListener != null)
			mListener.onSongPlayerBuilt();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (v != mSmallArt)
				return false;

			if (toggleVisibility == ToggleViewVisibility.HIDDEN) {
				toggleVisibility = ToggleViewVisibility.SHOWING;

				if (mListener != null)
					mListener.onPlayerToggling(toggleVisibility);
			} else if (toggleVisibility == ToggleViewVisibility.SHOWN)
				toggleVisibility = ToggleViewVisibility.HIDING;
			break;
		case MotionEvent.ACTION_MOVE:
			if (Math.abs(event.getY()) > SLOP) {
				mLastMovement = (int) event.getY();
				handlePrincipalToggling((int) event.getY() + SLOP);
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			if (mLastMovement >= 0)
				toggleVisibility = ToggleViewVisibility.HIDDEN;
			else
				toggleVisibility = ToggleViewVisibility.SHOWN;

			if (mListener != null)
				mListener.onPlayerToggling(toggleVisibility);

			toggleSongPanel();
			break;
		}
		return true;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (toggleVisibility == ToggleViewVisibility.SHOWN) {
			// mSongContainer.layout(l, 0, r, this.getHeight() -
			// mTopContainer.getHeight());
			// mFragmentContainer.layout(l, this.getHeight() -
			// mDropAnchor.getHeight() - SLOP, r,
			// this.getHeight() - mDropAnchor.getHeight() +
			// mFragmentContainer.getHeight());

			// if (mLyricsContainer.getVisibility() == VISIBLE)
			// lyricsVisibility = LyricsPanelVisibility.SHOWN;
			// else
			// lyricsVisibility = LyricsPanelVisibility.HIDDEN;
			mPrincipalContainer.layout(0, (this.getHeight() - TOP_PLAYER_EXPANDED_SIZE), this.getWidth(), this.getHeight());
			mSongContainer.layout(0, 0, this.getWidth(), (this.getHeight() - TOP_PLAYER_EXPANDED_SIZE));
		} else if (toggleVisibility == ToggleViewVisibility.HIDING || toggleVisibility == ToggleViewVisibility.SHOWING) {
			// Log.d(TAG, "Causing lay top" + mLayoutParams.topMargin +
			// " bottom  "
			// + (mLayoutParams.topMargin + mLayoutParams.height));
			// // mTopContainer.layout(0, mLayoutParams.topMargin,
			// this.getWidth(),
			// mLayoutParams.height);
			mPrincipalContainer.layout(0, mTopMarginSpan, this.getWidth(), this.getHeight());
			// mPrincipalContainer.layout(l, mPrincipalContainer.getTop(), r,
			// 1500);
			// mTopContainer.layout(l, 300, r, 600);
			// mPlayer.layout(l, 300, r, 600);

			// mSongContainer.layout(l, mTopContainer.getHeight(), r, b);
			//
			// mFragmentContainer.layout(l, mFragmentContainer.getTop(), r,
			// mFragmentContainer.getTop() + mFragmentContainer.getHeight());
		} else {
			// if (mListener != null) {
			// if (!mListener.isStopLayoutNeed())
			// super.onLayout(changed, l, t, r, b);
			// } else
			super.onLayout(changed, l, t, r, b);

		}

	}

	/* ToggleView implementation */
	@Override
	public void handleHiding() {
		hidePrincipalPanel(-mAnimOffset);
	}

	@Override
	public void handleShowing() {
		showPrincipalPanel(mAnimOffset);
	}

	@Override
	public boolean isContinueNeeded() {
		if (mPrincipalContainer.getTop() >= (this.getHeight() - TOP_PLAYER_EXPANDED_SIZE)) {
			toggleVisibility = ToggleViewVisibility.SHOWN;
			return false;
		} else if (mPrincipalContainer.getTop() <= 0) {
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
		// TODO Auto-generated method stub

	}

	/* OnPlayerListener implementation */
	@Override
	public void onPlayerBuilt() {

	}

	public void updateSongPanel(Song currentPlaySong, PlayerState playerState, int currentPosition) {
		mSmallSeekbar.setMax((int) currentPlaySong.duration);

		if (currentPlaySong != null) {
			mPlayer.updateSongText(currentPlaySong.title + " - " + currentPlaySong.artist);

			if (currentPlaySong.idAlbum != mBackIDSong) {
				updateTinyArt((Album) currentPlaySong);
				mBackIDSong = currentPlaySong.idAlbum;
			}
		}
	}

	private void hidePrincipalPanel(int heightSpan) {
		int nextStep = mPrincipalContainer.getTop() + heightSpan;

		if (nextStep <= 0)
			heightSpan = -mPrincipalContainer.getTop();

		expandPanel(heightSpan);
	}

	private void showPrincipalPanel(int heightSpan) {
		int nextStep = mPrincipalContainer.getTop() + heightSpan;

		if (nextStep >= (this.getHeight() - TOP_PLAYER_EXPANDED_SIZE))
			heightSpan = (this.getHeight() - TOP_PLAYER_EXPANDED_SIZE) - mPrincipalContainer.getTop();

		expandPanel(heightSpan);
	}

	private void expandPanel(int heightSpan) {
		float ratio;

		ratio = ((float) (mPrincipalContainer.getTop()) / (float) (this.getHeight() - TOP_PLAYER_NORMAL_SIZE)) * TOP_EXPAND_RATIO;
		mTopMarginSpan = mPrincipalContainer.getTop() + heightSpan;

		mTopContainer.getLayoutParams().height = (int) ((1 + ratio) * TOP_PLAYER_NORMAL_SIZE);

		mSmallArt.getLayoutParams().width = (int) ((1 - ratio) * SMALL_ART_NORMAL_SIZE[0]);
		mSmallArt.getLayoutParams().height = (int) ((1 - ratio) * SMALL_ART_NORMAL_SIZE[1]);

		mPlayer.handleExpand((float) (mPrincipalContainer.getTop()) / (float) (this.getHeight() - TOP_PLAYER_NORMAL_SIZE));

		mPrincipalContainer.requestLayout();
	}

	private void updateTinyArt(Album album) {

		mSmallArt.setImageWidth(SMALL_ART_NORMAL_SIZE[0]);
		mSmallArt.setImageHeight(SMALL_ART_NORMAL_SIZE[1]);
		mSmallArtUpdater = new ArtworkHelper.AlbumArtworkLoader(album, mSmallArt);
		mHandler.post(mSmallArtUpdater);

	}

	public void updateSongProgress(int progress) {
		mSmallSeekbar.setProgress(progress);
	}

	public void handlePrincipalToggling(int heightSpan) {
		if (mLastMovement < 0)
			hidePrincipalPanel(heightSpan);
		else
			showPrincipalPanel(heightSpan);

	}

	public void toggleSongPanel() {
		mAnimOffset = (int) (((this.getHeight() - mTopContainer.getHeight()) * PIXEL_RATE) / REFRESH_TIME);

		super.toggle();
	}

	/*
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
	//
	// public LyricsPanelVisibility lyricsVisibility =
	// LyricsPanelVisibility.HIDDEN;
	// public enum LyricsPanelVisibility {
	// SHOWN, HIDDEN, CHANGING
	// }
	// private class ArtworkUpdater implements Runnable {
	// private Album mCurrentAlbum;
	// private Album mPreviousAlbum;
	// private Album mNextAlbum;
	//
	// public ArtworkUpdater(UpdateSongSender s) {
	// mCurrentAlbum = (Album) s.currentPlaySong;
	// mPreviousAlbum = (Album) s.previousPlaySong;
	// mNextAlbum = (Album) s.nextPlaySong;
	// }
	//
	// @SuppressLint("NewApi")
	// @Override
	// public void run() {
	// // BitmapDrawable drawable;
	// // Bitmap b =
	// //
	// ImageHelper.getInstance(getContext()).getAlbumImage(mCurrentAlbum.idAlbum,
	// // mCurrentAlbum.arturi,
	// // mCurrentArtwork.getWidth(), mCurrentArtwork.getHeight());
	// //
	// // if (b != null) {
	// // drawable = new BitmapDrawable(getResources(), b);
	// // // b = ImageHelper.getInstance(getContext()).scaleImage(b,
	// // // mCurrentArtwork.getWidth(), mCurrentArtwork.getHeight());
	// // // b = ImageHelper.getInstance(getContext()).scaleImage(b,
	// // // mArtwork.getWidth() / 5, mArtwork.getHeight() / 5);
	// // //
	// // // Bitmap newOne =
	// // // ImageHelper.getInstance(getContext()).blurImage(b, 5);
	// // // BitmapDrawable bmr = new BitmapDrawable(getResources(),
	// // // newOne);
	// // // ((RelativeLayout) mSongContainer).setBackground(bmr);
	// // mCurrentArtwork.setBackground(drawable);
	// // }
	// //
	// // if (mNextAlbum != null) {
	// // b =
	// //
	// ImageHelper.getInstance(getContext()).getAlbumImage(mNextAlbum.idAlbum,
	// // mNextAlbum.arturi,
	// // mNextArtwork.getWidth() / 5, mNextArtwork.getHeight() / 5);
	// //
	// // if (b != null) {
	// // b = ImageHelper.getInstance(getContext()).blurImage(b, 3);
	// // drawable = new BitmapDrawable(getResources(), b);
	// // mNextArtwork.setBackground(drawable);
	// // }
	// // }
	// //
	// // if (mPreviousAlbum != null) {
	// // b =
	// //
	// ImageHelper.getInstance(getContext()).getAlbumImage(mPreviousAlbum.idAlbum,
	// // mPreviousAlbum.arturi,
	// // mPrevArtwork.getWidth() / 5, mPrevArtwork.getHeight() / 5);
	// //
	// // if (b != null) {
	// // b = ImageHelper.getInstance(getContext()).blurImage(b, 3);
	// // drawable = new BitmapDrawable(getResources(), b);
	// // mPrevArtwork.setBackground(drawable);
	// // }
	// //
	// // }
	// }
	// }
	//
	// // private class ArtworkGesture implements OnTouchListener {
	// //
	// // @Override
	// // public boolean onTouch(View v, MotionEvent event) {
	// // switch (event.getAction()) {
	// // case MotionEvent.ACTION_DOWN:
	// //
	// // if (v != mCurrentArtwork)
	// // return false;
	// //
	// // mOriginalArtTouch = (int) event.getX();
	// // mBackArtTouch = mOriginalArtTouch;
	// //
	// // mMobileCurrent = getMobileView(mCurrentArtwork);
	// // invalidate();
	// // break;
	// //
	// // case MotionEvent.ACTION_MOVE:
	// // if (Math.abs(event.getX() - mBackArtTouch) > SLOP) {
	// // int travelSpan = (int) event.getX() - mOriginalArtTouch;
	// // mBackArtTouch = (int) event.getX() - mBackArtTouch;
	// //
	// // float span = (float) travelSpan / mTravelSpanWidth;
	// //
	// // handleCurrentMove(travelSpan);
	// // invalidate();
	// // }
	// // // int imageSpanX = mBackImageX - (int) event.getX();
	// //
	// // // handleCurrentMove((int) event.getX());
	// // // invalidate();
	// // break;
	// // case MotionEvent.ACTION_UP:
	// // case MotionEvent.ACTION_CANCEL:
	// // nullifyBuffers();
	// // break;
	// // }
	// //
	// // return true;
	// // }
	// // }
	// @Override
	// protected void dispatchDraw(Canvas canvas) {
	// super.dispatchDraw(canvas);
	// if (mMobileCurrent != null) {
	// mMobileCurrent.draw(canvas);
	// }
	// }
	//
	// @Override
	// public void stopCustomView() {
	// super.stopCustomView();
	// mHandler.removeCallbacksAndMessages(null);
	// }

	//
	// private void hideLyricsPanel() {
	// // lyricsVisibility = LyricsPanelVisibility.CHANGING;
	// //
	// //
	// mTitle.setTextColor(getContext().getResources().getColor(R.color.cspl_text));
	// //
	// mArtist.setTextColor(getContext().getResources().getColor(R.color.cspl_text));
	// //
	// mDuration.setTextColor(getContext().getResources().getColor(R.color.cspl_text));
	// //
	// mCurrentTime.setTextColor(getContext().getResources().getColor(R.color.cspl_text));
	// //
	// // mLyricsContainer.setVisibility(View.GONE);
	// }
	//
	// private void showLyricsPanel() {
	// // lyricsVisibility = LyricsPanelVisibility.CHANGING;
	// //
	// //
	// mTitle.setTextColor(getContext().getResources().getColor(R.color.cspl_back_color));
	// //
	// mArtist.setTextColor(getContext().getResources().getColor(R.color.cspl_back_color));
	// //
	// mDuration.setTextColor(getContext().getResources().getColor(R.color.cspl_back_color));
	// //
	// mCurrentTime.setTextColor(getContext().getResources().getColor(R.color.cspl_back_color));
	// /
	// // mLyricsContainer.setVisibility(View.VISIBLE);
	// }
	//
	//
	// private void updateArt(UpdateSongSender songSender) {
	// mLargeArtworkUpdater = new ArtworkUpdater(songSender);
	// mHandler.post(mLargeArtworkUpdater);
	// }
	//
	// private void nullifyBuffers() {
	// mMobileCurrent.getBitmap().recycle();
	// mMobileCurrent = null;
	//
	// invalidate();
	// }
	//
	// private void handleCurrentMove(int span) {
	// // float size;
	// //
	// // if (span < 0) {
	// // size = computeNewSize((float) span, mCurrentArtwork.getHeight(),
	// mPrevArtwork.getHeight());
	// // mCurrentBounds.right = mOriginalBounds.right + span;
	// // mCurrentBounds.left = (int) (mCurrentBounds.right - size);
	// // } else {
	// // size = computeNewSize((float) span, mCurrentArtwork.getHeight(),
	// mNextArtwork.getHeight());
	// //
	// // mCurrentBounds.left = mOriginalBounds.left + span;
	// // mCurrentBounds.right = (int) (mCurrentBounds.left + size);
	// // }
	// //
	// // if (Math.abs((float) span / mTravelSpanWidth) >= 1)
	// // return;
	// //
	// // Log.d(TAG, "span: " + span);
	// //
	// // mCurrentBounds.bottom = (int) (mCurrentBounds.top + size);
	// // // mCurrentBounds.left = xSpan;
	// // // mCurrentBounds.right = xSpan + mCurrentArtwork.getWidth();
	// // //
	// // // Log.d(TAG, "Original pos L:" + mCurrentBounds.left + " R:" +
	// // // mCurrentBounds.right);
	// // mMobileCurrent.setBounds(mCurrentBounds);
	// }
	//
	// private float computeNewSize(float span, int originalSize, int newSize) {
	// float factor = Math.abs((float) span / mTravelSpanWidth);
	// float sizeWidth = (float) (1 - Math.pow(factor, 6));
	//
	// if (originalSize >= newSize) {
	// if (sizeWidth < 0)
	// return newSize;
	//
	// return originalSize - ((originalSize - newSize) * factor);
	// }
	//
	// if (sizeWidth < 0)
	// return 0;
	//
	// return sizeWidth;
	// }
	//
	// private BitmapDrawable getMobileView(View v) {
	// int w = v.getWidth();
	// int h = v.getHeight();
	// int top = v.getTop();
	// int left = v.getLeft();
	//
	// Bitmap bitmap = getBitmapFromView(v);
	//
	// BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
	//
	// mOriginalBounds = new Rect(left, top, left + w, top + h);
	// mCurrentBounds = new Rect(mOriginalBounds);
	//
	// drawable.setBounds(mCurrentBounds);
	//
	// return drawable;
	// }
	//
	// private Bitmap getBitmapFromView(View view) {
	// Bitmap b = Bitmap.createBitmap(view.getWidth(), view.getHeight(),
	// Config.ARGB_8888);
	// Canvas c = new Canvas(b);
	//
	// view.draw(c);
	// return b;
	// }
	//
	// public void toggleLyricsPanel() {
	// if (lyricsVisibility == LyricsPanelVisibility.HIDDEN)
	// showLyricsPanel();
	// else if (lyricsVisibility == LyricsPanelVisibility.SHOWN)
	// hideLyricsPanel();
	// }
	//
	// public void changeLyrics(String lyrics) {
	// //mLyrics.setText(lyrics);
	// }
}

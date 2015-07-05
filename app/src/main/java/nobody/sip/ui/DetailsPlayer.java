package nobody.sip.ui;

import nobody.sip.R;
import nobody.sip.core.PlayerService;
import nobody.sip.core.SongUtilities;
import nobody.sip.core.PlayerService.PlayerFragmentItem;
import nobody.sip.core.PlayerService.PlayerState;
import nobody.sip.helpers.ImageHelper;
import nobody.sip.prots.Song;
import nobody.sip.prots.SongWithExtras;
import nobody.sip.prots.CommonImage.CommonConfigObject;
import nobody.sip.prots.views.AlbumImage;
import nobody.sip.prots.views.AlbumImageView;
import nobody.sip.prots.views.EffectAlbumImage;
import nobody.sip.ui.ToggleView.ToggleViewVisibility;
import nobody.sip.ui.commons.CommonPlayerFragment;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.AlphaAnimation;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class DetailsPlayer extends CommonPlayerFragment implements OnSeekBarChangeListener, OnGlobalLayoutListener,
		OnClickListener {
	private final String TAG = "sip.ui.DETAILS_PLAYER";
	private final int UPDATE_TIME_INTERVAL = 1000;
	private final int CHANGED_TIME_DELAY = 500;
	private final int BLURRED_RATIO = 1;
	private final int SMALL_RATIO_IMAGE = 4;

	private TextView mTitle;
	private TextView mArtist;
	private TextView mAlbum;
	private TextView mCurrentTime;
	private TextView mDuration;
	private TextView mChangedTime;

	private ViewGroup mArtworkContainer;
	private AlbumImageView mCurrentArtwork;
	private AlbumImageView mPrevArtwork;
	private AlbumImageView mNextArtwork;

	private View mGoPlaylist;
 
	private SeekBar mSeekBar;

	private Handler mHandler = new Handler();
	private CurrentTimeUpdater mTimeUpdater = new CurrentTimeUpdater();
	private CurrentArtUpdater mCurrentUpdater = new CurrentArtUpdater();
	private PrevArtUpdater mPrevUpdater = new PrevArtUpdater();
	private NextArtUpdater mNextUpdater = new NextArtUpdater();

	private long mBackSongID = PlayerService.INVALID_ID_OR_POSITION;

	private final CommonConfigObject mAlbumConfig = new CommonConfigObject(true, false);
	private final CommonConfigObject mNoCacheAlbumConfig = new CommonConfigObject(false, false);

	private class CurrentTimeUpdater implements Runnable {

		@Override
		public void run() {
			if (getOwnVisibility() != ToggleViewVisibility.HIDDEN && getPlayerState() == PlayerState.PLAYING) {
				int time = getCurrentTime();

				if (time != PlayerService.INVALID_ID_OR_POSITION) {
					mSeekBar.setProgress(time / 1000);
					mCurrentTime.setText(SongUtilities.getInstance(getActivity()).millisToTime(time));
				}
			}

			mHandler.postDelayed(mTimeUpdater, UPDATE_TIME_INTERVAL);
		}
	}

	private class NextArtUpdater implements Runnable {

		@Override
		public void run() {
			int currentPosition = getCurrentPosition();
			if (currentPosition != PlayerService.INVALID_ID_OR_POSITION) {
				final Song nextSong = getSongAtPosition(currentPosition + 1);
				if (nextSong != null) {
					if (mNextArtwork.getVisibility() != View.VISIBLE)
						mNextArtwork.setVisibility(View.VISIBLE);
					final EffectAlbumImage nextImage = new EffectAlbumImage(getSongAtPosition(currentPosition + 1),
							mNoCacheAlbumConfig).setBlurRatio(BLURRED_RATIO);

					nextImage.setImageHeight(mNextArtwork.getHeight() / SMALL_RATIO_IMAGE);
					nextImage.setImageWidth(mNextArtwork.getWidth() / SMALL_RATIO_IMAGE);
					mNextArtwork.setImageHeight(mNextArtwork.getHeight() / SMALL_RATIO_IMAGE);
					mNextArtwork.setImageWidth(mNextArtwork.getWidth() / SMALL_RATIO_IMAGE);

					mNextArtwork.setImgDrawable(nextImage).startImageDisplaying();
				} else
					mNextArtwork.setVisibility(View.INVISIBLE);
			}
		}

	}

	private class PrevArtUpdater implements Runnable {

		@Override
		public void run() {
			int currentPosition = getCurrentPosition();
			if (currentPosition > 0) {
				if (mPrevArtwork.getVisibility() != View.VISIBLE)
					mPrevArtwork.setVisibility(View.VISIBLE);
				final EffectAlbumImage prevImage = new EffectAlbumImage(getSongAtPosition(currentPosition - 1),
						mNoCacheAlbumConfig).setBlurRatio(BLURRED_RATIO);

				prevImage.setImageHeight(mPrevArtwork.getHeight() / SMALL_RATIO_IMAGE);
				prevImage.setImageWidth(mPrevArtwork.getWidth() / SMALL_RATIO_IMAGE);
				mPrevArtwork.setImageHeight(mPrevArtwork.getHeight() / SMALL_RATIO_IMAGE);
				mPrevArtwork.setImageWidth(mPrevArtwork.getWidth() / SMALL_RATIO_IMAGE);

				mPrevArtwork.setImgDrawable(prevImage).startImageDisplaying();
			} else
				mPrevArtwork.setVisibility(View.INVISIBLE);
		}

	}

	private class CurrentArtUpdater implements Runnable {

		@Override
		public void run() {
			int currentPosition = getCurrentPosition();

			if (currentPosition != PlayerService.INVALID_ID_OR_POSITION) {

				final AlbumImage image = new AlbumImage(getSongAtPosition(currentPosition), mAlbumConfig);
				mCurrentArtwork.setImgDrawable(image).startImageDisplaying();
			}
		}

	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.details_player, container, false);
		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		init();
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		if (mHandler == null)
			mHandler = new Handler();

		getView().getViewTreeObserver().addOnGlobalLayoutListener(this);
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	@Override
	public void onGlobalLayout() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
			getView().getViewTreeObserver().removeGlobalOnLayoutListener(this);
		else
			getView().getViewTreeObserver().removeOnGlobalLayoutListener(this);

		setArtworkSpecs(getView().getWidth(), getView().getHeight());
	}

	@Override
	public void onClick(View v) {
		if (v == mGoPlaylist)
			super.navigateToPlayerFragment(PlayerFragmentItem.CURRENT_PLAYLIST, null);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		mChangedTime.setText(SongUtilities.getInstance(getActivity()).millisToTime(progress * 1000));
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		if (mChangedTime.getVisibility() != View.VISIBLE)
			mChangedTime.setVisibility(View.VISIBLE);

		mHandler.removeCallbacks(mTimeUpdater);
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		AlphaAnimation anim = new AlphaAnimation(1, 0);
		anim.setDuration(CHANGED_TIME_DELAY);
		mChangedTime.startAnimation(anim);

		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				mChangedTime.setText("");
				mChangedTime.setVisibility(View.GONE);
			}
		}, CHANGED_TIME_DELAY);

		super.reportSelectedTime(seekBar.getProgress() * 1000);

		mHandler.post(mTimeUpdater);
	}

	/* CommonPlayerFragment implementation */
	@Override
	public void updateFragmentInterface(SongWithExtras currentSong, PlayerState playerState, int currentPosition) {
		try {
			startUpdate(currentSong);
		} catch (Exception e) {
		}
	}

	@Override
	public void handleStartOpening() {
		requestFragmentInfo();
	}

	@Override
	public void playlistHasChanged() {

	}

	@Override
	public void closeFragment() {
		mBackSongID = PlayerService.INVALID_ID_OR_POSITION;
		mHandler.removeCallbacksAndMessages(null);
	}

	private void init() {
		mArtworkContainer = (ViewGroup) getActivity().findViewById(R.id.det_player_artwork_container);
		mCurrentArtwork = (AlbumImageView) mArtworkContainer.findViewById(R.id.det_player_current_artwork);
		mPrevArtwork = (AlbumImageView) mArtworkContainer.findViewById(R.id.det_player_prev_artwork);
		mNextArtwork = (AlbumImageView) mArtworkContainer.findViewById(R.id.det_player_next_artwork);

		mTitle = (TextView) getActivity().findViewById(R.id.det_player_title);
		mArtist = (TextView) getActivity().findViewById(R.id.det_player_artist);
		mAlbum = (TextView) getActivity().findViewById(R.id.det_player_album);
		mCurrentTime = (TextView) getActivity().findViewById(R.id.det_player_current_time);
		mDuration = (TextView) getActivity().findViewById(R.id.det_player_duration);

		mGoPlaylist = getActivity().findViewById(R.id.det_player_go_playlist);

		mChangedTime = (TextView) getActivity().findViewById(R.id.det_player_changed_time);

		mSeekBar = (SeekBar) getActivity().findViewById(R.id.det_player_seekbar);
		mSeekBar.setOnSeekBarChangeListener(this);
		mGoPlaylist.setOnClickListener(this);

		// mArtworkGesture = new ArtworkGesture();
		// toggleVisibility = ToggleViewVisibility.HIDDEN;

		// mLyricsContainer = (RelativeLayout)
		// mSongContainer.findViewById(R.id.cspl_lyrics_container);
		// mLyrics = (TextView) mSongContainer.findViewById(R.id.cspl_lyrics);

		// mCurrentArtwork.setOnTouchListener(mArtworkGesture);

	}

	private void setArtworkSpecs(int width, int height) {
		if (width >= height) {
			// mArtworkContainer.getLayoutParams().width = this.getHeight();
			// mArtworkContainer.getLayoutParams().height = (this.getHeight() /
			// 3) *
			// 2;
			//
			// mCurrentArtwork.getLayoutParams().height = (this.getHeight() / 3)
			// *
			// 2;
			// mCurrentArtwork.getLayoutParams().width = (this.getHeight() / 3)
			// * 2;

		} else {
			mArtworkContainer.getLayoutParams().width = width;
			mArtworkContainer.getLayoutParams().height = (width / 3) * 2;

			mCurrentArtwork.getLayoutParams().height = (width / 3) * 2;
			mCurrentArtwork.getLayoutParams().width = (width / 3) * 2;
		}

		mPrevArtwork.getLayoutParams().height = (mCurrentArtwork.getLayoutParams().height / 4) * 3;
		mPrevArtwork.getLayoutParams().width = (mCurrentArtwork.getLayoutParams().height / 4) * 3;

		mNextArtwork.getLayoutParams().height = (mCurrentArtwork.getLayoutParams().height / 4) * 3;
		mNextArtwork.getLayoutParams().width = (mCurrentArtwork.getLayoutParams().height / 4) * 3;

		mCurrentArtwork.bringToFront();

		requestFragmentInfo();
		// mTravelSpanWidth = (3 * mCurrentArtwork.getLayoutParams().width) / 4;
	}

	private void requestFragmentInfo() {
		int currPos = getCurrentPosition();

		if (currPos != PlayerService.INVALID_ID_OR_POSITION)
			startUpdate(getSongAtPosition(currPos));

	}

	private void startUpdate(SongWithExtras song) {
		if (getListener() != null) {
			if (song.idSong != mBackSongID) {
				mTitle.setText(song.title);
				mArtist.setText(song.artist);
				mAlbum.setText(song.album);
				mDuration.setText(SongUtilities.getInstance(getActivity()).millisToTime(song.duration));

				updateArt();
				mBackSongID = song.idSong;
			}

			startTimeUpdating((int) song.duration);
		}
	}

	private void updateArt() {
		mHandler.post(mCurrentUpdater);
		mHandler.post(mNextUpdater);
		mHandler.post(mPrevUpdater);
	}

	private void startTimeUpdating(int max) {
		mSeekBar.setMax(max / 1000);
		mCurrentTime.setText(SongUtilities.getInstance(getActivity()).millisToTime(getCurrentTime()));
		mHandler.removeCallbacks(mTimeUpdater);
		mHandler.post(mTimeUpdater);
	}
}

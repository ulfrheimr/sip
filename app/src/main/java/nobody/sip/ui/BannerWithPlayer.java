package nobody.sip.ui;

import nobody.sip.R;
import nobody.sip.core.PlayerService;
import nobody.sip.core.PlayerService.PlayerState;
import nobody.sip.helpers.ArtworkHelper;
import nobody.sip.prots.Album;
import nobody.sip.prots.Song;
import nobody.sip.prots.views.AlbumImageView;
import nobody.sip.ui.LayoutWithBanner.OnLayoutWithBannerListener;
import nobody.sip.ui.Player.OnPlayerListener;
import nobody.sip.ui.ToggleView.ToggleViewVisibility;
import nobody.sip.ui.commons.OnStopCommonView;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

public class BannerWithPlayer extends RelativeLayout implements OnPlayerListener, OnLayoutWithBannerListener, OnStopCommonView {
	private final String TAG = "sip.ui.BANNER_WITH_PLAYER";

	public LayoutWithBanner mBannerLayout;
	private Player mPlayer;

	private AlbumImageView mArtwork;

	private OnBannerWithPlayerListener mListener;
	private ArtworkHelper.AlbumArtworkLoader mSmallArtUpdater;

	private Song mBackSong = null;

	private Handler mHandler = new Handler();

	public BannerWithPlayer(Context context) {
		super(context);
	}

	public BannerWithPlayer(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BannerWithPlayer(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public interface OnBannerWithPlayerListener {
		public void onPlayerBuilt();

		public void onBannerLayoutBuilt();

		public View getBannerView();

	}

	public void setOnBannerWithPlayerListener(OnBannerWithPlayerListener listener) {
		mListener = listener;
	}

	public void setSeekBarMax(int max) {
		mBannerLayout.setSeekBarSpecs(max);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();

		mPlayer = (Player) findViewById(R.id.banner_player_player);
		mBannerLayout = (LayoutWithBanner) findViewById(R.id.banner_player_layout);
		mArtwork = (AlbumImageView) findViewById(R.id.banner_player_small_art);

		mPlayer.setOnPlayerListener(this);
		mBannerLayout.setOnLayoutWithBannerListener(this);
	}

	/* OnPlayerListener implementation */
	@Override
	public void onPlayerBuilt() {
		if (mListener != null)
			mListener.onPlayerBuilt();

	}

	/* OnLayoutWithBannerListener implementation */
	@Override
	public void onLayoutWithBannerBuilt() {

		if (mListener != null) {
			mListener.onBannerLayoutBuilt();

			View bannerView = mListener.getBannerView();
			mBannerLayout.setBannerView(bannerView);
		}
	}

	/* OnStopCustomView implementation */
	@Override
	public void stopCommonView() {
		// mBannerLayout.stopCommonView();

	}

	private void updateSmallArt(Album album) {
		mSmallArtUpdater = new ArtworkHelper.AlbumArtworkLoader(album, mArtwork);
		mHandler.post(mSmallArtUpdater);
	}

	public void updatePlayer(Song song, PlayerState playerState) {
		if (song != null) {
			if (song != mBackSong) {
				mPlayer.updateSongText(song.title + " - " + song.artist);
				if (mBackSong == null || song.idAlbum != mBackSong.idAlbum)
					updateSmallArt(song);

				mBackSong = song;
			}

		}
	}

	public void updateSeekBarProgress(int progress) {
		mBannerLayout.updateSeekBar(progress);
	}

	public void updateBanner() {

	}
}

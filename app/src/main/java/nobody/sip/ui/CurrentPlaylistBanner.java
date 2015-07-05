package nobody.sip.ui;

import nobody.sip.R;
import nobody.sip.prots.Song;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CurrentPlaylistBanner extends RelativeLayout implements OnGlobalLayoutListener {
	private final String TAG = "sip.ui.CURRENT_PLAYLIST_BANNER";
	private int mNumberText = 1;

	private ImageView mArt;

	private TextView mArtist;
	private TextView mAlbum;
	private TextView mSong;

	private OnCurrentPlaylistBannerListener mListener;

	public CurrentPlaylistBanner(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public CurrentPlaylistBanner(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public CurrentPlaylistBanner(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public interface OnCurrentPlaylistBannerListener {
		public void onCurrentPlaylistBannerBuilt();

		public int[] onRequestSize();
	}

	public void setOnCurrentPlaylistBannerListener(OnCurrentPlaylistBannerListener listener) {
		mListener = listener;
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();

		mArtist = (TextView) findViewById(R.id.cp_banner_artist);
		mAlbum = (TextView) findViewById(R.id.cp_banner_album);
		mSong = (TextView) findViewById(R.id.cp_banner_song);

		mArt = (ImageView) findViewById(R.id.cp_banner_art);

		if (mListener != null)
			mListener.onCurrentPlaylistBannerBuilt();

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

		mArt.setLayoutParams(new LayoutParams(this.getHeight(), this.getHeight()));

		int contractedSize = 0;

		if (mListener != null)
			contractedSize = mListener.onRequestSize()[1];

		calculateTextNumber(contractedSize);
	}

	public void calculateTextNumber(int contractedSize) {
		int temp = mArtist.getHeight() + mAlbum.getHeight() + mSong.getHeight();
		if (contractedSize >= temp) {
			mNumberText = 3;
			return;
		}

		temp = mArtist.getHeight() + mAlbum.getHeight();
		if (contractedSize >= temp) {
			mNumberText = 2;
			return;
		}
	}

	public void updateBanner(boolean expanded, Song song) {
		mArtist.setText(song.artist);
		mAlbum.setText(song.album);
		mSong.setText(song.title);

		mAlbum.setVisibility(VISIBLE);
		mSong.setVisibility(VISIBLE);

		if (expanded)
			mArt.setVisibility(View.VISIBLE);
		else {
			mArt.setVisibility(View.INVISIBLE);

			if (mNumberText == 2) {
				mArtist.setText(song.album);
				mAlbum.setText(song.title);
				mSong.setVisibility(INVISIBLE);
			} else if (mNumberText == 1) {
				mArtist.setText(song.album + " - " + song.title);
				mAlbum.setVisibility(INVISIBLE);
				mSong.setVisibility(INVISIBLE);
			}

		}

	}
}

package nobody.sip.ui;

import nobody.sip.R;
import nobody.sip.core.PlayerService.FragmentItem;
import nobody.sip.prots.Song;
import nobody.sip.ui.CurrentSongPlayer.OnSongPlayerListener;
import nobody.sip.ui.ToggleView.ToggleViewVisibility;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.View.OnTouchListener;
import android.view.View.OnClickListener;

public class SideNavWithPlayer extends ToggleView implements OnTouchListener, OnClickListener, OnSongPlayerListener {
	private final String TAG = "sip.ui.SIDENAV_PLAYER";

	private final int REFRESH_TIME = 8;
	private final double PIXEL_RATE = 0.5;
	private int TOUCH_WIDTH_SLOP = 30;
	private int USER_SPACE = 100;

	private int mAnimOffset = 0;

	private View mFragmentContainer;
	private View mSideMenu;
	private View mContainer;
	private View mMask;

	private View mGenres;
	private View mArtists;
	private View mAlbums;
	private View mSongs;
	private View mPlaylists;

	private OnSideNavListener mListener;
	public FragmentItem sideMenuItem;
	private FragmentItem mBackItem = sideMenuItem;

	public CurrentSongPlayer songPlayer;

	public SideNavWithPlayer(Context context) {
		super(context);
	}

	public SideNavWithPlayer(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SideNavWithPlayer(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public interface OnSideNavListener extends OnSongPlayerListener {
		public void onItemSelected(FragmentItem item);

		public void onStartOpening();

		public void onFinishClosing();
	}

	public void setNavigationItem(FragmentItem navItem) {
		sideMenuItem = navItem;

		markItemAsSelected();
	}

	public void setOnSideNavListener(OnSideNavListener l) {
		this.mListener = l;
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();

		super.setUpdateRate(PIXEL_RATE);
		super.setUpdateTime(REFRESH_TIME);

		TOUCH_WIDTH_SLOP = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, TOUCH_WIDTH_SLOP, getContext()
				.getResources().getDisplayMetrics());
		USER_SPACE = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, USER_SPACE, getContext().getResources()
				.getDisplayMetrics());

		mSideMenu = this.getChildAt(0);
		mContainer = this.getChildAt(1);

		mGenres = mSideMenu.findViewById(R.id.sidenav_genres);
		mArtists = mSideMenu.findViewById(R.id.sidenav_artists);
		mAlbums = mSideMenu.findViewById(R.id.sidenav_albums);
		mSongs = mSideMenu.findViewById(R.id.sidenav_songs);
		mPlaylists = mSideMenu.findViewById(R.id.sidenav_playlists);

		mMask = mContainer.findViewById(R.id.sidenav_mask);
		mSideMenu.setOnClickListener(this);

		mGenres.setOnClickListener(this);
		mArtists.setOnClickListener(this);
		mAlbums.setOnClickListener(this);
		mSongs.setOnClickListener(this);
		mPlaylists.setOnClickListener(this);

		mMask.setOnTouchListener(this);

		songPlayer = (CurrentSongPlayer) mContainer.findViewById(R.id.sidenav_player);
		songPlayer.setOnSongPlayerListener(this);

	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		mSideMenu.layout(l, t, r - USER_SPACE, b);
		mContainer.layout(l, t, r, b);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		return true;
	}

	@Override
	public void onClick(View v) {
		if (v == mGenres)
			sideMenuItem = FragmentItem.GENRES;
		else if (v == mArtists)
			sideMenuItem = FragmentItem.ARTISTS;
		else if (v == mAlbums)
			sideMenuItem = FragmentItem.ALBUMS;
		else if (v == mSongs)
			sideMenuItem = FragmentItem.SONGS;
		else if (v == mPlaylists)
			sideMenuItem = FragmentItem.PLAYLISTS;

		toggle(ToggleViewVisibility.HIDDEN);
	}

	/* OnSongPlayerListener implementation */
	@Override
	public void onSongPlayerBuilt() {
		mFragmentContainer = songPlayer.findViewById(R.id.fragment_container);
		mFragmentContainer.setOnTouchListener(this);
		if (mListener != null)
			mListener.onSongPlayerBuilt();
	}

	@Override
	public void onPlayerToggling(ToggleViewVisibility visibility) {
		if (mListener != null)
			mListener.onPlayerToggling(visibility);
	}

	// @Override
	// public boolean isStopLayoutNeed() {
	// if (mListener != null)
	// return mListener.isStopLayoutNeed();
	// else
	// return false;
	// }

	/* OnSongPlayerListener implementation */
	@Override
	public void handleHiding() {
		if ((mContainer.getLeft() - mAnimOffset) <= 0)
			mAnimOffset = mContainer.getLeft();

		mMask.setAlpha((float) mContainer.getLeft() / ((float) (this.getWidth() - USER_SPACE)));

		mContainer.offsetLeftAndRight(-mAnimOffset);
	}

	@Override
	public void handleShowing() {
		if ((mContainer.getLeft() + mAnimOffset) >= (this.getWidth() - USER_SPACE))
			mAnimOffset = (this.getWidth() - USER_SPACE) - mContainer.getLeft();

		mContainer.offsetLeftAndRight(mAnimOffset);
		mMask.setAlpha((float) mContainer.getLeft() / ((float) (this.getWidth() - USER_SPACE)));

	}

	@Override
	public boolean isContinueNeeded() {
		if (mContainer.getLeft() == 0) {
			toggleVisibility = ToggleViewVisibility.HIDDEN;
			mMask.setVisibility(View.GONE);

			if (mListener != null) {
				mListener.onFinishClosing();
				mListener.onItemSelected(sideMenuItem);
			}

			markItemAsSelected();

			return false;
		} else if (mContainer.getLeft() == (this.getWidth() - USER_SPACE)) {
			toggleVisibility = ToggleViewVisibility.SHOWN;
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

	@Override
	public void stopCommonView() {
		// TODO Auto-generated method stub
		super.stopCommonView();
	}

	private void markItemAsSelected() {
		if (sideMenuItem != mBackItem) {
			if (mBackItem != null)
				switch (mBackItem) {
				case GENRES:
					mGenres.setBackgroundColor(getContext().getResources().getColor(R.color.non_selected_color));
					break;
				case ARTISTS:
					mArtists.setBackgroundColor(getContext().getResources().getColor(R.color.non_selected_color));
					break;
				case ALBUMS:
					mAlbums.setBackgroundColor(getContext().getResources().getColor(R.color.non_selected_color));
					break;
				case SONGS:
					mSongs.setBackgroundColor(getContext().getResources().getColor(R.color.non_selected_color));
					break;
				case PLAYLISTS:
					mPlaylists.setBackgroundColor(getContext().getResources().getColor(R.color.non_selected_color));
					break;
				}

			mBackItem = sideMenuItem;
			switch (sideMenuItem) {
			case GENRES:
				mGenres.setBackgroundColor(getContext().getResources().getColor(R.color.selected_color));
				break;
			case ARTISTS:
				mArtists.setBackgroundColor(getContext().getResources().getColor(R.color.selected_color));
				break;
			case ALBUMS:
				mAlbums.setBackgroundColor(getContext().getResources().getColor(R.color.selected_color));
				break;
			case SONGS:
				mSongs.setBackgroundColor(getContext().getResources().getColor(R.color.selected_color));
				break;
			case PLAYLISTS:
				mPlaylists.setBackgroundColor(getContext().getResources().getColor(R.color.selected_color));
				break;
			}

		}
	}

	public void toggle(ToggleViewVisibility visibility) {
		if (visibility == ToggleViewVisibility.SHOWN)
			visibility = ToggleViewVisibility.HIDDEN;
		else if (visibility == ToggleViewVisibility.HIDDEN)
			visibility = ToggleViewVisibility.SHOWN;

		toggleVisibility = visibility;
		// toggleVisibility = ToggleViewVisibility.SHOWN;
		// else
		// toggleVisibility = ToggleViewVisibility.HIDDEN;
		//
		// toggle();
		// }

		mAnimOffset = (int) ((this.getWidth() * PIXEL_RATE) / REFRESH_TIME);

		if (toggleVisibility == ToggleViewVisibility.HIDDEN)
			if (mListener != null)
				mListener.onStartOpening();

		super.toggle();
	}

	public void handlePanelSizing(float widthSpan) {
		if (mMask.getVisibility() != View.VISIBLE)
			mMask.setVisibility(VISIBLE);
		// mMask.setAlpha(0f);
		if ((mContainer.getLeft() + widthSpan) < 0) {
			mContainer.offsetLeftAndRight((int) -mContainer.getLeft());
		} else if ((mContainer.getLeft() + widthSpan) <= (this.getWidth() - USER_SPACE))
			mContainer.offsetLeftAndRight((int) widthSpan);
		else
			mContainer.offsetLeftAndRight((this.getWidth() - USER_SPACE) - mContainer.getLeft());

		mMask.setAlpha((float) mContainer.getLeft() / ((float) this.getWidth() - (float) USER_SPACE));
	}

	/* CHECK IT */
	// private View mSideMenu, mContainer, mMask;
	// private View mGenres, mArtists, mAlbums, mSongs, mPlaylists;

	//
	// @Override
	// protected void onAttachedToWindow() {
	//
	// mGenres = mSideMenu.findViewById(R.id.sidenav_genres);
	// mArtists = mSideMenu.findViewById(R.id.sidenav_artists);
	// mAlbums = mSideMenu.findViewById(R.id.sidenav_albums);
	// mSongs = mSideMenu.findViewById(R.id.sidenav_songs);
	// mPlaylists = mSideMenu.findViewById(R.id.sidenav_playlists);
	//
	// mGenres.setOnClickListener(this);
	// mArtists.setOnClickListener(this);
	// mAlbums.setOnClickListener(this);
	// mSongs.setOnClickListener(this);
	// mPlaylists.setOnClickListener(this);
	// }
	//
	// @Override
	// protected void onLayout(boolean changed, int l, int t, int r, int b) {
	//
	// if (changed) {
	// calculateSizes();
	// }
	//
	//
	// }
	//
	// @Override
	// public void onClick(View v) {
	// mBackItem = selectedMenuItem;
	//
	// if (v == mGenres && mListener != null) {
	// selectedMenuItem = NavigationItem.GENRES;
	// } else if (v == mArtists && mListener != null) {
	// selectedMenuItem = NavigationItem.ARTISTS;
	// } else if (v == mAlbums && mListener != null) {
	// selectedMenuItem = NavigationItem.ALBUMS;
	// } else if (v == mSongs && mListener != null) {
	// selectedMenuItem = NavigationItem.SONGS;
	// } else if (v == mPlaylists && mListener != null) {
	// selectedMenuItem = NavigationItem.PLAYLISTS;
	// }
	//
	// toggle();
	// }

}

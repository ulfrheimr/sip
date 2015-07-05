package nobody.sip.ui;

import java.util.List;

import nobody.sip.R;
import nobody.sip.core.PlayerService;
import nobody.sip.core.PlayerService.DetailsFragmentType;
import nobody.sip.core.PlayerService.FragmentItem;
import nobody.sip.core.PlayerService.ItemType;
import nobody.sip.core.PlayerService.PlayerState;
import nobody.sip.core.PlayerService.PlayerFragmentItem;
import nobody.sip.core.SongSelector.AddMode;
import nobody.sip.core.SongUtilities;
import nobody.sip.prots.Album;
import nobody.sip.prots.Artist;
import nobody.sip.prots.IDProt;
import nobody.sip.prots.Song;
import nobody.sip.prots.SongWithExtras;
import nobody.sip.ui.SideNavWithPlayer.OnSideNavListener;
import nobody.sip.ui.ToggleView.ToggleViewVisibility;
import nobody.sip.ui.commons.CommonNavigationFragment;
import nobody.sip.ui.commons.CommonPlayerFragment;
import nobody.sip.ui.commons.OnCommonNavigationFragmentListener;
import nobody.sip.ui.commons.OnCommonPlayerFragmentListener;
import nobody.sip.ui.commons.OnStopCommonView;
import android.app.ActionBar;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

public class Principal extends ServiceBroadcastActivity implements OnSideNavListener, Runnable,
		OnCommonNavigationFragmentListener, OnCommonPlayerFragmentListener {
	private final String TAG = "sip.ui.PRINCIPAL";
	private final int SEEKBAR_UPDATE_TIME = 1000;
	private final FragmentItem INITIAL_FRAGMENT_ITEM = FragmentItem.ALBUMS;
	private int SLOP;

	private FragmentManager mFragmentManager;
	private FragmentTransaction mFragmentTransaction;

	private ActionBar mActionBar;

	private Genres mGenres;
	private Artists mArtists;
	private Albums mAlbums;
	private Songs mSongs;
	private Playlists mPlaylists;

	private DetailsPlayer mDetailsPlayer;
	private CurrentPlaylist mCurrentPlaylist;

	private SideNavWithPlayer mSideNavPlayer;

	private FragmentItem mBackFragmentItem = null;
	private FragmentItem mRestoreFragmentItem = null;
	private PlayerFragmentItem mBackPlayerFragmentItem = null;

	private Handler mHandler = new Handler();

	private boolean mReady = false;

	private final String PRINCIPAL_FRAGMENT_STATE = "sip.ui.PRINCIPAL_FRAGMENT_STATE";

	private int mStartX = 0, dX = 0;
	private int mStartY = 0, dY = 0;
	private boolean mIsHandlePanel = false;
	private boolean mIsAllowHandlePanel = true;
	private boolean mIsOpening = true;

	private class LyricsGetter implements Runnable {
		private Song mSong;
		private Context mInnerContext;

		public LyricsGetter(Song song, Context context) {
			this.mSong = song;
			this.mInnerContext = context;
		}

		@Override
		public void run() {
			String lyrics = SongUtilities.getInstance(mInnerContext).getLyrics(mSong);
			// mSideNavPlayer.songPlayer.changeLyrics(lyrics);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			int fragOrdinal = savedInstanceState.getInt(PRINCIPAL_FRAGMENT_STATE, -1);
			if (fragOrdinal != -1) {
				mRestoreFragmentItem = FragmentItem.values()[fragOrdinal];
			}
		}

		mSideNavPlayer = (SideNavWithPlayer) getLayoutInflater().inflate(R.layout.sidenav_with_player, null);
		setContentView(mSideNavPlayer);

		mSideNavPlayer.setOnSideNavListener(this);

		init();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		mSideNavPlayer.setOnSideNavListener(this);
		SLOP = ViewConfiguration.get(this).getScaledTouchSlop();
		mHandler = new Handler();
	}

	@Override
	protected void onStop() {
		super.onStop();

		stopCommonView();

		mSideNavPlayer.setOnSideNavListener(null);
		mSideNavPlayer.stopCommonView();

		mHandler.removeCallbacksAndMessages(null);
		mHandler = null;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(PRINCIPAL_FRAGMENT_STATE, mBackFragmentItem.ordinal());
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if ((mSideNavPlayer.songPlayer.toggleVisibility != ToggleViewVisibility.SHOWN && mSideNavPlayer.songPlayer.toggleVisibility != ToggleViewVisibility.SHOWING)
					&& getCurrentFragment().isAllowToHandlePanel((int) event.getX(), (int) event.getY())) {
				if (mSideNavPlayer.toggleVisibility == ToggleViewVisibility.SHOWN)
					mSideNavPlayer.toggleVisibility = ToggleViewVisibility.SHOWING;
				else if (mSideNavPlayer.toggleVisibility == ToggleViewVisibility.HIDDEN)
					mSideNavPlayer.toggleVisibility = ToggleViewVisibility.HIDING;

				mStartX = (int) event.getX();
				mStartY = (int) event.getY();
				mIsHandlePanel = false;
				mIsAllowHandlePanel = true;
			} else
				mIsAllowHandlePanel = false;

			break;

		case MotionEvent.ACTION_MOVE:
			if (mIsAllowHandlePanel) {
				dX = (int) Math.abs(mStartX - event.getX());
				dY = (int) Math.abs(mStartY - event.getY());

				if (dX > SLOP || dY > SLOP) {
					if (dX > (3 * dY)) {
						mIsHandlePanel = true;
						mSideNavPlayer.handlePanelSizing(event.getX() - mStartX);
					}

					if (mIsHandlePanel) {
						getCurrentFragment().stopCommonTouch();
						mIsOpening = mStartX < event.getX() ? true : false;
					}

					mStartX = (int) event.getX();
					mStartY = (int) event.getY();
				}
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			if (mIsHandlePanel) {
				mSideNavPlayer.toggle(mIsOpening ? ToggleViewVisibility.SHOWN : ToggleViewVisibility.HIDDEN);
				getCurrentFragment().restartCommonTouch();
			}
			mIsHandlePanel = false;
			mIsAllowHandlePanel = true;
			mStartX = 0;
			mStartY = 0;
			dX = 0;
			dY = 0;
			break;
		}

		return super.dispatchTouchEvent(event);
	}

	/* ServiceBroadcastActivity implementation */
	@Override
	public void updateInterface(SongWithExtras song, PlayerState playerState, int currentPosition) {
		if (mHandler != null)
			mHandler.removeCallbacks(this);

		if (currentSong != null && mSideNavPlayer.songPlayer != null) {
			mSideNavPlayer.songPlayer.updateSongPanel(song, playerState, currentPosition);

			// onRequestLyrics(currentSong);

			if (playerState == PlayerState.PLAYING)
				mHandler.post(this);
		}

		updatePlayerFragment(song, playerState, currentPosition);

	}

	@Override
	public void playlistHasChanged() {
		if (getPlayerFragment() != null)
			getPlayerFragment().playlistHasChanged();
	}

	@Override
	public void albumChanged(long idAlbum) {
		// TODO Auto-generated method stub

	}

	@Override
	public void serviceConnected() {

		if (currentSong != null) {
			updateInterface(getCurrentSongWithExtras(), playerState, playerService.getCurrentPosition());
		}

	}

	@Override
	public void serviceDisconnected() {
		// TODO Auto-generated method stub

	}

	/* OnSideNavListener implementation */

	@Override
	public void onItemSelected(FragmentItem item) {
		changeFragment(item);
	}

	@Override
	public void onStartOpening() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFinishClosing() {
		// TODO Auto-generated method stub

	}

	/* OnSongPlayerListener implementation */

	@Override
	public void onPlayerToggling(ToggleViewVisibility visibility) {
		updatePlayerFragment(getCurrentSongWithExtras(), playerState, playerService.getCurrentPosition());

	}

	@Override
	public void onSongPlayerBuilt() {
		mReady = true;

		if (mBackPlayerFragmentItem != null)
			changePlayerFragment(mBackPlayerFragmentItem, null);
		else
			changePlayerFragment(PlayerFragmentItem.DETAILS_PLAYER, null);

		if (mRestoreFragmentItem != null)
			changeFragment(mRestoreFragmentItem);
		else if (mBackFragmentItem != null)
			changeFragment(mBackFragmentItem);
		else
			changeFragment(INITIAL_FRAGMENT_ITEM);

		if (super.currentSong != null)
			updateInterface(getCurrentSongWithExtras(), super.playerState, playerService.getCurrentPosition());
	}

	/* OnCommonFragmentListener implementation */
	@Override
	public void onSendingToDetails(DetailsFragmentType detailsFragment, Object item) {
		Intent sendIntent;
		sendIntent = new Intent(this, Details.class);

		sendIntent.putExtra(PlayerService.EXTRA_DETAILS_SENDER, (Parcelable) item);

		if (detailsFragment == DetailsFragmentType.GENRE_DETAILS)
			sendIntent.putExtra(PlayerService.EXTRA_DETAILS_FRAGMENT, (Parcelable) DetailsFragmentType.GENRE_DETAILS);
		else if (detailsFragment == DetailsFragmentType.ARTIST_DETAILS)
			sendIntent.putExtra(PlayerService.EXTRA_DETAILS_FRAGMENT, (Parcelable) DetailsFragmentType.ARTIST_DETAILS);
		else if (detailsFragment == DetailsFragmentType.PLAYLIST_DETAILS)
			sendIntent.putExtra(PlayerService.EXTRA_DETAILS_FRAGMENT, (Parcelable) DetailsFragmentType.PLAYLIST_DETAILS);
		else
			sendIntent.putExtra(PlayerService.EXTRA_DETAILS_FRAGMENT, (Parcelable) DetailsFragmentType.ALBUM_DETAILS);

		startActivity(sendIntent);
	}

	@Override
	public void onSendToEdit(ItemType itemType, Object item) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFragmentMoving(float widthValue) {
		mSideNavPlayer.handlePanelSizing(widthValue);
	}

	@Override
	public void onFragmentStopMoving(boolean opening) {
		mSideNavPlayer.toggle();
	}

	/* OnCommonNavigationFragmentListener implementation */
	@Override
	public void onAddingSongs(List<Song> songs, AddMode addMode) {
		playerService.addBatchSongs(songs, addMode);

		if (addMode == AddMode.FIRST || addMode == AddMode.CLEAR)
			mHandler.removeCallbacks(this);
	}

	@Override
	public void onAddingItem(ItemType itemType, Object item, AddMode addMode) {
		switch (itemType) {
		case GENRE:
			playerService.addGenre(((IDProt) item).getId(), addMode);
			break;
		case ARTIST:
			if (((Artist) item).isVA)
				Log.d(TAG, "At last now adding VA artist");
			else
				playerService.addArtist(((IDProt) item).getId(), addMode);
			break;
		case ALBUM:
			playerService.addAlbum(((IDProt) item).getId(), addMode);
			break;
		case SONG:
			playerService.addSong(((IDProt) item).getId(), addMode);
			break;
		case PLAYLIST:

			break;
		}

	}

	/* OnCommonPlayerFragmentListener implementation */
	@Override
	public void onReportSelectedTime(int time) {
		playerService.setCurrentTime(time);
	}

	@Override
	public int onRequestCurrentTime() {
		return playerService.getCurrentTime();
	}

	@Override
	public ToggleViewVisibility onRequestPlayerFragmentVisibility() {
		return mSideNavPlayer.songPlayer.toggleVisibility;
	}

	@Override
	public PlayerState onRequestPlayerState() {
		return playerState;
	}

	@Override
	public void onRequestPlayerNavigation(PlayerFragmentItem playerFragment, Object item) {
		changePlayerFragment(playerFragment, item);
	}

	@Override
	public List<Song> onRequestCurrentPlaylist() {
		return playerService.getCurrentPlaylist();
	}

	@Override
	public SongWithExtras onRequestSongAtPosition(int position) {
		try {
			return playerService.getSongWithExtrasAtPosition(position);
		} catch (Exception ex) {
			return null;
		}
	}

	@Override
	public int onRequestCurrentPosition() {
		return playerService.getCurrentPosition();
	}

	/* onStopCustomView implementation */
	@Override
	public void stopCommonView() {
		getCurrentFragment().closeFragment();
		getPlayerFragment().closeFragment();
	}

	/* Runnable implementation */
	@Override
	public void run() {
		try {
			mSideNavPlayer.songPlayer.updateSongProgress(playerService.getCurrentTime());
		} catch (Exception e) {
		}

		mHandler.postDelayed(this, SEEKBAR_UPDATE_TIME);
	}

	private void init() {
		SLOP = ViewConfiguration.get(this).getScaledTouchSlop();

		mFragmentManager = getFragmentManager();
		mActionBar = getActionBar();
		mActionBar.setHomeButtonEnabled(true);
	}

	private void updatePlayerFragment(SongWithExtras currentSong, PlayerState playerState, int currentPosition) {
		if (getPlayerFragment() != null) {
			getPlayerFragment().updateFragmentInterface(currentSong, playerState, currentPosition);

		}

	}

	private CommonNavigationFragment getCurrentFragment() {
		switch (mBackFragmentItem) {
		case SONGS:
			return mSongs;
		case ALBUMS:
			return mAlbums;
		case ARTISTS:
			return mArtists;
		case GENRES:
			return mGenres;
		default:
			return mPlaylists;
		}
	}

	private CommonPlayerFragment getPlayerFragment() {
		if (mBackPlayerFragmentItem == null)
			return null;

		switch (mBackPlayerFragmentItem) {
		case CURRENT_PLAYLIST:
			return mCurrentPlaylist;

		case DETAILS_PLAYER:
			return mDetailsPlayer;
		default:
			return null;
		}
	}

	private void nullify() {
		if (mGenres != null) {
			mGenres = null;
		}
		if (mArtists != null) {
			mArtists = null;
		}

		if (mAlbums != null) {
			mAlbums.closeFragment();
			mAlbums.setOnCommonNavigationFragmentListener(null);
			mAlbums = null;
		}

		if (mSongs != null) {
			mSongs = null;
		}

		if (mPlaylists != null) {
			mPlaylists = null;
		}
	}

	private void nullifySongFragment() {
		if (mDetailsPlayer != null) {
			mDetailsPlayer.closeFragment();
			mDetailsPlayer.setOnCommonPlayerFragmentListener(null);
			mDetailsPlayer = null;
		}

		if (mCurrentPlaylist != null) {
			mCurrentPlaylist.setOnCommonPlayerFragmentListener(null);
			mCurrentPlaylist = null;
		}
	}

	private void changePlayerFragment(PlayerFragmentItem item, Object extras) {
		if (item != mBackPlayerFragmentItem) {
			mBackPlayerFragmentItem = item;

			nullifySongFragment();

			mFragmentTransaction = mFragmentManager.beginTransaction();
			switch (item) {
			case DETAILS_PLAYER:
				mDetailsPlayer = new DetailsPlayer();
				mDetailsPlayer.setOnCommonPlayerFragmentListener(this);
				mFragmentTransaction.replace(R.id.cspl_song_fragment_container, mDetailsPlayer);
				break;
			case CURRENT_PLAYLIST:
				mCurrentPlaylist = new CurrentPlaylist();
				mCurrentPlaylist.setOnCommonPlayerFragmentListener(this);
				mFragmentTransaction.replace(R.id.cspl_song_fragment_container, mCurrentPlaylist);
				break;
			default:
				break;
			}

			mFragmentTransaction.commit();
		}

	}

	private void changeFragment(FragmentItem item) {
		if (item != mBackFragmentItem) {
			mBackFragmentItem = item;

			nullify();

			mSideNavPlayer.setNavigationItem(item);

			mFragmentTransaction = mFragmentManager.beginTransaction();

			switch (item) {
			case GENRES:
				mGenres = new Genres();
				mGenres.setOnCommonNavigationFragmentListener(this);

				mFragmentTransaction.replace(R.id.fragment_container, mGenres);
				break;
			case ARTISTS:
				mArtists = new Artists();
				mArtists.setOnCommonNavigationFragmentListener(this);

				mFragmentTransaction.replace(R.id.fragment_container, mArtists);
				break;
			case ALBUMS:
				mAlbums = new Albums();
				mAlbums.setOnCommonNavigationFragmentListener(this);

				mFragmentTransaction.replace(R.id.fragment_container, mAlbums);
				break;
			case SONGS:
				mSongs = new Songs();
				mSongs.setOnCommonNavigationFragmentListener(this);

				mFragmentTransaction.replace(R.id.fragment_container, mSongs);
				break;
			case PLAYLISTS:
				mPlaylists = new Playlists();
				mPlaylists.setOnCommonNavigationFragmentListener(this);

				mFragmentTransaction.replace(R.id.fragment_container, mPlaylists);
				break;
			}

			mFragmentTransaction.commit();
		}
	}
}

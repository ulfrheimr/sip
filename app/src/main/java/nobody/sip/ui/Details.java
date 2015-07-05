package nobody.sip.ui;

import java.util.List;

import nobody.sip.R;
import nobody.sip.core.PlayerService;
import nobody.sip.core.PlayerService.DetailsFragmentType;
import nobody.sip.core.PlayerService.ItemType;
import nobody.sip.core.PlayerService.PlayerState;
import nobody.sip.core.SongSelector.AddMode;
import nobody.sip.prots.Album;
import nobody.sip.prots.Artist;
import nobody.sip.prots.Genre;
import nobody.sip.prots.IDProt;
import nobody.sip.prots.Song;
import nobody.sip.prots.SongWithExtras;
import nobody.sip.ui.BannerWithPlayer.OnBannerWithPlayerListener;
import nobody.sip.ui.DetailsBanner.OnDetailsBannerListener;
import nobody.sip.ui.commons.CommonDetailsFragment;
import nobody.sip.ui.commons.OnCommonDetailsFragmentListener;
import android.app.ActionBar;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLayoutChangeListener;

public class Details extends ServiceBroadcastActivity implements OnBannerWithPlayerListener, Runnable,
		OnCommonDetailsFragmentListener, OnDetailsBannerListener {
	private final String TAG = "sip.ui.DETAILS";
	private final int UPDATE_TIME = 1000;

	private FragmentManager mFragmentManager;
	private FragmentTransaction mFragmentTransaction;
	private ActionBar mActionBar;

	private BannerWithPlayer mBannerPlayer;
	private DetailsBanner mBannerDetails;

	private Parcelable mSender;

	private Handler mHandler = new Handler();

	private DetailsFragmentType mFragmentItem;
	private DetailsFragmentType mBackFragmentItem;

	private GenreDetails mGenreDetails;
	private ArtistDetails mArtistDetails;
	private AlbumDetails mAlbumDetails;
	private PlaylistDetails mPlaylistDetails;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getIntent().getExtras().size() != 0) {
			mFragmentItem = getIntent().getParcelableExtra(PlayerService.EXTRA_DETAILS_FRAGMENT);
			mSender = getIntent().getParcelableExtra(PlayerService.EXTRA_DETAILS_SENDER);

			mBannerPlayer = (BannerWithPlayer) getLayoutInflater().inflate(R.layout.banner_with_player, null);
			mBannerDetails = (DetailsBanner) getLayoutInflater().inflate(R.layout.details_banner, null);
			setContentView(mBannerPlayer);

			mBannerPlayer.setOnBannerWithPlayerListener(this);
			mBannerDetails.setOnBannerDetailsListener(this);
			init();
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();

		if (mHandler == null)
			mHandler = new Handler();

		mBannerPlayer.setOnBannerWithPlayerListener(this);
		mBannerDetails.setOnBannerDetailsListener(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();

		mBannerDetails.setOnBannerDetailsListener(null);

		mBannerPlayer.setOnBannerWithPlayerListener(null);
		mBannerPlayer.stopCommonView();

		mHandler.removeCallbacksAndMessages(null);
		mHandler = null;
	}

	@Override
	public void run() {
		mBannerPlayer.updateSeekBarProgress(playerService.getCurrentTime());
		mHandler.postDelayed(this, UPDATE_TIME);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			if (requestNavigation(mFragmentItem)) {
				changeFragment(null);
				updateBanner();
				return true;
			} else
				return false;

		default:
			return super.onOptionsItemSelected(item);
		}

	}

	/* ServiceBroadcastActivity implementation */
	@Override
	public void playlistHasChanged() {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateInterface(SongWithExtras song, PlayerState playerState, int currentPosition) {
		mBannerPlayer.updatePlayer(song, playerState);

		if (playerState == PlayerState.PLAYING)
			mHandler.post(this);
	}

	@Override
	public void albumChanged(long idAlbum) {
		// TODO Auto-generated method stub

	}

	@Override
	public void serviceConnected() {

	}

	@Override
	public void serviceDisconnected() {

	}

	/* OnBannerWithPlayerListener implementation */
	@Override
	public void onPlayerBuilt() {

		if (currentSong != null)
			updateInterface(getCurrentSongWithExtras(), playerState, playerService.getCurrentPosition());
	}

	@Override
	public void onBannerLayoutBuilt() {
		changeFragment(mSender);

		if (currentSong != null) {
			mBannerPlayer.setSeekBarMax((int) currentSong.duration);
			mBannerPlayer.updateSeekBarProgress(playerService.getCurrentTime());

			if (playerState == PlayerState.PLAYING)
				mHandler.post(this);
		}
	}

	@Override
	public View getBannerView() {
		return mBannerDetails;
	}

	/* OnDetailsBannerListener implementation */
	@Override
	public void onDetailsBannerBuilt() {
		updateBanner();
	}

	/* OnDetailsFragmentListener implementation */
	@Override
	public void stopCommonView() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAddingSongs(List<Song> songs, AddMode addMode) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAddingItem(ItemType itemType, Object item, AddMode addMode) {
		switch (itemType) {
		case GENRE:
			playerService.addGenre(((Genre) item).getId(), addMode);
			break;
		case ARTIST:
			playerService.addArtist(((Artist) item).getId(), addMode);
			break;
		case ALBUM:
			playerService.addAlbum(((Album) item).getId(), addMode);
			break;
		case SONG:
			playerService.addSong(((Song) item).getId(), addMode);
			break;
		case PLAYLIST:

			break;
		}
	}

	@Override
	public void onSendingToDetails(DetailsFragmentType detailsFragment, Object item) {

	}

	@Override
	public void onSendToEdit(ItemType itemType, Object item) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFragmentMoving(float widthValue) {

	}

	@Override
	public void onFragmentStopMoving(boolean opening) {

	}

	@Override
	public void onRequestNavigation(DetailsFragmentType fragment, Object item) {
		changeFragment((Parcelable) item);
		updateBanner();
	}

	// @Override
	// public void onRequestNavigation(DetailsFragmentType fragment, Object
	// item) {
	// mFragmentItem = fragment;
	// mID = ((IDProt) item).getId();
	// switch (fragment) {
	// case ALBUM_DETAILS:
	// mISVA = ((Album) item).isCompilation;
	// break;
	// case ARTIST_DETAILS:
	// mISVA = ((Artist) item).isVA;
	// break;
	// default:
	// mISVA = false;
	// break;
	// }
	//
	// changeFragment();
	// updateBanner();
	//
	// }

	private void init() {
		mFragmentManager = getFragmentManager();

		mActionBar = getActionBar();
		mActionBar.setHomeButtonEnabled(true);
	}

	private CommonDetailsFragment getCurrentFragment() {
		switch (mBackFragmentItem) {
		case ARTIST_DETAILS:
			return mArtistDetails;

		case ALBUM_DETAILS:
			return mAlbumDetails;

		case GENRE_DETAILS:
			return mGenreDetails;

		case PLAYLIST_DETAILS:
			return mPlaylistDetails;
		}

		return null;
	}

	private boolean requestNavigation(DetailsFragmentType fragment) {
		switch (mFragmentItem) {
		case GENRE_DETAILS:
			return false;

		case ARTIST_DETAILS:
			mFragmentItem = DetailsFragmentType.GENRE_DETAILS;
			// mID = PlayerService.INVALID_ID_OR_POSITION;
			return true;

		case ALBUM_DETAILS:
			mFragmentItem = DetailsFragmentType.ARTIST_DETAILS;
			// mID = PlayerService.INVALID_ID_OR_POSITION;
			return true;

		case PLAYLIST_DETAILS:

			return true;
		default:
			return false;
		}
	}

	private void nullify() {
		if (mGenreDetails != null) {
			mGenreDetails = null;
		}
		if (mArtistDetails != null) {
			mArtistDetails = null;
		}

		if (mAlbumDetails != null) {
			mAlbumDetails = null;
		}

		if (mPlaylistDetails != null) {
			mPlaylistDetails = null;
		}
	}

	private void updateBanner() {
		switch (mFragmentItem) {
		case GENRE_DETAILS:
			mBannerDetails.detailsBannerTitle.setText("Generos");
			break;
		case ARTIST_DETAILS:
			mBannerDetails.detailsBannerTitle.setText("Artistas");
			break;
		case ALBUM_DETAILS:
			mBannerDetails.detailsBannerTitle.setText("albumes");
			break;
		case PLAYLIST_DETAILS:
			mBannerDetails.detailsBannerTitle.setText("playl");
			break;
		}
	}

	private void changeFragment(Parcelable sender) {
		if (mFragmentItem != mBackFragmentItem) {
			mBackFragmentItem = mFragmentItem;

			nullify();

			Bundle b = new Bundle();
			b.putParcelable(PlayerService.EXTRA_DETAILS_SENDER, sender);

			mFragmentTransaction = mFragmentManager.beginTransaction();

			switch (mFragmentItem) {
			case GENRE_DETAILS:
				mGenreDetails = new GenreDetails();
				mGenreDetails.setOnCommonDetailsFragmentListener(this);
				mGenreDetails.setArguments(b);

				mFragmentTransaction.replace(R.id.lay_banner_user_container, mGenreDetails);
				break;
			case ARTIST_DETAILS:
				mArtistDetails = new ArtistDetails();
				mArtistDetails.setOnCommonDetailsFragmentListener(this);
				mArtistDetails.setArguments(b);

				mFragmentTransaction.replace(R.id.lay_banner_user_container, mArtistDetails);
				break;
			case ALBUM_DETAILS:
				mAlbumDetails = new AlbumDetails();
				mAlbumDetails.setOnCommonDetailsFragmentListener(this);
				mAlbumDetails.setArguments(b);

				mFragmentTransaction.replace(R.id.lay_banner_user_container, mAlbumDetails);
				break;
			case PLAYLIST_DETAILS:
				mPlaylistDetails = new PlaylistDetails();
				mPlaylistDetails.setOnCommonDetailsFragmentListener(this);
				mPlaylistDetails.setArguments(b);

				mFragmentTransaction.replace(R.id.lay_banner_user_container, mPlaylistDetails);
				break;
			}

			mFragmentTransaction.commit();
		}
	}

}

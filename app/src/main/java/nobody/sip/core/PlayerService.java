package nobody.sip.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nobody.sip.core.SongSelector.AddMode;
import nobody.sip.core.SongSelector.OnPlaylistControlListener;
import nobody.sip.helpers.PlayerNotification;
import nobody.sip.prots.Playlist;
import nobody.sip.prots.Song;
import nobody.sip.prots.SongWithExtras;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class PlayerService extends Service implements OnPlaylistControlListener, OnPreparedListener, OnSeekCompleteListener,
		OnCompletionListener, OnErrorListener {

	private final String TAG = "sip.core.PLAYER_SERVICE";
	private final int REWIND_SPAN = 5000;
	public static final int INVALID_ID_OR_POSITION = -1;
	public static final String EMPTY_FIELD = "";

	public static final String BROADCAST_PLAY_FILTER = "nobody.sip.broadcast.PLAY_FILTER";
	public static final String BROADCAST_PLAYLIST_CHANGED = "nobody.sip.broadcast.PLAYLIST_CHANGED";

	public static final String JUST_START = "nobody.sip.action.JUST_START";
	public static final String PLAY = "nobody.sip.action.PLAY";
	public static final String TOGGLE = "nobody.sip.action.TOGGLE";
	public static final String PAUSE = "nobody.sip.action.PAUSE";
	public static final String FORWARD = "nobody.sip.action.FORWARD";
	public static final String REWIND = "nobody.sip.action.REWIND";
	public static final String CLOSE = "nobody.sip.action.CLOSE";

	public static final String EXTRA_CURRENT_SONG = "nobody.sip.extra.CURRENT_SONG";
	public static final String EXTRA_SONG_NUMBER = "nobody.sip.extra.SONG_NUMBER";
	public static final String EXTRA_PLAYER_STATE = "nobody.sip.extra.PLAYER_STATE";

	public static final String EXTRA_DETAILS_FRAGMENT = "nobody.sip.extra.DETAILS_FRAGMENT";
	public static final String EXTRA_DETAILS_SENDER = "nobody.sip.extra.ID_DETAILS";

	private MediaPlayer mPlayer = null;
	private SongSelector mSongSelector = null;
	private boolean mIsModified = false;

	private PlayerNotification mNotification = null;

	private final Binder mBinderService = new BinderService();

	public PlayerState playerState = PlayerState.STOPPED;

	public class BinderService extends Binder {
		public PlayerService getService() {
			return PlayerService.this;
		}
	}

	public static enum ItemType {
		GENRE, ARTIST, ALBUM, SONG, PLAYLIST
	}

	public static enum FragmentItem {
		GENRES, ARTISTS, ALBUMS, SONGS, PLAYLISTS
	}

	public static enum PlayerFragmentItem {
		DETAILS_PLAYER, CURRENT_PLAYLIST
	}

	public static enum DetailsFragmentType implements Parcelable {
		GENRE_DETAILS, ARTIST_DETAILS, ALBUM_DETAILS, PLAYLIST_DETAILS;

		public static final Creator<DetailsFragmentType> CREATOR = new Creator<PlayerService.DetailsFragmentType>() {

			@Override
			public DetailsFragmentType[] newArray(int size) {
				return new DetailsFragmentType[size];

			}

			@Override
			public DetailsFragmentType createFromParcel(Parcel source) {
				return DetailsFragmentType.values()[source.readInt()];
			}
		};

		@Override
		public int describeContents() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeInt(ordinal());
		}
	}

	public enum PlayerState implements Parcelable {
		PLAYING, STOPPED, PAUSED;

		public static final Creator<PlayerState> CREATOR = new Creator<PlayerService.PlayerState>() {

			@Override
			public PlayerState[] newArray(int size) {
				return new PlayerState[size];
			}

			@Override
			public PlayerState createFromParcel(Parcel source) {
				return PlayerState.values()[source.readInt()];
			}
		};

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeInt(ordinal());
		}
	}

	public List<Song> getCurrentPlaylist() {
		return mSongSelector.getCurrentPlaylist();
	}

	public Song getCurrentSong() {
		if (mSongSelector != null)
			return mSongSelector.getCurrentSong();

		return null;
	}

	public Song getPreviousSong() {
		if (mSongSelector != null)
			return mSongSelector.getPreviousSong();

		return null;
	}

	public Song getNextSong() {
		if (mSongSelector != null)
			return mSongSelector.getNextSong();

		return null;
	}

	public Song getSongAtPosition(int position) {
		if (mSongSelector != null)
			return mSongSelector.getSongAtPosition(position);

		return null;
	}

	public SongWithExtras getCurrentSongWithExtras() {
		if (mSongSelector != null)
			return SongUtilities.getInstance(getApplicationContext()).getSongwithExtras(getCurrentSong());

		return null;
	}

	public SongWithExtras getSongWithExtras(Song song) {
		if (song != null)
			return SongUtilities.getInstance(getApplicationContext()).getSongwithExtras(song);

		return null;
	}

	public SongWithExtras getSongWithExtrasAtPosition(int position) {
		Song song = getSongAtPosition(position);

		if (song != null)
			return SongUtilities.getInstance(getApplicationContext()).getSongwithExtras(song);

		return null;
	}

	public int getCurrentPosition() {
		return mSongSelector.getCurrentPosition();
	}

	public void setCurrentPosition(int position) {
		mSongSelector.setCurrentPosition(position);
	}

	public int getCurrentTime() {
		if (mSongSelector.getCurrentSong() != null)
			return mPlayer.getCurrentPosition();
		else
			return 0;
	}

	public void setCurrentTime(int position) {
		if (mSongSelector.getCurrentSong() != null)
			mPlayer.seekTo(position);
	}

	private class Restorer implements Runnable {

		@Override
		public void run() {
			Cursor c = SongUtilities.getInstance(getApplicationContext()).getBackPlaylist();
			int i = 0;
			int currentPosition = PlayerService.INVALID_ID_OR_POSITION;
			List<Song> songs = new ArrayList<Song>();
			Song current;

			while (c.moveToNext()) {
				current = new Song(c.getLong(0), c.getString(1), c.getLong(2), c.getString(3), c.getLong(4), c.getString(5),
						c.getInt(6), c.getLong(7));

				if (c.getInt(8) > 0)
					currentPosition = i - 1;

				songs.add(current);
				i++;
			}

			if (mSongSelector == null)
				mSongSelector = new SongSelector(getContentResolver(), PlayerService.this);

			if (songs.size() > 0) {
				mSongSelector.setCurrentPlaylist(songs, currentPosition);

				sendLocalBroadcast();
				SongUtilities.getInstance(getApplicationContext()).clearBackList();
			}
		}

	}

	private class PlaylistRestorer extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			Log.d(TAG, "Restoring");
			SongUtilities.getInstance(getApplicationContext());

			return true;
			// return songs.size() > 0;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);

			if (result) {
				Log.d(TAG, "Something to restore");
				sendLocalBroadcast();
			}
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();

		if (mSongSelector == null)
			mSongSelector = new SongSelector(getContentResolver(), this);

		getApplication().startService(new Intent(PlayerService.JUST_START));
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent.getAction().equals(JUST_START))
			restorePlaylist();
		else if (intent.getAction().equals(PLAY))
			play();
		else if (intent.getAction().equals(TOGGLE))
			toggle();
		else if (intent.getAction().equals(PAUSE))
			pause();
		else if (intent.getAction().equals(FORWARD))
			forward();
		else if (intent.getAction().equals(REWIND))
			rewind();
		else if (intent.getAction().equals(CLOSE))
			close();

		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		Log.d(TAG, "Service destry, restoring...");

		SongUtilities.getInstance(getApplicationContext()).backPlaylist(getCurrentPlaylist(), getCurrentPosition());
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}

	@Override
	public void onTrimMemory(int level) {
		super.onTrimMemory(level);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinderService;
	}

	/* OnPreparedListener implementation section */
	@Override
	public void onPrepared(MediaPlayer mp) {
		playerState = PlayerState.PLAYING;
		mPlayer.start();

		setForeground();
	}

	/* OnSeekCompleteListener implementation section */
	@Override
	public void onSeekComplete(MediaPlayer mp) {
		Log.d(TAG, "Song " + mSongSelector.getCurrentSong().title + " modified");

		mIsModified = true;
	}

	/* OnCompletionListener implementation section */
	@Override
	public void onCompletion(MediaPlayer mp) {
		Log.d(TAG, "Was modified? " + mIsModified);

		if (!mIsModified)
			SongUtilities.getInstance(getApplicationContext()).addPlay(getCurrentSong());

		forward();
	}

	/* OnErrorListener implementation section */
	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		return false;
	}

	/* OnPlaylistControlListener implementation section */
	@Override
	public void onFinishLoad(Boolean play) {
		forcePlay(play);
		notifyPlaylistHasChanged();
	}

	@Override
	public void onCurrentPlaylistFinishes() {
		stop();

	}

	private void setForeground() {
		if (mNotification == null)
			mNotification = new PlayerNotification(getApplicationContext());

		if (getCurrentSong() != null) {
			Log.d("NOTIF", mNotification.toString());
			mNotification.update(getCurrentSong(), playerState);

			PlayerService.this.startForeground(mNotification.idNotification, mNotification.notification);

			sendLocalBroadcast();
		}
	}

	private void createPlayer() {
		try {
			if (mSongSelector == null)
				mSongSelector = new SongSelector(getContentResolver(), this);

			if (mPlayer == null) {
				mPlayer = new MediaPlayer();

				mPlayer.setOnPreparedListener(this);
				mPlayer.setOnSeekCompleteListener(this);
				mPlayer.setOnCompletionListener(this);
				mPlayer.setOnErrorListener(this);

				mPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
			}
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// private void play() {
	// getAudioFocus();
	// }

	private void play() {
		try {
			if (mSongSelector == null)
				mSongSelector = new SongSelector(getContentResolver(), this);

			if (getCurrentPlaylist().size() == 0) {
				mSongSelector.playAll();
			} else {
				if (playerState == PlayerState.PAUSED) {
					playerState = PlayerState.PLAYING;
					mPlayer.start();
					setForeground();
				} else if (playerState == PlayerState.STOPPED) {
					forward();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void forcePlay(boolean play) {
		if (play && playerState != PlayerState.STOPPED) {
			mPlayer.stop();
			playerState = PlayerState.STOPPED;

		}

		play();
	}

	private void toggle() {
		if (playerState == PlayerState.STOPPED || playerState == PlayerState.PAUSED)
			play();
		else
			pause();
	}

	// private void pause() {
	// releaseAudioFocus();
	// }
	private void pause() {
		try {
			playerState = PlayerState.PAUSED;
			mPlayer.pause();
			setForeground();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void forward() {
		try {
			mIsModified = false;
			if (mSongSelector.forward()) {
				setSong();
				mPlayer.prepareAsync();
			}
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void rewind() {
		try {
			if (mPlayer.getCurrentPosition() > REWIND_SPAN) {
				mPlayer.seekTo(0);
			} else {
				mIsModified = false;
				mSongSelector.rewind();
				setSong();
				mPlayer.prepareAsync();
			}
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void stop() {
		mSongSelector.clearPlaylist();
		mPlayer.stop();

		playerState = PlayerState.STOPPED;
	}

	private void close() {
		SongUtilities.getInstance(getApplicationContext()).backPlaylist(getCurrentPlaylist(), getCurrentPosition());
		stop();
		stopSelf();
	}

	private void sendLocalBroadcast() {
		if (mSongSelector.getCurrentSong() != null) {
			Intent intent = new Intent(BROADCAST_PLAY_FILTER);

			intent.putExtra(EXTRA_CURRENT_SONG, mSongSelector.getCurrentSong());
			intent.putExtra(EXTRA_SONG_NUMBER, mSongSelector.getCurrentPosition());
			intent.putExtra(EXTRA_PLAYER_STATE, (Parcelable) playerState);

			LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
		}
	}

	private void notifyPlaylistHasChanged() {
		if (mSongSelector.getCurrentSong() != null) {
			Intent intent = new Intent(BROADCAST_PLAYLIST_CHANGED);

			LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
		}
	}

	private void setSong() {
		if (mSongSelector.getCurrentSong() != null) {
			if (mPlayer == null) {
				createPlayer();
				mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			}
			mPlayer.reset();

			try {
				mPlayer.setDataSource(getApplicationContext(), mSongSelector.getCurrentSong().getUri());
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void addGenre(long idGenre, AddMode enqueue) {
		if (idGenre != PlayerService.INVALID_ID_OR_POSITION)
			mSongSelector.addGenre(idGenre, enqueue);
	}

	public void addArtist(long idArtist, AddMode enqueue) {
		if (idArtist != PlayerService.INVALID_ID_OR_POSITION)
			mSongSelector.addArtist(idArtist, enqueue);
	}

	public void addAlbum(long idAlbum, AddMode enqueue) {
		if (idAlbum != PlayerService.INVALID_ID_OR_POSITION)
			mSongSelector.addAlbum(idAlbum, enqueue);
	}

	public void addSong(long idSong, AddMode enqueue) {
		if (idSong != PlayerService.INVALID_ID_OR_POSITION)
			mSongSelector.addSong(idSong, enqueue);
	}

	public void addPlaylist(Playlist playlist, AddMode enqueue) {
		mSongSelector.addPlaylist(playlist, enqueue);
	}

	public void addBatchSongs(List<Song> songs, AddMode enqueue) {
		mSongSelector.addBatchSongs(songs, enqueue);
	}

	public void toggleSelectionMode() {
		mSongSelector.toggleSelectionMode();
	}

	public void togglePlayMode() {
		mSongSelector.togglePlayMode();
	}

	public void forcePlay() {
		forcePlay(true);
	}

	public void restorePlaylist() {
		Handler h = new Handler();
		h.post(new Restorer());
	}
	// private final float DUCK_VOLUME = 0.2f;
	// public static final String WIDGET_UPDATE =
	// "nobody.sip.action.UPDATE_WIDGET";
	// public static final String WIDGET_TOGGLE_PANEL =
	// "nobody.sip.action.SHOW_RANK_PANEL";
	// public static final String RANK_SONG = "nobody.sip.action.RANK_SONG";
	// public static final String EXTRA_RANK = "nobody.sip.extra.RANK";
	// private FocusState mFocusState = FocusState.LOST_FOCUS;
	// private MusicFocus mMusicFocus;
	// private boolean mIsReceiverRegistered = false;
	//
	// private enum FocusState {
	// FOCUSED, DUCK_FOCUS, LOST_FOCUS
	// }
	//
	//
	// @Override
	// public void onCreate() {
	// if (!mIsReceiverRegistered) {
	// ((AudioManager)
	// getSystemService(AUDIO_SERVICE)).registerMediaButtonEventReceiver(new
	// ComponentName(this,
	// PlayerReceiver.class));
	// mIsReceiverRegistered = true;
	// }
	//
	// if (mMusicFocus == null)
	// mMusicFocus = new MusicFocus(getApplicationContext(), this);
	// }
	//
	// /* OnMusicFocusChanged implementation section */
	// @Override
	// public void onGainFocus() {
	// if (mFocusState == FocusState.DUCK_FOCUS)
	// mPlayer.setVolume(1.0f, 1.0f);
	// else if (mFocusState == FocusState.LOST_FOCUS)
	// play();
	//
	// mFocusState = FocusState.FOCUSED;
	//
	// }
	//
	// @Override
	// public void onLostFocus() {
	// if (mPlayerState == PlayerState.PLAYING)
	// pause();
	//
	// mFocusState = FocusState.LOST_FOCUS;
	// }
	//
	// @Override
	// public void onDuckFocus() {
	// if (mPlayerState == PlayerState.PLAYING)
	// mPlayer.setVolume(DUCK_VOLUME, DUCK_VOLUME);
	//
	// mFocusState = FocusState.DUCK_FOCUS;
	//
	// }

	// private void rankSong(int rank) {
	// SongUtilities.getSongUtilities(getApplicationContext()).rankSong(mSongSelector.getCurrentSong(),
	// rank);
	// }
	// private void getAudioFocus() {
	// if (mFocusState != FocusState.FOCUSED) {
	//
	// if (mMusicFocus == null)
	// mMusicFocus = new MusicFocus(getApplicationContext(), this);
	//
	// mMusicFocus.requestGainFocus();
	// }
	// }
	//
	// private void releaseAudioFocus() {
	// if (mMusicFocus == null)
	// mMusicFocus = new MusicFocus(getApplicationContext(), this);
	//
	// mMusicFocus.requesLostFocus();
	// mFocusState = FocusState.LOST_FOCUS;
	// }
	//
	// private void setForeground() {
	// // getSmallWidget().updateWidget(getApplicationContext(),
	// // getCurrentSongWithExtras(), mPlayerState, 0);
	// // getLargeWidget().updateWidget(getApplicationContext(),
	// // getCurrentSongWithExtras(), mPlayerState, 0);
	// }

}

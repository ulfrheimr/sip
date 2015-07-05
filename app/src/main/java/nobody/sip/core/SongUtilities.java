package nobody.sip.core;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import nobody.sip.core.PlayerData.PlayerDataManager;
import nobody.sip.prots.Song;
import nobody.sip.prots.SongWithExtras;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.util.Log;

public class SongUtilities extends PlayerDataManager {
	private final String DUMMY_LYRICS = "THESE ARE LYRICS FROM THE SONG:\n";
	private final String TAG = "sip.core.SONG_UTILITIES";
	private static SongUtilities mInstance = null;
	private Handler mHandler = new Handler();

	private SongUtilities(Context context) {
		super(context);

	}

	public static SongUtilities getInstance(Context context) {
		if (mInstance == null) {
			Context dbContext = context.getApplicationContext();
			mInstance = new SongUtilities(dbContext);
		}

		return mInstance;
	}

	public class Extras {
		public int rank;
		public int plays;
		public Date lastPlayed;

		public void setLastPlayed(long lastPlayed) {
			this.lastPlayed = new Date(lastPlayed);
		}

		public long getLastPlayedTime() {
			return this.lastPlayed.getTime();
		}

		public Extras(int rank, int plays, long lastPlayed) {
			this.rank = rank;
			this.plays = plays;
			this.lastPlayed = new Date(lastPlayed);
		}
	}

	private class Ranker implements Runnable {
		private final Song mSong;
		private final int mRank;

		public Ranker(Song song, int rank) {
			this.mSong = song;
			this.mRank = rank;
		}

		@Override
		public void run() {
			ContentValues values = new ContentValues();

			String filter = String.format("%s=%s and %s='%s' and %s=%s", SONG_ID, mSong.idSong, SONG_NAME,
					mSong.title.replaceAll("\'", ""), ALBUM_ID, mSong.idAlbum);
			Cursor c = SongUtilities.super.select(CONTROL_TABLE, new String[] { SONG_NAME, ALBUM_ID, CONTROL_PLAYS }, filter,
					null, null, null, false);
			try {
				if (c.getCount() != 0) {
					c.moveToFirst();

					if (c.getString(0).equals(mSong.title) && c.getLong(1) == mSong.idAlbum) {
						values.put(CONTROL_RANK, mRank);
						SongUtilities.super.update(CONTROL_TABLE, values, filter);
					} else {
						values.put(SONG_NAME, mSong.title.replaceAll("\'", ""));
						values.put(ALBUM_ID, mSong.idAlbum);
						values.put(CONTROL_PLAYS, 0);
						values.put(CONTROL_RANK, mRank);
						values.put(CONTROL_LAST_PLAYED, "");
						values.put(CONTROL_SAVED_LYRICS, String.format("%s%s", DUMMY_LYRICS, mSong.title));

						SongUtilities.super.update(CONTROL_TABLE, values, filter);
					}

				} else {
					values.put(SONG_ID, mSong.idSong);
					values.put(SONG_NAME, mSong.title);
					values.put(ALBUM_ID, mSong.idAlbum);
					values.put(CONTROL_PLAYS, 0);
					values.put(CONTROL_RANK, mRank);
					values.put(CONTROL_LAST_PLAYED, "");

					SongUtilities.super.insert(CONTROL_TABLE, values);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public class PlaylistBacker implements Runnable {
		private final List<Song> mSongs;
		private final int mCurrentPosition;

		public PlaylistBacker(List<Song> playlist, int currentPosition) {
			mSongs = playlist;
			mCurrentPosition = currentPosition;
		}

		@Override
		public void run() {
			if (mSongs != null) {
				ContentValues values = new ContentValues();

				for (int i = 0; i < mSongs.size(); i++) {
					Log.d(TAG, "Backing " + mSongs.get(i).title);
					values.put(BACKSTATE_ID_ARTIST, mSongs.get(i).idArtist);
					values.put(BACKSTATE_ARTIST, mSongs.get(i).artist);
					values.put(BACKSTATE_ID_ALBUM, mSongs.get(i).idAlbum);
					values.put(BACKSTATE_ALBUM, mSongs.get(i).album);
					values.put(BACKSTATE_ID_SONG, mSongs.get(i).idSong);
					values.put(BACKSTATE_TITLE, mSongs.get(i).title);
					values.put(BACKSTATE_TRACK, mSongs.get(i).track);
					values.put(BACKSTATE_DURATION, mSongs.get(i).duration);

					if (i == mCurrentPosition)
						values.put(BACKSTATE_IS_PLAYING, 1);
					else
						values.put(BACKSTATE_IS_PLAYING, 0);

					try {
						SongUtilities.super.insert(BACKSTATE_TABLE, values);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private class PlayAdder implements Runnable {
		private final Song mSong;

		public PlayAdder(Song song) {
			mSong = song;
		}

		@Override
		public void run() {
			ContentValues values = new ContentValues();

			String filter = String.format("%s=%s and %s='%s' and %s=%s", SONG_ID, mSong.idSong, SONG_NAME,
					mSong.title.replaceAll("\'", ""), ALBUM_ID, mSong.idAlbum);
			Cursor c = SongUtilities.super.select(CONTROL_TABLE, new String[] { CONTROL_PLAYS }, filter, null, null, "1", false);

			try {
				values.put(SONG_ID, mSong.idSong);
				values.put(CONTROL_LAST_PLAYED, new Date().getTime());

				if (c.getCount() == 0) {
					values.put(SONG_NAME, mSong.title.replaceAll("\'", ""));
					values.put(ALBUM_ID, mSong.idAlbum);
					values.put(CONTROL_PLAYS, 1);
					values.put(CONTROL_RANK, 0);
					values.put(CONTROL_SAVED_LYRICS, String.format("%s%s", DUMMY_LYRICS, mSong.title));

					SongUtilities.super.insert(CONTROL_TABLE, values);
				} else {
					c.moveToPosition(0);

					values.put(CONTROL_PLAYS, c.getInt(0) + 1);
					SongUtilities.super.update(CONTROL_TABLE, values, filter);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public Cursor getBackPlaylist() {
		Cursor c = super.select(BACKSTATE_TABLE, new String[] { BACKSTATE_ID_ARTIST, BACKSTATE_ARTIST, BACKSTATE_ID_ALBUM,
				BACKSTATE_ALBUM, BACKSTATE_ID_SONG, BACKSTATE_TITLE, BACKSTATE_TRACK, BACKSTATE_DURATION, BACKSTATE_IS_PLAYING },
				null, null, null, null, false);

		return c;
	}

	public Extras getExtras(long idSong) {
		Extras extras = null;

		String filter = String.format("%s=%s", SONG_ID, idSong);
		Cursor c = super.select(CONTROL_TABLE, new String[] { CONTROL_PLAYS, CONTROL_RANK, CONTROL_LAST_PLAYED }, filter, null,
				null, null, false);

		if (c.getCount() == 1) {
			c.moveToFirst();

			extras = new Extras(c.getInt(1), c.getInt(0), c.getLong(2));
		}

		return extras;
	}

	public SongWithExtras getSongwithExtras(Song song) {
		SongWithExtras songExtras = null;

		if (song != null) {
			Extras extras = getExtras(song.idSong);
			songExtras = new SongWithExtras(song, 0, 0, 0);

			if (extras != null) {
				songExtras.plays = extras.plays;
				songExtras.rank = extras.rank;
				songExtras.lastPlayed = extras.getLastPlayedTime();
			}
		}

		return songExtras;
	}

	public String getLyrics(Song song) {
		String lyrics = "";

		if (verifySong(song)) {
			String filter = String.format("%s=%s", SONG_ID, song.idSong);
			Cursor c = super.select(CONTROL_TABLE, new String[] { CONTROL_SAVED_LYRICS }, filter, null, null, null, false);

			c.moveToFirst();
			lyrics = c.getString(0);
		}

		return lyrics;
	}

	public void addPlay(Song song) {
		PlayAdder adder = new PlayAdder(song);
		mHandler.post(adder);
	}

	public boolean verifySong(Song song) {
		boolean exists = true;

		String filter = String.format("%s=%s and %s='%s' and %s=%s", SONG_ID, song.idSong, SONG_NAME,
				song.title.replaceAll("\'", ""), ALBUM_ID, song.idAlbum);
		Cursor c = super.select(CONTROL_TABLE, null, filter, null, null, null, false);

		if (c.getCount() == 0)
			exists = false;

		return exists;
	}

	public void backPlaylist(List<Song> songs, int currentPosition) {
		PlaylistBacker backer = new PlaylistBacker(songs, currentPosition);

		mHandler.post(backer);
	}

	public void clearBackList() {
		try {
			super.delete(BACKSTATE_TABLE, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressLint("DefaultLocale")
	public String millisToTime(long millis) {
		long hours = TimeUnit.MILLISECONDS.toHours(millis);
		long minutes = TimeUnit.MILLISECONDS.toMinutes(millis - TimeUnit.HOURS.toMillis(hours));
		long seconds = TimeUnit.MILLISECONDS.toSeconds(millis - TimeUnit.MINUTES.toMillis(minutes)
				- TimeUnit.HOURS.toMillis(hours));

		return hours == 0 ? String.format("%02d:%02d", minutes, seconds) : String.format("%02d'%02d:%02d", hours, minutes,
				seconds);
	}

	/* Check it */
	// public void rankSong(Song song, int rank) {
	// Ranker ranker = new Ranker(song, rank);
	// Thread t = new Thread(ranker);
	// t.start();
	// }
	//
	// public int getPercentage(long currentTime, long totalTime) {
	// int percentage = 0;
	//
	// percentage = (int) (totalTime / currentTime);
	//
	// return percentage;
	// }
}

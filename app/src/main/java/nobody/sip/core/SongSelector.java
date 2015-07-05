package nobody.sip.core;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AudioColumns;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import nobody.sip.prots.Playlist;
import nobody.sip.prots.Song;

public class SongSelector implements Runnable {
	private final String TAG = "sip.core.SONG_SELECTOR";

	private int mNumberSong = PlayerService.INVALID_ID_OR_POSITION;
	private List<Song> mSongs = new ArrayList<Song>();
	private List<Integer> mBackOrder = null;
	private List<Integer> mRandomOrder = null;

	private ContentResolver mContentResolver;
	private OnPlaylistControlListener mListener;

	private PlayMode mPlayMode = PlayMode.NORMAL;
	private SelectionMode mSelectionMode = SelectionMode.NORMAL;

	private Handler mHandler = new Handler();
	private boolean mForcePlay = false;

	public SongSelector(ContentResolver contentResolver, OnPlaylistControlListener listener) {
		mContentResolver = contentResolver;
		mListener = listener;
	}

	public interface OnPlaylistControlListener {
		public void onFinishLoad(Boolean play);

		public void onCurrentPlaylistFinishes();
	}

	public class BatchConfig {
		private AddMode mEnqueue;
		private List<Song> mSongs;

		public BatchConfig(List<Song> songs, AddMode enqueue) {
			mEnqueue = enqueue;
			this.mSongs = songs;
		}
	}

	public class Config {
		private String mSelector;
		private String mOrder;
		private AddMode mEnqueue;

		public Config(String selector, SortMode sorting, AddMode enqueue) {
			mSelector = selector;
			mEnqueue = enqueue;
			mOrder = sorting.getSort();
		}
	}

	private class AsyncLoader extends AsyncTask<Config, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Config... config) {
			boolean force = false;

			List<Song> tempSongs = new ArrayList<Song>();
			Song current;
			int currentSong = mNumberSong;
			String order = config[0].mOrder == null ? AudioColumns.TRACK : config[0].mOrder;

			Cursor c = mContentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[] { AudioColumns.ARTIST_ID,
					AudioColumns.ARTIST, AudioColumns.ALBUM_ID, AudioColumns.ALBUM, BaseColumns._ID, MediaColumns.TITLE,
					AudioColumns.DURATION, AudioColumns.TRACK }, config[0].mSelector, null, order);

			switch (config[0].mEnqueue) {
			case CLEAR:
				mNumberSong = PlayerService.INVALID_ID_OR_POSITION;
				mSongs = new ArrayList<Song>();

				while (c.moveToNext()) {
					current = new Song(c.getLong(0), c.getString(1), c.getLong(2), c.getString(3), c.getLong(4), c.getString(5),
							c.getInt(7), c.getLong(6));
					mSongs.add(current);
				}

				force = true;
				break;
			case FIRST:
				mNumberSong = PlayerService.INVALID_ID_OR_POSITION;
				tempSongs = mSongs;

				if (mSongs.size() != 0)
					force = true;

				mSongs = new ArrayList<Song>();

				while (c.moveToNext()) {
					current = new Song(c.getLong(0), c.getString(1), c.getLong(2), c.getString(3), c.getLong(4), c.getString(5),
							c.getInt(7), c.getLong(6));

					mSongs.add(current);
				}
				mSongs.addAll(tempSongs.subList(currentSong + 1, tempSongs.size()));
				break;
			case NEXT_TO:
				while (c.moveToNext()) {
					current = new Song(c.getLong(0), c.getString(1), c.getLong(2), c.getString(3), c.getLong(4), c.getString(5),
							c.getInt(7), c.getLong(6));
					tempSongs.add(current);
				}

				if (mSongs.size() <= 1 || mSongs == null || mNumberSong == mSongs.size()) {
					mSongs.addAll(tempSongs);
				} else {
					List<Song> tempLast = new ArrayList<Song>(mSongs.subList(mNumberSong + 1, mSongs.size()));
					List<Song> tempInit = new ArrayList<Song>(mSongs.subList(0, mNumberSong + 1));

					mSongs.clear();

					if (tempInit.size() > 0)
						mSongs.addAll(tempInit);
					if (tempSongs.size() > 0)
						mSongs.addAll(tempSongs);
					if (tempLast.size() > 0)
						mSongs.addAll(tempLast);
				}
				break;
			case AT_LAST:
				while (c.moveToNext()) {
					current = new Song(c.getLong(0), c.getString(1), c.getLong(2), c.getString(3), c.getLong(4), c.getString(5),
							c.getInt(7), c.getLong(6));
					mSongs.add(current);
				}

				mSongs.addAll(tempSongs);

			}

			c.close();

			return force;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			for (Song s : mSongs) {
				Log.d(TAG, s.title + " - " + s.artist);
			}

			mListener.onFinishLoad(result);
		}
	}

	private class AsyncBatchLoader extends AsyncTask<BatchConfig, Void, Boolean> {

		@Override
		protected Boolean doInBackground(BatchConfig... config) {
			boolean force = false;
			List<Song> songs = config[0].mSongs;
			List<Song> tempSongs = new ArrayList<Song>();
			int currentSong = mNumberSong;

			switch (config[0].mEnqueue) {
			case CLEAR:
				mNumberSong = PlayerService.INVALID_ID_OR_POSITION;
				mSongs = new ArrayList<Song>();
				mSongs.addAll(songs);

				force = true;
				break;
			case FIRST:
				mNumberSong = PlayerService.INVALID_ID_OR_POSITION;
				tempSongs = mSongs;

				if (mSongs.size() != 0)
					force = true;

				mSongs = new ArrayList<Song>();
				mSongs.addAll(songs);

				if (tempSongs.size() > 1)
					mSongs.addAll(tempSongs.subList(currentSong + 1, tempSongs.size()));
				break;
			case NEXT_TO:
				if (mSongs.size() <= 1 || mNumberSong == mSongs.size()) {
					mSongs.addAll(songs);
				} else {
					List<Song> tempLast = new ArrayList<Song>(mSongs.subList(mNumberSong + 1, mSongs.size()));
					List<Song> tempInit = new ArrayList<Song>(mSongs.subList(0, mNumberSong + 1));

					mSongs = new ArrayList<Song>();

					mSongs.addAll(tempInit);
					mSongs.addAll(songs);
					mSongs.addAll(tempLast);
				}
				break;
			case AT_LAST:
				mSongs.addAll(songs);

			}

			return force;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			for (Song s : mSongs) {
				Log.d(TAG, s.title + " - " + s.artist);
			}

			mListener.onFinishLoad(result);
		}

	}

	public static enum SelectionMode {
		NORMAL, RANDOM
	}

	public static enum PlayMode {
		NORMAL, LOOP_ALL, LOOP_ONE
	}

	public static enum SortMode {
		GENRE, ARTIST, ALBUM, SONG, TITLE;

		public String getSort() {
			switch (this) {
			case GENRE:
				return null;
			case ARTIST:
				return MediaColumns.TITLE;
			case ALBUM:
				return AudioColumns.ALBUM + " asc, " + AudioColumns.TRACK + " asc";
			case SONG:
				return AudioColumns.TRACK;
			case TITLE:
				return MediaColumns.TITLE;
			default:
				return null;
			}
		}
	}

	public static enum AddMode implements Parcelable {
		CLEAR, FIRST, NEXT_TO, AT_LAST;

		public static final Creator<AddMode> CREATOR = new Creator<SongSelector.AddMode>() {

			@Override
			public AddMode[] newArray(int size) {
				return new AddMode[size];
			}

			@Override
			public AddMode createFromParcel(Parcel source) {
				return AddMode.values()[source.readInt()];
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

	public PlayMode getPlayMode() {
		return mPlayMode;
	}

	public SelectionMode getSelectionMode() {
		return mSelectionMode;
	}

	public int getCurrentPosition() {
		int position = PlayerService.INVALID_ID_OR_POSITION;
		switch (mSelectionMode) {
		case NORMAL:
			position = mNumberSong;
			break;
		case RANDOM:
			position = mRandomOrder.get(mNumberSong);
			break;
		}

		return position;
	}

	public void setCurrentPosition(int position) {
		mNumberSong = position;
	}

	public Song getSongAtPosition(int position) {
		Song song = null;

		if (mSongs.size() > 0) {
			switch (mSelectionMode) {
			case NORMAL:
				song = mSongs.get(position);
				break;

			case RANDOM:
				song = mSongs.get(mRandomOrder.get(position));
				break;
			}
		}

		return song;
	}

	public Song getCurrentSong() {
		Song song = null;
		if (mSongs.size() > 0) {
			switch (mSelectionMode) {
			case NORMAL:
				song = mSongs.get(mNumberSong);
				break;
			case RANDOM:
				song = mSongs.get(mRandomOrder.get(mNumberSong));
			}
		}

		return song;
	}

	public Song getPreviousSong() {
		Song song = null;
		if (mSongs.size() > 0 && mNumberSong >= 1) {
			switch (mSelectionMode) {
			case NORMAL:
				song = mSongs.get(mNumberSong - 1);
				break;
			case RANDOM:
				song = mSongs.get(mRandomOrder.get(mNumberSong - 1));
			}
		}
		
		return song;
	}

	public Song getNextSong() {
		Song song = null;
		if (mSongs.size() > 0 && mNumberSong < mSongs.size() - 1) {
			switch (mSelectionMode) {
			case NORMAL:
				song = mSongs.get(mNumberSong + 1);
				break;
			case RANDOM:
				song = mSongs.get(mRandomOrder.get(mNumberSong + 1));
			}
		}

		return song;
	}

	public List<Song> getCurrentPlaylist() {
		if (mSelectionMode == SelectionMode.NORMAL)
			return mSongs;
		else
			return randomList();

	}

	public void setCurrentPlaylist(List<Song> playlist, int currentPosition) {
		mNumberSong = currentPosition;
		mSongs = playlist;
	}

	@Override
	public void run() {
		if (mSelectionMode == SelectionMode.RANDOM)
			calculateRandom();

		mListener.onFinishLoad(mForcePlay);

	}

	private List<Song> randomList() {
		if (mSongs.size() > 0) {
			List<Song> list = new ArrayList<Song>();

			for (int i = 0; i < list.size(); i++)
				list.add(mSongs.get(mRandomOrder.get(i)));

			return list;
		} else
			return mSongs;
	}

	private void calculateRandom() {
		int i = 0;
		if (mSongs.size() > 0) {
			mBackOrder = new ArrayList<Integer>();
			mRandomOrder = new ArrayList<Integer>();

			for (i = 0; i < mSongs.size(); i++)
				mBackOrder.add(i);

			i = 0;

			mRandomOrder.add(mNumberSong);
			mBackOrder.remove(mNumberSong);

			while (mBackOrder.size() > 0) {
				i = (int) Math.round(mBackOrder.size() * Math.random());

				mRandomOrder.add(i);
				mBackOrder.remove(i);
			}
		}
		mRandomOrder = null;

	}

	public void clearPlaylist() {
		mNumberSong = PlayerService.INVALID_ID_OR_POSITION;
		mSongs = new ArrayList<Song>();
	}

	public void togglePlayMode() {
		switch (mPlayMode) {
		case NORMAL:
			mPlayMode = PlayMode.LOOP_ONE;
			break;

		case LOOP_ONE:
			mPlayMode = PlayMode.LOOP_ALL;
			break;
		case LOOP_ALL:
			mPlayMode = PlayMode.NORMAL;
			break;
		}
	}

	public void toggleSelectionMode() {
		switch (mSelectionMode) {
		case NORMAL:
			mSelectionMode = SelectionMode.RANDOM;
			break;

		case RANDOM:
			mSelectionMode = SelectionMode.NORMAL;
			mRandomOrder = null;
			break;
		}

		mForcePlay = false;
		mHandler.post(this);
	}

	public void addGenre(long idGenre, AddMode enqueue) {

	}

	public void addArtist(long idArtist, AddMode enqueue) {
		String selector = String.format("%s=1 and %s=%s", AudioColumns.IS_MUSIC, AudioColumns.ARTIST_ID, idArtist);

		new AsyncLoader().execute(new Config(selector, SortMode.ALBUM, enqueue));
	}

	public void addAlbum(long idAlbum, AddMode enqueue) {
		String selector = String.format("%s=1 and %s=%s", AudioColumns.IS_MUSIC, AudioColumns.ALBUM_ID, idAlbum);

		new AsyncLoader().execute(new Config(selector, SortMode.SONG, enqueue));
	}

	public void addSong(long idSong, AddMode enqueue) {
		String selector = String.format("%s=1 and %s=%s", AudioColumns.IS_MUSIC, BaseColumns._ID, idSong);

		new AsyncLoader().execute(new Config(selector, SortMode.TITLE, enqueue));
	}

	public void addPlaylist(Playlist playlist, AddMode enqueue) {
		// TODO: RUNNABLE PLAYLIST
	}

	public void addBatchSongs(List<Song> songs, AddMode enqueue) {
		new AsyncBatchLoader().execute(new BatchConfig(songs, enqueue));
	}

	public void playAll() {
		String selector = String.format("%s=1", AudioColumns.IS_MUSIC);

		new AsyncLoader().execute(new Config(selector, SortMode.TITLE, AddMode.CLEAR));
	}

	public boolean forward() {
		boolean ret = true;
		switch (mPlayMode) {
		case NORMAL:
			if (mNumberSong >= mSongs.size() - 1) {
				mListener.onCurrentPlaylistFinishes();
				ret = false;
			} else
				mNumberSong++;

			break;
		case LOOP_ONE:

			break;
		case LOOP_ALL:
			if (mNumberSong >= mSongs.size() - 1)
				mNumberSong = -1;

			mNumberSong++;
			break;
		}

		return ret;
	}

	public void rewind() {
		switch (mPlayMode) {
		case NORMAL:
			if (mNumberSong > 0)
				--mNumberSong;
			break;
		case LOOP_ALL:
			if (mNumberSong > 0)
				--mNumberSong;
			else
				mNumberSong = mSongs.size() - 1;
			break;
		default:
			break;
		}
	}

}

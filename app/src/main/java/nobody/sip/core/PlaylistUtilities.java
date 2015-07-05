package nobody.sip.core;

import nobody.sip.core.PlayerData.PlayerDataManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.util.SparseArray;

public class PlaylistUtilities extends PlayerDataManager implements Runnable {
	private static PlaylistUtilities mInstance;
	private SparseArray<String> mPlaylistMap = new SparseArray<String>();
	private boolean mIsCreated = false;

	private PlaylistUtilities(Context context) {
		super(context);
	}

	public static PlaylistUtilities getInstance(Context context) {
		
		if (mInstance == null) {
			Context dbContext = context.getApplicationContext();
			mInstance = new PlaylistUtilities(dbContext);
		}

		return mInstance;
	}

	public void getPlaylistByID(int ID) {
		int index = mPlaylistMap.indexOfKey(ID);
		String namxxe = mPlaylistMap.valueAt(index);
	}

	@Override
	public void run() {
		Cursor c = getPlaylists();
		int key = 1000;

		while (c.moveToNext()) {
			mPlaylistMap.append(key, c.getString(0));
			key++;
		}

		mIsCreated = true;
	}

	private Cursor getPlaylists() {
		Cursor c = super.select(PLAYLISTS_TABLE, new String[] { PLAYLISTS_NAME }, null, null, PLAYLISTS_NAME, null, false);
		return c;
	}

	public void insertPlaylist() {
		try {
			ContentValues cv = new ContentValues();

			cv.put(PLAYLISTS_NAME, "test5");
			cv.put(PLAYLISTS_RANK, 0);
			cv.put(PLAYLISTS_RANK_COMPARER, PlayerComparer.LESS_THAN.toString());
			cv.put(PLAYLISTS_PLAYS, 2);
			cv.put(PLAYLISTS_PLAYS_COMPARER, PlayerComparer.LESS_THAN.toString());
			cv.put(PLAYLISTS_GAA, "None");
			cv.put(PLAYLISTS_LIMITED, 110);
			cv.put(PLAYLISTS_ORDERED, OrderType.NAME.toString());

			super.insert(PLAYLISTS_TABLE, cv);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void create() {
		if (!mIsCreated)
			new Handler().post(this);
	}
}

package nobody.sip.core;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PlayerData {
	public static class PlayerDataManager {
		public static final String CONTROL_TABLE = "SongsPlayed";
		public static final String CONTROL_PLAYS = "number_plays";
		public static final String CONTROL_RANK = "rank";
		public static final String CONTROL_LAST_PLAYED = "last_played";
		public static final String CONTROL_SAVED_LYRICS = "lyrics";

		public static final String PLAYLISTS_TABLE = "Playlists";
		public static final String PLAYLISTS_NAME = "name";
		public static final String PLAYLISTS_PLAYS = "plays";
		public static final String PLAYLISTS_PLAYS_COMPARER = "plays_comparer";
		public static final String PLAYLISTS_RANK = "rank";
		public static final String PLAYLISTS_RANK_COMPARER = "rank_comparer";
		public static final String PLAYLISTS_LIMITED = "limited";
		public static final String PLAYLISTS_ORDERED = "ordered";
		public static final String PLAYLISTS_GAA = "gaa";

		public static final String BACKSTATE_TABLE = "BackState";
		public static final String BACKSTATE_ID_ARTIST = "idArtist";
		public static final String BACKSTATE_ARTIST = "artist";
		public static final String BACKSTATE_ID_ALBUM = "idAlbum";
		public static final String BACKSTATE_ALBUM = "album";
		public static final String BACKSTATE_ID_SONG = "idSong";
		public static final String BACKSTATE_TITLE = "title";
		public static final String BACKSTATE_TRACK = "track";
		public static final String BACKSTATE_DURATION = "duration";
		public static final String BACKSTATE_IS_PLAYING = "playing";

		public static final String SONG_ID = "ID";
		public static final String SONG_NAME = "name";
		public static final String ALBUM_ID = "albumID";

		private final DBHelper mDBHelper;

		public PlayerDataManager(Context context) {
			this.mDBHelper = new DBHelper(context);
		}

		public static enum OrderType {
			NAME, RANK, PLAYS
		}

		public static enum PlaylistType {
			STATIC, AUTOMATIC
		}

		public static enum PlayerComparer {
			LESS_THAN, EQUALS, MORE_THAN
		}

		private class DBHelper extends SQLiteOpenHelper {
			private static final String DB_NAME = "SiP_DB";
			private static final int DB_VERSION = 1;

			public DBHelper(Context context) {

				super(context, DB_NAME, null, DB_VERSION);
			}

			@Override
			public void onCreate(SQLiteDatabase db) {
				String controlQuery = String.format(
						"create table %s(%s int primary key, %s text, %s int, %s real, %s int, %s int, %s text)", CONTROL_TABLE,
						SONG_ID, SONG_NAME, ALBUM_ID, CONTROL_LAST_PLAYED, CONTROL_PLAYS, CONTROL_RANK, CONTROL_SAVED_LYRICS);
				db.execSQL(controlQuery);

				String playlistQuery = String.format(
						"create table %s(%s text primary key, %s int, %s text, %s int, %s text, %s text, %s int, %s text)",
						PLAYLISTS_TABLE, PLAYLISTS_NAME, PLAYLISTS_RANK, PLAYLISTS_RANK_COMPARER, PLAYLISTS_PLAYS,
						PLAYLISTS_PLAYS_COMPARER, PLAYLISTS_GAA, PLAYLISTS_LIMITED, PLAYLISTS_ORDERED);
				db.execSQL(playlistQuery);

				String backUpPlaylistQuery = String.format(
						"create table %s(%s integer, %s text, %s integer, %s text, %s integer, %s text, %s integer, %s integer, %s integer)",
						BACKSTATE_TABLE, BACKSTATE_ID_ARTIST, BACKSTATE_ARTIST, BACKSTATE_ID_ALBUM, BACKSTATE_ALBUM,
						BACKSTATE_ID_SONG, BACKSTATE_TITLE, BACKSTATE_TRACK, BACKSTATE_DURATION, BACKSTATE_IS_PLAYING);
				db.execSQL(backUpPlaylistQuery);
			}

			@Override
			public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
				// TODO Auto-generated method stub

			}

		}

		protected synchronized void update(String table, ContentValues values, String filter) throws Exception {
			try {
				SQLiteDatabase db = this.mDBHelper.getWritableDatabase();

				db.update(table, values, filter, null);
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		}

		protected synchronized Cursor select(String table, String[] columns, String filter, String group, String order,
				String limit, Boolean distinct) {
			SQLiteDatabase db = this.mDBHelper.getReadableDatabase();

			return db.query(distinct, table, columns, filter, null, group, null, order, limit);

		}

		protected synchronized void insert(String table, ContentValues values) throws Exception {
			try {
				SQLiteDatabase db = this.mDBHelper.getWritableDatabase();

				db.insert(table, null, values);
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}

		}

		protected synchronized void delete(String table, String filter) throws Exception {
			try {
				SQLiteDatabase db = this.mDBHelper.getWritableDatabase();
				db.delete(table, filter, null);
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}

		}

		public synchronized void close() {
			this.mDBHelper.close();
		}
	}

}

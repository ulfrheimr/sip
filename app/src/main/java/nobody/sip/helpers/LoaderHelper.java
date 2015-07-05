package nobody.sip.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nobody.sip.R;
import nobody.sip.core.PlayerService;
import nobody.sip.prots.Album;
import nobody.sip.prots.Artist;
import nobody.sip.prots.Song;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Audio.AlbumColumns;
import android.provider.MediaStore.Audio.ArtistColumns;
import android.provider.MediaStore.Audio.AudioColumns;
import android.provider.MediaStore.MediaColumns;

public class LoaderHelper {
	private static final String TAG = "sip.helpers.LOADER_HELPER";

	public static class SongLoader extends AsyncTaskLoader<List<Song>> {
		private Context mContext;
		private long mAlbumID = PlayerService.INVALID_ID_OR_POSITION;
		private String mOrder = MediaColumns.TITLE;

		public SongLoader(Context context) {
			super(context);
			mContext = context;
			mAlbumID = PlayerService.INVALID_ID_OR_POSITION;
		}

		public SongLoader(Context context, long albumID) {
			super(context);
			mContext = context;
			mAlbumID = albumID;
			mOrder = AudioColumns.TRACK;
		}

		@Override
		public List<Song> loadInBackground() {
			List<Song> songs = new ArrayList<Song>();
			Song current;

			String selectionFilter = AudioColumns.IS_MUSIC + "=1";

			if (mAlbumID != PlayerService.INVALID_ID_OR_POSITION)
				selectionFilter += " and " + AudioColumns.ALBUM_ID + "=" + mAlbumID;

			Cursor c = mContext.getContentResolver().query(
					Audio.Media.EXTERNAL_CONTENT_URI,
					new String[] { AudioColumns.ARTIST_ID, AudioColumns.ARTIST, AudioColumns.ALBUM_ID, AudioColumns.ALBUM,
							BaseColumns._ID, AudioColumns.TITLE, AudioColumns.TRACK, AudioColumns.DURATION }, selectionFilter,
					null, mOrder);

			while (c.moveToNext()) {
				current = new Song(c.getLong(0), c.getString(1), c.getLong(2), c.getString(3), c.getLong(4), c.getString(5),
						c.getInt(6), c.getLong(7));
				songs.add(current);
			}

			c.close();

			return songs;
		}
	}

	public static class AlbumLoader extends AsyncTaskLoader<List<Album>> {
		private Context mContext;
		private Artist mArtist = null;

		public AlbumLoader(Context context, Artist artist) {
			super(context);
			this.mContext = context;
			this.mArtist = artist;
		}

		public AlbumLoader(Context context) {
			super(context);
			this.mContext = context;
		}

		@Override
		public List<Album> loadInBackground() {
			String filter = String.format("%s=1", MediaStore.Audio.AudioColumns.IS_MUSIC);
			boolean hasSameNumberSongs = true;
			List<Album> albums = new ArrayList<Album>();
			Album currentAlbum;
			Cursor c = null;
			Cursor a = null;

			if (mArtist == null) {
				c = mContext.getContentResolver().query(
						MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
						new String[] { AudioColumns.ARTIST_ID, AudioColumns.ARTIST, BaseColumns._ID, AlbumColumns.ALBUM,
								AlbumColumns.ALBUM_ART }, null, null, AlbumColumns.ALBUM);
				while (c.moveToNext()) {
					a = mContext.getContentResolver()
							.query(MediaStore.Audio.Artists.Albums.getContentUri("external", c.getLong(0)),
									new String[] { AlbumColumns.NUMBER_OF_SONGS, AlbumColumns.NUMBER_OF_SONGS_FOR_ARTIST,
											BaseColumns._ID }, null, null, AlbumColumns.ALBUM);

					while (a.moveToNext()) {
						hasSameNumberSongs = a.getInt(0) == a.getInt(1);
						if (hasSameNumberSongs)
							currentAlbum = new Album(c.getLong(0), c.getString(1), c.getLong(2), c.getString(3), false);
						else
							currentAlbum = new Album(PlayerService.INVALID_ID_OR_POSITION, mContext.getResources().getString(
									R.string.various_artists), c.getLong(2), c.getString(3), true);

						currentAlbum.arturi = c.getString(4);

						albums.add(currentAlbum);
						break;
					}

					a.close();
				}

			} else {
				if (mArtist.isVA) {
					c = mContext.getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
							new String[] { AudioColumns.ARTIST_ID, BaseColumns._ID, AlbumColumns.ALBUM, AlbumColumns.ALBUM_ART },
							filter, null, AlbumColumns.ALBUM);
					while (c.moveToNext()) {
						a = mContext.getContentResolver().query(
								MediaStore.Audio.Artists.Albums.getContentUri("external", c.getLong(0)),
								new String[] { AlbumColumns.NUMBER_OF_SONGS, AlbumColumns.NUMBER_OF_SONGS_FOR_ARTIST,
										BaseColumns._ID }, filter, null, AlbumColumns.ALBUM);

						while (a.moveToNext()) {
							hasSameNumberSongs = a.getInt(0) == a.getInt(1);

							if (!hasSameNumberSongs) {
								currentAlbum = new Album(PlayerService.INVALID_ID_OR_POSITION, mContext.getResources().getString(
										R.string.various_artists), c.getLong(1), c.getString(2), true);
								currentAlbum.arturi = c.getString(3);

								albums.add(currentAlbum);
							}
						}

						a.close();
					}

				} else {
					c = mContext.getContentResolver().query(
							MediaStore.Audio.Artists.Albums.getContentUri("external", mArtist.idArtist),
							new String[] { AudioColumns.ARTIST_ID, AlbumColumns.ARTIST, BaseColumns._ID, AlbumColumns.ALBUM },
							filter, null, AlbumColumns.ALBUM);
					while (c.moveToNext()) {
						currentAlbum = new Album(c.getLong(0), c.getString(1), c.getLong(2), c.getString(3), false);
						currentAlbum.arturi = c.getString(3);

						albums.add(currentAlbum);
					}
				}
			}

			c.close();

			return albums;
		}
	}

	public static class ArtistLoader extends AsyncTaskLoader<List<Artist>> {
		private Context mContext;

		public ArtistLoader(Context context) {
			super(context);
			mContext = context;
		}

		@Override
		public List<Artist> loadInBackground() {
			boolean isVA = false;
			Long[] idAlbums;
			List<Artist> artists = new ArrayList<Artist>();
			Artist currentArtist;
			HashMap<Long, String> albumsVA = new HashMap<Long, String>();
			HashMap<Long, String> albums = new HashMap<Long, String>();

			Cursor c = mContext.getContentResolver().query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
					new String[] { BaseColumns._ID, ArtistColumns.ARTIST }, null, null, AlbumColumns.ARTIST);

			while (c.moveToNext()) {
				isVA = false;

				Cursor c2 = mContext.getContentResolver().query(
						MediaStore.Audio.Artists.Albums.getContentUri("external", c.getLong(0)),
						new String[] { AlbumColumns.NUMBER_OF_SONGS, AlbumColumns.NUMBER_OF_SONGS_FOR_ARTIST, BaseColumns._ID,
								AlbumColumns.ALBUM }, AudioColumns.IS_MUSIC + "=1", null, null);

				albums = new HashMap<Long, String>();

				while (c2.moveToNext()) {
					if (c2.getLong(0) == c2.getLong(1)) {
						albums.put(c2.getLong(2), c2.getString(3));
					} else {
						isVA = true;
						albumsVA.put(c2.getLong(2), c2.getString(3));
					}
				}

				c2.close();

				if (!isVA) {
					currentArtist = new Artist(c.getLong(0), c.getString(1), false);
					idAlbums = albums.keySet().toArray(new Long[albums.keySet().size()]);
					currentArtist.setAlbums(idAlbums);
					artists.add(currentArtist);
				}

			}

			c.close();

			if (albumsVA.size() > 0) {
				currentArtist = new Artist(PlayerService.INVALID_ID_OR_POSITION, mContext.getResources().getString(
						R.string.various_artists), true);
				idAlbums = albumsVA.keySet().toArray(new Long[albums.keySet().size()]);
				artists.add(currentArtist);
			}

			return artists;
		}
	}

	// public static class SongLoader extends AsyncTaskLoader<List<Song>> {
	// private Context mContext;
	// private Song mCurrent;
	//
	// public SongLoader(Context context) {
	// super(context);
	// this.mContext = context;
	// }
	//
	// @Override
	// public List<Song> loadInBackground() {
	// List<Song> songs = new ArrayList<Song>();
	//
	// Cursor cp;
	// Cursor co =
	// mContext.getContentResolver().query(MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI,
	// new String[] { MediaStore.Audio.Genres._ID, MediaStore.Audio.Genres.NAME
	// }, null, null,
	// MediaStore.Audio.Genres.NAME);
	// while (co.moveToNext()) {
	// cp = mContext.getContentResolver().query(
	// MediaStore.Audio.Genres.Members.getContentUri("external", co.getLong(0)),
	// new String[] { MediaStore.Audio.Genres.Members.ARTIST,
	// MediaStore.Audio.Genres.Members.ARTIST_ID },
	// MediaStore.Audio.Genres.Members.ARTIST_ID + "=16", null,
	// MediaStore.Audio.Genres.Members.ARTIST);
	//
	// while (cp.moveToNext()) {
	// if (cp.getCount() > 0) {
	//
	// }
	// }
	// }
	// co.close();
	// //
	// // Cursor c =
	// //
	// mContext.getContentResolver().query(MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI,
	// // new String[] { MediaStore.Audio.Genres._ID,
	// // MediaStore.Audio.Genres.NAME }, null, null,
	// // MediaStore.Audio.Genres.NAME);
	//
	// // Cursor c =
	// //
	// mContext.getContentResolver().query(MediaStore.Audio.Genres.Members.getContentUri("external",
	// // 12),
	// // new String[] { MediaStore.Audio.Genres.Members.ARTIST,
	// // MediaStore.Audio.Genres.Members.ARTIST_ID },
	// // MediaStore.Audio.Genres.Members.ARTIST_ID + "=99987", null,
	// // MediaStore.Audio.Genres.Members.ARTIST);
	// //
	// // while (c.moveToNext()) {
	// // if (c.getString(1) == "") {
	// //
	// // }
	// // }
	// // c.close();
	//
	// // Cursor c = mContext.getContentResolver().query(
	// // MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
	// // new String[] { AudioColumns.ARTIST_ID, AudioColumns.ARTIST,
	// // AudioColumns.ALBUM_ID, AudioColumns.ALBUM,
	// // BaseColumns._ID, MediaColumns.TITLE, AudioColumns.DURATION,
	// // AudioColumns.TRACK },
	// // AudioColumns.IS_MUSIC + "=1", null, MediaColumns.TITLE);
	// //
	// // while (c.moveToNext()) {
	// // // mCurrent = new Song(c.getLong(0), c.getString(1),
	// // // c.getLong(2), c.getString(3), c.getLong(4),
	// // // c.getString(5), c.getLong(6), c.getInt(7));
	// // //
	// // // songs.add(mCurrent);
	// // }
	//
	// // c.close();
	//
	// return songs;
	// }
	// }
}

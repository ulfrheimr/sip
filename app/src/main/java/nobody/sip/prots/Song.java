package nobody.sip.prots;

import android.content.ContentUris;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;

public class Song extends Album implements Parcelable {
	public long idSong;
	public String title;
	public int track;
	public long duration;

	public static final Parcelable.Creator<Song> CREATOR = new Parcelable.Creator<Song>() {

		@Override
		public Song[] newArray(int size) {
			return new Song[size];
		}

		@Override
		public Song createFromParcel(Parcel source) {
			return new Song(source);
		}
	};

	public Song(Parcel source) {
		super(source);

		this.idSong = source.readLong();
		this.title = source.readString();
		this.track = source.readInt();
		this.duration = source.readLong();
	}

	public Song(Album album, long idSong, String title, int track, long duration) {
		super(album.idArtist, album.artist, album.idAlbum, album.album, album.isCompilation);

		this.idSong = idSong;
		this.title = title;
		this.track = track;
		this.duration = duration;
	}

	public Song(long idArtist, String artist, long idAlbum, String album, long idSong, String title, int track, long duration) {
		super(idArtist, artist, idAlbum, album, false);

		this.idSong = idSong;
		this.title = title;
		this.track = track;
		this.duration = duration;
	}

	public Uri getUri() {
		return ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, this.idSong);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(idGenre);
		dest.writeString(genre);

		dest.writeLong(idArtist);
		dest.writeString(artist);
		dest.writeByte((byte) (isVA ? 1 : 0));

		dest.writeLong(idAlbum);
		dest.writeString(album);
		dest.writeByte((byte) (isCompilation ? 1 : 0));

		dest.writeLong(idSong);
		dest.writeString(title);
		dest.writeInt(track);
		dest.writeLong(duration);
	}

	@Override
	public long getId() {
		return idSong;
	}
}

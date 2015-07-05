package nobody.sip.prots;

import android.os.Parcel;
import android.os.Parcelable;

public class SongWithExtras extends Song implements Parcelable {
	public int plays;
	public int rank;
	public long lastPlayed;

	public static final Parcelable.Creator<SongWithExtras> CREATOR = new Parcelable.Creator<SongWithExtras>() {

		@Override
		public SongWithExtras[] newArray(int size) {
			return new SongWithExtras[size];
		}

		@Override
		public SongWithExtras createFromParcel(Parcel source) {
			return new SongWithExtras(source);
		}
	};

	public SongWithExtras(Parcel source) {
		super(source);

		this.plays = source.readInt();
		this.rank = source.readInt();
		this.lastPlayed = source.readLong();
	}

	public SongWithExtras(Song song, int plays, int rank, long lastPlayed) {
		super(song.idArtist, song.artist, song.idAlbum, song.album, song.idSong, song.title, song.track, song.duration);

		this.plays = plays;
		this.rank = rank;
		this.lastPlayed = lastPlayed;
	}

	public SongWithExtras(long idArtist, String artist, long idAlbum, String album, long idSong, String title, int track,
			long duration, int plays, int rank, long lastPlayed) {
		super(idArtist, artist, idAlbum, album, idSong, title, track, duration);

		this.plays = plays;
		this.rank = rank;
		this.lastPlayed = lastPlayed;
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

		dest.writeInt(plays);
		dest.writeInt(rank);
		dest.writeLong(lastPlayed);
	}
}

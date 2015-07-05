package nobody.sip.prots;

import android.os.Parcel;
import android.os.Parcelable;

public class Album extends Artist implements Parcelable {
	public long idAlbum;
	public String album;
	public boolean isCompilation;

	public String arturi;

	public static final Parcelable.Creator<Album> CREATOR = new Parcelable.Creator<Album>() {

		@Override
		public Album[] newArray(int size) {
			return new Album[size];
		}

		@Override
		public Album createFromParcel(Parcel source) {
			return new Album(source);
		}
	};

	public Album(Parcel source) {
		super(source);

		this.idAlbum = source.readLong();
		this.album = source.readString();
		this.isCompilation = source.readByte() == 1;
	}

	public Album(Artist artist, long idAlbum, String album, boolean isCompilation) {
		super(artist.idArtist, artist.artist, isCompilation);

		this.idAlbum = idAlbum;
		this.album = album;
		this.isCompilation = isCompilation;

	}

	public Album(long idArtist, String artist, long idAlbum, String album, boolean isCompilation) {
		super(idArtist, artist, isCompilation);

		this.idAlbum = idAlbum;
		this.album = album;
		this.isCompilation = isCompilation;
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
	}

	@Override
	public long getId() {
		return idAlbum;
	}
}

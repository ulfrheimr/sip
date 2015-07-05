package nobody.sip.prots;

import nobody.sip.core.PlayerService;
import android.os.Parcel;
import android.os.Parcelable;

public class Artist extends Genre implements Parcelable {
	public long idArtist;
	public String artist;
	public boolean isVA;
	public Long[] albums;

	public static final Parcelable.Creator<Artist> CREATOR = new Parcelable.Creator<Artist>() {

		@Override
		public Artist[] newArray(int size) {
			return new Artist[size];
		}

		@Override
		public Artist createFromParcel(Parcel source) {
			return new Artist(source);
		}
	};

	public Artist(Parcel source) {
		super(source);

		this.idArtist = source.readLong();
		this.artist = source.readString();
		this.isVA = source.readByte() == 1;
	}

	public Artist(Genre gender, long idArtist, String artist, boolean isVA) {
		super(gender.idGenre, gender.genre);

		this.idArtist = idArtist;
		this.artist = artist;
		this.isVA = isVA;
	}

	public Artist(long idArtist, String artist, boolean isVA) {
		super(PlayerService.INVALID_ID_OR_POSITION, PlayerService.EMPTY_FIELD);

		this.idArtist = idArtist;
		this.artist = artist;
		this.isVA = isVA;
	}

	public void setAlbums(Long[] idAlbums) {
		this.albums = idAlbums;
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
	}

	@Override
	public long getId() {
		return idArtist;
	}
}

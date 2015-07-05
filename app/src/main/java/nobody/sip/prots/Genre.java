package nobody.sip.prots;

import android.os.Parcel;
import android.os.Parcelable;

public class Genre extends IDProt implements Parcelable {
	public long idGenre;
	public String genre;

	public static final Parcelable.Creator<Genre> CREATOR = new Parcelable.Creator<Genre>() {

		@Override
		public Genre[] newArray(int size) {
			return new Genre[size];
		}

		@Override
		public Genre createFromParcel(Parcel source) {
			return new Genre(source);
		}
	};

	public Genre(Parcel source) {
		this.idGenre = source.readLong();
		this.genre = source.readString();
	}

	public Genre(long idGenre, String genre) {
		this.idGenre = idGenre;
		this.genre = genre;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(idGenre);
		dest.writeString(genre);
	}

	@Override
	public long getId() {
		return idGenre;
	}

}

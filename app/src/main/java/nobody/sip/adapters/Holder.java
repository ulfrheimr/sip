package nobody.sip.adapters;

import nobody.sip.prots.views.MeasuredImageView;
import android.widget.ImageView;
import android.widget.TextView;

public class Holder {
	public interface HeaderHolder {
		public boolean isHeader();

		public void setAsHeader(boolean setIsHeader);

		public Object getHolderID();

		public void setHolderID(Object id);
	}

	public static class GenreHolder {
		ImageView art;
		ImageView options;
		TextView genre;
	}

	public static class BasicArtistHolder {
		ImageView options;
		TextView artist;
	}

	public static class ArtistHolder extends BasicArtistHolder {
		MeasuredImageView art;
	}

	public static class AlbumHolder extends ArtistHolder {
		TextView album;
	}

	public static class SongHolder extends ArtistHolder {
		TextView title;
		TextView duration;
	}

	public static class SongWithExtrasHolder extends SongHolder {
		TextView plays;
		ImageView rank;
		ImageView isPlaying;
	}

	public static class BasicSongHolder extends BasicArtistHolder {
		TextView track;
		TextView title;
	}

	public static class PlaylistHolder {
		ImageView playlistType;
		TextView name;
		TextView plays;
		ImageView rank;
		ImageView options;
	}
}

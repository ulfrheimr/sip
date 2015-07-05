package nobody.sip.prots.views;

import nobody.sip.core.PlayerService.FragmentItem;
import nobody.sip.prots.Album;
import nobody.sip.prots.ImageTemplate.ImageSource;

public class SongImage extends AlbumImage {

	@Override
	public String getImageID() {
		return FragmentItem.SONGS + "-" + ImageSource.ALBUM_ART + ":" + ((Album) getImageTag()).idAlbum;
	}

	public SongImage(Album album, CommonConfigObject configObject) {
		super(album, configObject);
	}

}

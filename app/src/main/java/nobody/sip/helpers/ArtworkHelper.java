package nobody.sip.helpers;

import android.util.Log;
import nobody.sip.prots.Album;
import nobody.sip.prots.CommonImage.CommonConfigObject;
import nobody.sip.prots.views.AlbumImage;
import nobody.sip.prots.views.MeasuredImageView;

public class ArtworkHelper {
	public static class AlbumArtworkLoader implements Runnable {
		private MeasuredImageView mView;
		private Album mAlbum;
		private final CommonConfigObject mConfig = new CommonConfigObject(true, false);

		public AlbumArtworkLoader(Album album, MeasuredImageView view) {
			mAlbum = album;
			mView = view;
		}

		@Override
		public synchronized void run() {
			final AlbumImage image = new AlbumImage(mAlbum, mConfig);
			mView.setImgDrawable(image).startImageDisplaying();
		}

	}
}

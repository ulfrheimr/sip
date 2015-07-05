package nobody.sip.prots.views;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import nobody.sip.core.PlayerService.FragmentItem;
import nobody.sip.helpers.ImageHelper;
import nobody.sip.prots.Album;
import nobody.sip.prots.CommonImage;
import nobody.sip.prots.ImageTemplate.ImageSource;

public class AlbumImage extends CommonImage {
	private final String TAG = "sip.helpers.ALBUM_IMAGE";
	private final Album mAlbum;
	
	public AlbumImage(Album album, CommonConfigObject configObject) {
		super(configObject);
		mAlbum = album;
	}

	@Override
	public String getImageID() {
		return FragmentItem.ALBUMS + "-" + ImageSource.ALBUM_ART + ":" + mAlbum.idAlbum;
	}

	@Override
	public WeakReference<Bitmap> createBitmapBuffer(Context context, CommonConfigObject config) {
		WeakReference<Bitmap> bitmapRef;
		Bitmap b = null;

		if (config.isCacheGetEnabled())
			b = getFromCache(context);

		if (b == null)
			b = ImageHelper.getInstance(context).getAlbumImage(mAlbum.idAlbum, mAlbum.arturi, getImageWidth(), getImageHeight());

		if (b != null && config != null) {

			if (config.isCachePutEnabled())
				cache(context, b);
		}

		bitmapRef = new WeakReference<Bitmap>(b);

		return bitmapRef;
	}

	@Override
	public Object getImageTag() {
		return mAlbum;
	}

}

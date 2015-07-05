package nobody.sip.prots.views;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import nobody.sip.helpers.ImageHelper;
import nobody.sip.prots.Album;

public class EffectAlbumImage extends AlbumImage {
	private final String TAG = "sip.helpers.ALBUM_IMAGE";
	private int mRatio = 2;

	public EffectAlbumImage(Album album, CommonConfigObject configObject) {
		super(album, configObject);
	}

	public EffectAlbumImage setBlurRatio(int ratio) {
		mRatio = ratio;
		return this;
	}

	@Override
	public WeakReference<Bitmap> createBitmapBuffer(Context context, CommonConfigObject config) {
		WeakReference<Bitmap> bitmapRef = null;
		Album album = (Album) getImageTag();
		Bitmap b = null;

		if (config.isCacheGetEnabled()) {
			b = getFromCache(context);
		}

		if (b == null)
			b = ImageHelper.getInstance(context).getAlbumImage(album.idAlbum, album.arturi, getImageWidth(), getImageHeight());

		if (b != null) {
			b = ImageHelper.getInstance(context).blurImage(b, mRatio);

			if (config.isCachePutEnabled())
				cache(context, b);

		}

		bitmapRef = new WeakReference<Bitmap>(b);

		return bitmapRef;
	}
}

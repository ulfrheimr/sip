package nobody.sip.prots;

import android.content.Context;
import android.graphics.Bitmap;

public class ImageTemplate {
	public enum ImageSource {
		ALBUM_ART
	}

	public interface ImageConfig {
		public ImageConfig cacheGet(Boolean get);

		public ImageConfig cachePut(Boolean put);

		public Boolean isCacheGetEnabled();

		public Boolean isCachePutEnabled();
	}

	public interface Image {
		public String getImageID();

		public Object getImageTag();

		public Bitmap getBitmap(Context context);

		public int getImageWidth();

		public int getImageHeight();

		public void setImageWidth(int w);

		public void setImageHeight(int h);

		public Bitmap getFromCache(Context context);

		public void cache(Context context, Bitmap b);
	}

}

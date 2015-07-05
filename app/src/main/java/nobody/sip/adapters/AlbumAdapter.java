package nobody.sip.adapters;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import nobody.sip.R;
import nobody.sip.adapters.Holder.AlbumHolder;
import nobody.sip.helpers.TestHelper;
import nobody.sip.prots.Album;
import nobody.sip.prots.CommonImage.CommonConfigObject;
import nobody.sip.prots.views.AlbumImage;
import nobody.sip.prots.views.AlbumImageView;

public class AlbumAdapter extends CommonAdapter<List<Album>> {
	private static final String TAG = "sip.adapters.ALBUM_ADAPTER";

	private boolean mIsStarted = false;

	private AlbumHolder mAlbumHolder;
	private AlbumListener mCallback;
	private boolean mIsLongAllow = true;
	private final CommonConfigObject mAlbumConfig = new CommonConfigObject(true, true);

	public AlbumAdapter(Context context, List<Album> data, OnCommonAdapterListener listener) {
		super(context, data, listener);

	}

	private class AlbumListener extends CommonAdapterListener<Album> implements OnClickListener, OnLongClickListener {

		public AlbumListener(OnCommonAdapterListener listener, Album item) {
			super(listener, item);
		}

		@Override
		public boolean onLongClick(View v) {
			if (mIsLongAllow)
				if (v.getId() == mAlbumHolder.options.getId())
					listener.onOptionsLongPressed(v, currentItem);
				else
					listener.onArtLongPressed(v, currentItem);

			return true;
		}

		@Override
		public void onClick(View v) {
			if (v.getId() == mAlbumHolder.options.getId())
				listener.onOptionsPressed(v, currentItem);
			else
				listener.onArtPressed(v, currentItem);
		}

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (position == 0 && !mIsStarted) {
			mIsStarted = true;
		}

		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.album_row, null);
			mAlbumHolder = new AlbumHolder();
			mAlbumHolder.art = (AlbumImageView) convertView.findViewById(R.id.alrow_art);
			mAlbumHolder.options = (ImageView) convertView.findViewById(R.id.alrow_options);

			mAlbumHolder.artist = (TextView) convertView.findViewById(R.id.alrow_artist);
			mAlbumHolder.album = (TextView) convertView.findViewById(R.id.alrow_album);

			convertView.setTag(mAlbumHolder);
		} else
			mAlbumHolder = (AlbumHolder) convertView.getTag();

		AlbumImage albumImage = new AlbumImage(data.get(position), mAlbumConfig);
		mAlbumHolder.art.setImgDrawable(albumImage).startImageDisplaying();

		mAlbumHolder.artist.setText(data.get(position).artist);
		mAlbumHolder.album.setText(data.get(position).album);

		mCallback = new AlbumListener(listener, data.get(position));
		mAlbumHolder.art.setOnLongClickListener(mCallback);
		mAlbumHolder.art.setOnClickListener(mCallback);
		mAlbumHolder.options.setOnClickListener(mCallback);
		mAlbumHolder.options.setOnLongClickListener(mCallback);
		convertView.setOnLongClickListener(mCallback);
		convertView.setOnClickListener(mCallback);

		return convertView;
	}

	public void disallowLongClick() {
		mIsLongAllow = false;
	}

	public void allowLongClick() {
		mIsLongAllow = true;
	}
}

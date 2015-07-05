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
import nobody.sip.adapters.Holder.ArtistHolder;
import nobody.sip.prots.Artist;
import nobody.sip.prots.views.MeasuredImageView;

public class ArtistAdapter extends CommonAdapter<List<Artist>> {
	private static final String TAG = "sip.adapters.ALBUM_ADAPTER";

	private ArtistHolder mArtistHolder;
	private ArtistListener mCallback;

	public ArtistAdapter(Context context, List<Artist> data, OnCommonAdapterListener listener) {
		super(context, data, listener);
	}

	private class ArtistListener extends CommonAdapterListener<Artist> implements OnClickListener, OnLongClickListener {

		public ArtistListener(OnCommonAdapterListener listener, Artist item) {
			super(listener, item);
		}

		@Override
		public boolean onLongClick(View v) {
			if (v.getId() == mArtistHolder.options.getId())
				listener.onOptionsLongPressed(v, currentItem);
			else
				listener.onArtLongPressed(v, currentItem);

			return true;
		}

		@Override
		public void onClick(View v) {
			if (v.getId() == mArtistHolder.options.getId())
				listener.onOptionsPressed(v, currentItem);
			else
				listener.onArtPressed(v, currentItem);

		}

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.artist_row, null);

			mArtistHolder = new ArtistHolder();
			mArtistHolder.art = (MeasuredImageView) convertView.findViewById(R.id.arrow_art);
			mArtistHolder.artist = (TextView) convertView.findViewById(R.id.arrow_artist);
			mArtistHolder.options = (ImageView) convertView.findViewById(R.id.arrow_options);

			convertView.setTag(mArtistHolder);
		} else
			mArtistHolder = (ArtistHolder) convertView.getTag();

		mArtistHolder.artist.setText(data.get(position).artist);

		mCallback = new ArtistListener(listener, data.get(position));
		mArtistHolder.art.setOnClickListener(mCallback);
		mArtistHolder.art.setOnLongClickListener(mCallback);
		mArtistHolder.options.setOnClickListener(mCallback);
		mArtistHolder.options.setOnLongClickListener(mCallback);
		convertView.setOnClickListener(mCallback);
		convertView.setOnLongClickListener(mCallback);

		return convertView;
	}

}

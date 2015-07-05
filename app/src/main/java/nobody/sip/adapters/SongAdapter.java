package nobody.sip.adapters;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import nobody.sip.R;
import nobody.sip.adapters.Holder.SongHolder;
import nobody.sip.core.SongUtilities;
import nobody.sip.prots.CommonImage.CommonConfigObject;
import nobody.sip.prots.Album;
import nobody.sip.prots.Song;
import nobody.sip.prots.views.AlbumImage;
import nobody.sip.prots.views.AlbumImageView;
import nobody.sip.prots.views.MeasuredImageView;
import nobody.sip.prots.views.SongImage;

public class SongAdapter extends CommonAdapter<List<Song>> {
	private static final String TAG = "sip.adapters.SONG_ADAPTER";
	private LinkedHashSet<Song> mHashSet = new LinkedHashSet<Song>();
	private final CommonConfigObject mConfig = new CommonConfigObject(true, true);

	private SongHolder mSongHolder;
	private SongListener mCallback;

	public SongAdapter(Context context, List<Song> data, OnCommonAdapterListener listener) {
		super(context, data, listener);
	}

	private class SongListener extends CommonAdapterListener<Song> implements OnClickListener, OnLongClickListener {
		private int mPosition;

		public SongListener(OnCommonAdapterListener listener, Song item, int position) {
			super(listener, item);
			mPosition = position;
		}

		@Override
		public boolean onLongClick(View v) {
			if (v.getId() == mSongHolder.art.getId())
				innerListener.onArtLongPressed(v, currentItem);
			else if (v.getId() == mSongHolder.options.getId())
				innerListener.onOptionsLongPressed(v, currentItem);
			else
				innerListener.onRowLongPressed(v, mPosition);

			return true;
		}

		@Override
		public void onClick(View v) {
			if (v.getId() == mSongHolder.options.getId())
				innerListener.onOptionsPressed(v, currentItem);
			else
				innerListener.onRowPressed(v, currentItem);
		}

	}

	public List<Song> getSelectedSongs() {
		return new ArrayList<Song>(mHashSet);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.song_row, null);
			mSongHolder = new SongHolder();
			mSongHolder.art = (AlbumImageView) convertView.findViewById(R.id.srow_art);

			mSongHolder.options = (ImageView) convertView.findViewById(R.id.srow_options);
			mSongHolder.title = (TextView) convertView.findViewById(R.id.srow_title);
			mSongHolder.artist = (TextView) convertView.findViewById(R.id.srow_artist);
			mSongHolder.duration = (TextView) convertView.findViewById(R.id.srow_duration);

			convertView.setTag(mSongHolder);
		} else
			mSongHolder = (SongHolder) convertView.getTag();

		convertView.setBackgroundColor(context.getResources().getColor(R.color.non_selected_color));

		if (mHashSet.contains(data.get(position)))
			convertView.setBackgroundColor(context.getResources().getColor(R.color.selected_color));

		SongImage songImage = new SongImage((Album) data.get(position), mConfig);
		mSongHolder.art.setImgDrawable(songImage).startImageDisplaying();

		mSongHolder.title.setText(data.get(position).title);
		mSongHolder.artist.setText(data.get(position).artist);
		mSongHolder.duration.setText(SongUtilities.getInstance(context).millisToTime(data.get(position).duration));

		mCallback = new SongListener(listener, data.get(position), position);

		convertView.setOnClickListener(mCallback);
		convertView.setOnLongClickListener(mCallback);
		mSongHolder.options.setOnClickListener(mCallback);
		mSongHolder.options.setOnLongClickListener(mCallback);
		mSongHolder.art.setOnLongClickListener(mCallback);

		return convertView;
	}

	public void clearSelection() {
		mHashSet.clear();
	}

	public int toggleSelection(View v, Song song) {
		if (mHashSet.contains(song)) {
			mHashSet.remove(song);
			v.setBackgroundColor(context.getResources().getColor(R.color.non_selected_color));
		} else {
			mHashSet.add(song);
			v.setBackgroundColor(context.getResources().getColor(R.color.selected_color));
		}

		return mHashSet.size();
	}
}

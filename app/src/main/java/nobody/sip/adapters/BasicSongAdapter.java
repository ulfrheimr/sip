package nobody.sip.adapters;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import nobody.sip.R;
import nobody.sip.adapters.Holder.BasicSongHolder;
import nobody.sip.prots.Song;

public class BasicSongAdapter extends CommonAdapter<List<Song>> {
	private final String TAG = "sip.adapters.BASIC_SONG_ADAPTER";
	private BasicSongHolder mSongHolder = new BasicSongHolder();
	private HashMap<Long, Integer> mHeaderIDs = new HashMap<Long, Integer>();
	private int mCurrDisk = 1;

	public BasicSongAdapter(Context context, List<Song> data, OnCommonAdapterListener listener) {
		super(context, data, listener);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (position == 0 || checkHeader(data.get(position).track))
			mHeaderIDs.put(data.get(position).idSong, mCurrDisk++);

		if (mHeaderIDs.containsKey(data.get(position).idSong))
			convertView = null;

		if (convertView != null)
			if (convertView.getTag(R.id.bsrow_options) != null)
				convertView = null;

		if (convertView == null) {
			mSongHolder = new BasicSongHolder();

			if (mHeaderIDs.containsKey((Long) data.get(position).idSong)) {
				convertView = layoutInflater.inflate(R.layout.song_basic_row_header, null);
				convertView.setTag(R.id.bsrow_options, new Object());
			} else
				convertView = layoutInflater.inflate(R.layout.song_basic_row, null);

			mSongHolder.track = (TextView) convertView.findViewById(R.id.bsrow_track);
			mSongHolder.artist = (TextView) convertView.findViewById(R.id.bsrow_artist);
			mSongHolder.title = (TextView) convertView.findViewById(R.id.bsrow_title);

			mSongHolder.options = (ImageView) convertView.findViewById(R.id.bsrow_options);

			convertView.setTag(mSongHolder);
		} else
			mSongHolder = (BasicSongHolder) convertView.getTag();

		mSongHolder.track.setText(data.get(position).track + "");
		mSongHolder.artist.setText(data.get(position).artist);
		mSongHolder.title.setText(data.get(position).title);

		return convertView;
	}

	private boolean checkHeader(int track) {
		String val = String.valueOf(track);

		if (track > 1000) {
			if (val.endsWith("001"))
				return true;
		} else {
			if (val.endsWith("1"))
				return true;
		}

		return false;
	}
}

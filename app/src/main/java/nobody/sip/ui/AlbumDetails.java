package nobody.sip.ui;

import java.util.List;

import nobody.sip.R;
import nobody.sip.adapters.BasicSongAdapter;
import nobody.sip.adapters.OnCommonAdapterListener;
import nobody.sip.core.PlayerService;
import nobody.sip.core.PlayerService.ItemType;
import nobody.sip.helpers.LoaderHelper;
import nobody.sip.prots.Album;
import nobody.sip.prots.Song;
import nobody.sip.ui.commons.CommonDetailsFragment;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class AlbumDetails extends CommonDetailsFragment implements LoaderCallbacks<List<Song>>, OnCommonAdapterListener {
	private final String TAG = "sip.ui.ALBUM_DETAILS";

	private ListView mDetailsList;
	private LoaderHelper.SongLoader mLoader;
	private BasicSongAdapter mSongAdapter;

	private Album mAlbum;

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.album_details, container, false);
		super.setExtrasMenuType(ItemType.ALBUM);
		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mAlbum = (Album) getArguments().getParcelable(PlayerService.EXTRA_DETAILS_SENDER);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		init();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
	}

	/* LoaderCallbacks implementation */
	@Override
	public Loader<List<Song>> onCreateLoader(int id, Bundle args) {
		mLoader = new LoaderHelper.SongLoader(getActivity(), mAlbum.idAlbum);
		return mLoader;
	}

	@Override
	public void onLoadFinished(Loader<List<Song>> loader, List<Song> data) {
		mSongAdapter = new BasicSongAdapter(getActivity(), data, this);
		mDetailsList.setAdapter(mSongAdapter);
	}

	@Override
	public void onLoaderReset(Loader<List<Song>> loader) {
		mDetailsList.setAdapter(null);
	}

	/* Common Fragment implementation */
	@Override
	public void stopCommonTouch() {

	}

	public void restartCommonTouch() {

	}

	@Override
	public boolean isAllowToHandlePanel(int x, int y) {
		return false;
	}

	@Override
	public void closeFragment() {

	}

	/* OnCommonAdapterListener implementation */
	@Override
	public void onArtPressed(View view, Object item) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onArtLongPressed(View view, Object item) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onOptionsPressed(View view, Object item) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onOptionsLongPressed(View view, Object item) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRowPressed(View view, Object item) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRowLongPressed(View view, Object item) {
		// TODO Auto-generated method stub

	}

	private void init() {
		mDetailsList = (ListView) getActivity().findViewById(R.id.album_det_grid_view);
		getLoaderManager().initLoader(0, null, this).forceLoad();
	}
}

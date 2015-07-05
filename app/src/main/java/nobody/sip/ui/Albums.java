package nobody.sip.ui;

import java.util.List;

import nobody.sip.R;
import nobody.sip.adapters.AlbumAdapter;
import nobody.sip.adapters.OnCommonAdapterListener;
import nobody.sip.core.PlayerService.DetailsFragmentType;
import nobody.sip.core.PlayerService.ItemType;
import nobody.sip.helpers.LoaderHelper;
import nobody.sip.prots.Album;
import nobody.sip.ui.commons.CommonNavigationFragment;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

public class Albums extends CommonNavigationFragment implements LoaderCallbacks<List<Album>>, OnCommonAdapterListener {
	private static final String TAG = "sip.ui.ALBUMS";
	private AlbumAdapter mAlbumAdapter;
	private GridView mGridView;

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.albums, container, false);
		super.setExtrasMenuType(ItemType.ALBUM);
		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		init();
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
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

	@Override
	public Loader<List<Album>> onCreateLoader(int id, Bundle args) {
		return new LoaderHelper.AlbumLoader(getActivity());
	}

	@Override
	public void onLoadFinished(Loader<List<Album>> loader, List<Album> albums) {
		mAlbumAdapter = new AlbumAdapter(getActivity(), albums, this);
		mGridView.setAdapter(mAlbumAdapter);
	}

	@Override
	public void onLoaderReset(Loader<List<Album>> arg0) {
		// TODO Auto-generated method stub

	}

	/* CommonFragment implementation */
	@Override
	public void stopCommonTouch() {
		mGridView.requestDisallowInterceptTouchEvent(true);
		mGridView.setEnabled(false);
		mAlbumAdapter.disallowLongClick();
	}

	public void restartCommonTouch() {
		if (mAlbumAdapter != null)
			mAlbumAdapter.allowLongClick();

		if (mGridView != null)
			mGridView.setEnabled(true);
	}

	@Override
	public boolean isAllowToHandlePanel(int x, int y) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void closeFragment() {

	}

	/* OnCommonAdapterListener implementation */
	@Override
	public void onArtPressed(View view, Object item) {
		extrasMenu.forceHide();
		super.sendToDetails(DetailsFragmentType.ALBUM_DETAILS, item);

	}

	@Override
	public void onArtLongPressed(View view, Object item) {
		extrasMenu.forceHide();
		super.addAndClear(item, ItemType.ALBUM);
	}

	@Override
	public void onOptionsPressed(View view, Object item) {
		extrasMenu.forceHide();
		super.addFirst(item, ItemType.ALBUM);
	}

	@Override
	public void onOptionsLongPressed(View view, Object item) {
		extrasMenu.forceHide();
		extrasMenu.toggle(view, item);
	}

	@Override
	public void onRowPressed(View view, Object item) {
	}

	@Override
	public void onRowLongPressed(View view, Object item) {
	}

	private void init() {
		mGridView = (GridView) getActivity().findViewById(R.id.albums_grid_view);
		// mGridView.setOnTouchListener(this);
		getLoaderManager().initLoader(0, null, this).forceLoad();
	}
}

package nobody.sip.ui;

import java.util.List;

import nobody.sip.R;
import nobody.sip.adapters.AlbumAdapter;
import nobody.sip.adapters.ArtistAdapter;
import nobody.sip.adapters.OnCommonAdapterListener;
import nobody.sip.core.PlayerService;
import nobody.sip.core.PlaylistUtilities;
import nobody.sip.core.PlayerService.DetailsFragmentType;
import nobody.sip.core.PlayerService.ItemType;
import nobody.sip.helpers.LoaderHelper;
import nobody.sip.prots.Artist;
import nobody.sip.ui.commons.CommonNavigationFragment;
import nobody.sip.ui.commons.OnCommonNavigationFragmentListener;
import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;

public class Artists extends CommonNavigationFragment implements LoaderCallbacks<List<Artist>>, OnCommonAdapterListener {
	private static final String TAG = "sip.ui.ARTISTS";
	private ArtistAdapter mArtistAdapter;
	private GridView mGridView;

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.artists, container, false);
		super.setExtrasMenuType(ItemType.ARTIST);
		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
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
	public Loader<List<Artist>> onCreateLoader(int id, Bundle args) {
		return new LoaderHelper.ArtistLoader(getActivity());
	}

	@Override
	public void onLoadFinished(Loader<List<Artist>> loader, List<Artist> artists) {
		mArtistAdapter = new ArtistAdapter(getActivity(), artists, this);
		mGridView.setAdapter(mArtistAdapter);

	}

	@Override
	public void onLoaderReset(Loader<List<Artist>> arg0) {
		// TODO Auto-generated method stub

	}

	/* Common Fragment implementation */
	@Override
	public void stopCommonTouch() {

	}

	public void restartCommonTouch() {

	}

	@Override
	public boolean isAllowToHandlePanel(int x, int y) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void closeFragment() {
		
	}

	/* OnCommonAdapterListener implementation */
	@Override
	public void onArtPressed(View view, Object item) {
		extrasMenu.forceHide();
		super.addAndClear(item, ItemType.ARTIST);
	}

	@Override
	public void onArtLongPressed(View view, Object item) {
		extrasMenu.forceHide();
		super.sendToDetails(DetailsFragmentType.ARTIST_DETAILS, item);
	}

	@Override
	public void onOptionsPressed(View view, Object item) {
		extrasMenu.forceHide();
		super.addFirst(item, ItemType.ARTIST);
	}

	@Override
	public void onOptionsLongPressed(View view, Object item) {
		extrasMenu.toggle(view, item);
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
		mGridView = (GridView) getActivity().findViewById(R.id.artists_grid_view);
		((MarginLayoutParams) mGridView.getLayoutParams()).setMargins((int) (ViewConfiguration.get(getActivity())
				.getScaledTouchSlop() * 1.7), 0, 0, 0);

		// mListView.setOnTouchListener(this);
		getLoaderManager().initLoader(0, null, this).forceLoad();
	}
}

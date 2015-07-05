package nobody.sip.ui;

import java.util.List;

import nobody.sip.R;
import nobody.sip.adapters.CommonAdapter;
import nobody.sip.adapters.OnCommonAdapterListener;
import nobody.sip.adapters.SongAdapter;
import nobody.sip.core.PlayerService;
import nobody.sip.core.PlayerService.DetailsFragmentType;
import nobody.sip.core.PlayerService.ItemType;
import nobody.sip.core.SongSelector.AddMode;
import nobody.sip.helpers.LoaderHelper;
import nobody.sip.prots.Album;
import nobody.sip.prots.Genre;
import nobody.sip.prots.IDProt;
import nobody.sip.prots.Song;
import nobody.sip.ui.MultiSelect.OnMultiSelectListener;
import nobody.sip.ui.ToggleView.ToggleViewVisibility;
import nobody.sip.ui.commons.CommonNavigationFragment;
import nobody.sip.ui.commons.OnCommonNavigationFragmentListener;
import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

public class Songs extends CommonNavigationFragment implements LoaderCallbacks<List<Song>>, OnCommonAdapterListener,
		OnMultiSelectListener {
	private static final String TAG = "sip.ui.SONGS";
	private SongAdapter mSongAdapter;

	private ListView mListView;
	private MultiSelect mMultiSelect;

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.multiselect, container, false);
		super.setExtrasMenuType(ItemType.SONG);
		mMultiSelect = (MultiSelect) view;
		mMultiSelect.setOnMultiSelectListener(this);
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
	public Loader<List<Song>> onCreateLoader(int id, Bundle args) {
		return new LoaderHelper.SongLoader(getActivity());
	}

	@Override
	public void onLoadFinished(Loader<List<Song>> songsLoader, List<Song> songs) {
		mSongAdapter = new SongAdapter(getActivity(), songs, this);
		mListView.setAdapter(mSongAdapter);
	}

	@Override
	public void onLoaderReset(Loader<List<Song>> arg0) {
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
		super.sendToDetails(DetailsFragmentType.ALBUM_DETAILS, item);
	}

	@Override
	public void onOptionsPressed(View view, Object item) {
		extrasMenu.forceHide();
		super.addFirst(item, ItemType.SONG);
	}

	@Override
	public void onOptionsLongPressed(View view, Object item) {
		extrasMenu.toggle(view, item);
	}

	@Override
	public void onRowPressed(View view, Object item) {
		extrasMenu.forceHide();
		super.addFirst(item, ItemType.SONG);
	}

	@Override
	public void onRowLongPressed(View view, Object item) {
		extrasMenu.forceHide();

		mMultiSelect.setItemCount(mSongAdapter.toggleSelection(
				mListView.getChildAt((Integer) item - mListView.getFirstVisiblePosition()),
				((Song) mListView.getItemAtPosition((Integer) item))));
	}

	// @Override
	// public boolean reportScrolling() {
	// // TODO Auto-generated method stub
	// return false;
	// }

	/* OnMultiselectListener implementation */
	@Override
	public void onOKPressed() {
		switch (mMultiSelect.bannerState) {
		case ADDING:
			super.addSongList(mSongAdapter.getSelectedSongs(), mMultiSelect.addMode);
			break;

		case PLAYLIST:

			break;
		}

		mSongAdapter.clearSelection();
		setAllUnselected();

	}

	private void init() {
		View v = getActivity().getLayoutInflater().inflate(R.layout.songs, mMultiSelect.multiContainer, true);
		mListView = (ListView) v.findViewById(R.id.songs_list_view);
		// mListView.setOnTouchListener(this);
		getLoaderManager().initLoader(0, null, this).forceLoad();
	}

	private void setAllUnselected() {
		for (int i = 0; i <= (mListView.getLastVisiblePosition() - mListView.getFirstVisiblePosition()); i++)
			mListView.getChildAt(i).setBackgroundColor(getActivity().getResources().getColor(R.color.non_selected_color));

	}
}

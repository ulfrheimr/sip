package nobody.sip.ui.commons;

import java.util.List;

import nobody.sip.core.PlayerService;
import nobody.sip.core.PlayerService.DetailsFragmentType;
import nobody.sip.core.PlayerService.ItemType;
import nobody.sip.core.SongSelector.AddMode;
import nobody.sip.prots.Song;
import nobody.sip.ui.ExtraOptionsMenu;
import nobody.sip.ui.ExtraOptionsMenu.OnExtraOptionsMenuListener;
import android.app.Activity;
import android.os.Bundle;

public abstract class CommonNavigationFragment extends CommonFragment implements OnExtraOptionsMenuListener {
	public ExtraOptionsMenu extrasMenu;
	private PlayerService.ItemType mExtrasMenuType = ItemType.SONG;

	private OnCommonNavigationFragmentListener mListener;

	public void setOnCommonNavigationFragmentListener(OnCommonNavigationFragmentListener listener) {
		super.setOnCommonFragmentListener((OnCommonFragmentListener) mListener);
		mListener = listener;
	}

	public void setExtrasMenuType(PlayerService.ItemType type) {
		mExtrasMenuType = type;
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

	@Override
	public void onStart() {
		super.onStart();
		init();
	}

	@Override
	public void onStop() {
		super.onStop();

		extrasMenu.stopCommonView();
		extrasMenu.setOnExtraOptionsMenuListener(null);
	}

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
	}

	/* CommonFragment implementation */
	@Override
	public abstract void stopCommonTouch();

	@Override
	public abstract void restartCommonTouch();

	@Override
	public abstract boolean isAllowToHandlePanel(int x, int y);

	/* OnExtraOptionsMenuListener implementation */
	@Override
	public ItemType onRequestFragmentItem() {
		return mExtrasMenuType;
	}

	@Override
	public void onNextTopSelected(Object item, ItemType type) {
		addNextTo(item, type);
	}

	@Override
	public void onAtLastSelected(Object item, ItemType type) {
		addAtLast(item, type);
	}

	@Override
	public void onEditSelected(Object item, ItemType type) {
		super.sendToEdit(item, type);
	}

	private void init() {
		try {
			if (extrasMenu == null)
				extrasMenu = new ExtraOptionsMenu(getActivity());

			extrasMenu.setOnExtraOptionsMenuListener(this);
			extrasMenu.setRoot(mySelf);

		} catch (Exception ex) {
		}
	}

	public void addSongList(List<Song> songs, AddMode addMode) {
		if (mListener != null)
			mListener.onAddingSongs(songs, addMode);
	}

	public void addAndClear(Object item, ItemType type) {
		if (mListener != null)
			mListener.onAddingItem(type, item, AddMode.CLEAR);
	}

	public void addFirst(Object item, ItemType type) {
		if (mListener != null)
			mListener.onAddingItem(type, item, AddMode.FIRST);
	}

	public void addNextTo(Object item, ItemType type) {
		if (mListener != null)
			mListener.onAddingItem(type, item, AddMode.NEXT_TO);
	}

	public void addAtLast(Object item, ItemType type) {
		if (mListener != null)
			mListener.onAddingItem(type, item, AddMode.AT_LAST);
	}

	public void sendToDetails(DetailsFragmentType detailsFragment, Object item) {
		if (mListener != null)
			mListener.onSendingToDetails(detailsFragment, item);
	}
}

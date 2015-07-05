package nobody.sip.ui;

import nobody.sip.R;
import nobody.sip.core.PlayerService;
import nobody.sip.core.PlayerService.DetailsFragmentType;
import nobody.sip.core.PlayerService.ItemType;
import nobody.sip.ui.commons.CommonDetailsFragment;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class PlaylistDetails extends CommonDetailsFragment implements OnClickListener {
	private Button mTest;
	private Button mTestNavigation;

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.playlist_details, container, false);
		super.setExtrasMenuType(ItemType.PLAYLIST);
		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
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
	public void onClick(View v) {
		// if (v == mTest)
		// super.extrasMenu.toggle(v, PlayerService.INVALID_ID_OR_POSITION);
		// else
		// super.navigate(DetailsFragmentType.GENRE_DETAILS,
		// PlayerService.INVALID_ID_OR_POSITION, false);
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

	private void init() {
		mTest = (Button) getActivity().findViewById(R.id.playlists_det_test);
		mTestNavigation = (Button) getActivity().findViewById(R.id.playlists_det_nav_test);

		mTestNavigation.setOnClickListener(this);
		mTest.setOnClickListener(this);
	}
}

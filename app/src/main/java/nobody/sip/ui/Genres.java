package nobody.sip.ui;

import nobody.sip.R;
import nobody.sip.core.PlayerService.ItemType;
import nobody.sip.ui.commons.CommonNavigationFragment;
import nobody.sip.ui.commons.OnCommonNavigationFragmentListener;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Genres extends CommonNavigationFragment implements OnClickListener {
	private Button mTest;
	private Button mTestNavigation;

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.genres, container, false);
		super.setExtrasMenuType(ItemType.GENRE);
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
	public void onClick(View v) {
		// if (v == mTest)
		// super.extrasMenu.toggle(v, PlayerService.INVALID_ID_OR_POSITION);
		// else if (v == mTestNavigation)
		// super.sendToDetails(DetailsFragmentType.GENRE_DETAILS,
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
		mTest = (Button) getActivity().findViewById(R.id.genres_test);
		mTest.setOnClickListener(this);

		mTestNavigation = (Button) getActivity().findViewById(R.id.genres_nav_test);
		mTestNavigation.setOnClickListener(this);
	}

}

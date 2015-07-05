package nobody.sip.ui.commons;

import nobody.sip.core.PlayerService.DetailsFragmentType;
import android.app.Activity;
import android.os.Bundle;
import android.widget.AbsListView;

public abstract class CommonDetailsFragment extends CommonNavigationFragment {
	private OnCommonDetailsFragmentListener mListener;

	public void setOnCommonDetailsFragmentListener(OnCommonDetailsFragmentListener listener) {
		this.mListener = listener;
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

//	public void navigate(DetailsFragmentType fragment, Object item) {
//		if (mListener != null)
//			mListener.onRequestNavigation(fragment, item);
//	}

	public abstract void stopCommonTouch();

	public abstract void restartCommonTouch();

	public abstract boolean isAllowToHandlePanel(int x, int y);
}

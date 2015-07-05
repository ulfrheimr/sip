package nobody.sip.ui.commons;

import nobody.sip.core.PlayerService.ItemType;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.ViewGroup;

public abstract class CommonFragment extends Fragment {
	protected ViewGroup mySelf;
	private OnCommonFragmentListener mListener;

	protected void setOnCommonFragmentListener(OnCommonFragmentListener listener) {
		mListener = listener;
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
		mySelf = (ViewGroup) getView();
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
	}

	public void sendToEdit(Object item, ItemType type) {
		if (mListener != null)
			mListener.onSendToEdit(type, item);
	}

	public void fragmentMove(int widthSpan) {
		if (mListener != null)
			mListener.onFragmentMoving(widthSpan);
	}

	public abstract void stopCommonTouch();

	public abstract void restartCommonTouch();

	public abstract boolean isAllowToHandlePanel(int x, int y);

	public abstract void closeFragment();
}

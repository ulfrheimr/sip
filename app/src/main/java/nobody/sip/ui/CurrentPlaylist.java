package nobody.sip.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import nobody.sip.R;
import nobody.sip.core.PlayerService.PlayerFragmentItem;
import nobody.sip.core.PlayerService.PlayerState;
import nobody.sip.prots.SongWithExtras;
import nobody.sip.ui.commons.CommonPlayerFragment;

public class CurrentPlaylist extends CommonPlayerFragment {
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.current_playlist, container, false);
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

	/* CommonPlayerFragment implementation */
	@Override
	public void updateFragmentInterface(SongWithExtras currentSong, PlayerState playerState, int currentPosition) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleStartOpening() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void playlistHasChanged() {
		// TODO Auto-generated method stub

	}

	@Override
	public void closeFragment() {

	}

	private void init() {
		Button b = (Button) getActivity().findViewById(R.id.curr_playlist_texst);
		b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				navigateToPlayerFragment(PlayerFragmentItem.DETAILS_PLAYER, null);
			}
		});
	}

}

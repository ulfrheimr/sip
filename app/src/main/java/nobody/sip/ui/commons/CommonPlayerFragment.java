package nobody.sip.ui.commons;

import java.util.List;

import nobody.sip.core.PlayerService;
import nobody.sip.core.PlayerService.PlayerFragmentItem;
import nobody.sip.core.PlayerService.PlayerState;
import nobody.sip.prots.Song;
import nobody.sip.prots.SongWithExtras;
import nobody.sip.ui.ToggleView.ToggleViewVisibility;

public abstract class CommonPlayerFragment extends CommonFragment {

	private OnCommonPlayerFragmentListener mListener;

	public OnCommonPlayerFragmentListener getListener() {
		return mListener;
	}

	public int getCurrentPosition() {
		if (mListener != null)
			return mListener.onRequestCurrentPosition();

		return PlayerService.INVALID_ID_OR_POSITION;
	}

	public SongWithExtras getSongAtPosition(int position) {
		if (mListener != null)
			return mListener.onRequestSongAtPosition(position);

		return null;
	}

	public List<Song> getCurrentPlaylist() {
		if (mListener != null)
			return mListener.onRequestCurrentPlaylist();

		return null;
	}

	public PlayerState getPlayerState() {
		if (mListener != null)
			return mListener.onRequestPlayerState();

		return PlayerState.STOPPED;
	}

	public int getCurrentTime() {
		if (mListener != null)
			return mListener.onRequestCurrentTime();

		return PlayerService.INVALID_ID_OR_POSITION;
	}

	public ToggleViewVisibility getOwnVisibility() {
		if (mListener != null)
			return mListener.onRequestPlayerFragmentVisibility();

		return ToggleViewVisibility.HIDDEN;
	}

	public void setOnCommonPlayerFragmentListener(OnCommonPlayerFragmentListener listener) {
		super.setOnCommonFragmentListener((OnCommonFragmentListener) mListener);
		mListener = listener;
	}

	@Override
	public void stopCommonTouch() {
	}

	@Override
	public void restartCommonTouch() {
	}

	@Override
	public boolean isAllowToHandlePanel(int x, int y) {
		return false;
	}

	public void navigateToPlayerFragment(PlayerFragmentItem item, Object extras) {
		if (mListener != null)
			mListener.onRequestPlayerNavigation(item, extras);
	}

	public void reportSelectedTime(int time) {
		if (mListener != null)
			mListener.onReportSelectedTime(time);
	}

	public abstract void handleStartOpening();

	public abstract void playlistHasChanged();

	public abstract void updateFragmentInterface(SongWithExtras currentSong, PlayerState playerState, int currentPosition);
}

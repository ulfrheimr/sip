package nobody.sip.ui.commons;

import java.util.List;

import nobody.sip.core.PlayerService.PlayerFragmentItem;
import nobody.sip.core.PlayerService.PlayerState;
import nobody.sip.prots.Song;
import nobody.sip.prots.SongWithExtras;
import nobody.sip.ui.ToggleView.ToggleViewVisibility;

public interface OnCommonPlayerFragmentListener extends OnCommonFragmentListener {
	public void onRequestPlayerNavigation(PlayerFragmentItem item, Object extras);

	public List<Song> onRequestCurrentPlaylist();

	public SongWithExtras onRequestSongAtPosition(int position);

	public int onRequestCurrentPosition();

	public PlayerState onRequestPlayerState();

	public ToggleViewVisibility onRequestPlayerFragmentVisibility();

	public int onRequestCurrentTime();

	public void onReportSelectedTime(int time);
}

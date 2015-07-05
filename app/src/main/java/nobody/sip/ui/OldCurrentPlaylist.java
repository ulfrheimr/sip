package nobody.sip.ui;

import nobody.sip.R;
import nobody.sip.core.PlayerService.PlayerState;
import nobody.sip.prots.SongWithExtras;
import nobody.sip.ui.CurrentPlaylistBanner.OnCurrentPlaylistBannerListener;
import nobody.sip.ui.LayoutWithBanner.OnLayoutWithBannerListener;
import nobody.sip.ui.ToggleView.ToggleViewVisibility;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

public class OldCurrentPlaylist {
	private final String TAG = "sip.ui.CURRENT_PLAYLIST";
	private final int SEEKBAR_UPDATE_TIME = 100;

	private LayoutWithBanner mBannerLayout;
	private CurrentPlaylistBanner mBanner;

	private Handler mHandler = new Handler();

	// public int[] getBannerSize() {
	// return new int[] { mBannerLayout.NORMAL_SIZE,
	// mBannerLayout.CONTRACTED_SIZE };
	// }
	//
	// public int getBannerContractedSize() {
	// return mBannerLayout.CONTRACTED_SIZE;
	// }
	//
	// @Override
	// protected void onCreate(Bundle savedInstanceState) {
	// super.onCreate(savedInstanceState);
	//
	// mBannerLayout = (LayoutWithBanner)
	// getLayoutInflater().inflate(R.layout.layout_with_banner, null);
	// setContentView(mBannerLayout);
	//
	// mBannerLayout.setOnLayoutWithBannerListener(this);
	// }
	//
	// @Override
	// protected void onStop() {
	// super.onStop();
	//
	// mBannerLayout.setOnLayoutWithBannerListener(null);
	// mBannerLayout.stopCommonView();
	// mHandler.removeCallbacksAndMessages(null);
	// mHandler = null;
	// }
	//
	// @Override
	// public void run() {
	// mBannerLayout.updateSeekBar(playerService.getCurrentTime());
	// mHandler.postDelayed(this, SEEKBAR_UPDATE_TIME);
	// }
	//
	// @Override
	// protected void onRestart() {
	// super.onRestart();
	// mHandler = new Handler();
	// mBannerLayout.setOnLayoutWithBannerListener(this);
	// }
	//
	// /* ServiceBroadcastActivity implementation */
	// @Override
	// public void updateInterface(SongWithExtras song, PlayerState playerState,
	// int currentPosition) {
	// mBannerLayout.setSeekBarSpecs((int) currentSong.duration);
	// if (playerState == PlayerState.PLAYING)
	// mHandler.post(this);
	//
	// mBanner.updateBanner(mBannerLayout.isExpanded(), currentSong);
	// }
	//
	// @Override
	// public void albumChanged(long idAlbum) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public void serviceConnected() {
	//
	// }
	//
	// @Override
	// public void serviceDisconnected() {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// /* OnLayoutWithBannerListener implementation */
	// @Override
	// public void onLayoutWithBannerBuilt() {
	// mBannerLayout.toggleVisibility = ToggleViewVisibility.SHOWN;
	// if (currentSong != null) {
	// mBannerLayout.setSeekBarSpecs((int) currentSong.duration);
	//
	// if (playerState == PlayerState.PLAYING)
	// mHandler.post(this);
	// }
	//
	// mBanner = (CurrentPlaylistBanner)
	// getLayoutInflater().inflate(R.layout.current_playlist_banner, null);
	// mBanner.setOnCurrentPlaylistBannerListener(this);
	// mBannerLayout.setBannerView(mBanner);
	// }
	//
	// @Override
	// public void onToggleTriggered() {
	// if (currentSong != null)
	// mBanner.updateBanner(mBannerLayout.isExpanded(), currentSong);
	// }
	//
	// /* OnCurrentPlaylistBannerListener implementation */
	// @Override
	// public void onCurrentPlaylistBannerBuilt() {
	// if (currentSong != null)
	// mBanner.updateBanner(mBannerLayout.isExpanded(), currentSong);
	// }
	//
	// @Override
	// public int[] onRequestSize() {
	// return getBannerSize();
	// }
	//
	// @Override
	// public void playlistHasChanged() {
	// // TODO Auto-generated method stub
	//
	// }

}

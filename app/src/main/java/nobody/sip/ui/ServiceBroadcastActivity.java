package nobody.sip.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import nobody.sip.BuildConfig;
import nobody.sip.core.PlayerService;
import nobody.sip.core.PlayerService.PlayerState;
import nobody.sip.core.SongUtilities;
import nobody.sip.prots.Song;
import nobody.sip.prots.SongWithExtras;

public abstract class ServiceBroadcastActivity extends Activity {
	private final String TAG = "sip.ui.BASE_ACTIVITY";
	private long mBackIDAlbum = PlayerService.INVALID_ID_OR_POSITION;

	public Song currentSong;
	public PlayerState playerState;
	public PlayerService playerService;

	private ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			serviceDisconnected();
			mServiceConnection = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			if (BuildConfig.DEBUG)
				Log.d(TAG, "Base service connected");

			playerService = ((PlayerService.BinderService) service).getService();

			// playerService.restorePlaylist();

			currentSong = playerService.getCurrentSong();
			playerState = playerService.playerState;

			if (currentSong != null)
				mBackIDAlbum = currentSong.idAlbum;

			serviceConnected();
		}
	};

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle extras = intent.getExtras();

			if (intent.getAction().equals(PlayerService.BROADCAST_PLAY_FILTER)) {
				currentSong = (Song) extras.get(PlayerService.EXTRA_CURRENT_SONG);
				playerState = (PlayerState) extras.get(PlayerService.EXTRA_PLAYER_STATE);

				if (currentSong != null) {
					if (BuildConfig.DEBUG)
						Log.d(TAG, "Base activity received -> " + currentSong.title + " -  " + currentSong.album
								+ ". Playerstate -> " + playerState);

					updateInterface(playerService.getCurrentSongWithExtras(), playerState, playerService.getCurrentPosition());

					if (mBackIDAlbum != currentSong.idAlbum) {
						albumChanged(currentSong.idAlbum);
						mBackIDAlbum = currentSong.idAlbum;
					}

				}
			} else if (intent.getAction().equals(PlayerService.BROADCAST_PLAYLIST_CHANGED)) {
				playlistHasChanged();
			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		IntentFilter filter = new IntentFilter();
		filter.addAction(PlayerService.BROADCAST_PLAY_FILTER);
		filter.addAction(PlayerService.BROADCAST_PLAYLIST_CHANGED);

		bindService(new Intent(this, PlayerService.class), mServiceConnection, BIND_AUTO_CREATE);
		LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, filter);

	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onRestart() {
		super.onRestart();

		bindService(new Intent(this, PlayerService.class), mServiceConnection, BIND_AUTO_CREATE);
		LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver,
				new IntentFilter(PlayerService.BROADCAST_PLAY_FILTER));
	}

	@Override
	protected void onStop() {
		super.onStop();
		stopActivity();
	}

	private void stopActivity() {
		unbindService(mServiceConnection);

		try {
			LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
		} catch (IllegalArgumentException e) {
			Log.d(TAG, "Error unsregistering");
		}
	}

	public SongWithExtras getCurrentSongWithExtras() {
		return playerService.getCurrentSongWithExtras();
	}

	public abstract void playlistHasChanged();

	public abstract void updateInterface(SongWithExtras song, PlayerState playerState, int currentPosition);

	public abstract void albumChanged(long idAlbum);

	public abstract void serviceConnected();

	public abstract void serviceDisconnected();
}

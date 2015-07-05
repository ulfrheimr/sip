package nobody.sip.helpers;

import java.lang.ref.WeakReference;

import nobody.sip.R;
import nobody.sip.core.PlayerService;
import nobody.sip.core.PlayerService.PlayerState;
import nobody.sip.prots.Album;
import nobody.sip.prots.Song;
import nobody.sip.prots.ImageTemplate.ImageSource;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RemoteViews;

public class PlayerNotification {
	private Context mContext;

	private PendingIntent mToggleIntent;
	private PendingIntent mForwardIntent;
	private PendingIntent mRewindIntent;
	private PendingIntent mCloseIntent;

	public Notification notification;
	public int idNotification = 10098;
	private int mSmallSize = PlayerService.INVALID_ID_OR_POSITION;
	private int mBigSize = PlayerService.INVALID_ID_OR_POSITION;

	private RemoteViews mSmallViews;
	private RemoteViews mBigViews;

	private RemoteViews getSmallContentView() {
		return null;
	}

	private RemoteViews getBigConentView() {
		return null;
	}

	private ArtUpdater mArtUpdater;
	private Bitmap mBitmapBuffer;

	public PlayerNotification(Context context) {
		mContext = context;

		mForwardIntent = PendingIntent.getService(mContext, 0, new Intent(PlayerService.FORWARD), 0);
		mToggleIntent = PendingIntent.getService(mContext, 0, new Intent(PlayerService.TOGGLE), 0);
		mRewindIntent = PendingIntent.getService(mContext, 0, new Intent(PlayerService.REWIND), 0);
		mCloseIntent = PendingIntent.getService(mContext, 0, new Intent(PlayerService.CLOSE), 0);

		mBitmapBuffer = ImageHelper.getInstance(mContext).getAlbumImage(25, "", 200, 200);

		create();
	}

	private class ArtUpdater extends Thread {
		private Album mAlbum;

		public ArtUpdater(Album album) {
			mAlbum = album;
		}

		@SuppressLint("NewApi")
		@Override
		public synchronized void start() {
			super.start();

			if (mSmallSize == PlayerService.INVALID_ID_OR_POSITION)
				mSmallSize = LayoutInflater.from(mContext).inflate(R.layout.notif_small, null).findViewById(R.id.s_notif_art)
						.getLayoutParams().width;

			if (mBigSize == PlayerService.INVALID_ID_OR_POSITION)
				mBigSize = LayoutInflater.from(mContext).inflate(R.layout.notif_big, null).findViewById(R.id.b_notif_art)
						.getLayoutParams().width;

			mBitmapBuffer = CacheHelper.getInstance(mContext).get(ImageSource.ALBUM_ART + ":" + mAlbum.idAlbum);

			if (mBitmapBuffer != null) {
				Log.d("NOTIF", "get From cache");
				mBitmapBuffer = ImageHelper.getInstance(mContext).fillSizingScale(mBitmapBuffer, mBigSize, mBigSize);
				notification.bigContentView.setImageViewBitmap(R.id.b_notif_art, mBitmapBuffer);

				mBitmapBuffer = ImageHelper.getInstance(mContext).fillSizingScale(mBitmapBuffer, mSmallSize, mSmallSize);
				notification.contentView.setImageViewBitmap(R.id.s_notif_art, mBitmapBuffer);

				return;
			}

			if (mBitmapBuffer == null) {
				Log.d("NOTIF", "not on cahce");
				mBitmapBuffer = ImageHelper.getInstance(mContext)
						.getAlbumImage(mAlbum.idAlbum, mAlbum.arturi, mBigSize, mBigSize);
				notification.bigContentView.setImageViewBitmap(R.id.b_notif_art, mBitmapBuffer);

				if (mBitmapBuffer != null) {
					mBitmapBuffer = ImageHelper.getInstance(mContext).fillSizingScale(mBitmapBuffer, mSmallSize, mSmallSize);
					notification.contentView.setImageViewBitmap(R.id.s_notif_art, mBitmapBuffer);
				}
			}

			try {
				this.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public void create() {
		Log.d("NOTIF", "create notification");
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN)
			createNotif();
		else
			createOldNotif();
	}

	@SuppressWarnings("deprecation")
	private void createOldNotif() {
		notification = new Notification.Builder(mContext).setSmallIcon(android.R.drawable.sym_call_outgoing).setLargeIcon(null)
				.setContentTitle(mContext.getResources().getString(R.string.playing)).setOngoing(true).getNotification();
	}

	@SuppressLint("NewApi")
	private void createNotif() {
		notification = new Notification.Builder(mContext).setOngoing(true).setSmallIcon(android.R.drawable.sym_call_outgoing)
				.build();
		notification.flags |= Notification.FLAG_NO_CLEAR;

		notification.contentView = getSmallNotification();
		notification.bigContentView = getBigNotification();

	}

	private RemoteViews getSmallNotification() {
		RemoteViews v = new RemoteViews(mContext.getPackageName(), R.layout.notif_small);

		mSmallSize = LayoutInflater.from(mContext).inflate(R.layout.notif_small, null).findViewById(R.id.s_notif_art)
				.getLayoutParams().width;

		v.setOnClickPendingIntent(R.id.s_notif_fwd, mForwardIntent);
		v.setOnClickPendingIntent(R.id.s_notif_toggle, mToggleIntent);
		v.setOnClickPendingIntent(R.id.s_notif_rwd, mRewindIntent);
		v.setOnClickPendingIntent(R.id.s_notif_close, mCloseIntent);

		return v;
	}

	@SuppressLint("NewApi")
	private RemoteViews getBigNotification() {
		RemoteViews v = new RemoteViews(mContext.getPackageName(), R.layout.notif_big);

		mBigSize = LayoutInflater.from(mContext).inflate(R.layout.notif_big, null).findViewById(R.id.b_notif_art)
				.getLayoutParams().width;

		v.setOnClickPendingIntent(R.id.b_notif_fwd, mForwardIntent);
		v.setOnClickPendingIntent(R.id.b_notif_toggle, mToggleIntent);
		v.setOnClickPendingIntent(R.id.b_notif_rwd, mRewindIntent);
		v.setOnClickPendingIntent(R.id.b_notif_close, mCloseIntent);

		return v;
	}

	@SuppressLint("NewApi")
	private synchronized void updateNew(Song song, PlayerState playerState) {
		notification.contentView.setTextViewText(R.id.s_notif_title, song.title + " - " + song.artist);

		notification.bigContentView.setTextViewText(R.id.b_notif_title, song.title);
		notification.bigContentView.setTextViewText(R.id.b_notif_artist, song.artist);

		Log.d("NOTIF", "update notif" + notification.bigContentView.toString());

		updateArt(song);
	}

	private void updateOld(Song song, PlayerState playerState) {

	}

	public void update(Song song, PlayerState playerState) {
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN)
			updateNew(song, playerState);
		else
			updateOld(song, playerState);
	}

	@SuppressLint("NewApi")
	private void updateArt(Album mAlbum) {
		if (mSmallSize == PlayerService.INVALID_ID_OR_POSITION)
			mSmallSize = LayoutInflater.from(mContext).inflate(R.layout.notif_small, null).findViewById(R.id.s_notif_art)
					.getLayoutParams().width;

		if (mBigSize == PlayerService.INVALID_ID_OR_POSITION)
			mBigSize = LayoutInflater.from(mContext).inflate(R.layout.notif_big, null).findViewById(R.id.b_notif_art)
					.getLayoutParams().width;

//		final Bitmap b = ImageHelper.getInstance(mContext).getAlbumImage(mAlbum.idAlbum, mAlbum.arturi, 200, 200);
//
//		notification.bigContentView.setImageViewBitmap(R.id.b_notif_art, b);

		// mBitmapBuffer =
		// CacheHelper.getInstance(mContext).get(ImageSource.ALBUM_ART + ":" +
		// mAlbum.idAlbum);
		//
		// if (mBitmapBuffer != null) {
		// Log.d("NOTIF", "get From cache");
		// mBitmapBuffer =
		// ImageHelper.getInstance(mContext).fillSizingScale(mBitmapBuffer,
		// mBigSize, mBigSize);
		// notification.bigContentView.setImageViewBitmap(R.id.b_notif_art,
		// mBitmapBuffer);
		//
		// mBitmapBuffer =
		// ImageHelper.getInstance(mContext).fillSizingScale(mBitmapBuffer,
		// mSmallSize, mSmallSize);
		// notification.contentView.setImageViewBitmap(R.id.s_notif_art,
		// mBitmapBuffer);
		//
		// return;
		// }

		// Log.d("NOTIF", "not on cahce");
		// mBitmapBuffer =
		// ImageHelper.getInstance(mContext).getAlbumImage(mAlbum.idAlbum,
		// mAlbum.arturi, 800, 800);
		// notification.bigContentView.setImageViewBitmap(R.id.b_notif_art,
		// mBitmapBuffer);
		//
		// if (mBitmapBuffer != null) {
		// mBitmapBuffer =
		// ImageHelper.getInstance(mContext).fillSizingScale(mBitmapBuffer,
		// mSmallSize, mSmallSize);
		// notification.contentView.setImageViewBitmap(R.id.s_notif_art,
		// mBitmapBuffer);
		// }

	}
}

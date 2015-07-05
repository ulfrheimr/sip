package nobody.sip.ui;

import java.util.List;

import nobody.sip.R;
import nobody.sip.adapters.AlbumAdapter;
import nobody.sip.adapters.OnCommonAdapterListener;
import nobody.sip.helpers.LoaderHelper;
import nobody.sip.prots.Album;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;

public class Test extends Activity implements OnClickListener {

	private CurrentSongPlayer mPlayer;
	private View Nada;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.test);

		mPlayer = (CurrentSongPlayer) findViewById(R.id.test_sidenav_player);
		Nada = mPlayer.findViewById(R.id.fragment_container);
		Nada.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Log.d("TEXT", "Click");
		
	}

}

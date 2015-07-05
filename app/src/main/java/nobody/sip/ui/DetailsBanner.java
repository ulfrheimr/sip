package nobody.sip.ui;

import nobody.sip.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DetailsBanner extends RelativeLayout implements OnGlobalLayoutListener {
	private final String TAG = "sip.ui.DETAILS_BANNER";

	private ImageView mArt;
	private ImageView mEdit;

	public TextView detailsBannerTitle;

	private OnDetailsBannerListener mListener;

	public interface OnDetailsBannerListener {
		public void onDetailsBannerBuilt();
	}

	public void setOnBannerDetailsListener(OnDetailsBannerListener listener) {
		mListener = listener;
	}

	public DetailsBanner(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public DetailsBanner(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public DetailsBanner(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();

		mArt = (ImageView) findViewById(R.id.details_banner_art);
		mEdit = (ImageView) findViewById(R.id.details_banner_edit);

		detailsBannerTitle = (TextView) findViewById(R.id.details_banner_title);
		getViewTreeObserver().addOnGlobalLayoutListener(this);

		if (mListener != null)
			mListener.onDetailsBannerBuilt();
	}

	@Override
	public void onGlobalLayout() {
		// TODO Auto-generated method stub
	}

}

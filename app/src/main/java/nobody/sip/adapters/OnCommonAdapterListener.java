package nobody.sip.adapters;

import android.view.View;

public interface OnCommonAdapterListener {
	public void onArtPressed(View view, Object item);

	public void onArtLongPressed(View view, Object item);

	public void onOptionsPressed(View view, Object item);

	public void onOptionsLongPressed(View view, Object item);

	public void onRowPressed(View view, Object item);

	public void onRowLongPressed(View view, Object item);
}

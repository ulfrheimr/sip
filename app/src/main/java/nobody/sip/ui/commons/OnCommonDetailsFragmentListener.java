package nobody.sip.ui.commons;

import nobody.sip.core.PlayerService.DetailsFragmentType;

public interface OnCommonDetailsFragmentListener extends OnCommonNavigationFragmentListener {
	public void onRequestNavigation(DetailsFragmentType fragment, Object item);
}

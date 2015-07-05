package nobody.sip.ui.commons;

import nobody.sip.core.PlayerService.ItemType;

public interface OnCommonFragmentListener extends OnStopCommonView {

	public void onSendToEdit(ItemType itemType, Object item);

	public void onFragmentMoving(float widthValue);

	public void onFragmentStopMoving(boolean opening);
}

package nobody.sip.adapters;

import nobody.sip.prots.IDProt;

public class CommonAdapterListener<T extends IDProt> {
	public OnCommonAdapterListener innerListener;
	public T currentItem;

	public CommonAdapterListener(OnCommonAdapterListener listener, T item) {
		innerListener = listener;
		currentItem = item;
	}
}

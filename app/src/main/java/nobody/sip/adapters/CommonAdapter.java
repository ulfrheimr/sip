package nobody.sip.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import nobody.sip.core.PlayerService;
import nobody.sip.prots.IDProt;

public abstract class CommonAdapter<T extends List<? extends IDProt>> extends BaseAdapter {
	public Context context;
	public LayoutInflater layoutInflater;
	public T data;
	public OnCommonAdapterListener listener;

	public CommonAdapter(Context context, T data, OnCommonAdapterListener listener) {
		this.context = context;
		this.layoutInflater = LayoutInflater.from(context);
		this.data = data;
		this.listener = listener;
	}

	@Override
	public int getCount() {
		if (data != null)
			return data.size();
		else
			return PlayerService.INVALID_ID_OR_POSITION;
	}

	@Override
	public Object getItem(int position) {
		if (data != null)
			return data.get(position);
		else
			return null;
	}

	@Override
	public long getItemId(int position) {
		if (data != null)
			return data.get(position).getId();
		else
			return PlayerService.INVALID_ID_OR_POSITION;
	}
}

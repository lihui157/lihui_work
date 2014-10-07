package net.cstong.android.ui.adapter;

import java.util.List;

import net.cstong.android.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ab.model.AbMenuItem;

public class ListPopAdapter extends BaseAdapter {

	private Context context;

	private List<AbMenuItem> list;

	private int itemResource;

	public ListPopAdapter(final Context context, final List<AbMenuItem> list, final int itemResource) {
		this.context = context;
		this.list = list;
		this.itemResource = itemResource;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(final int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(final int position) {
		AbMenuItem item = ((AbMenuItem) getItem(position));
		return item.getId();
	}

	@Override
	public View getView(final int position, View convertView, final ViewGroup viewGroup) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(itemResource, null);
			holder = new ViewHolder();
			holder.itemText = (TextView) convertView.findViewById(R.id.pop_item);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		AbMenuItem item = list.get(position);
		holder.itemText.setText(item.getText());

		return convertView;
	}

	static class ViewHolder {
		TextView itemText;
	}

}

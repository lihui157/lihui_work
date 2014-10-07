package net.cstong.android.ui.adapter;

import net.cstong.android.R;
import net.cstong.android.api.MessageApi.NoticeInfo;
import net.cstong.android.api.MessageApi.NoticeListInfo;
import net.cstong.android.util.Utils;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class NoticeListAdapter extends BaseAdapter {
	private static final String TAG = "NoticeListAdapter";

	private Context mContext;
	//xml转View对象
	private LayoutInflater mInflater;
	//单行的布局
	private int mResource;
	//列表展现的数据
	private NoticeListInfo mData;

	/**
	 * 构造方法
	 * @param context
	 * @param data 列表展现的数据
	 * @param resource 单行的布局
	 * @param from Map中的key
	 * @param to view的id
	 */
	public NoticeListAdapter(final Context context, final NoticeListInfo data, final int resource) {
		mContext = context;
		mData = data;
		mResource = resource;
		//用于将xml转为View
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return mData.noticeData.size();
	}

	@Override
	public Object getItem(final int position) {
		return mData.noticeData.get(position);
	}

	@Override
	public long getItemId(final int position) {
		return mData.noticeData.get(position).id;
	}

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			//使用自定义的list_items作为Layout
			convertView = mInflater.inflate(mResource, parent, false);
			//减少findView的次数
			holder = new ViewHolder();
			//初始化布局中的元素
			holder.itemsTitle = ((TextView) convertView.findViewById(R.id.noticeItemContent));
			holder.itemsCreatedTime = ((TextView) convertView.findViewById(R.id.noticeItemCreatedTime));
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		//获取该行的数据
		NoticeInfo obj = mData.noticeData.get(position);
		holder.itemsTitle.setText(obj.content);
		holder.itemsCreatedTime.setText(Utils.timestampToString(obj.createdTime));
		return convertView;
	}

	/**
	 * View元素
	 */
	static class ViewHolder {
		TextView itemsTitle;
		TextView itemsCreatedTime;
	}
}
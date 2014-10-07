package net.cstong.android.ui.adapter;

import net.cstong.android.R;
import net.cstong.android.api.MessageApi.MessageInfo;
import net.cstong.android.api.MessageApi.MessageListInfo;
import net.cstong.android.ui.widget.CircularImageView;
import net.cstong.android.util.Constant;
import net.cstong.android.util.Utils;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ab.bitmap.AbImageDownloader;
import com.ab.util.AbImageUtil;

public class MessageListAdapter extends BaseAdapter {
	private static final String TAG = "MessageListAdapter";
	private static final boolean D = Constant.DEBUG;

	private Context mContext;
	//xml转View对象
	private LayoutInflater mInflater;
	//单行的布局
	private int mResource;
	//列表展现的数据
	private MessageListInfo mData;
	//图片下载器
	private AbImageDownloader mAbImageDownloader = null;

	/**
	 * 构造方法
	 * @param context
	 * @param data 列表展现的数据
	 * @param resource 单行的布局
	 * @param from Map中的key
	 * @param to view的id
	 */
	public MessageListAdapter(final Context context, final MessageListInfo data, final int resource) {
		mContext = context;
		mData = data;
		mResource = resource;
		//用于将xml转为View
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		//图片下载器
		mAbImageDownloader = new AbImageDownloader(mContext);
		mAbImageDownloader.setWidth(100);
		mAbImageDownloader.setHeight(100);
		mAbImageDownloader.setType(AbImageUtil.SCALEIMG);
		mAbImageDownloader.setLoadingImage(R.drawable.image_loading);
		mAbImageDownloader.setErrorImage(R.drawable.image_error);
		mAbImageDownloader.setNoImage(R.drawable.image_no);
		//mAbImageDownloader.setAnimation(true);
	}

	@Override
	public int getCount() {
		return mData.messageData.size();
	}

	@Override
	public Object getItem(final int position) {
		return mData.messageData.get(position);
	}

	@Override
	public long getItemId(final int position) {
		return mData.messageData.get(position).dialogId;
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
			holder.itemsAvatar = ((CircularImageView) convertView.findViewById(R.id.messageItemAvatar));
			holder.itemsTitle = ((TextView) convertView.findViewById(R.id.messageItemTitle));
			holder.itemsUsername = ((TextView) convertView.findViewById(R.id.messageItemUsername));
			holder.itemsCreatedTime = ((TextView) convertView.findViewById(R.id.messageItemCreatedTime));
			//holder.itemsViews = ((TextView) convertView.findViewById(R.id.itemsViews));
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		//获取该行的数据
		MessageInfo obj = mData.messageData.get(position);
		String imageUrl = obj.fromAvatar;
		holder.itemsTitle.setText(obj.lastContent);
		holder.itemsUsername.setText(obj.lastFromUsername);
		holder.itemsCreatedTime.setText(Utils.timestampToString(obj.modifiedTime));

		//设置加载中的View
		mAbImageDownloader.setLoadingView(convertView.findViewById(R.id.progressBar));
		//图片的下载
		mAbImageDownloader.display(holder.itemsAvatar, imageUrl);
		mAbImageDownloader.setType(AbImageUtil.SCALEIMG);

		return convertView;
	}

	/**
	 * View元素
	 */
	static class ViewHolder {
		CircularImageView itemsAvatar;
		TextView itemsTitle;
		TextView itemsUsername;
		TextView itemsCreatedTime;
	}
}
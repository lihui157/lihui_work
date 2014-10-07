package net.cstong.android.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import net.cstong.android.R;
import net.cstong.android.api.ForumApi.ThreadInfo;
import net.cstong.android.ui.widget.CircularImageView;
import net.cstong.android.util.AsynImageLoader;
import net.cstong.android.util.Constant;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ab.bitmap.AbImageDownloader;
import com.ab.util.AbImageUtil;

/**
 * Copyright (c) 2011 All rights reserved
 * 名称：MyListViewAdapter
 * 描述：在Adapter中释放Bitmap
 * @author zhaoqp
 * @date 2011-12-10
 * @version
 */
public class ThreadItemAdapter extends BaseAdapter {

	private static final String TAG = "ThreadItemAdapter";
	private static final boolean D = Constant.DEBUG;
	private static int MAX_THUMBS = 3;
	// 缩略图relativeLayout's id
	private static int[] RL_IDS = { R.id.rl_thumb0, R.id.rl_thumb1, R.id.rl_thumb2 };
	private static int[] IV_IDS = { R.id.iv_thumb0, R.id.iv_thumb1, R.id.iv_thumb2 };

	private Context mContext;
	//xml转View对象
	private LayoutInflater mInflater;
	//单行的布局
	private int mResource;
	//列表展现的数据
	private List<?> mData;
	//view的id
	private int[] mTo;
	//图片下载器
//	private AbImageDownloader mAbImageDownloader = null;
	private AsynImageLoader asynImageLoader = null;

	/**
	 * 构造方法
	 * @param context
	 * @param data 列表展现的数据
	 * @param resource 单行的布局
	 * @param from Map中的key
	 * @param to view的id
	 */
	public ThreadItemAdapter(final Context context, final List<?> data, final int resource, final int[] to) {
		mContext = context;
		mData = data;
		mResource = resource;
		mTo = to;
		//用于将xml转为View
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		//图片下载器
//		mAbImageDownloader = new AbImageDownloader(mContext);
//		mAbImageDownloader.setWidth(80);
//		mAbImageDownloader.setHeight(80);
//		mAbImageDownloader.setType(AbImageUtil.CUTIMG);
//		mAbImageDownloader.setLoadingImage(R.drawable.touxiang90);
//		mAbImageDownloader.setErrorImage(R.drawable.touxiang90);
//		mAbImageDownloader.setNoImage(R.drawable.touxiang90);
		asynImageLoader = new AsynImageLoader();
		//mAbImageDownloader.setAnimation(true);
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Object getItem(final int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(final int position) {
		return ((ThreadInfo) mData.get(position)).tid;
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
			holder.itemsAvatar = ((CircularImageView) convertView.findViewById(mTo[0]));
			holder.itemsTitle = ((TextView) convertView.findViewById(mTo[1]));
			holder.itemsUsername = ((TextView) convertView.findViewById(mTo[2]));
			holder.itemsCreatedTime = ((TextView) convertView.findViewById(mTo[3]));
			holder.itemsViews = ((TextView) convertView.findViewById(mTo[4]));
			holder.itemsThumb = new ArrayList<ImageView>();
			holder.itemsThumb.add((ImageView) convertView.findViewById(IV_IDS[0]));
			holder.itemsThumb.add((ImageView) convertView.findViewById(IV_IDS[1]));
			holder.itemsThumb.add((ImageView) convertView.findViewById(IV_IDS[2]));
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		//获取该行的数据
		ThreadInfo obj = (ThreadInfo) mData.get(position);
		Log.d(TAG, "obj.title = "+obj.title);
		String imageUrl = obj.avatar;
		holder.itemsTitle.setText(obj.title);
		holder.itemsUsername.setText(obj.createdUser);
		holder.itemsCreatedTime.setText(obj.createdTime);
		holder.itemsViews.setText(String.valueOf(obj.replies) + "/" + String.valueOf(obj.hits));
		
//		AbImageDownloader thumbDownloader = null;
		if ((obj.imgs == null) || obj.imgs.isEmpty()) {
			for (int i = 0; i < MAX_THUMBS; i++) {
				convertView.findViewById(RL_IDS[i]).setVisibility(View.GONE);
			}
		} else if (obj.imgs.size() >= MAX_THUMBS) {
			// 显示三张三张缩略图
			for (int i = 0; i < MAX_THUMBS; i++) {
				convertView.findViewById(RL_IDS[i]).setVisibility(View.VISIBLE);
//				//图片下载器
//				thumbDownloader = new AbImageDownloader(mContext);
//				thumbDownloader.setWidth(60);
//				thumbDownloader.setHeight(60);
//				thumbDownloader.setType(AbImageUtil.CUTIMG);
//				thumbDownloader.setLoadingImage(R.drawable.image_loading);
//				thumbDownloader.setErrorImage(R.drawable.image_error);
//				thumbDownloader.setNoImage(R.drawable.image_no);
//
//				//设置加载中的View
//				thumbDownloader.setLoadingView(convertView.findViewById(R.id.progressBar));
				//图片的下载
//				thumbDownloader.display(holder.itemsThumb.get(i), obj.imgs.get(i));
				holder.itemsThumb.get(i).setBackgroundColor(Color.GRAY);
				holder.itemsThumb.get(i).setScaleType(ImageView.ScaleType.CENTER_CROP);
				asynImageLoader.showImageAsyn(holder.itemsThumb.get(i), obj.imgs.get(i), R.drawable.image_loading);
			}
		} else {
			convertView.findViewById(RL_IDS[0]).setVisibility(View.VISIBLE);
			// 只显示第一张缩略图
			for (int i = 1; i < holder.itemsThumb.size(); i++) {
				convertView.findViewById(RL_IDS[i]).setVisibility(View.GONE);
			}
//			//图片下载器
//			thumbDownloader = new AbImageDownloader(mContext);
//			thumbDownloader.setWidth(120);
//			thumbDownloader.setHeight(120);
//			thumbDownloader.setType(AbImageUtil.CUTIMG);
//			thumbDownloader.setLoadingImage(R.drawable.image_loading);
//			thumbDownloader.setErrorImage(R.drawable.image_error);
//			thumbDownloader.setNoImage(R.drawable.image_no);
//
//			//设置加载中的View
//			thumbDownloader.setLoadingView(convertView.findViewById(R.id.progressBar));
//			//图片的下载
//			thumbDownloader.display(holder.itemsThumb.get(0), obj.imgs.get(0));
			holder.itemsThumb.get(0).setBackgroundColor(Color.GRAY);
			holder.itemsThumb.get(0).setScaleType(ImageView.ScaleType.CENTER_CROP);
			asynImageLoader.showImageAsyn(holder.itemsThumb.get(0), obj.imgs.get(0), R.drawable.image_loading);
		}

		//设置加载中的View
//		mAbImageDownloader.setLoadingView(convertView.findViewById(R.id.progressBar));
//		//图片的下载
//		mAbImageDownloader.display(holder.itemsAvatar, imageUrl);
		holder.itemsAvatar.setScaleType(ImageView.ScaleType.CENTER_CROP);
		asynImageLoader.showImageAsyn(holder.itemsAvatar, imageUrl, R.drawable.headphoto);

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
		TextView itemsViews;
		List<ImageView> itemsThumb;
	}

}

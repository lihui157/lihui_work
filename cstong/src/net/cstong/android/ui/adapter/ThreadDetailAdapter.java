package net.cstong.android.ui.adapter;

import java.io.UnsupportedEncodingException;

import net.cstong.android.R;
import net.cstong.android.api.ForumApi.ThreadDetail;
import net.cstong.android.api.ForumApi.ThreadDetailList;
import net.cstong.android.ui.ThreadReadActivity;
import net.cstong.android.ui.ThreadReadFragment;
import net.cstong.android.ui.widget.CircularImageView;
import net.cstong.android.ui.widget.TouchWebView;
import net.cstong.android.util.AsynHtmlContentLoader;
import net.cstong.android.util.AsynImageLoader;
import net.cstong.android.util.HtmlImageGetter;
import net.cstong.android.util.Utils;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.ab.bitmap.AbImageDownloader;
import com.ab.util.AbFileUtil;
import com.ab.util.AbImageUtil;

/**
 * Copyright (c) 2011 All rights reserved
 * 名称：MyListViewAdapter
 * 描述：在Adapter中释放Bitmap
 * @author zhaoqp
 * @date 2011-12-10
 * @version
 */
public class ThreadDetailAdapter extends BaseAdapter {
	private static final String TAG = "ThreadDetailAdapter";

	private Context mContext;
	//xml转View对象
	private LayoutInflater mInflater;
	//列表展现的数据
	private ThreadDetailList mData;
	//图片下载器
//	private AbImageDownloader mAbImageDownloader = null;
	private AsynImageLoader asynImageLoader = null;
	
	private AsynHtmlContentLoader asynHtmlContentLoader = null;
	
	protected RelativeLayout layout;
	
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
		}
		
	};

	/**
	 * 构造方法
	 * @param context
	 * @param data 列表展现的数据
	 * @param resource 单行的布局
	 * @param from Map中的key
	 * @param to view的id
	 */
	public ThreadDetailAdapter( Context context,  ThreadDetailList data,Handler handler) {
//		this.handler = handler;
		mContext = context;
		mData = data;
		//用于将xml转为View
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		//图片下载器
		asynImageLoader = new AsynImageLoader();
		asynHtmlContentLoader = new AsynHtmlContentLoader(context);
	}

	@Override
	public int getCount() {
		return mData.detailList.size();
	}

	@Override
	public Object getItem(final int position) {
		return mData.detailList.get(position);
	}

	@Override
	public long getItemId(final int position) {
		return mData.detailList.get(position).lou;
	}

	@Override
	public View getView( final int position, View convertView,  ViewGroup parent) {
		//获取该行的数据
		 final ThreadDetail obj = (ThreadDetail) getItem(position);
		 final ViewHolder holder;
		if (convertView == null) {
			//减少findView的次数
			holder = new ViewHolder();
			//使用自定义的list_items作为Layout

			if (getItemViewType(position) == 0) {
				convertView = mInflater.inflate(R.layout.fragment_threaddetail_main, parent, false);
				holder.threadTitle = ((TextView) convertView.findViewById(R.id.threadTitle));
			} else {
				convertView = mInflater.inflate(R.layout.fragment_threaddetail, parent, false);
				holder.threadQuoteReplyAuthor = (TextView) convertView.findViewById(R.id.threadQuoteReplyAuthor);
				holder.threadQuoteReplyContent = (TextView) convertView.findViewById(R.id.threadQuoteReplyContent);
				holder.wvThreadQuoteReplyContent = (WebView) convertView.findViewById(R.id.wvThreadQuoteReplyContent);
			}
			//初始化布局中的元素
			holder.threadAvatar = ((CircularImageView) convertView.findViewById(R.id.threadAuthorAvatar));
			holder.threadUsername = ((TextView) convertView.findViewById(R.id.threadAuthorName));
			holder.threadContent = ((TextView) convertView.findViewById(R.id.threadContent));
			holder.threadCreatedTime = ((TextView) convertView.findViewById(R.id.threadCreatedTime));
			holder.wvThreadContent = (TouchWebView) convertView.findViewById(R.id.wvThreadContent);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (position == 0) {
			// 楼主
			holder.threadTitle.setText(obj.title);
		}
		String imageUrl = obj.avatar;
		holder.threadUsername.setText(obj.createdUsername);
		holder.threadCreatedTime.setText(Utils.timestampToString(obj.createdTime));

		
		//设置加载中的View
		asynImageLoader.showImageAsyn(holder.threadAvatar, imageUrl, R.drawable.image_loading);

		handleBlockquote(holder, obj.content);

		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Log.e(TAG, "---"+obj.content);
//				if(mContext instanceof ThreadReadFragment){
//					
//				}
				((ThreadReadActivity)mContext).readFragment.onItemClick(position);
				
			}
		});
		
		return convertView;
	}

	@Override
	public int getItemViewType(final int position) {
		if (position == 0) {
			return 0;
		}
		return 1;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public void handleBlockquote( final ViewHolder holder,  final String content) {
		

		String newContent = content;
		int startPos = content.indexOf("<blockquote>");
		int endPos = content.indexOf("</blockquote>");
		if ((startPos != -1) && (endPos != -1)) {
			String quoteAuthor = content.substring(startPos + 12, content.indexOf(":"));
			String quoteContent = content.substring(content.indexOf(":") + 1, endPos);
			holder.threadQuoteReplyAuthor.setText(quoteAuthor);
			holder.threadQuoteReplyAuthor.setVisibility(View.VISIBLE);
//			holder.threadQuoteReplyContent.setText(quoteContent);
//			holder.threadQuoteReplyContent.setVisibility(View.VISIBLE);
			holder.wvThreadQuoteReplyContent.loadDataWithBaseURL(null, quoteContent, "text/html", "utf-8", null);
			holder.wvThreadQuoteReplyContent.setVisibility(View.VISIBLE);
			newContent = content.substring(endPos + 13);
		} else {
			if (holder.threadQuoteReplyAuthor != null) {
				holder.threadQuoteReplyAuthor.setVisibility(View.GONE);
			}
			if (holder.threadQuoteReplyContent != null) {
//				holder.threadQuoteReplyContent.setVisibility(View.GONE);
				holder.wvThreadQuoteReplyContent.setVisibility(View.GONE);
			}
		}
		
//		asynHtmlContentLoader.showHtmlContentAsyn(holder.threadContent, newContent, R.drawable.progress_circular);
		Log.e(TAG, "newContent:"+newContent);
//		newContent = newContent.replace("\" </img>", "</img>").replace("[img]", "").replace("[/img]", "");
		newContent = newContent
				.replace("\" </img>", "</img>")
				.replace("[img]", "")
				.replace("[/img]", "")
				.replace("\"/>\"/>", "\"/>")
				.replace("src=\"<img", "")
//				.replace("<img", "<br/><img")
				;
		Log.i(TAG, "newContent-format:"+newContent);
		final String temp = newContent;
//		String head = "<style>window,html,body{overflow-x:hidden !important;-webkit-overflow-scrolling: touch !important;overflow: scroll !important;}</style>";
//		holder.wvThreadContent.getSettings().setBuiltInZoomControls(true);
		holder.wvThreadContent.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
//		holder.wvThreadContent.loadDataWithBaseURL(null, temp, "text/html", "utf-8", null);
		handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				holder.wvThreadContent.loadDataWithBaseURL(null, temp, "text/html", "utf-8", null);
				
			}
		}, 300);
		
		
		
	}

	/**
	 * View元素
	 */
	static class ViewHolder {
		TextView threadTitle;
		CircularImageView threadAvatar;
		TextView threadUsername;
		TextView threadContent;
		TextView threadCreatedTime;
		TextView threadQuoteReplyAuthor;
		TextView threadQuoteReplyContent;
		WebView wvThreadQuoteReplyContent;
		TouchWebView wvThreadContent;
	}
}

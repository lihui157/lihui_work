package net.cstong.android.ui.adapter;

/*
 * Copyright (C) 2013 www.418log.org
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.util.List;

import net.cstong.android.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ab.bitmap.AbImageCache;
import com.ab.bitmap.AbImageDownloader;
import com.ab.util.AbFileUtil;
import com.ab.util.AbImageUtil;
import com.ab.util.AbStrUtil;

// TODO: Auto-generated Javadoc
/**
 * 适配器 网络URL的图片.
 */
public class ImageShowAdapter extends BaseAdapter {

	/** The m context. */
	private Context mContext;

	/** The m image paths. */
	private List<String> mImagePaths = null;

	/** The m width. */
	private int mWidth;

	/** The m height. */
	private int mHeight;

	//图片下载器
	private AbImageDownloader mAbImageDownloader = null;

	/**
	 * Instantiates a new ab image show adapter.
	 * @param context the context
	 * @param imagePaths the image paths
	 * @param width the width
	 * @param height the height
	 */
	public ImageShowAdapter(final Context context, final List<String> imagePaths, final int width, final int height) {
		mContext = context;
		mImagePaths = imagePaths;
		mWidth = width;
		mHeight = height;
		//图片下载器
		mAbImageDownloader = new AbImageDownloader(mContext);
		mAbImageDownloader.setWidth(mWidth);
		mAbImageDownloader.setHeight(mHeight);
		mAbImageDownloader.setLoadingImage(R.drawable.image_loading);
		mAbImageDownloader.setErrorImage(R.drawable.image_error);
		mAbImageDownloader.setNoImage(R.drawable.image_no);
	}

	/**
	 * 描述：获取数量.
	 *
	 * @return the count
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		return mImagePaths.size();
	}

	/**
	 * 描述：获取索引位置的路径.
	 *
	 * @param position the position
	 * @return the item
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(final int position) {
		return mImagePaths.get(position);
	}

	/**
	 * 描述：获取位置.
	 *
	 * @param position the position
	 * @return the item id
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(final int position) {
		return position;
	}

	/**
	 * 描述：显示View.
	 *
	 * @param position the position
	 * @param convertView the convert view
	 * @param parent the parent
	 * @return the view
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {

		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			LinearLayout mLinearLayout = new LinearLayout(mContext);
			RelativeLayout mRelativeLayout = new RelativeLayout(mContext);
			ImageView mImageView1 = new ImageView(mContext);
			mImageView1.setScaleType(ScaleType.FIT_CENTER);
			ImageView mImageView2 = new ImageView(mContext);
			mImageView2.setScaleType(ScaleType.FIT_CENTER);
			holder.mImageView1 = mImageView1;
			holder.mImageView2 = mImageView2;
			LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
			lp1.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
			RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(mWidth, mHeight);
			lp2.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
			lp2.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
			mRelativeLayout.addView(mImageView1, lp2);
			mRelativeLayout.addView(mImageView2, lp2);
			mLinearLayout.addView(mRelativeLayout, lp1);

			convertView = mLinearLayout;
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.mImageView1.setImageBitmap(null);
		holder.mImageView2.setBackgroundDrawable(null);

		String imagePath = mImagePaths.get(position);

		if (!AbStrUtil.isEmpty(imagePath)) {
			//从缓存中获取图片，很重要否则会导致页面闪动
			Bitmap bitmap = AbImageCache.getBitmapFromCache(imagePath);
			//缓存中没有则从网络和SD卡获取
			if (bitmap == null) {
				holder.mImageView1.setImageResource(R.drawable.image_loading);
				if (imagePath.indexOf("http://") != -1) {
					//图片的下载
					mAbImageDownloader.setType(AbImageUtil.ORIGINALIMG);
					mAbImageDownloader.display(holder.mImageView1, imagePath);

				} else if (imagePath.indexOf("/") == -1) {
					//索引图片
					try {
						int res = Integer.parseInt(imagePath);
						holder.mImageView1.setImageDrawable(mContext.getResources().getDrawable(res));
					} catch (Exception e) {
						holder.mImageView1.setImageResource(R.drawable.image_error);
					}
				} else {
					Bitmap mBitmap = AbFileUtil.getBitmapFromSD(new File(imagePath), AbImageUtil.SCALEIMG, mWidth, mHeight);
					if (mBitmap != null) {
						holder.mImageView1.setImageBitmap(mBitmap);
					} else {
						// 无图片时显示
						holder.mImageView1.setImageResource(R.drawable.image_no);
					}
				}
			} else {
				//直接显示
				holder.mImageView1.setImageBitmap(bitmap);
			}
		} else {
			// 无图片时显示
			holder.mImageView1.setImageResource(R.drawable.image_no);
		}
		holder.mImageView1.setAdjustViewBounds(true);
		return convertView;
	}

	/**
	 * 增加并改变视图.
	 * @param position the position
	 * @param imagePaths the image paths
	 */
	public void addItem(final int position, final String imagePaths) {
		mImagePaths.add(position, imagePaths);
		notifyDataSetChanged();
	}

	/**
	 * 增加多条并改变视图.
	 * @param imagePaths the image paths
	 */
	public void addItems(final List<String> imagePaths) {
		mImagePaths.addAll(imagePaths);
		notifyDataSetChanged();
	}

	/**
	 * 增加多条并改变视图.
	 */
	public void clearItems() {
		mImagePaths.clear();
		notifyDataSetChanged();
	}

	/**
	 * View元素.
	 */
	public static class ViewHolder {

		/** The m image view1. */
		public ImageView mImageView1;

		/** The m image view2. */
		public ImageView mImageView2;
	}

}

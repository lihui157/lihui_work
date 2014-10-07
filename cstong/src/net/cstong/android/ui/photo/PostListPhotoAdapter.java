package net.cstong.android.ui.photo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import net.cstong.android.R;
import net.cstong.android.ui.photo.MyImageView.OnMeasureListener;
import net.cstong.android.ui.photo.NativeImageLoader.NativeImageCallBack;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.ImageView;

public class PostListPhotoAdapter extends BaseAdapter {
	private Point mPoint = new Point(0, 0);//用来封装ImageView的宽和高的对象
	private Context mContext;
	/**
	 * 用来存储图片的选中情况
	 */
	private HashMap<Integer, String> mSelectedMap = new HashMap<Integer, String>();
	private GridView mGridView;
	private List<String> list;
	protected LayoutInflater mInflater;

	public PostListPhotoAdapter(final Context context, final List<String> list, final GridView mGridView) {
		mContext = context;
		this.list = list;
		this.mGridView = mGridView;
		mInflater = LayoutInflater.from(context);
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
		return position;
	}

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		final ViewHolder viewHolder;
		String path = list.get(position);

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.griditem_photo_add, null);
			viewHolder = new ViewHolder();
			viewHolder.mImageView = (MyImageView) convertView.findViewById(R.id.iv_photo);
			viewHolder.mCheckBox = (CheckBox) convertView.findViewById(R.id.child_checkbox);

			//用来监听ImageView的宽和高
			viewHolder.mImageView.setOnMeasureListener(new OnMeasureListener() {
				@Override
				public void onMeasureSize(final int width, final int height) {
					mPoint.set(width, height);
				}
			});

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.mImageView.setTag(path);
		viewHolder.mImageView.setCheckBox(viewHolder.mCheckBox);
		viewHolder.mCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
				//如果是未选中的CheckBox,则添加动画
				/**
				if (!mSelectedMap.containsKey(position) || !mSelectedMap.get(position)) {
					addAnimation(viewHolder.mCheckBox);
				}
				**/
				if (isChecked) {
					mSelectedMap.put(position, (String) PostListPhotoAdapter.this.getItem(position));
				}else{
					mSelectedMap.remove(position);
				}
			}
		});
		viewHolder.mCheckBox.setChecked(mSelectedMap.containsKey(position) ? true : false);

		//利用NativeImageLoader类加载本地图片
		Bitmap bitmap = NativeImageLoader.getInstance().loadNativeImage(path, mPoint, new NativeImageCallBack() {
			@Override
			public void onImageLoader(final Bitmap bitmap, final String path) {
				ImageView mImageView = (ImageView) mGridView.findViewWithTag(path);
				if ((bitmap != null) && (mImageView != null)) {
					mImageView.setImageBitmap(bitmap);
				}
			}
		});

		if (bitmap != null) {
			viewHolder.mImageView.setImageBitmap(bitmap);
		} else {
			viewHolder.mImageView.setImageResource(R.drawable.image_no);
		}

		return convertView;
	}

	/**
	 * 给CheckBox加点击动画，利用开源库nineoldandroids设置动画 
	 * @param view
	 */
	/**
	private void addAnimation(final View view) {
		float[] vaules = new float[] { 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f, 1.1f, 1.2f, 1.3f, 1.25f, 1.2f, 1.15f, 1.1f, 1.0f };
		AnimatorSet set = new AnimatorSet();
		set.playTogether(ObjectAnimator.ofFloat(view, "scaleX", vaules),
				ObjectAnimator.ofFloat(view, "scaleY", vaules));
		set.setDuration(150);
		set.start();
	}
	**/

	/**
	 * 获取选中的Item的position
	 * @return
	 */
	public List<String> getSelectItems() {
		List<String> list = new ArrayList<String>();
		for (Entry<Integer, String> entry : mSelectedMap.entrySet()) {
			list.add(entry.getValue());
		}

		return list;
	}

	public static class ViewHolder {
		public MyImageView mImageView;
		public CheckBox mCheckBox;
	}
}

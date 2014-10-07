package net.cstong.android.ui.photo;

import java.util.List;

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
import android.widget.GridView;
import android.widget.ImageView;

public class PostViewPhotoAdapter extends BaseAdapter {
	private Point mPoint = new Point(0, 0);//用来封装ImageView的宽和高的对象
	private Context mContext;
	private GridView mGridView;
	private List<String> list;
	protected LayoutInflater mInflater;

	public PostViewPhotoAdapter(final Context context, final List<String> list, final GridView mGridView) {
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

	public static class ViewHolder {
		public MyImageView mImageView;
	}
}

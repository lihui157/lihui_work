package net.cstong.android.ui.photo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.CheckBox;
import android.widget.ImageView;

public class MyImageView extends ImageView {
	private OnMeasureListener onMeasureListener;
	private CheckBox mCheckBox = null;

	public MyImageView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
	}

	public MyImageView(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setCheckBox(final CheckBox checkBox) {
		mCheckBox = checkBox;
	}

	public void setOnMeasureListener(final OnMeasureListener onMeasureListener) {
		this.onMeasureListener = onMeasureListener;
	}

	@Override
	protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		//将图片测量的大小回调到onMeasureSize()方法中
		if (onMeasureListener != null) {
			onMeasureListener.onMeasureSize(getMeasuredWidth(), getMeasuredHeight());
		}
	}

	@Override
	public boolean onTouchEvent(final MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			if (mCheckBox != null) {
				mCheckBox.toggle();
			}
			break;
		}
		return true;
	}

	public interface OnMeasureListener {
		public void onMeasureSize(int width, int height);
	}
}

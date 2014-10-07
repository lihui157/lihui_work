package net.cstong.android.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class ScrollGridView extends GridView {
	public ScrollGridView(final Context context) {
		super(context);
	}

	public ScrollGridView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
	}

	public ScrollGridView(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
	}

	//该自定义控件只是重写了GridView的onMeasure方法，使其不会出现滚动条，ScrollView嵌套ListView也是同样的道理，不再赘述。 
	@Override
	public void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(
				Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}

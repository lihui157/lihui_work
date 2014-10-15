package net.cstong.android.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;

public class TouchWebView extends WebView {
	public TouchWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TouchWebView(Context context) {
		super(context);
	}

	

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
    	if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            onScrollChanged(getScrollX(), getScrollY(), getScrollX(), getScrollY());
        }
        return false;
    }

    
    
}
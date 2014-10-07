package net.cstong.android.ui;

import net.cstong.android.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ab.view.sliding.AbSlidingTabView;

public class MessageTabsFragment extends Fragment {
	private static final String TAG = "MessageTabsFragment";
	private MessageActivity mActivity = null;
	private AbSlidingTabView mAbSlidingTabView = null;

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.tab_messages, null);
		mAbSlidingTabView = (AbSlidingTabView) view.findViewById(R.id.messageTabs);
		mActivity = (MessageActivity) getActivity();

		initTabs();
		return view;
	}

	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	private void initTabs() {
		//设置样式
		mAbSlidingTabView.setTabTextColor(getResources().getColor(R.color.black));
		mAbSlidingTabView.setTabSelectColor(getResources().getColor(R.color.green));
		mAbSlidingTabView.setTabBackgroundResource(R.drawable.tab_bg);
		mAbSlidingTabView.setTabLayoutBackgroundResource(R.drawable.slide_top);
		mAbSlidingTabView.setTabPadding(40, 8, 40, 8);

		String title = getResources().getString(R.string.notice);
		NoticeListFragment noticeListFragment = new NoticeListFragment();
		mAbSlidingTabView.addItemView(title, noticeListFragment);

		title = getResources().getString(R.string.chat);
		MessageListFragment messageListFragment2 = new MessageListFragment();
		mAbSlidingTabView.addItemView(title, messageListFragment2);

		//如果里面的页面列表不能下载原因：
		//Fragment里面用的AbTaskQueue,由于有多个tab，顺序下载有延迟，还没下载好就被缓存了。改成用AbTaskPool，就ok了。
		//或者setOffscreenPageLimit(0)
		//缓存数量
		mAbSlidingTabView.getViewPager().setOffscreenPageLimit(1);
		//禁止滑动
		/*
		mAbSlidingTabView.getViewPager().setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}			
		});
		*/
	}
}
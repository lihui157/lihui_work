package net.cstong.android.ui;

import net.cstong.android.R;
import net.cstong.android.api.ForumApi.ForumInfo;
import net.cstong.android.util.Constant;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ab.adapter.AbFragmentPagerAdapter;
import com.ab.view.sliding.AbSlidingTabView;

public class ThreadReadTabFragment extends Fragment {
	private final String TAG = "ThreadReadTabFragment";
	private ThreadReadActivity mActivity = null;
	private AbSlidingTabView mAbSlidingTabView = null;
	private int tabsNum = 9;
	protected int tabIndex = 0;

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.thread_reply_bar, null);

		initFragmentTabs();
		return view;
	}

	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	public int getCurrentTabPosition() {
		return tabIndex;
	}

	public void reloadTab(final int index) {
		AbFragmentPagerAdapter adapter = (AbFragmentPagerAdapter) mAbSlidingTabView.getViewPager().getAdapter();
		ThreadsFragment fragment = (ThreadsFragment) adapter.getItem(index);
		fragment.reset();
	}

	public ForumInfo getTabData(final int index) {
		AbFragmentPagerAdapter adapter = (AbFragmentPagerAdapter) mAbSlidingTabView.getViewPager().getAdapter();
		ThreadsFragment fragment = (ThreadsFragment) adapter.getItem(index);
		Bundle bundle = fragment.getArguments();
		ForumInfo forumInfo = (ForumInfo) bundle.getSerializable(Constant.KEY_FORUMINFO);
		return forumInfo;
	}

	private void initFragmentTabs() {}
}

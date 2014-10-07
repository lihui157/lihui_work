package net.cstong.android.ui;

import java.util.ArrayList;
import java.util.List;

import net.cstong.android.R;
import net.cstong.android.api.ForumApi;
import net.cstong.android.api.ForumApi.ForumInfo;
import net.cstong.android.util.Constant;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ab.adapter.AbFragmentPagerAdapter;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.util.AbAppUtil;
import com.ab.view.sliding.AbSlidingTabView;

public class ForumsTabFragment extends Fragment {
	private final String TAG = "ForumsTabFragment";
	private MainActivity mActivity = null;
	private AbSlidingTabView mAbSlidingTabView = null;
	private AbStringHttpResponseListener forumsListener = null;
	private int tabsNum = 9;
	protected int tabIndex = 0;

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.tab_forums, null);
		mAbSlidingTabView = (AbSlidingTabView) view.findViewById(R.id.forumTabs);

		mActivity = (MainActivity) getActivity();

		//如果里面的页面列表不能下载原因：
		//Fragment里面用的AbTaskQueue,由于有多个tab，顺序下载有延迟，还没下载好就被缓存了。改成用AbTaskPool，就ok了。
		//或者setOffscreenPageLimit(0)

		//缓存数量
		mAbSlidingTabView.getViewPager().setOffscreenPageLimit(0);
		//禁止滑动
		/*
		mAbSlidingTabView.getViewPager().setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}			
		});
		*/
		//设置样式
		mAbSlidingTabView.setTabTextColor(getResources().getColor(R.color.black));
		mAbSlidingTabView.setTabSelectColor(getResources().getColor(R.color.green));
		mAbSlidingTabView.setTabBackgroundResource(R.drawable.tab_bg);
		mAbSlidingTabView.setTabLayoutBackgroundResource(R.drawable.slide_top);
		mAbSlidingTabView.setTabPadding(80, 8, 80, 8);
		mAbSlidingTabView.setMinimumWidth(200);
		mAbSlidingTabView.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(final int position) {
				//Log.d(TAG, "page pos:" + String.valueOf(position));
				tabIndex = position;
				if (position == 0) {
					mActivity.showPostButton(false);
				} else {
					mActivity.showPostButton(true);
				}
			}

			@Override
			public void onPageScrollStateChanged(final int arg0) {
				//Log.d(TAG, "page arg0:" + String.valueOf(arg0));
			}

			@Override
			public void onPageScrolled(final int arg0, final float arg1, final int arg2) {
				//Log.d(TAG, "page arg0:" + String.valueOf(arg0) + " arg1:" + String.valueOf(arg1) + " arg2:" + String.valueOf(arg2));
			}
		});

		Log.d(TAG, "forumsListener = "+forumsListener);
		forumsListener = new AbStringHttpResponseListener() {
			// 获取数据成功会调用这里
			@Override
			public void onSuccess(final int statusCode, final String content) {
//				Log.d(TAG, "onSuccess:" + content);
				List<ForumInfo> forums = ForumApi.parseIndexResponse(content);
				initFragmentTabs(forums);
			}

			// 开始执行前
			@Override
			public void onStart() {
				Log.d(TAG, "onStart");
				//显示进度框
				mActivity.showProgressDialog();
			}

			// 失败，调用
			@Override
			public void onFailure(final int statusCode, final String content, final Throwable error) {
				mActivity.showToast("加载失败，请稍后再试");
			}

			// 完成后调用，失败，成功
			@Override
			public void onFinish() {
				Log.d(TAG, "onFinish");
				//移除进度框
				mActivity.removeProgressDialog();
			};
		};

		initFragmentTabs(null);
		//forumApi.index(forumsListener);

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
		Log.d(TAG, "reloadTab:"+index);
		AbFragmentPagerAdapter adapter = (AbFragmentPagerAdapter) mAbSlidingTabView.getViewPager().getAdapter();
		ThreadsFragment fragment = (ThreadsFragment) adapter.getItem(index);
		fragment.reset();
	}

	public ForumInfo getTabData(final int index) {
		Log.d(TAG, "getTabData:"+index);
		AbFragmentPagerAdapter adapter = (AbFragmentPagerAdapter) mAbSlidingTabView.getViewPager().getAdapter();
		ThreadsFragment fragment = (ThreadsFragment) adapter.getItem(index);
		Bundle bundle = fragment.getArguments();
		ForumInfo forumInfo = (ForumInfo) bundle.getSerializable(Constant.KEY_FORUMINFO);
		return forumInfo;
	}

	private void initFragmentTabs(final List<ForumInfo> forums) {
		Log.d(TAG, "initFragmentTabs");
		if (forums == null || !AbAppUtil.isNetworkAvailable(getActivity())) {
			int tabId = 0;
			ForumInfo info = new ForumInfo();
			info.fid = 0;
			info.tabId = tabId++;
			info.title = "聚焦";
			info.url = "";
			ThreadsFragment newFragment = ThreadsFragment.newInstance(info);
			Constant.forumNames.put(info.fid, info.title);
			mAbSlidingTabView.addItemView(info.title, newFragment);

			info = new ForumInfo();
			info.fid = 1;
			info.tabId = tabId++;
			info.title = "购物";
			info.url = "http://bbs.cstong.net/thread-htm-fid-1.html";
			newFragment = ThreadsFragment.newInstance(info);
			Constant.forumNames.put(info.fid, info.title);
			mAbSlidingTabView.addItemView(info.title, newFragment);

			info = new ForumInfo();
			info.fid = 88;
			info.tabId = tabId++;
			info.title = "团购";
			info.url = "http://bbs.cstong.net/thread-htm-fid-88.html";
			newFragment = ThreadsFragment.newInstance(info);
			Constant.forumNames.put(info.fid, info.title);
			mAbSlidingTabView.addItemView(info.title, newFragment);

			info = new ForumInfo();
			info.fid = 215;
			info.tabId = tabId++;
			info.title = "穿搭";
			info.url = "http://bbs.cstong.net/thread-htm-fid-215.html";
			newFragment = ThreadsFragment.newInstance(info);
			Constant.forumNames.put(info.fid, info.title);
			mAbSlidingTabView.addItemView(info.title, newFragment);

			info = new ForumInfo();
			info.fid = 19;
			info.tabId = tabId++;
			info.title = "装修";
			info.url = "http://bbs.cstong.net/thread-htm-fid-19.html";
			newFragment = ThreadsFragment.newInstance(info);
			Constant.forumNames.put(info.fid, info.title);
			mAbSlidingTabView.addItemView(info.title, newFragment);

			info = new ForumInfo();
			info.fid = 127;
			info.tabId = tabId++;
			info.title = "亲子";
			info.url = "http://bbs.cstong.net/thread-htm-fid-127.html";
			newFragment = ThreadsFragment.newInstance(info);
			Constant.forumNames.put(info.fid, info.title);
			mAbSlidingTabView.addItemView(info.title, newFragment);

			info = new ForumInfo();
			info.fid = 44;
			info.tabId = tabId++;
			info.title = "结婚";
			info.url = "http://bbs.cstong.net/thread-htm-fid-44.html";
			newFragment = ThreadsFragment.newInstance(info);
			Constant.forumNames.put(info.fid, info.title);
			mAbSlidingTabView.addItemView(info.title, newFragment);

			info = new ForumInfo();
			info.fid = 4;
			info.tabId = tabId++;
			info.title = "美食";
			info.url = "http://bbs.cstong.net/thread-htm-fid-4.html";
			newFragment = ThreadsFragment.newInstance(info);
			Constant.forumNames.put(info.fid, info.title);
			mAbSlidingTabView.addItemView(info.title, newFragment);

			info = new ForumInfo();
			info.fid = 6;
			info.tabId = tabId++;
			info.title = "越策";
			info.url = "http://bbs.cstong.net/thread-htm-fid-6.html";
			newFragment = ThreadsFragment.newInstance(info);
			Constant.forumNames.put(info.fid, info.title);
			mAbSlidingTabView.addItemView(info.title, newFragment);
		} else {
			mAbSlidingTabView.removeAllItemViews();
			//演示增加一组
			List<Fragment> mFragments = new ArrayList<Fragment>();
			ForumInfo info = new ForumInfo();
			info.fid = 0;
			info.tabId = 0;
			info.title = "聚焦";
			info.url = "";
			ThreadsFragment fragment = ThreadsFragment.newInstance(info);
			Constant.forumNames.put(info.fid, info.title);
			// 先只显示聚焦
			fragment.setUserVisibleHint(true);
			mFragments.add(fragment);
			List<String> tabTexts = new ArrayList<String>();
			tabTexts.add(info.title);

			for (int i = 0; i < tabsNum && i < forums.size(); i++) {
				info = forums.get(i);
				fragment = ThreadsFragment.newInstance(info);
				fragment.setUserVisibleHint(false);
				tabTexts.add(info.title);
				Constant.forumNames.put(info.fid, info.title);
				mFragments.add(fragment);
			}
			mAbSlidingTabView.addItemViews(tabTexts, mFragments);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}
	
	
}

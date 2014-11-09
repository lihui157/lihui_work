package com.jhgzs.mybox.activity;

import com.jhgzs.mybox.R;
import com.jhgzs.mybox.activity.ImgListFragment.ImgListFragmentListener;
import com.jhgzs.mybox.broadcast.MediaInfoChangeReceiver;
import com.jhgzs.mybox.sys.Config;
import com.jhgzs.mybox.sys.FileFilter;

import android.R.integer;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBar.TabListener;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class BoxMainFrameAct extends ActionBarActivity implements TabListener,
		ImgListFragmentListener {

	private static final String TAG = "BoxMainFrameAct";
	private ActionBar actionBar;

	private ImgListFragment imgListFragment;
	private MusicListFragment musicListFragment;
	private VideoListFragment videoListFragment;

	private MediaInfoChangeReceiver receiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_act);

		actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.addTab(actionBar.newTab()
				.setText(getString(R.string.title_section1))
				.setTabListener(this));

		actionBar.addTab(actionBar.newTab()
				.setText(getString(R.string.title_section2))
				.setTabListener(this));

		actionBar.addTab(actionBar.newTab()
				.setText(getString(R.string.title_section3))
				.setTabListener(this));

//		new Thread(new FileFilter(this)).start();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// º”‘ÿaction items
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub

	}

	public void refreshImgList() {
		if (actionBar.getSelectedTab().getPosition() == 1) {
			imgListFragment.refreshList();
		}
	}

	public void refreshVideoList() {

	}

	public void refreshMusicList() {

	}

	@Override
	public void onTabSelected(Tab arg0, FragmentTransaction arg1) {
		Log.i(TAG, "onTabSelected:" + arg0.getText().toString());
		displayFragment(arg1, arg0.getPosition());
	}

	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
		Log.i(TAG, "onTabUnselected:" + arg0.getText().toString());
		hiddenFragment(arg1, arg0.getPosition());
	}

	private void displayFragment(FragmentTransaction ft, int i) {
		switch (i) {
		case 0:
			imgListFragment = (ImgListFragment) getSupportFragmentManager()
					.findFragmentByTag(ImgListFragment.class.getCanonicalName());
			if (imgListFragment == null) {
				imgListFragment = new ImgListFragment();
				ft = getSupportFragmentManager().beginTransaction();
				ft.add(R.id.fl_fragment_container, imgListFragment,
						ImgListFragment.class.getCanonicalName());
				ft.commit();
			} else {
				ft = getSupportFragmentManager().beginTransaction();
				ft.show(imgListFragment);
				ft.commit();
			}
			break;
		case 1:
			videoListFragment = (VideoListFragment) getSupportFragmentManager()
					.findFragmentByTag(
							VideoListFragment.class.getCanonicalName());
			if (videoListFragment == null) {
				videoListFragment = new VideoListFragment();
				ft = getSupportFragmentManager().beginTransaction();
				ft.add(R.id.fl_fragment_container, videoListFragment,
						VideoListFragment.class.getCanonicalName());
				ft.commit();
			} else {
				ft = getSupportFragmentManager().beginTransaction();
				ft.show(videoListFragment);
				ft.commit();
			}
			break;
		case 2:
			musicListFragment = (MusicListFragment) getSupportFragmentManager()
					.findFragmentByTag(
							MusicListFragment.class.getCanonicalName());
			if (musicListFragment == null) {
				musicListFragment = new MusicListFragment();
				ft = getSupportFragmentManager().beginTransaction();
				ft.add(R.id.fl_fragment_container, musicListFragment,
						MusicListFragment.class.getCanonicalName());
				ft.commit();
			} else {
				ft = getSupportFragmentManager().beginTransaction();
				ft.show(musicListFragment);
				ft.commit();
			}
			break;

		default:
			break;
		}
	}

	private void hiddenFragment(FragmentTransaction ft, int i) {
		switch (i) {
		case 0:
			imgListFragment = (ImgListFragment) getSupportFragmentManager()
					.findFragmentByTag(ImgListFragment.class.getCanonicalName());
			ft = getSupportFragmentManager().beginTransaction();
			ft.hide(imgListFragment);
			ft.commit();
			break;
		case 1:
			videoListFragment = (VideoListFragment) getSupportFragmentManager()
					.findFragmentByTag(
							VideoListFragment.class.getCanonicalName());

			ft = getSupportFragmentManager().beginTransaction();
			ft.hide(videoListFragment);
			ft.commit();
			break;
		case 2:
			musicListFragment = (MusicListFragment) getSupportFragmentManager()
					.findFragmentByTag(
							MusicListFragment.class.getCanonicalName());

			ft = getSupportFragmentManager().beginTransaction();
			ft.hide(musicListFragment);
			ft.commit();
			break;

		default:
			break;
		}
	}

	@Override
	protected void onPause() {
		stopBroadCact();
		super.onPause();
	}

	@Override
	protected void onResume() {
		startBroadCast();
		super.onResume();
	}

	private void startBroadCast() {
		stopBroadCact();
		if (receiver == null) {
			receiver = new MediaInfoChangeReceiver();
			IntentFilter filter = new IntentFilter();
			filter.addAction(Config.BroadcastConf.ACT_REFRESH_IMG);
			filter.addAction(Config.BroadcastConf.ACT_REFRESH_MUSIC);
			filter.addAction(Config.BroadcastConf.ACT_REFRESH_VIDEO);
			registerReceiver(receiver, filter);
		}
	}

	private void stopBroadCact() {
		if (receiver != null) {
			unregisterReceiver(receiver);
			receiver = null;
		}
	}

}

package com.hui.mybox.activity;

import com.hui.mybox.R;
import com.hui.mybox.activity.ImgListFragment.ImgListFragmentListener;
import com.hui.mybox.broadcast.MediaInfoChangeReceiver;
import com.hui.mybox.sys.Config;
import com.hui.mybox.sys.FileFilter;

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

public class BoxMainFrameAct extends ActionBarActivity implements TabListener,ImgListFragmentListener  {
	
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
		actionBar.addTab(actionBar.newTab().setText(getString(R.string.title_section1))
				.setTabListener(this));

		actionBar.addTab(actionBar.newTab().setText(getString(R.string.title_section2))
				.setTabListener(this));

		actionBar.addTab(actionBar.newTab().setText(getString(R.string.title_section3))
				.setTabListener(this));
		
		new Thread(new FileFilter(this)).start();

	}

	@Override  
    public boolean onCreateOptionsMenu(Menu menu) {  
        //º”‘ÿaction items  
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

	@Override
	public void onTabSelected(Tab arg0, FragmentTransaction arg1) {
		Log.i(TAG, arg0.getText().toString());
		switch (arg0.getPosition()) {
		case 0:
//			imgListFragment = (ImgListFragment) getSupportFragmentManager().findFragmentByTag(ImgListFragment.class.getCanonicalName());
//			if(imgListFragment!=null){
//				if(imgListFragment.isAdded()) break;
//				arg1 = getSupportFragmentManager().beginTransaction();
//				arg1.replace(R.id.fl_fragment_container, imgListFragment, ImgListFragment.class.getCanonicalName());
//				arg1.commit();
//			}else{
//				imgListFragment = new ImgListFragment();
//				if(imgListFragment.isAdded()) break;
//				arg1 = getSupportFragmentManager().beginTransaction();
//				arg1.replace(R.id.fl_fragment_container, imgListFragment, ImgListFragment.class.getCanonicalName());
//				arg1.commit();
//			}
			displayTab(arg1,imgListFragment,arg0.getPosition());
			break;
		case 1:
//			videoListFragment = (VideoListFragment) getSupportFragmentManager().findFragmentByTag(VideoListFragment.class.getCanonicalName());
//			if(videoListFragment!=null){
//				if(videoListFragment.isAdded()) break;
//				arg1 = getSupportFragmentManager().beginTransaction();
//				arg1.replace(R.id.fl_fragment_container, videoListFragment, VideoListFragment.class.getCanonicalName());
//				arg1.commit();
//			}else{
//				videoListFragment = new VideoListFragment();
//				if(videoListFragment.isAdded()) break;
//				arg1 = getSupportFragmentManager().beginTransaction();
//				arg1.replace(R.id.fl_fragment_container, videoListFragment, VideoListFragment.class.getCanonicalName());
//				arg1.commit();
//			}
			displayTab(arg1,videoListFragment,arg0.getPosition());
			break;
		case 2:
//			musicListFragment = (MusicListFragment) getSupportFragmentManager().findFragmentByTag(MusicListFragment.class.getCanonicalName());
//			if(musicListFragment!=null){
//				if(musicListFragment.isAdded()) break;
//				arg1 = getSupportFragmentManager().beginTransaction();
//				arg1.replace(R.id.fl_fragment_container, musicListFragment, MusicListFragment.class.getCanonicalName());
//				arg1.commit();
//			}else{
//				musicListFragment = new MusicListFragment();
//				if(musicListFragment.isAdded()) break;
//				arg1 = getSupportFragmentManager().beginTransaction();
//				arg1.replace(R.id.fl_fragment_container, musicListFragment, MusicListFragment.class.getCanonicalName());
//				arg1.commit();
//			}
			displayTab(arg1,musicListFragment,arg0.getPosition());
			break;

		default:
			break;
		}
		
		
	}
	
	private void displayTab(FragmentTransaction ft,Fragment fragment,int i){
		String classNameString = null;
		if(i==0) classNameString = ImgListFragment.class.getCanonicalName();
		if(i==1) classNameString = VideoListFragment.class.getCanonicalName();
		if(i==2) classNameString = MusicListFragment.class.getCanonicalName();
		fragment = (MusicListFragment) getSupportFragmentManager().findFragmentByTag(classNameString);
		if(fragment!=null){
			if(fragment.isAdded()) return;
			ft = getSupportFragmentManager().beginTransaction();
			ft.replace(R.id.fl_fragment_container, fragment, classNameString);
			ft.commit();
		}else{
			if(i==0) fragment = new ImgListFragment();
			if(i==2) fragment = new MusicListFragment();
			if(i==1) fragment = new VideoListFragment();
			if(fragment.isAdded()) return;
			ft = getSupportFragmentManager().beginTransaction();
			ft.replace(R.id.fl_fragment_container, fragment, classNameString);
			ft.commit();
		}
	}
	
	public void refreshImgList(){
		if(actionBar.getSelectedTab().getPosition()==1){
			imgListFragment.refreshList();
		}
	}
	
	public void refreshVideoList(){
		
	}

	public void refreshMusicList(){
	
	}
	
	

	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
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

	private void startBroadCast(){
		stopBroadCact();
		if(receiver==null){
			receiver = new MediaInfoChangeReceiver();
			IntentFilter filter = new IntentFilter();
			filter.addAction(Config.BroadcastConf.ACT_REFRESH_IMG);
			filter.addAction(Config.BroadcastConf.ACT_REFRESH_MUSIC);
			filter.addAction(Config.BroadcastConf.ACT_REFRESH_VIDEO);
			registerReceiver(receiver, filter);
		}
	}
	
	private void stopBroadCact(){
		if(receiver!=null){
			unregisterReceiver(receiver);
			receiver = null;
		}
	}
	
	

}

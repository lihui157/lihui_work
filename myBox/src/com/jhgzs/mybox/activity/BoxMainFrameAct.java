package com.jhgzs.mybox.activity;

import java.util.List;

import junit.framework.Test;

import com.connectsdk.device.ConnectableDevice;
import com.connectsdk.device.ConnectableDeviceListener;
import com.connectsdk.device.DevicePicker;
import com.connectsdk.discovery.CapabilityFilter;
import com.connectsdk.discovery.DiscoveryManager;
import com.connectsdk.discovery.DiscoveryManagerListener;
import com.connectsdk.service.DeviceService;
import com.connectsdk.service.DeviceService.PairingType;
import com.connectsdk.service.capability.MediaControl;
import com.connectsdk.service.capability.MediaPlayer;
import com.connectsdk.service.capability.MediaPlayer.LaunchListener;
import com.connectsdk.service.capability.MediaPlayer.MediaLaunchObject;
import com.connectsdk.service.capability.VolumeControl;
import com.connectsdk.service.command.ServiceCommandError;
import com.connectsdk.service.sessions.LaunchSession;
import com.jhgzs.mybox.R;
import com.jhgzs.mybox.activity.ImgListFragment.ImgListFragmentListener;
import com.jhgzs.mybox.broadcast.MediaInfoChangeReceiver;
import com.jhgzs.mybox.connect.BoxConnectableDeviceListener;
import com.jhgzs.mybox.connect.BoxDiscoverManagerListener;
import com.jhgzs.mybox.sys.Config;
import com.jhgzs.mybox.sys.FileFilter;

import android.R.integer;
import android.app.AlertDialog;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class BoxMainFrameAct extends ActionBarActivity implements TabListener,
		ImgListFragmentListener  {

	private static final String TAG = "BoxMainFrameAct";
	private ActionBar actionBar;

	private ImgListFragment imgListFragment;
	private MusicListFragment musicListFragment;
	private VideoListFragment videoListFragment;
	
	private DiscoveryManager mDiscoveryManager;
	private BoxDiscoverManagerListener discoverManagerListener;
	private ConnectableDevice mDevice;
	LaunchSession mLaunchSession;
	MediaControl mMediaControl;
	private BoxConnectableDeviceListener connectableDeviceListener;
	private OnItemClickListener selectDevice = new AdapterView.OnItemClickListener() {
	    @Override
	    public void onItemClick(AdapterView adapter, View parent, int position, long id) {
	        mDevice = (ConnectableDevice) adapter.getItemAtPosition(position);
	        if(connectableDeviceListener==null){
	        	connectableDeviceListener = new BoxConnectableDeviceListener();
	        }
	        mDevice.addListener(connectableDeviceListener);
	        mDevice.connect();
	    }
	};

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

		
		DiscoveryManager.init(getApplicationContext());

	    // This step could even happen in your app's delegate
		CapabilityFilter videoFilter = new CapabilityFilter(
				MediaPlayer.Display_Video, 
				MediaControl.Any, 
				VolumeControl.Volume_Up_Down
		);
		
		CapabilityFilter audioFilter = new CapabilityFilter(
				MediaPlayer.Display_Audio, 
				MediaControl.Any, 
				VolumeControl.Volume_Up_Down
		);

		CapabilityFilter imageCapabilities = new CapabilityFilter(
				MediaPlayer.Display_Image
		);

		
	    mDiscoveryManager = DiscoveryManager.getInstance();
	    mDiscoveryManager.setCapabilityFilters(videoFilter, imageCapabilities,audioFilter);
	    discoverManagerListener = new BoxDiscoverManagerListener();
	    mDiscoveryManager.addListener(discoverManagerListener);
	    mDiscoveryManager.start();
	    
	    showImage();

	}
	
	
	private void test(){
		String mediaURL = "http://www.connectsdk.com/files/9613/9656/8539/test_image.jpg"; // credit: Blender Foundation/CC By 3.0
		String iconURL = "http://www.connectsdk.com/files/2013/9656/8845/test_image_icon.jpg"; // credit: sintel-durian.deviantart.com
		String title = "Sintel Character Design";
		String description = "Blender Open Movie Project";
		String mimeType = "image/jpeg";

		

		MediaPlayer.LaunchListener listener = new LaunchListener() {
		    

		    @Override
		    public void onError(ServiceCommandError error) {
		        Log.d("App Tag", "Display photo failure: " + error);
		    }

			@Override
			public void onSuccess(MediaLaunchObject object) {
				 mLaunchSession = object.launchSession;
			        mMediaControl = object.mediaControl;
				
			}
		};

		mDevice.getMediaPlayer().displayImage( mediaURL,iconURL,title,description,mimeType,listener);
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
	
	private void showImage() {
	    DevicePicker devicePicker = new DevicePicker(this);
	    AlertDialog dialog = devicePicker.getPickerDialog("Show Image", selectDevice);
	    dialog.show();
	}

	

	

}

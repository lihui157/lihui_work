package com.jhgzs.connect;

import android.util.Log;

import com.connectsdk.device.ConnectableDevice;
import com.connectsdk.discovery.DiscoveryManager;
import com.connectsdk.discovery.DiscoveryManagerListener;
import com.connectsdk.service.command.ServiceCommandError;

public class BoxDiscoverManagerListener implements DiscoveryManagerListener {
	
	private static final String TAG = "BoxDiscoverManagerListener";

	@Override
	public void onDeviceAdded(DiscoveryManager manager, ConnectableDevice device) {
		Log.i(TAG, "onDeviceAdded = "+device.getFriendlyName());
		
	}

	@Override
	public void onDeviceUpdated(DiscoveryManager manager,
			ConnectableDevice device) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDeviceRemoved(DiscoveryManager manager,
			ConnectableDevice device) {
		Log.i(TAG, "onDeviceRemoved = "+device.getFriendlyName());
		
	}

	@Override
	public void onDiscoveryFailed(DiscoveryManager manager,
			ServiceCommandError error) {
		// TODO Auto-generated method stub
		
	}

}

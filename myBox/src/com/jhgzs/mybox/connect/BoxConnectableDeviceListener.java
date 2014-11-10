package com.jhgzs.mybox.connect;

import java.util.List;

import android.util.Log;

import com.connectsdk.device.ConnectableDevice;
import com.connectsdk.device.ConnectableDeviceListener;
import com.connectsdk.service.DeviceService;
import com.connectsdk.service.DeviceService.PairingType;
import com.connectsdk.service.command.ServiceCommandError;

public class BoxConnectableDeviceListener implements ConnectableDeviceListener {


	private static final String TAG = "BoxConnectableDeviceListener";
	
	@Override
	public void onPairingRequired(ConnectableDevice device,
			DeviceService service, PairingType pairingType) {
		Log.i(TAG, "onPairingRequired:"+device.getFriendlyName()+"||"+service.getServiceName()+"||"+pairingType.name());
		
	}
	
	@Override
	public void onDeviceReady(ConnectableDevice device) {
		Log.i(TAG, "onDeviceReady:"+device.getFriendlyName());
//		test();
	}
	
	@Override
	public void onDeviceDisconnected(ConnectableDevice device) {
		Log.i(TAG, "onDeviceDisconnected:"+device.getFriendlyName());
		
	}
	
	@Override
	public void onConnectionFailed(ConnectableDevice device,
			ServiceCommandError error) {
		Log.e(TAG, "onConnectionFailed:"+device.getFriendlyName()+"||"+error.getMessage());
		
	}
	
	@Override
	public void onCapabilityUpdated(ConnectableDevice device,
			List<String> added, List<String> removed) {
		Log.e(TAG, "onCapabilityUpdated:"+device.getFriendlyName());
		
	}

}

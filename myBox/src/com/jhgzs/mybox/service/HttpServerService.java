package com.jhgzs.mybox.service;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.jhgzs.http.HttpServer;


import android.app.Service;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.IBinder;

public class HttpServerService extends Service {

	private static final String TAG = "HttpServerService";
	public static final String SERVICE_ACTION = "com.jhgzs.mybox.service.HttpServerService";
	private HttpServerService instance ;
	private static HttpServer server;
	public static final int START_HTTPSERVER = 0;
	public static final int STOP_HTTPSERVER = 1;
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		instance = this;
		startWebServer();
		super.onCreate();
	}
	
	

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		instance = null;
		stopWebServer();
		super.onDestroy();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		int action = intent.getIntExtra("action", -1);
		switch (action) {
		case START_HTTPSERVER:
			startWebServer();
			break;
		case STOP_HTTPSERVER:
			stopWebServer();
			break;

		default:
			break;
		}
		return super.onStartCommand(intent, flags, startId);
	}
	
	/**
	 * »ñÈ¡ip
	 * @return
	 * @throws UnknownHostException
	 */
	private InetAddress getLocalIpAddress() throws UnknownHostException {
        WifiManager wifiManager = (WifiManager) getSystemService(android.content.Context.WIFI_SERVICE );
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        return InetAddress.getByName(String.format("%d.%d.%d.%d",
                        (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                        (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff)));

    }   
	
	private void startWebServer(){
		String rootPath = "/";
//			ServerApplication.setServer(new CustomWebServer(getApplication(),8080, new File(rootPath)));
		try {
			if(server==null){
				server = new HttpServer(null, 8080, Environment.getExternalStorageDirectory(), true);
			}
			if(!server.isAlive()){
				server.start();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void stopWebServer(){
//		ServerApplication.getServer().stop();
//		ServerApplication.setServer(null);
		if(server!=null)
		server.stop();
	}

}

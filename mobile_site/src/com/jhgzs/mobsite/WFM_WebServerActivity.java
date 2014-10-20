package com.jhgzs.mobsite;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import com.jhgzs.mobsite.http.WFM_CustomWebServer;
import com.jhgzs.mobsite.http.WFM_NanoHTTPD;
import com.jhgzs.mobsite.util.WFM_FileUtil;



import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class WFM_WebServerActivity extends Activity {
    
	private TextView tvUrl,tvUrlDesc;
	private Button btStartOrStop;
	
	private static final String TAG_START = "start";
	private static final String TAG_STOP = "stop";
	
	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wfm_webserver_act);
		try {
			System.out.println(getBaseContext().getFilesDir().getCanonicalFile());
			WFM_FileUtil.deleteDirectory(getBaseContext().getFilesDir().getCanonicalFile()+"/webroot/");
//			FileUtil.copyDirectiory(Environment.getExternalStorageDirectory().getCanonicalPath()+"/webroot/", getBaseContext().getFilesDir().getCanonicalFile()+"/webroot/");
			copyAssets("webroot", getBaseContext().getFilesDir().getCanonicalFile()+"/webroot/");
			File file = getBaseContext().getFilesDir();
			showAllFiles(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		initUI();
	}
	
	private void  showAllFiles(File dir){
		  File[] fs = dir.listFiles();
		  for(int i=0; i<fs.length; i++){
		   System.out.println(fs[i].getAbsolutePath());
		   if(fs[i].isDirectory()){
		    try{
		     showAllFiles(fs[i]);
		    }catch(Exception e){
		    	e.printStackTrace();
		    }
		   }
		  }
		 }
	
	
	
	private void copyAssets(String assetDir, String dir) {
		String[] files;
		try {
			files = this.getResources().getAssets().list(assetDir);
		} catch (IOException e1) {
			return;
		}
		File mWorkingPath = new File(dir);
		// if this directory does not exists, make one.
		if (!mWorkingPath.exists()) {
			if (!mWorkingPath.mkdirs()) {
				Log.e("--CopyAssets--", "cannot create directory.");
			}
		}
		for (int i = 0; i < files.length; i++) {
			try {
				String fileName = files[i];
				// we make sure file name not contains '.' to be a folder.
				if (!fileName.contains(".")) {
					if (0 == assetDir.length()) {
						copyAssets(fileName, dir + fileName + "/");
					} else {
						copyAssets(assetDir + "/" + fileName, dir + fileName
								+ "/");
					}
					continue;
				}
				File outFile = new File(mWorkingPath, fileName);
				if (outFile.exists())
					outFile.delete();
				InputStream in = null;
				if (0 != assetDir.length())
					in = getAssets().open(assetDir + "/" + fileName);
				else
					in = getAssets().open(fileName);
				OutputStream out = new FileOutputStream(outFile);
				// Transfer bytes from in to out
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				out.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void initUI(){
		tvUrl = (TextView) findViewById(R.id.tv_url);
		tvUrlDesc = (TextView) findViewById(R.id.tv_url_desc);
		btStartOrStop = (Button) findViewById(R.id.bt_startorstop);
		btStartOrStop.setTag(TAG_STOP);
		if(ServerApplication.getServer()!=null){
			btStartOrStop.setTag(TAG_START);
			btStartOrStop.setText(R.string.stop_server);
			tvUrlDesc.setText(R.string.url_how_open);
			try {
				tvUrl.setText("http://"+getLocalIpAddress().toString()+":8080/");
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}else{
			btStartOrStop.setTag(TAG_STOP);
			btStartOrStop.setText(R.string.start_server);
			tvUrlDesc.setText(R.string.url_desc);
			tvUrl.setText("");
		}
		btStartOrStop.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//启动
				if(btStartOrStop.getTag().equals(TAG_STOP)){
					if(ServerApplication.getServer()==null){
						startWebServer();
					}else{
						stopWebServer();
						startWebServer();
					}
					btStartOrStop.setTag(TAG_START);
					btStartOrStop.setText(R.string.stop_server);
					tvUrlDesc.setText(R.string.url_how_open);
					try {
						tvUrl.setText("http://"+getLocalIpAddress().toString()+":8080/");
					} catch (UnknownHostException e) {
						e.printStackTrace();
					}
					return;
				}
				//关闭
				if(btStartOrStop.getTag().equals(TAG_START)){
					if(ServerApplication.getServer()!=null){
						stopWebServer();
					}
					btStartOrStop.setTag(TAG_STOP);
					btStartOrStop.setText(R.string.start_server);
					tvUrlDesc.setText(R.string.url_desc);
					tvUrl.setText("");
					return;
				}
				
			}
		});
	}
	
	/**
	 * 获取ip
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
		Intent intent = new Intent("com.jhgzs.mobsite.HttpService");
		intent.putExtra("action", HttpService.START_HTTPSERVER);
		startService(intent);
	}
	
	private void stopWebServer(){
		Intent intent = new Intent("com.jhgzs.mobsite.HttpService");
		intent.putExtra("action", HttpService.STOP_HTTPSERVER);
		startService(intent);
	}
	

}

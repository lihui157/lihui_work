package com.jhgzs.mybox.broadcast;

import com.jhgzs.mybox.activity.BoxMainFrameAct;
import com.jhgzs.mybox.sys.Config;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MediaInfoChangeReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		
		if(arg0 instanceof BoxMainFrameAct ){
			
			if(arg1.getAction().equals(Config.BroadcastConf.ACT_REFRESH_IMG)){
				
				((BoxMainFrameAct)arg0).refreshImgList();
				
			}else if(arg1.getAction().equals(Config.BroadcastConf.ACT_REFRESH_IMG)){
				
				((BoxMainFrameAct)arg0).refreshVideoList();
				
			}else if(arg1.getAction().equals(Config.BroadcastConf.ACT_REFRESH_IMG)){
				
				((BoxMainFrameAct)arg0).refreshMusicList();
				
			}
		}

	}

}

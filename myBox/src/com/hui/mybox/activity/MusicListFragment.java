package com.hui.mybox.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hui.mybox.R;
import com.hui.mybox.model.MediaFileInfo;
import com.hui.mybox.sys.Config;
import com.hui.mybox.utils.FileUtil;
import com.hui.mybox.utils.LogUtil;
import com.hui.mybox.view.ImageAdapter;
import com.hui.mybox.view.MediaFileAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class MusicListFragment extends Fragment {
	
	private static final String TAG = "MusicListFragment";
	
	private ListView listView;
	private ImageAdapter mideaAdapter;
	private List<MediaFileInfo> dataList;
	
	public static final int VAL_GET_FILE = 1001;
	public static final int VAL_REFRESH_ADAPTER = 1002;
	
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case VAL_GET_FILE:
				initData();
				handler.sendEmptyMessage(VAL_REFRESH_ADAPTER);
				break;
			case VAL_REFRESH_ADAPTER:
				refreshData();
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
		
	};

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		initUI();
		handler.sendEmptyMessage(VAL_GET_FILE);
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
//		return super.onCreateView(inflater, container, savedInstanceState);
		return inflater.inflate(R.layout.music_list_fragment, container, false);
	}
	
	
	private void initData(){
		String musicIndexPath;
		try {
			musicIndexPath = Environment.getExternalStorageDirectory().getCanonicalPath()
					+Config.Sys.APP_ROOT
					+Config.Sys.MUSIC_INDEX_FILE;
			if(dataList==null){
				dataList = new ArrayList<MediaFileInfo>();
			}
			dataList.clear();
			ArrayList<MediaFileInfo> tempList = new Gson().fromJson(
					FileUtil.readTextFile(musicIndexPath), 
					new TypeToken<ArrayList<MediaFileInfo>>(){}.getType()
					);
			for(MediaFileInfo info:tempList){
				dataList.add(info);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void refreshData(){
		if(mideaAdapter==null) return;
		mideaAdapter.notifyDataSetChanged();
	}
	
	private void initUI(){
		listView = (ListView) getActivity().findViewById(R.id.lv_music_list);
		if(dataList==null){
			dataList = new ArrayList<MediaFileInfo>();
		}
		if(mideaAdapter==null){
			mideaAdapter = new ImageAdapter(getActivity(), dataList);
		}
		listView.setAdapter(mideaAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				LogUtil.info(TAG, "listView.setOnItemClickListener", dataList.get(arg2).getFileName());
				
			}
		});
	}
	

}

package com.jhgzs.mybox.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jhgzs.mybox.R;
import com.jhgzs.mybox.activity.view.ImageAdapter;
import com.jhgzs.mybox.activity.view.MediaFileAdapter;
import com.jhgzs.mybox.model.PhotoManager;
import com.jhgzs.mybox.model.bean.MediaFileInfo;
import com.jhgzs.mybox.sys.Config;
import com.jhgzs.utils.FileUtil;
import com.jhgzs.utils.LogUtil;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ImgListFragment extends Fragment {
	
	private static final String TAG = "ImgListFragment";
	
	private ListView listView;
	private ImageAdapter imgAdapter;
	private List<MediaFileInfo> dataList;
	private ImgListFragmentListener mCallback;
	
	public static final int VAL_GET_FILE = 1001;
	public static final int VAL_REFRESH_ADAPTER = 1002;
	
	private PhotoManager photoManager;
	
	public interface ImgListFragmentListener{
//		public void refreshImgList();
	}
	
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
		super.onAttach(activity);
		try {
			mCallback = (ImgListFragmentListener) activity;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
//		return super.onCreateView(inflater, container, savedInstanceState);
		return inflater.inflate(R.layout.img_list_fragment, container, false);
	}
	
	
	private void initData(){
		if(photoManager==null){
			photoManager = new PhotoManager();
		}
		if(dataList==null){
			dataList = new ArrayList<MediaFileInfo>();
		}
		dataList.clear();
		dataList.addAll(photoManager.getImages(getActivity()));
		
	}
	
	private void refreshData(){
		if(imgAdapter==null) return;
		imgAdapter.notifyDataSetChanged();
	}
	
	private void initUI(){
		listView = (ListView) getActivity().findViewById(R.id.lv_img_list);
		if(dataList==null){
			dataList = new ArrayList<MediaFileInfo>();
		}
		if(imgAdapter==null){
			imgAdapter = new ImageAdapter(getActivity(), dataList);
		}
		listView.setAdapter(imgAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				LogUtil.info(TAG, "listView.setOnItemClickListener", dataList.get(arg2).getFileName());
				
			}
		});
		
		listView.setOnScrollListener(new OnScrollListener(){

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
	                   int visibleItemCount, int totalItemCount) {
				if(imgAdapter!=null){
//					Log.e(TAG, "start:"+firstVisibleItem);
					imgAdapter.setStart(firstVisibleItem);
					imgAdapter.setEnd(firstVisibleItem+visibleItemCount);
            }
				
			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_FLING
	                       || scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
	                   //CancelIconThread();
					imgAdapter.setLoading(false);
	                   return;
	               }
	 
	               if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
	                   //GetIconOnThread();
	            	   imgAdapter.setLoading(true);
	            	   imgAdapter.runLoading();
	                   return;
	               }
			}
			
		});
	}
	
	public void refreshList(){
		imgAdapter.notifyDataSetChanged();
	}
	

}

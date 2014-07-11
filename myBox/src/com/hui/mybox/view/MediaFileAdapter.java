package com.hui.mybox.view;

import java.io.File;
import java.util.List;


import com.hui.mybox.R;
import com.hui.mybox.model.MediaFileInfo;
import com.hui.mybox.sys.MediaApp;
import com.hui.mybox.utils.BoxUtil;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MediaFileAdapter extends BaseAdapter {
	
	private static final String TAG = "MediaFileAdapter";
	
	private List<MediaFileInfo> data;
	
	private LayoutInflater listContainer;
	
	private Context context;
	
	public MediaFileAdapter(Context context,List<MediaFileInfo> dataList){
		this.data = dataList;
		listContainer = LayoutInflater.from(context);
		this.context = context;
	}

	@Override
	public int getCount() {
		if(data==null) return 0;
		return data.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int arg0, View convertView, ViewGroup arg2) {
		try {
			//自定义视图   
	        ListItemView  listItemView = null;   
	        if (convertView == null) {   
	            listItemView = new ListItemView();    
	            //获取list_item布局文件的视图   
	            convertView = listContainer.inflate(R.layout.img_list_item, null);   
	            //获取控件对象   
	            listItemView.name = (TextView)convertView.findViewById(R.id.tv_img_item_name);   
	            listItemView.icon = (ImageView)convertView.findViewById(R.id.iv_icon);   
	            listItemView.size = (TextView)convertView.findViewById(R.id.tv_img_item_size);   
	            listItemView.time = (TextView)convertView.findViewById(R.id.tv_img_item_time);   
	            
	            //设置控件集到convertView   
	            convertView.setTag(listItemView);   
	        }else {   
	            listItemView = (ListItemView)convertView.getTag();   
	        } 
	        
	        listItemView.name.setText(data.get(arg0).getFileName());
	        Log.e(TAG, "name = "+data.get(arg0).getFileName());
	        listItemView.icon.setImageBitmap(BoxUtil.getBitmap(context, data.get(arg0).getPath()));
	        Log.e(TAG, "path = "+data.get(arg0).getPath());
	        listItemView.size.setText((data.get(arg0).getFileType()==MediaFileInfo.FILE_TYPE_FOLDER)? String.valueOf(data.get(arg0).getLength()):"");
	        Log.e(TAG, "size = "+listItemView.size.getText().toString());
	        listItemView.time.setText(Long.toString(data.get(arg0).getLastModifTime()));
	        Log.e(TAG, "time = "+listItemView.time.getText().toString());
	        
		} catch (Exception e) {
			e.printStackTrace();
		}
        return convertView;   
    
	}
	
	
	
	class ListItemView{
		TextView time;
		TextView size;
		ImageView icon;
		TextView name;
	}

}
